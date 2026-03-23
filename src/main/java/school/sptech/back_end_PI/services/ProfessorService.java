package school.sptech.back_end_PI.services;

import org.springframework.stereotype.Service;
import school.sptech.back_end_PI.Exception.ConflictException;
import school.sptech.back_end_PI.dto.ResponsePessoa;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.repository.ProfessorRepository;

@Service
public class ProfessorService {
    private final ProfessorRepository repository;

    public ProfessorService(ProfessorRepository repository) {
        this.repository = repository;
    }

    public Professor create(Professor professor){
        if(repository.existsProfessorByEmail(professor.getEmail())){
            throw new ConflictException("Email professor já cadastrado");
        }

        return repository.save(professor);
    }

}
