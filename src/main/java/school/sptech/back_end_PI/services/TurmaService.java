package school.sptech.back_end_PI.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import school.sptech.back_end_PI.entity.Horario;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.exception.BusinessRuleException;
import school.sptech.back_end_PI.dto.turma.TurmaRequest;
import school.sptech.back_end_PI.entity.Aluno;
import school.sptech.back_end_PI.entity.Turma;
import school.sptech.back_end_PI.mapper.TurmaMapper;
import school.sptech.back_end_PI.repository.AlunoRepository;
import school.sptech.back_end_PI.repository.HorarioRepository;
import school.sptech.back_end_PI.repository.ProfessorRepository;
import school.sptech.back_end_PI.repository.TurmaRepository;

import java.util.List;

@Service
public class TurmaService {
    @Autowired
    private TurmaRepository turmaRepository;
    @Autowired
    private AlunoRepository alunoRepository;
    @Autowired
    private HorarioRepository horarioRepository;
    @Autowired
    private ProfessorRepository professorRepository;

    public Turma salvar(TurmaRequest novaTurma) {
        // 1. Validação de Duplicidade (Nome da Turma)
        if (turmaRepository.existsByNome(novaTurma.getNome())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Turma já cadastrada com este nome.");
        }

        // 2. VALIDAÇÃO DOS HORÁRIOS
        if (novaTurma.getHorariosIds() == null || novaTurma.getHorariosIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A turma precisa de pelo menos um horário informado.");
        }

        List<Horario> horarios = horarioRepository.findAllById(novaTurma.getHorariosIds());

        // Garante que TODOS os IDs enviados realmente existem no banco
        if (horarios.size() != novaTurma.getHorariosIds().size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Um ou mais IDs de horários informados são inválidos.");
        }

        // 3. Mapeamento e Persistência (Sem setar o Professor)
        Turma turmaSalvar = TurmaMapper.toEntity(novaTurma, horarios);

        // O professor fica como null explicitamente ou por omissão no banco
        turmaSalvar.setProfessor(null);

        return turmaRepository.save(turmaSalvar);
    }

    // 1. EDITAR TURMA (Apenas dados cadastrais e horários)
    @Transactional
    public Turma atualizar(Long id, TurmaRequest request) {
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada."));

        if (!turma.getNome().equals(request.getNome()) && turmaRepository.existsByNome(request.getNome())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe outra turma com este nome.");
        }

        // Se a turma já tiver um professor vinculado, mudar os horários da turma pode bagunçar a agenda dele.
        // O ideal é liberar os horários antigos dele antes de aplicar os novos.
        if (turma.getProfessor() != null && turma.getHorarios() != null) {
            alterarDisponibilidadeProfessor(turma.getProfessor().getId(), turma.getHorarios(), true);
        }

        turma.setNome(request.getNome());
        turma.setNivel(request.getNivel());
        turma.setLimiteAlunos(request.getLimiteAlunos());
        turma.setTipo(request.getTipo());

        // Atualiza os horários
        List<Horario> novosHorarios = horarioRepository.findAllById(request.getHorariosIds());
        if (novosHorarios.size() != request.getHorariosIds().size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Um ou mais IDs de horários são inválidos.");
        }

        // Se houver um professor vinculado, valida se ele está disponível para os NOVOS horários
        if (turma.getProfessor() != null) {
            List<Long> idsNovosHorarios = novosHorarios.stream().map(Horario::getId).toList();
            if (!turma.getProfessor().getHorarios().containsAll(novosHorarios)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O professor atual não possui esses novos horários na grade dele.");
            }
            if (professorRepository.contarHorariosIndisponiveis(turma.getProfessor().getId(), idsNovosHorarios) > 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O professor atual já tem um ou mais destes novos horários ocupados.");
            }
            // Bloqueia os novos horários na agenda do professor
            alterarDisponibilidadeProfessor(turma.getProfessor().getId(), novosHorarios, false);
        }

        turma.setHorarios(novosHorarios);
        return turmaRepository.save(turma);
    }

    // 2. DELETAR TURMA
    @Transactional
    public void deletar(Long id) {
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada."));

        // Se tiver professor, libera os horários dele de volta antes de apagar a turma
        if (turma.getProfessor() != null && turma.getHorarios() != null) {
            alterarDisponibilidadeProfessor(turma.getProfessor().getId(), turma.getHorarios(), true);
        }

        turmaRepository.delete(turma);
    }

    // 3. ADICIONAR PROFESSOR À TURMA
    @Transactional
    public Turma adicionarProfessor(Long turmaId, Long professorId) {
        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada."));

        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor não encontrado."));

        if (professor.getAtivo() != null && !professor.getAtivo()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível vincular um professor inativo.");
        }

        // Se a turma já tinha um professor diferente, precisamos liberar a agenda do professor antigo primeiro
        if (turma.getProfessor() != null) {
            if (turma.getProfessor().getId().equals(professorId)) {
                return turma; // Já é o mesmo professor, não faz nada
            }
            alterarDisponibilidadeProfessor(turma.getProfessor().getId(), turma.getHorarios(), true);
        }

        // VALIDAÇÃO DE CONFLITO: Verifica se o novo professor pode assumir estes horários
        if (turma.getHorarios() != null && !turma.getHorarios().isEmpty()) {
            // A: Verifica se os horários existem no escopo base cadastrado do professor
            if (!professor.getHorarios().containsAll(turma.getHorarios())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O professor não possui um ou mais horários desta turma cadastrados em sua grade básica.");
            }

            // B: Verifica se algum deles está indisponível (is_disponivel = false)
            List<Long> idsHorariosTurma = turma.getHorarios().stream().map(Horario::getId).toList();
            if (professorRepository.contarHorariosIndisponiveis(professorId, idsHorariosTurma) > 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O professor selecionado já possui um ou mais horários desta turma ocupados em outra atividade.");
            }

            // CONDUZIR O BLOQUEIO: Altera 'is_disponivel' para FALSE na agenda do professor
            alterarDisponibilidadeProfessor(professorId, turma.getHorarios(), false);
        }

        turma.setProfessor(professor);
        return turmaRepository.save(turma);
    }

    // 4. REMOVER PROFESSOR DA TURMA
    @Transactional
    public Turma removerProfessor(Long turmaId) {
        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada."));

        if (turma.getProfessor() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esta turma já não possui nenhum professor vinculado.");
        }

        // LIBERAÇÃO: Altera 'is_disponivel' para TRUE na agenda do professor que está saindo
        if (turma.getHorarios() != null && !turma.getHorarios().isEmpty()) {
            alterarDisponibilidadeProfessor(turma.getProfessor().getId(), turma.getHorarios(), true);
        }

        turma.setProfessor(null); // Deixa a turma "órfã" novamente
        return turmaRepository.save(turma);
    }

    public List<Turma> listarTodas() {
        return turmaRepository.findAll();
    }

    public Turma buscarPorId(Long id) {
        return turmaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada com o ID: " + id));
    }

    // ============================================================================
    // MÉTODO AUXILIAR DE CONTROLE DE AGENDA DO PROFESSOR
    // ============================================================================
    private void alterarDisponibilidadeProfessor(Long professorId, List<Horario> horarios, boolean status) {
        if (horarios != null && !horarios.isEmpty()) {
            List<Long> ids = horarios.stream().map(Horario::getId).toList();
            professorRepository.atualizarDisponibilidadeHorarios(professorId, ids, status);
        }
    }
}