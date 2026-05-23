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

    // FLUXO DE GRUPO
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
        contrato.setProfessor(null); // No grupo, o professor vem da turma dinamicamente
        if (turma.getHorarios() != null) {
            contrato.setHorarios(new ArrayList<>(turma.getHorarios()));
        }

        contratoRepository.save(contrato);
        System.out.println("Chegou até o fim do service!");
        return ContratoMapper.toResponse(contrato);
    }

    // FLUXO INDIVIDUAL
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
        contrato.setTurma(null); // Individual não tem turma fixa
        contrato.setHorarios(horarios); // Salva na tabela auxiliar ids_horario_contrato

        contratoRepository.save(contrato);
        return ContratoMapper.toResponse(contrato);
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
        // 1. Verifica limite de alunos baseado nos contratos ativos daquela turma
        Long totalAlunosMatriculados = contratoRepository.countByTurmaAndDataFimGreaterThanEqual(turma, request.getDataInicio());
        if (totalAlunosMatriculados >= turma.getLimiteAlunos()) {
            throw new BusinessRuleException("A turma " + turma.getNome() + " já atingiu o limite de alunos");
        }

        // 2. Evita duplicidade: Verifica se o aluno já tem um contrato ativo nessa mesma turma e período
        boolean contratoExistente = contratoRepository.existsByAlunoAndTurmaAndDataInicioAndDataFim(
                aluno, turma, request.getDataInicio(), request.getDataFim());
        if (contratoExistente) {
            throw new BusinessRuleException("Este aluno já possui um contrato ativo para esta turma neste período");
        }

        // 3. NOVA VALIDAÇÃO: Verifica se o aluno tem disponibilidade para TODOS os horários que essa turma exige
        // Nota: Assumindo que sua Entidade Turma tem 'getHorarios()' e Aluno tem 'getHorarios()' mapeados do banco
        if (turma.getHorarios() != null && !aluno.getHorarios().containsAll(turma.getHorarios())) {
            throw new BusinessRuleException("O aluno não possui disponibilidade compatível com todos os horários desta turma");
        }
    }

    private void validarRegrasIndividual(Aluno aluno, Professor professor, List<Horario> horariosSolicitados, ContratoRequest request) {
        // 1. Evita duplicidade: Verifica se o aluno já tem uma aula particular agendada com o mesmo professor no mesmo período
        boolean contratoExistente = contratoRepository.existsByAlunoAndProfessorAndDataInicioAndDataFim(
                aluno, professor, request.getDataInicio(), request.getDataFim());
        if (contratoExistente) {
            throw new BusinessRuleException("Já existe um contrato individual entre este aluno e professor para este período");
        }

        // 2. NOVA VALIDAÇÃO: Verifica se o Aluno possui todos os horários solicitados em sua agenda de disponibilidade
        if (!aluno.getHorarios().containsAll(horariosSolicitados)) {
            throw new BusinessRuleException("O aluno não possui disponibilidade para um ou mais horários selecionados");
        }

        // 3. NOVA VALIDAÇÃO: Verifica se o Professor possui todos os horários solicitados em sua agenda de disponibilidade
        if (!professor.getHorarios().containsAll(horariosSolicitados)) {
            throw new BusinessRuleException("O professor não possui disponibilidade para um ou mais horários selecionados");
        }
    }


    @Transactional
    public ContratoResponse atualizarContrato(Long id, ContratoRequest request) {
        Contrato contratoExistente = contratoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Contrato não encontrado com o ID: " + id));

        validarDatas(request);

        if ("Grupo".equalsIgnoreCase(request.getTipo())) {
            atualizarContratoGrupo(contratoExistente, request);
        } else if ("Individual".equalsIgnoreCase(request.getTipo())) {
            atualizarContratoIndividual(contratoExistente, request);
        } else {
            throw new BusinessRuleException("Tipo de contrato inválido para atualização. Use 'Grupo' ou 'Individual'.");
        }

        Contrato contratoAtualizado = contratoRepository.save(contratoExistente);
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

        if (turma.getHorarios() != null && !aluno.getHorarios().containsAll(turma.getHorarios())) {
            throw new BusinessRuleException("O aluno não possui disponibilidade compatível com todos os horários desta turma");
        }
    }

    private void validarRegrasIndividualParaAtualizacao(Long contratoId, Aluno aluno, Professor professor, List<Horario> horariosSolicitados, ContratoRequest request) {
        if (!aluno.getHorarios().containsAll(horariosSolicitados)) {
            throw new BusinessRuleException("O aluno não possui disponibilidade para um ou mais horários selecionados");
        }

        if (!professor.getHorarios().containsAll(horariosSolicitados)) {
            throw new BusinessRuleException("O professor não possui disponibilidade para um ou mais horários selecionados");
        }
    }

    @Transactional
    public void deletarContrato(Long id) {
        Contrato contrato = contratoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Contrato não encontrado com o ID: " + id));

        if (contrato.getHorarios() != null) {
            contrato.getHorarios().clear();
        }

        contratoRepository.delete(contrato);

        System.out.println("Contrato deletado com sucesso!");
    }

    @Transactional(readOnly = true)
    public List<ContratoResponse> listarTodosContratos() {
        List<Contrato> contratos = contratoRepository.findAll();

        return contratos.stream()
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
        return alunoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Aluno não encontrado"));
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