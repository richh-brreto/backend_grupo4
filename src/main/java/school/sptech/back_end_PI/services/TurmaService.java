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

        turma.setNome(request.getNome());
        turma.setNivel(request.getNivel());
        turma.setLimiteAlunos(request.getLimiteAlunos());
        turma.setTipo(request.getTipo());

        // Atualiza os horários
        List<Horario> novosHorarios = horarioRepository.findAllById(request.getHorariosIds());
        if (novosHorarios.size() != request.getHorariosIds().size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Um ou mais IDs de horários são inválidos.");
        }
        turma.setHorarios(novosHorarios);

        return turmaRepository.save(turma);
    }

    // 2. DELETAR TURMA
    @Transactional
    public void deletar(Long id) {
        if (!turmaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada.");
        }
        turmaRepository.deleteById(id);
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

}