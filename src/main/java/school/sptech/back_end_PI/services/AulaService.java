package school.sptech.back_end_PI.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sptech.back_end_PI.dto.aula.*;
import school.sptech.back_end_PI.entity.*;
import school.sptech.back_end_PI.exception.BusinessRuleException;
import school.sptech.back_end_PI.exception.EntityNotFound;
import school.sptech.back_end_PI.mapper.AulaMapper;
import school.sptech.back_end_PI.repository.AulaRepository;
import school.sptech.back_end_PI.repository.ContratoRepository;
import school.sptech.back_end_PI.repository.LogAulaRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AulaService {

    private static final long MAX_DIAS_PERIODO = 93;

    @Autowired
    private AulaRepository aulaRepository;

    @Autowired
    private LogAulaRepository logAulaRepository;

    @Autowired
    private ContratoRepository contratoRepository;

    @Autowired
    private AuditService audit;

    // ============================================================================
    // GERAÇÃO AUTOMÁTICA DE AULAS
    // ============================================================================

    @Transactional
    public void gerarAulasParaContrato(Contrato contrato) {
        List<Horario> horarios = contrato.getHorarios();
        if (horarios == null || horarios.isEmpty()) return;

        List<Aula> aulas = new ArrayList<>();

        for (Horario horario : horarios) {
            DayOfWeek diaSemana = mapDiaSemana(horario.getDiaSemana());
            LocalDate dataAtual = proximoDiaDaSemana(contrato.getDataInicio(), diaSemana);

            while (!dataAtual.isAfter(contrato.getDataFim())) {
                Aula aula = new Aula();
                aula.setData(dataAtual);
                aula.setHoraInicio(horario.getHoraInicio());
                aula.setHoraFim(horario.getHoraFim());
                aula.setStatus(StatusAula.AGENDADA);
                aula.setPresenca(false);
                aula.setContrato(contrato);
                aulas.add(aula);

                dataAtual = dataAtual.plusWeeks(1);
            }
        }

        List<Aula> aulasSalvas = aulaRepository.saveAll(aulas);

        for (Aula aula : aulasSalvas) {
            registrarLog(aula, "AGENDADA", "Aula gerada automaticamente pelo contrato #" + contrato.getId());
        }
    }

    // ============================================================================
    // ENDPOINTS
    // ============================================================================

    @Transactional
    public AulaResponse adicionarAulaExtra(AulaExtraRequest request) {
        Contrato contrato = buscarContrato(request.getContratoId());

        if (request.getHoraInicio().isAfter(request.getHoraFim())) {
            throw new BusinessRuleException("Hora de início não pode ser após a hora de fim");
        }

        Aula aula = new Aula();
        aula.setData(request.getData());
        aula.setHoraInicio(request.getHoraInicio());
        aula.setHoraFim(request.getHoraFim());
        aula.setStatus(StatusAula.EXTRA);
        aula.setPresenca(false);
        aula.setContrato(contrato);

        Aula salva = aulaRepository.save(aula);
        registrarLog(salva, "EXTRA_ADICIONADA", "Aula extra adicionada para " + request.getData());

        return AulaMapper.toResponse(salva);
    }

    @Transactional
    public AulaResponse remarcarAula(Long id, RemarcarAulaRequest request) {
        Aula aula = buscarAula(id);

        if (aula.getStatus() == StatusAula.CANCELADA) {
            throw new BusinessRuleException("Não é possível remarcar uma aula cancelada");
        }
        validarNovoHorario(request.getNovaHoraInicio(), request.getNovaHoraFim());

        Aula salva = aplicarRemarcacao(aula, request.getNovaData(), request.getNovaHoraInicio(),
                request.getNovaHoraFim(), request.getMotivo());
        audit.log("aula.remarcar", audit.currentActor(), "aula:" + id, "success");

        return AulaMapper.toResponse(salva);
    }

    @Transactional
    public AulaResponse cancelarAula(Long id, CancelarAulaRequest request) {
        Aula aula = buscarAula(id);

        if (aula.getStatus() == StatusAula.CANCELADA) {
            throw new BusinessRuleException("A aula já está cancelada");
        }

        Aula salva = aplicarCancelamento(aula, request != null ? request.getMotivo() : null);
        audit.log("aula.cancelar", audit.currentActor(), "aula:" + id, "success");

        return AulaMapper.toResponse(salva);
    }

    @Transactional
    public void deletarAula(Long id) {
        Aula aula = buscarAula(id);
        logAulaRepository.deleteByAula(aula);
        aulaRepository.delete(aula);
    }

    @Transactional
    public AulaResponse atribuirPresenca(Long id, PresencaRequest request) {
        Aula aula = buscarAula(id);

        if (aula.getStatus() == StatusAula.CANCELADA) {
            throw new BusinessRuleException("Não é possível registrar presença em uma aula cancelada");
        }

        return AulaMapper.toResponse(aplicarPresenca(aula, request.getPresenca()));
    }

    // ============================================================================
    // AÇÕES SOBRE O ENCONTRO DE UMA TURMA (todas as aulas da turma no mesmo horário)
    // Aulas já canceladas são ignoradas; a operação é atômica (tudo ou nada).
    // ============================================================================

    @Transactional
    public List<AulaResponse> cancelarAulaTurma(Long turmaId, CancelarAulaTurmaRequest request) {
        List<Aula> aulas = buscarAulasAtivasDaTurma(turmaId, request);

        List<AulaResponse> resposta = aulas.stream()
                .map(aula -> aplicarCancelamento(aula, request.getMotivo()))
                .map(AulaMapper::toResponse)
                .toList();
        audit.log("aula.turma.cancelar", audit.currentActor(), descreverEncontro(turmaId, request), "success");
        return resposta;
    }

    @Transactional
    public List<AulaResponse> remarcarAulaTurma(Long turmaId, RemarcarAulaTurmaRequest request) {
        validarNovoHorario(request.getNovaHoraInicio(), request.getNovaHoraFim());
        List<Aula> aulas = buscarAulasAtivasDaTurma(turmaId, request);

        List<AulaResponse> resposta = aulas.stream()
                .map(aula -> aplicarRemarcacao(aula, request.getNovaData(), request.getNovaHoraInicio(),
                        request.getNovaHoraFim(), request.getMotivo()))
                .map(AulaMapper::toResponse)
                .toList();
        audit.log("aula.turma.remarcar", audit.currentActor(), descreverEncontro(turmaId, request), "success");
        return resposta;
    }

    @Transactional
    public List<AulaResponse> registrarPresencaTurma(Long turmaId, PresencaAulaTurmaRequest request) {
        List<Aula> aulas = buscarAulasAtivasDaTurma(turmaId, request);

        Set<Long> ausentes = new HashSet<>(request.getAlunosAusentesIds());
        Set<Long> alunosDaAula = aulas.stream()
                .map(aula -> aula.getContrato().getAluno())
                .filter(Objects::nonNull)
                .map(Aluno::getId)
                .collect(Collectors.toSet());
        if (!alunosDaAula.containsAll(ausentes)) {
            throw new BusinessRuleException("Um ou mais alunos informados não pertencem a esta aula da turma");
        }

        List<AulaResponse> resposta = aulas.stream()
                .map(aula -> {
                    Aluno aluno = aula.getContrato().getAluno();
                    boolean ausente = aluno != null && ausentes.contains(aluno.getId());
                    return aplicarPresenca(aula, !ausente);
                })
                .map(AulaMapper::toResponse)
                .toList();
        audit.log("aula.turma.presenca", audit.currentActor(), descreverEncontro(turmaId, request), "success");
        return resposta;
    }

    @Transactional(readOnly = true)
    public List<LogAulaResponse> listarLogsPorAula(Long aulaId) {
        buscarAula(aulaId);
        return logAulaRepository.findByAulaIdOrderByDataHoraDesc(aulaId)
                .stream()
                .map(AulaMapper::toLogResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AulaResponse> listarAulasPorContrato(Long contratoId) {
        if (!contratoRepository.existsById(contratoId)) {
            throw new EntityNotFound("Contrato não encontrado com ID: " + contratoId);
        }
        return aulaRepository.findByContratoIdOrderByDataAsc(contratoId)
                .stream()
                .map(AulaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AulaResponse> listarAulasPorPeriodo(LocalDate inicio, LocalDate fim) {
        validarPeriodo(inicio, fim);
        return aulaRepository.findByDataBetweenOrderByDataAscHoraInicioAsc(inicio, fim)
                .stream()
                .map(AulaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AulaResponse> listarAulasDoProfessorPorPeriodo(Long professorId, LocalDate inicio, LocalDate fim) {
        validarPeriodo(inicio, fim);
        return aulaRepository.findByProfessorAndPeriodo(professorId, inicio, fim)
                .stream()
                .map(AulaMapper::toResponse)
                .toList();
    }

    // ============================================================================
    // MÉTODOS AUXILIARES
    // ============================================================================

    private void registrarLog(Aula aula, String acao, String descricao) {
        LogAula log = new LogAula();
        log.setAula(aula);
        log.setAcao(acao);
        log.setDescricao(descricao);
        log.setDataHora(LocalDateTime.now());
        logAulaRepository.save(log);
    }

    private Aula aplicarCancelamento(Aula aula, String motivoInformado) {
        aula.setStatus(StatusAula.CANCELADA);
        Aula salva = aulaRepository.save(aula);

        String motivo = (motivoInformado != null && !motivoInformado.isBlank()) ? motivoInformado : "não informado";
        registrarLog(salva, "CANCELADA", "Aula cancelada. Motivo: " + motivo);
        return salva;
    }

    private Aula aplicarRemarcacao(Aula aula, LocalDate novaData, LocalTime novaHoraInicio,
                                   LocalTime novaHoraFim, String motivo) {
        String descricao = "Aula remarcada de " + aula.getData() + " " + aula.getHoraInicio()
                + " para " + novaData + " " + novaHoraInicio;
        if (motivo != null && !motivo.isBlank()) {
            descricao += ". Motivo: " + motivo;
        }

        aula.setData(novaData);
        aula.setHoraInicio(novaHoraInicio);
        aula.setHoraFim(novaHoraFim);
        aula.setStatus(StatusAula.REMARCADA);

        Aula salva = aulaRepository.save(aula);
        registrarLog(salva, "REMARCADA", descricao);
        return salva;
    }

    private Aula aplicarPresenca(Aula aula, Boolean presenca) {
        aula.setPresenca(presenca);

        Aula salva = aulaRepository.save(aula);
        String presencaStr = Boolean.TRUE.equals(presenca) ? "Presente" : "Ausente";
        registrarLog(salva, "PRESENCA_REGISTRADA", "Presença registrada: " + presencaStr);
        return salva;
    }

    private void validarNovoHorario(LocalTime novaHoraInicio, LocalTime novaHoraFim) {
        if (novaHoraInicio.isAfter(novaHoraFim)) {
            throw new BusinessRuleException("Hora de início não pode ser após a hora de fim");
        }
    }

    private List<Aula> buscarAulasAtivasDaTurma(Long turmaId, AulaTurmaRequest encontro) {
        List<Aula> aulas = aulaRepository.findByContratoTurmaIdAndDataAndHoraInicioAndHoraFim(
                turmaId, encontro.getData(), encontro.getHoraInicio(), encontro.getHoraFim());
        if (aulas.isEmpty()) {
            throw new EntityNotFound("Nenhuma aula da turma encontrada nesse dia e horário");
        }

        List<Aula> ativas = aulas.stream()
                .filter(aula -> aula.getStatus() != StatusAula.CANCELADA)
                .toList();
        if (ativas.isEmpty()) {
            throw new BusinessRuleException("A aula dessa turma já está cancelada");
        }
        return ativas;
    }

    private String descreverEncontro(Long turmaId, AulaTurmaRequest encontro) {
        return "turma:" + turmaId + " data:" + encontro.getData() + " hora:" + encontro.getHoraInicio();
    }

    // Limita o intervalo consultado para evitar consultas sem limite de tamanho
    private void validarPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio.isAfter(fim)) {
            throw new BusinessRuleException("A data de início deve ser anterior ou igual à data de fim.");
        }
        if (ChronoUnit.DAYS.between(inicio, fim) > MAX_DIAS_PERIODO) {
            throw new BusinessRuleException("O período consultado não pode ultrapassar " + MAX_DIAS_PERIODO + " dias.");
        }
    }

    private Aula buscarAula(Long id) {
        return aulaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Aula não encontrada com ID: " + id));
    }

    private Contrato buscarContrato(Long id) {
        return contratoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Contrato não encontrado com ID: " + id));
    }

    private LocalDate proximoDiaDaSemana(LocalDate dataInicio, DayOfWeek diaSemana) {
        LocalDate data = dataInicio;
        while (data.getDayOfWeek() != diaSemana) {
            data = data.plusDays(1);
        }
        return data;
    }

    private DayOfWeek mapDiaSemana(String diaSemana) {
        return switch (diaSemana.trim().toLowerCase()) {
            case "segunda-feira", "segunda" -> DayOfWeek.MONDAY;
            case "terça-feira", "terca-feira", "terça", "terca" -> DayOfWeek.TUESDAY;
            case "quarta-feira", "quarta" -> DayOfWeek.WEDNESDAY;
            case "quinta-feira", "quinta" -> DayOfWeek.THURSDAY;
            case "sexta-feira", "sexta" -> DayOfWeek.FRIDAY;
            case "sábado", "sabado" -> DayOfWeek.SATURDAY;
            case "domingo" -> DayOfWeek.SUNDAY;
            default -> throw new BusinessRuleException("Dia da semana inválido: " + diaSemana);
        };
    }
}
