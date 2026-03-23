package school.sptech.back_end_PI.services;

import org.springframework.stereotype.Service;
import school.sptech.back_end_PI.Exception.ConflictException;
import school.sptech.back_end_PI.entity.Aluno;
import school.sptech.back_end_PI.repository.AlunoRepository;

@Service
public class AlunoService {
    private final AlunoRepository repository;

    public AlunoService(AlunoRepository repository) {
        this.repository = repository;
    }

    public Aluno create(Aluno aluno) {
        if (repository.existsAlunoByEmail(aluno.getEmail())) {
            throw new ConflictException("Email já cadastrado");
        }
        return repository.save(aluno);
    }
}
