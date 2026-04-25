package school.sptech.back_end_PI.mapper;

import school.sptech.back_end_PI.dto.CreateProfessorRequest;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.entity.TipoProfessor;

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

    // Você também pode criar um método para transformar a Entity de volta em DTO de resposta
}