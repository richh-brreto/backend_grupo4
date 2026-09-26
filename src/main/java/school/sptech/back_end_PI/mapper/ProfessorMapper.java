package school.sptech.back_end_PI.mapper;

import school.sptech.back_end_PI.dto.professor.ProfessorRequest;
import school.sptech.back_end_PI.dto.professor.ProfessorResponse;
import school.sptech.back_end_PI.entity.Horario;
import school.sptech.back_end_PI.entity.Permissao;
import school.sptech.back_end_PI.entity.Professor;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ProfessorMapper {

    public static Professor toEntity(ProfessorRequest dto, List<Horario> horarios, Set<Permissao> permissoes) {
        if (dto == null) return null;

        Professor professor = new Professor();
        professor.setNome(dto.getNome());
        professor.setEmail(dto.getEmail());
        professor.setTelefone(dto.getTelefone());
        professor.setHorarios(horarios);
        professor.setPermissoes(permissoes == null ? new LinkedHashSet<>() : new LinkedHashSet<>(permissoes));
        professor.setAtivo(true); // Nasce ativo por padrão

        return professor;
    }

    public static ProfessorResponse toResponse(Professor professor) {
        if (professor == null) return null;

        ProfessorResponse dto = new ProfessorResponse();
        dto.setId(professor.getId());
        dto.setNome(professor.getNome());
        dto.setEmail(professor.getEmail());
        dto.setTelefone(professor.getTelefone());
        dto.setSenhaDefinida(professor.getSenha() != null && !professor.getSenha().isBlank());
        dto.setCodigoAcesso(professor.getCodigoAcesso());
        dto.setAtivo(professor.getAtivo());
        dto.setPermissoes(professor.nomesPermissoes());

        if (professor.getHorarios() != null) {
            List<ProfessorResponse.HorarioProfessorDto> horarios = professor.getHorarios()
                    .stream()
                    .map(horario -> {
                        ProfessorResponse.HorarioProfessorDto horarioDto = new ProfessorResponse.HorarioProfessorDto();

                        horarioDto.setId(horario.getId());
                        horarioDto.setDiaSemana(horario.getDiaSemana());
                        horarioDto.setHoraInicio(horario.getHoraInicio());
                        horarioDto.setHoraFim(horario.getHoraFim());

                        return horarioDto;
                    })
                    .toList();

            dto.setHorarios(horarios);
        } else {
            dto.setHorarios(new ArrayList<>());
        }

        return dto;
    }

    public static List<ProfessorResponse> toResponseList(List<Professor> professores) {
        if (professores == null) return new ArrayList<>();
        return professores.stream()
                .map(ProfessorMapper::toResponse)
                .toList();
    }
}
