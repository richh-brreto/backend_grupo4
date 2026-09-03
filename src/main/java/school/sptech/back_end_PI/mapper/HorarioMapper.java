package school.sptech.back_end_PI.mapper;

import school.sptech.back_end_PI.dto.horario.HorarioResponse;
import school.sptech.back_end_PI.entity.Horario;

import java.util.ArrayList;
import java.util.List;

public class HorarioMapper {

    public static HorarioResponse toResponse(Horario horario) {
        if (horario == null) return null;

        return new HorarioResponse(
                horario.getId(),
                horario.getDiaSemana(),
                horario.getHoraInicio(),
                horario.getHoraFim()
        );
    }

    public static List<HorarioResponse> toResponseList(List<Horario> horarios) {
        if (horarios == null) return new ArrayList<>();
        return horarios.stream()
                .map(HorarioMapper::toResponse)
                .toList();
    }
}
