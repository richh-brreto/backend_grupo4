package school.sptech.back_end_PI.services;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import school.sptech.back_end_PI.Exception.ConflictException;
import school.sptech.back_end_PI.Exception.EntityNotFound;
import school.sptech.back_end_PI.dto.AlunoRequest;
import school.sptech.back_end_PI.dto.HorarioAlunoProfessorRequest;
import school.sptech.back_end_PI.entity.Aluno;
import school.sptech.back_end_PI.entity.Horario;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.mapper.AlunoMapper;
import school.sptech.back_end_PI.repository.AlunoRepository;
import school.sptech.back_end_PI.repository.HorarioRepository;
import school.sptech.back_end_PI.repository.ProfessorRepository;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;
    private final HorarioRepository horarioRepository;

    public AlunoService(AlunoRepository alunoRepository, ProfessorRepository professorRepository, HorarioRepository horarioRepository) {
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
        this.horarioRepository = horarioRepository;
    }

    public List<Aluno> getAll() {
        return alunoRepository.findAll();
    }

    public Aluno getById(Long id) {
        return alunoRepository.findById(id).orElseThrow(() -> new EntityNotFound("Aluno não encontrado"));
    }


    public Aluno create(AlunoRequest aluno) {

        if (alunoRepository.existsAlunoByEmail(aluno.getEmail())) {
            throw new ConflictException("Email já cadastrado");
        }

        List<Horario> horarios = horarioRepository.findAllById(aluno.getHorariosIds());

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
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado"));

        alunoRepository.delete(aluno);
    }
}