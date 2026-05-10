package school.sptech.back_end_PI.mapper;

import school.sptech.back_end_PI.dto.ProfessorRequest;
import school.sptech.back_end_PI.entity.Horario;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.entity.TipoProfessor;
import school.sptech.back_end_PI.dto.ProfessorResponse;

import java.util.List;


public class ProfessorMapper {

    public static Professor toEntity(ProfessorRequest dto, TipoProfessor tipo, List<Horario> horarios) {
        if (dto == null) return null;

        Professor professor = new Professor();

        professor.setNome(dto.getNome());
        professor.setEmail(dto.getEmail());
        professor.setTelefone(dto.getTelefone());
        professor.setSenha(dto.getSenha());
        professor.setTipo(tipo);
        professor.setHorarios(horarios);

        return professor;
    }

    public static ProfessorResponse toResponse(Professor professor) {
        if (professor == null) return null;

        ProfessorResponse dto = new ProfessorResponse();
        dto.setId(professor.getId());
        dto.setNome(professor.getNome());
        dto.setEmail(professor.getEmail());
        dto.setTelefone(professor.getTelefone());

        // Mapeamento do Tipo de Professor
        if (professor.getTipo() != null) {
            dto.setTipo(TipoProfessorMapper.toResponse(professor.getTipo()));
        }

        // Mapeamento da lista de horários seguindo o padrão do AlunoMapper
        if (professor.getHorarios() != null) {
            List<ProfessorResponse.HorarioProfessorDto> horarios = professor.getHorarios()
                    .stream()
                    .map(horario -> {
                        // Instanciando a inner class de AlunoResponse conforme o seu padrão
                        ProfessorResponse.HorarioProfessorDto horarioDto = new ProfessorResponse().new HorarioProfessorDto();

                        horarioDto.setId(horario.getId());
                        horarioDto.setDiaSemana(horario.getDiaSemana());
                        horarioDto.setHoraInicio(horario.getHoraInicio());
                        horarioDto.setHoraFim(horario.getHoraFim());

                        return horarioDto;
                    })
                    .toList();

            dto.setHorarios(horarios);
        }

        return dto;
    }

    public static List<ProfessorResponse> toResponseList(List<Professor> professores) {
        return professores.stream()
                .map(ProfessorMapper::toResponse)
                .toList();
    }
}