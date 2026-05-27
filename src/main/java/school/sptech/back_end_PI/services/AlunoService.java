package school.sptech.back_end_PI.services;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;
import school.sptech.back_end_PI.entity.Contrato;
import school.sptech.back_end_PI.exception.ConflictException;
import school.sptech.back_end_PI.exception.EntityNotFound;
import school.sptech.back_end_PI.dto.aluno.AlunoRequest;
import school.sptech.back_end_PI.entity.Aluno;
import school.sptech.back_end_PI.entity.Horario;
import school.sptech.back_end_PI.mapper.AlunoMapper;
import school.sptech.back_end_PI.repository.*;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;
    private final HorarioRepository horarioRepository;
    private final TurmaRepository turmaRepository;
    private final ContratoRepository contratoRepository;

    public AlunoService(AlunoRepository alunoRepository, ProfessorRepository professorRepository, HorarioRepository horarioRepository, TurmaRepository turmaRepository, ContratoRepository contratoRepository) {
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
        this.horarioRepository = horarioRepository;
        this.turmaRepository = turmaRepository;
        this.contratoRepository = contratoRepository;
    }

    public List<Aluno> getAll() {
        return alunoRepository.findAll();
    }

    public Aluno getById(Long id) {
        return alunoRepository.findById(id).orElseThrow(() -> new EntityNotFound("Aluno não encontrado"));
    }

    public List<Aluno> getByTurmaId(Long id) {
        if (!turmaRepository.existsById(id)) {
            throw new EntityNotFound("Não foi possível encontrar a turma de ID " + id);
        }

        // Traz os contratos daquela turma e filtra os alunos ativos
        return contratoRepository.findByTurmaId(id).stream()
                .map(Contrato::getAluno)
                .filter(aluno -> aluno != null && aluno.getAtivo())
                .toList();
    }

    public Aluno create(AlunoRequest aluno) {

        if (alunoRepository.existsAlunoByEmail(aluno.getEmail())) {
            throw new ConflictException("Email já cadastrado");
        }

        List<Horario> horarios = horarioRepository.findAllById(aluno.getHorariosIds());

        if (horarios.isEmpty()) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Horários não informados ou inválidos");
        }

        Aluno alunoCriado = AlunoMapper.toEntity(aluno, horarios);

        return alunoRepository.save(alunoCriado);
    }


    public Aluno update(Long id, AlunoRequest alunoDetails) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Aluno não encontrado"));

        if (!aluno.getEmail().equals(alunoDetails.getEmail()) &&
                alunoRepository.existsAlunoByEmail(alunoDetails.getEmail())) {
            throw new ConflictException("Email já cadastrado");
        }

        aluno.setNome(alunoDetails.getNome());
        aluno.setEmail(alunoDetails.getEmail());
        aluno.setTelefone(alunoDetails.getTelefone());
        aluno.setNivel(alunoDetails.getNivel());

        if (alunoDetails.getHorariosIds() != null && !alunoDetails.getHorariosIds().isEmpty()) {
            List<Horario> novosHorarios = horarioRepository.findAllById(alunoDetails.getHorariosIds());
            aluno.setHorarios(novosHorarios);
        }

        return alunoRepository.save(aluno);
    }


    @Transactional
    public void delete(Long id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Aluno não encontrado"));

        // 1. Limpa os horários (disponibilidade_aluno)
        aluno.getHorarios().clear();

        // 2. NOVO: Deleta os contratos atrelados a este aluno para liberar a FK
        // Certifique-se de ter o contratoRepository injetado no AlunoService
        List<Contrato> contratosDoAluno = contratoRepository.findByAlunoId(id); // se tiver esse método
        contratoRepository.deleteAll(contratosDoAluno);

        alunoRepository.saveAndFlush(aluno);

        // 3. Agora o soft delete roda sem travas do banco
        alunoRepository.delete(aluno);
    }

    @Transactional
    public Aluno reativar(Long id) {
        // 1. Busca o aluno ignorando o filtro global para verificar se ele realmente existe
        Aluno aluno = alunoRepository.buscarPorIdIgnorandoFiltro(id)
                .orElseThrow(() -> new EntityNotFound("Aluno não encontrado com o ID: " + id));

        if (aluno.getAtivo()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este aluno já está ativo.");
        }

        // 2. Executa a query de atualização direta (Retorna a quantidade de linhas afetadas)
        alunoRepository.reativarPorId(id);

        // 3. Atualiza o objeto na memória apenas para o JSON do Mapper não ir desatualizado
        aluno.setAtivo(true);
        return aluno;
    }
}