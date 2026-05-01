package school.sptech.back_end_PI.services;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import school.sptech.back_end_PI.Exception.ConflictException;
import school.sptech.back_end_PI.Exception.EntityNotFound;
import school.sptech.back_end_PI.entity.Aluno;
import school.sptech.back_end_PI.repository.AlunoRepository;

@Service
public class AlunoService {
    private final AlunoRepository repository;

    public AlunoService(AlunoRepository repository) {
        this.repository = repository;
    }

    public List<Aluno> getAll() {
        return repository.findAll();
    }

    public Aluno getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFound("Aluno não encontrado"));
    }

    @Transactional
    public Aluno create(Aluno aluno) {
        if (repository.existsAlunoByEmail(aluno.getEmail())) {
            throw new ConflictException("Email já cadastrado");
        }
        return repository.save(aluno);
    }

    @Transactional
    public Aluno update(Long id, Aluno alunoDetails) {
        Aluno aluno = repository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Aluno não encontrado"));

        if (!aluno.getEmail().equals(alunoDetails.getEmail()) &&
                repository.existsAlunoByEmail(alunoDetails.getEmail())) {
            throw new ConflictException("Email já cadastrado");
        }

        aluno.setNome(alunoDetails.getNome());
        aluno.setEmail(alunoDetails.getEmail());
        aluno.setTelefone(alunoDetails.getTelefone());
        aluno.setNivel(alunoDetails.getNivel());

        return repository.save(aluno);
    }

    @Transactional
    public void delete(Long id) {
        Aluno aluno = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado"));

        repository.delete(aluno);
    }
}