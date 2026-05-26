package school.sptech.back_end_PI.mapper;

import school.sptech.back_end_PI.dto.aluno.AlunoRequest;
import school.sptech.back_end_PI.dto.aluno.AlunoResponse;
import school.sptech.back_end_PI.entity.Aluno;
import school.sptech.back_end_PI.entity.Horario;

import java.util.ArrayList;
import java.util.List;

public class AlunoMapper {

    public static Aluno toEntity(AlunoRequest request, List<Horario> horarios) {
        if (request == null) return null;

        Aluno aluno = new Aluno();
        aluno.setNome(request.getNome());
        aluno.setEmail(request.getEmail());
        aluno.setTelefone(request.getTelefone());
        aluno.setNivel(request.getNivel());
        aluno.setHorarios(horarios);
        aluno.setAtivo(true); // Garante que nasce ativo no banco

        return aluno;
    }

    public static Aluno toEntity(AlunoRequest request) {
        if (request == null) return null;

        Aluno aluno = new Aluno();
        aluno.setNome(request.getNome());
        aluno.setEmail(request.getEmail());
        aluno.setTelefone(request.getTelefone());
        aluno.setNivel(request.getNivel());
        aluno.setAtivo(true); // Garante que nasce ativo no banco

        return aluno;
    }

    public static AlunoResponse toResponse(Aluno aluno) {
        if (aluno == null) return null;

        AlunoResponse response = new AlunoResponse();

        response.setId(aluno.getId());
        response.setNome(aluno.getNome());
        response.setEmail(aluno.getEmail());
        response.setTelefone(aluno.getTelefone());
        response.setNivel(aluno.getNivel());
        response.setAtivo(aluno.getAtivo());

        if (aluno.getHorarios() != null) {
            List<AlunoResponse.HorarioAlunoDto> horarios = aluno.getHorarios()
                    .stream()
                    .map(horario -> {

                        AlunoResponse.HorarioAlunoDto dto = new AlunoResponse.HorarioAlunoDto();

                        dto.setId(horario.getId());
                        dto.setDiaSemana(horario.getDiaSemana());
                        dto.setHoraInicio(horario.getHoraInicio());
                        dto.setHoraFim(horario.getHoraFim());

                        return dto;
                    })
                    .toList();

            response.setHorarios(horarios);
        } else {
            response.setHorarios(new ArrayList<>()); // Proteção contra NullPointer no JSON
        }

        return response;
    }

    public static List<AlunoResponse> toResponseList(List<Aluno> alunos) {
        if (alunos == null) return new ArrayList<>();
        return alunos.stream()
                .map(AlunoMapper::toResponse)
                .toList();
    }
}