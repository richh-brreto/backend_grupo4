package school.sptech.back_end_PI.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import school.sptech.back_end_PI.Exception.ConflictException;
import school.sptech.back_end_PI.Exception.EntityNotFound;
import school.sptech.back_end_PI.dto.CreateProfessorRequest;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.entity.TipoProfessor;
import school.sptech.back_end_PI.mapper.ProfessorMapper;
import school.sptech.back_end_PI.repository.ProfessorRepository;
import school.sptech.back_end_PI.repository.TipoProfessorRepository;

@Service
public class ProfessorService {
    private final ProfessorRepository professorRepository;
    private final TipoProfessorRepository tipoProfessorRepository;

    public ProfessorService(ProfessorRepository professorRepository, TipoProfessorRepository tipoProfessorRepository) {
        this.professorRepository = professorRepository;
        this.tipoProfessorRepository = tipoProfessorRepository;
    }

//    public Professor create(Professor professor, Integer idTipoProfessor){
//        if(professorRepository.existsProfessorByEmail(professor.getEmail())){
//            throw new ConflictException("Email professor já cadastrado");
//        }
//
//        TipoProfessor tipoProfessor = tipoProfessorRepository.findById(idTipoProfessor).orElseThrow(
//                () -> new EntityNotFound("Tipo de professor nao encontrado")
//        );
//
//        professor.setTipo(tipoProfessor);
//
//
//        return professorRepository.save(professor);
//    }


    public Professor create(CreateProfessorRequest dto) {
        // 1. Validação de negócio
        if (professorRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email já cadastrado");
        }

        // 2. Busca a dependência (TipoProfessor)
        TipoProfessor tipo = tipoProfessorRepository.findById(dto.getIdTipoProfessor())
                .orElseThrow(() -> new EntityNotFoundException("Tipo não encontrado"));

        // 3. Usa o Mapper para converter DTO -> Entity
        Professor novoProfessor = ProfessorMapper.toEntity(dto, tipo);

        // 4. Salva no banco
        return professorRepository.save(novoProfessor);
    }

}
