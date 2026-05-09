package school.sptech.back_end_PI.mapper;

import school.sptech.back_end_PI.dto.TurmaRequest;
import school.sptech.back_end_PI.entity.Turma;

public class TurmaMapper {

    public static Turma toEntity (TurmaRequest request) {
        Turma turma = new Turma();
        turma.setNome(request.getNome());
        turma.setNivel(request.getNivel());
        turma.setLimiteAlunos(request.getLimiteAlunos());
        turma.setTipo(request.getTipo());
        return turma;
    }
}
