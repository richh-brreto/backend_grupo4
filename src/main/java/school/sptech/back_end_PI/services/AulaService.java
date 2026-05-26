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
import java.util.ArrayList;
import java.util.List;

@Service
public class AulaService {

    @Autowired
    private AulaRepository aulaRepository;

    @Autowired
    private LogAulaRepository logAulaRepository;

    @Autowired
    private ContratoRepository contratoRepository;

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

        if (request.getNovaHoraInicio().isAfter(request.getNovaHoraFim())) {
            throw new BusinessRuleException("Hora de início não pode ser após a hora de fim");
        }

        String descricao = "Aula remarcada de " + aula.getData() + " " + aula.getHoraInicio()
                + " para " + request.getNovaData() + " " + request.getNovaHoraInicio();
        if (request.getMotivo() != null && !request.getMotivo().isBlank()) {
            descricao += ". Motivo: " + request.getMotivo();
        }

        aula.setData(request.getNovaData());
        aula.setHoraInicio(request.getNovaHoraInicio());
        aula.setHoraFim(request.getNovaHoraFim());
        aula.setStatus(StatusAula.REMARCADA);

        Aula salva = aulaRepository.save(aula);
        registrarLog(salva, "REMARCADA", descricao);

        return AulaMapper.toResponse(salva);
    }

    @Transactional
    public AulaResponse cancelarAula(Long id, CancelarAulaRequest request) {
        Aula aula = buscarAula(id);

        if (aula.getStatus() == StatusAula.CANCELADA) {
            throw new BusinessRuleException("A aula já está cancelada");
        }

        aula.setStatus(StatusAula.CANCELADA);

        Aula salva = aulaRepository.save(aula);

        String motivo = (request != null && request.getMotivo() != null && !request.getMotivo().isBlank())
                ? request.getMotivo() : "não informado";
        registrarLog(salva, "CANCELADA", "Aula cancelada. Motivo: " + motivo);

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

        aula.setPresenca(request.getPresenca());

        Aula salva = aulaRepository.save(aula);
        String presencaStr = Boolean.TRUE.equals(request.getPresenca()) ? "Presente" : "Ausente";
        registrarLog(salva, "PRESENCA_REGISTRADA", "Presença registrada: " + presencaStr);

        return AulaMapper.toResponse(salva);
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
