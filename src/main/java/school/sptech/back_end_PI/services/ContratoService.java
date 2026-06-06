package school.sptech.back_end_PI.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sptech.back_end_PI.dto.contrato.ContratoRequest;
import school.sptech.back_end_PI.dto.contrato.ContratoResponse;
import school.sptech.back_end_PI.entity.*;
import school.sptech.back_end_PI.exception.BusinessRuleException;
import school.sptech.back_end_PI.exception.EntityNotFound;
import school.sptech.back_end_PI.mapper.ContratoMapper;
import school.sptech.back_end_PI.repository.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class ContratoService {

    @Autowired
    private ContratoRepository contratoRepository;
    @Autowired
    private TurmaRepository turmaRepository;
    @Autowired
    private ProfessorRepository professorRepository;
    @Autowired
    private AlunoRepository alunoRepository;
    @Autowired
    private HorarioRepository horarioRepository;
    @Autowired
    private AulaService aulaService;
    @Autowired
    private AulaRepository aulaRepository;

    @Transactional
    public ContratoResponse criarContrato(ContratoRequest request) {
        validarDatas(request);

        if ("Grupo".equalsIgnoreCase(request.getTipo())) {
            return criarContratoGrupo(request);
        } else if ("Individual".equalsIgnoreCase(request.getTipo())) {
            return criarContratoIndividual(request);
        }

        throw new BusinessRuleException("Tipo de contrato inválido. Use 'Grupo' ou 'Individual'.");
    }

    private ContratoResponse criarContratoGrupo(ContratoRequest request) {
        Aluno aluno = buscarAluno(request.getAlunoId());
        Turma turma = buscarTurma(request.getTurmaId());

        validarRegrasGrupo(turma, aluno, request);

        Contrato contrato = new Contrato();
        contrato.setTipo("Grupo");
        contrato.setDataInicio(request.getDataInicio());
        contrato.setDataFim(request.getDataFim());
        contrato.setAluno(aluno);
        contrato.setTurma(turma);
        contrato.setProfessor(null);
        if (turma.getHorarios() != null) {
            contrato.setHorarios(new ArrayList<>(turma.getHorarios()));
        }

        contratoRepository.save(contrato);

        // 🔒 Consome os horários da TURMA na agenda do ALUNO
        bloquearHorariosDoAlunoPorTurma(aluno.getId(), turma);

        aulaService.gerarAulasParaContrato(contrato);
        return ContratoMapper.toResponse(contrato);
    }

    private ContratoResponse criarContratoIndividual(ContratoRequest request) {
        Aluno aluno = buscarAluno(request.getAlunoId());
        Professor professor = buscarProfessor(request.getProfessorId());
        List<Horario> horarios = buscarHorarios(request.getHorariosIds());

        validarRegrasIndividual(aluno, professor, horarios, request);

        Contrato contrato = new Contrato();
        contrato.setTipo("Individual");
        contrato.setDataInicio(request.getDataInicio());
        contrato.setDataFim(request.getDataFim());
        contrato.setAluno(aluno);
        contrato.setProfessor(professor);
        contrato.setTurma(null);
        contrato.setHorarios(horarios);

        contratoRepository.save(contrato);

        // 🔒 Consome os horários na agenda do PROFESSOR e do ALUNO
        alterarDisponibilidadeHorarios(request.getAlunoId(), request.getProfessorId(), request.getHorariosIds(), false);

        aulaService.gerarAulasParaContrato(contrato);
        return ContratoMapper.toResponse(contrato);
    }

    @Transactional
    public ContratoResponse atualizarContrato(Long id, ContratoRequest request) {
        Contrato contratoExistente = contratoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Contrato não encontrado com o ID: " + id));

        validarDatas(request);

        // 🔓 Devolve os horários antigos dependendo do tipo antes de aplicar as novas mudanças
        if ("Individual".equalsIgnoreCase(contratoExistente.getTipo())) {
            liberarHorariosDeContratoIndividual(contratoExistente);
        } else if ("Grupo".equalsIgnoreCase(contratoExistente.getTipo())) {
            liberarHorariosDeContratoGrupo(contratoExistente);
        }

        if ("Grupo".equalsIgnoreCase(request.getTipo())) {
            atualizarContratoGrupo(contratoExistente, request);
        } else if ("Individual".equalsIgnoreCase(request.getTipo())) {
            atualizarContratoIndividual(contratoExistente, request);
        } else {
            throw new BusinessRuleException("Tipo de contrato inválido para atualização. Use 'Grupo' ou 'Individual'.");
        }

        Contrato contratoAtualizado = contratoRepository.save(contratoExistente);

        // 🔒 Aplica os novos bloqueios com base no novo estado do contrato
        if ("Individual".equalsIgnoreCase(contratoAtualizado.getTipo())) {
            alterarDisponibilidadeHorarios(
                    contratoAtualizado.getAluno().getId(),
                    contratoAtualizado.getProfessor().getId(),
                    request.getHorariosIds(),
                    false
            );
        } else if ("Grupo".equalsIgnoreCase(contratoAtualizado.getTipo())) {
            bloquearHorariosDoAlunoPorTurma(contratoAtualizado.getAluno().getId(), contratoAtualizado.getTurma());
        }

        return ContratoMapper.toResponse(contratoAtualizado);
    }

    private void atualizarContratoGrupo(Contrato contrato, ContratoRequest request) {
        Aluno aluno = buscarAluno(request.getAlunoId());
        Turma turma = buscarTurma(request.getTurmaId());

        validarRegrasGrupoParaAtualizacao(contrato.getId(), turma, aluno, request);

        contrato.setTipo("Grupo");
        contrato.setDataInicio(request.getDataInicio());
        contrato.setDataFim(request.getDataFim());
        contrato.setAluno(aluno);
        contrato.setTurma(turma);
        contrato.setProfessor(null);

        if (turma.getHorarios() != null) {
            contrato.setHorarios(new ArrayList<>(turma.getHorarios()));
        } else {
            contrato.setHorarios(new ArrayList<>());
        }
    }

    private void atualizarContratoIndividual(Contrato contrato, ContratoRequest request) {
        Aluno aluno = buscarAluno(request.getAlunoId());
        Professor professor = buscarProfessor(request.getProfessorId());
        List<Horario> horariosSolicitados = buscarHorarios(request.getHorariosIds());

        validarRegrasIndividualParaAtualizacao(contrato.getId(), aluno, professor, horariosSolicitados, request);

        contrato.setTipo("Individual");
        contrato.setDataInicio(request.getDataInicio());
        contrato.setDataFim(request.getDataFim());
        contrato.setAluno(aluno);
        contrato.setProfessor(professor);
        contrato.setTurma(null);
        contrato.setHorarios(horariosSolicitados);
    }

    @Transactional
    public void deletarContrato(Long id) {
        Contrato contrato = contratoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Contrato não encontrado com o ID: " + id));

        // 🔓 Libera as horas dependendo da modalidade que está sendo destruída
        if ("Individual".equalsIgnoreCase(contrato.getTipo())) {
            liberarHorariosDeContratoIndividual(contrato);
        } else if ("Grupo".equalsIgnoreCase(contrato.getTipo())) {
            liberarHorariosDeContratoGrupo(contrato);
        }

        if (contrato.getHorarios() != null) {
            contrato.getHorarios().clear();
        }

        contratoRepository.delete(contrato);
    }

    // ============================================================================
    // FUNÇÕES AUXILIARES DE CONTROLE DE DISPONIBILIDADE (INDIVIDUAL E GRUPO)
    // ============================================================================

    private void alterarDisponibilidadeHorarios(Long alunoId, Long professorId, List<Long> horariosIds, boolean status) {
        if (horariosIds != null && !horariosIds.isEmpty()) {
            if (professorId != null) {
                professorRepository.atualizarDisponibilidadeHorarios(professorId, horariosIds, status);
            }
            alunoRepository.atualizarDisponibilidadeHorarios(alunoId, horariosIds, status);
        }
    }

    private void bloquearHorariosDoAlunoPorTurma(Long alunoId, Turma turma) {
        if (turma != null && turma.getHorarios() != null && !turma.getHorarios().isEmpty()) {
            List<Long> idsHorarios = turma.getHorarios().stream().map(Horario::getId).toList();
            alterarDisponibilidadeHorarios(alunoId, null, idsHorarios, false);
        }
    }

    private void liberarHorariosDeContratoIndividual(Contrato contrato) {
        if (contrato.getProfessor() != null && contrato.getAluno() != null && contrato.getHorarios() != null) {
            List<Long> idsHorarios = contrato.getHorarios().stream().map(Horario::getId).toList();
            alterarDisponibilidadeHorarios(contrato.getAluno().getId(), contrato.getProfessor().getId(), idsHorarios, true);
        }
    }

    private void liberarHorariosDeContratoGrupo(Contrato contrato) {
        if (contrato.getAluno() != null && contrato.getHorarios() != null && !contrato.getHorarios().isEmpty()) {
            List<Long> idsHorarios = contrato.getHorarios().stream().map(Horario::getId).toList();
            alterarDisponibilidadeHorarios(contrato.getAluno().getId(), null, idsHorarios, true);
        }
    }

    // ============================================================================
    // MÉTODOS AUXILIARES: VALIDAÇÕES DE REGRAS DE NEGÓCIO
    // ============================================================================

    private void validarDatas(ContratoRequest request) {
        if (request.getDataInicio().isAfter(request.getDataFim())) {
            throw new BusinessRuleException("Data de início não pode ser maior que a data de fim");
        }
    }

    private void validarRegrasGrupo(Turma turma, Aluno aluno, ContratoRequest request) {
        Long totalAlunosMatriculados = contratoRepository.countByTurmaAndDataFimGreaterThanEqual(turma, request.getDataInicio());
        if (totalAlunosMatriculados >= turma.getLimiteAlunos()) {
            throw new BusinessRuleException("A turma " + turma.getNome() + " já atingiu o limite de alunos");
        }

        boolean contratoExistente = contratoRepository.existsByAlunoAndTurmaAndDataInicioAndDataFim(
                aluno, turma, request.getDataInicio(), request.getDataFim());
        if (contratoExistente) {
            throw new BusinessRuleException("Este aluno já possui um contrato ativo para esta turma neste período");
        }

        if (turma.getHorarios() != null) {
            if (!aluno.getHorarios().containsAll(turma.getHorarios())) {
                throw new BusinessRuleException("O aluno não possui esse horário cadastrado em sua grade de disponibilidade");
            }

            List<Long> idsHorariosTurma = turma.getHorarios().stream().map(Horario::getId).toList();
            // 🔄 Correção aqui: se a contagem for maior que 0, lança o erro
            if (alunoRepository.contarHorariosIndisponiveis(aluno.getId(), idsHorariosTurma) > 0) {
                throw new BusinessRuleException("O aluno já possui um ou mais horários desta turma ocupados por outro contrato");
            }
        }
    }

    private void validarRegrasIndividual(Aluno aluno, Professor professor, List<Horario> horariosSolicitados, ContratoRequest request) {
        boolean contratoExistente = contratoRepository.existsByAlunoAndProfessorAndDataInicioAndDataFim(
                aluno, professor, request.getDataInicio(), request.getDataFim());
        if (contratoExistente) {
            throw new BusinessRuleException("Já existe um contrato individual entre este aluno e professor para este período");
        }

        if (!aluno.getHorarios().containsAll(horariosSolicitados)) {
            throw new BusinessRuleException("O aluno não possui esse horário cadastrado em sua grade de disponibilidade");
        }

        if (!professor.getHorarios().containsAll(horariosSolicitados)) {
            throw new BusinessRuleException("O professor não possui esse horário cadastrado em sua grade de disponibilidade");
        }

        List<Long> idsHorarios = horariosSolicitados.stream().map(Horario::getId).toList();

        // 🔄 Correção aqui: usando a contagem numérica inteira para evitar erros de cast do driver MySQL
        if (alunoRepository.contarHorariosIndisponiveis(aluno.getId(), idsHorarios) > 0) {
            throw new BusinessRuleException("O aluno já possui um ou mais horários selecionados ocupados por outro contrato");
        }

        if (professorRepository.contarHorariosIndisponiveis(professor.getId(), idsHorarios) > 0) {
            throw new BusinessRuleException("O professor já possui um ou mais horários selecionados ocupados por outro contrato");
        }
    }

    private void validarRegrasGrupoParaAtualizacao(Long contratoId, Turma turma, Aluno aluno, ContratoRequest request) {
        Long totalAlunosMatriculados = contratoRepository.countByTurmaAndDataFimGreaterThanEqual(turma, request.getDataInicio());

        Contrato original = contratoRepository.findById(contratoId).orElse(null);
        boolean jaEstavaNaTurma = original != null && original.getTurma() != null && original.getTurma().getId().equals(turma.getId());

        if (!jaEstavaNaTurma && totalAlunosMatriculados >= turma.getLimiteAlunos()) {
            throw new BusinessRuleException("A turma " + turma.getNome() + " já atingiu o limite de alunos");
        }

        boolean contratoExistente = contratoRepository.existsByAlunoAndTurmaAndDataInicioAndDataFimAndIdNot(
                aluno, turma, request.getDataInicio(), request.getDataFim(), contratoId);

        if (contratoExistente) {
            throw new BusinessRuleException("Este aluno já possui outro contrato ativo para esta turma neste período");
        }

        if (turma.getHorarios() != null) {
            if (!aluno.getHorarios().containsAll(turma.getHorarios())) {
                throw new BusinessRuleException("O aluno não possui esse horário cadastrado em sua grade de disponibilidade");
            }

            List<Long> idsHorariosTurma = turma.getHorarios().stream().map(Horario::getId).toList();
            // 🔄 Correção aqui
            if (alunoRepository.contarHorariosIndisponiveis(aluno.getId(), idsHorariosTurma) > 0) {
                throw new BusinessRuleException("O aluno já possui um ou mais horários desta turma ocupados por outro contrato");
            }
        }
    }

    private void validarRegrasIndividualParaAtualizacao(Long contratoId, Aluno aluno, Professor professor, List<Horario> horariosSolicitados, ContratoRequest request) {
        if (!aluno.getHorarios().containsAll(horariosSolicitados)) {
            throw new BusinessRuleException("O aluno não possui esse horário cadastrado em sua grade de disponibilidade");
        }

        if (!professor.getHorarios().containsAll(horariosSolicitados)) {
            throw new BusinessRuleException("O professor não possui esse horário cadastrado em sua grade de disponibilidade");
        }

        List<Long> idsHorarios = horariosSolicitados.stream().map(Horario::getId).toList();

        // 🔄 Correção aqui
        if (alunoRepository.contarHorariosIndisponiveis(aluno.getId(), idsHorarios) > 0) {
            throw new BusinessRuleException("O aluno já possui um ou mais horários selecionados ocupados por outro contrato");
        }

        if (professorRepository.contarHorariosIndisponiveis(professor.getId(), idsHorarios) > 0) {
            throw new BusinessRuleException("O professor já possui um ou mais horários selecionados ocupados por outro contrato");
        }
    }

    @Transactional(readOnly = true)
    public List<ContratoResponse> listarTodosContratos() {
        return contratoRepository.findAll().stream()
                .map(ContratoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ContratoResponse buscarContratoPorId(Long id) {
        Contrato contrato = contratoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Contrato não encontrado com o ID: " + id));

        return ContratoMapper.toResponse(contrato);
    }

    private Aluno buscarAluno(Long id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Aluno não encontrado"));

        if (aluno.getAtivo() != null && !aluno.getAtivo()) {
            throw new BusinessRuleException("Não é possível criar ou atualizar contratos para um aluno inativo.");
        }
        return aluno;
    }

    private Turma buscarTurma(Long id) {
        return turmaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Turma não encontrada"));
    }

    private Professor buscarProfessor(Long id) {
        return professorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Professor não encontrado"));
    }

    private List<Horario> buscarHorarios(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessRuleException("É necessário informar pelo menos um horário para contratos individuais");
        }
        List<Horario> horarios = horarioRepository.findAllById(ids);
        if (horarios.size() != ids.size()) {
            throw new EntityNotFound("Um ou mais horários informados não foram encontrados");
        }
        return horarios;
    }
}