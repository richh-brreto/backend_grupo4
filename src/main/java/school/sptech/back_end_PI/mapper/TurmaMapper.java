package school.sptech.back_end_PI.mapper;

import school.sptech.back_end_PI.dto.AlunoResponse;
import school.sptech.back_end_PI.dto.TurmaRequest;
import school.sptech.back_end_PI.dto.TurmaResponse;
import school.sptech.back_end_PI.entity.Turma;

import java.util.List;

public class TurmaMapper {

    public static Turma toEntity (TurmaRequest request) {
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

        if (entity.getAlunos() != null) {
            List<AlunoResponse> alunosResponse = entity.getAlunos().stream()
                    .map(AlunoMapper::toResponse)
                    .toList();
            turmaResponse.setAlunos(alunosResponse);
        }

        return turmaResponse;
    }
}
