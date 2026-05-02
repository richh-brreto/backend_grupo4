package school.sptech.back_end_PI.mapper;

import school.sptech.back_end_PI.dto.CreateProfessorRequest;
import school.sptech.back_end_PI.dto.TipoProfessorResponse;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.entity.TipoProfessor;
import school.sptech.back_end_PI.dto.ProfessorResponse;

import java.util.List;


public class ProfessorMapper {

    public static Professor toEntity(CreateProfessorRequest dto, TipoProfessor tipo) {
        if (dto == null) return null;

        Professor professor = new Professor();

        professor.setNome(dto.getNome());
        // professor.setProfessor_id(null); // ID é gerado pelo banco
        professor.setEmail(dto.getEmail());
        professor.setTelefone(dto.getTelefone());
        professor.setSenha(dto.getSenha());

        // Associa a entidade de tipo completa que veio do banco
        professor.setTipo(tipo);

        return professor;
    }

    public static ProfessorResponse toResponse(Professor professor) {
        if (professor == null) return null;

        ProfessorResponse dto = new ProfessorResponse();
        dto.setId(professor.getId());
        dto.setNome(professor.getNome());
        dto.setEmail(professor.getEmail());
        dto.setTelefone(professor.getTelefone());

        // Mapeamento hierárquico:
        if (professor.getTipo() != null) {
            dto.setTipo(TipoProfessorMapper.toResponse(professor.getTipo()));
        }

        return dto;
    }

    public static List<ProfessorResponse> toResponseList(List<Professor> professores) {
        return professores.stream()
                .map(ProfessorMapper::toResponse)
                .toList();
    }
}