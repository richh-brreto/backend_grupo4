package school.sptech.back_end_PI.mapper;
import school.sptech.back_end_PI.dto.TipoProfessorResponse;
import school.sptech.back_end_PI.entity.TipoProfessor;

public class TipoProfessorMapper {

    public static TipoProfessorResponse toResponse(TipoProfessor tipo) {
        if (tipo == null) return null;
        TipoProfessorResponse dto = new TipoProfessorResponse();
        dto.setId(tipo.getId());
        dto.setTipoProfessor(tipo.getNomeTipo());
        return dto;
    }


}