package school.sptech.back_end_PI.mapper;

import school.sptech.back_end_PI.dto.turma.TurmaRequest;
import school.sptech.back_end_PI.dto.turma.TurmaResponse;
import school.sptech.back_end_PI.entity.Horario;
import school.sptech.back_end_PI.entity.Turma;

import java.util.ArrayList;
import java.util.List;

public class TurmaMapper {

    public static Turma toEntity(TurmaRequest request, List<Horario> horarios) {
        if (request == null) return null;

        Turma turma = new Turma();
        turma.setNome(request.getNome());
        turma.setNivel(request.getNivel());
        turma.setLimiteAlunos(request.getLimiteAlunos());
        turma.setTipo(request.getTipo());
        turma.setHorarios(horarios); // Associa as entidades Horario trazidas pelo Repository

        return turma;
    }

    public static Turma toEntity(TurmaRequest request) {
        if (request == null) return null;

        Turma turma = new Turma();
        turma.setNome(request.getNome());
        turma.setNivel(request.getNivel());
        turma.setLimiteAlunos(request.getLimiteAlunos());
        turma.setTipo(request.getTipo());
        return turma;
    }

    public static TurmaResponse toResponse(Turma entity) {
        if (entity == null) return null;

        TurmaResponse turmaResponse = new TurmaResponse();
        turmaResponse.setId(entity.getId());
        turmaResponse.setNome(entity.getNome());
        turmaResponse.setLimiteAlunos(entity.getLimiteAlunos());
        turmaResponse.setNivel(entity.getNivel());
        turmaResponse.setTipo(entity.getTipo());

        // Mapeia o Professor se ele existir na entidade para não dar NullPointerException
        if (entity.getProfessor() != null) {
            turmaResponse.setProfessorId(entity.getProfessor().getId());
            turmaResponse.setNomeProfessor(entity.getProfessor().getNome());
        }

        // Mapeia a lista de horários para a resposta do JSON (Usando a Inner Class Static do TurmaResponse)
        if (entity.getHorarios() != null) {
            List<TurmaResponse.HorarioTurmaDto> horariosDto = entity.getHorarios()
                    .stream()
                    .map(horario -> {
                        TurmaResponse.HorarioTurmaDto dto = new TurmaResponse.HorarioTurmaDto();
                        dto.setId(horario.getId());
                        dto.setDiaSemana(horario.getDiaSemana());
                        dto.setHoraInicio(horario.getHoraInicio());
                        dto.setHoraFim(horario.getHoraFim());
                        return dto;
                    })
                    .toList();
            turmaResponse.setHorarios(horariosDto);
        } else {
            turmaResponse.setHorarios(new ArrayList<>());
        }

        return turmaResponse;
    }

    public static List<TurmaResponse> toResponseList(List<Turma> turmas) {
        if (turmas == null) return new ArrayList<>();
        return turmas.stream()
                .map(TurmaMapper::toResponse)
                .toList();
    }
}