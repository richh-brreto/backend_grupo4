package school.sptech.back_end_PI.mapper.turma;

import school.sptech.back_end_PI.dto.turma.RequestTurma;
import school.sptech.back_end_PI.dto.turma.ResponseTurma;
import school.sptech.back_end_PI.entity.Turma;


public class TurmaMapper {

    public static Turma toEntity(RequestTurma turmaRequest){

        Turma turma = new Turma();

        turma.setNome(turmaRequest.getNome());
        turma.setNivel(turmaRequest.getNivel());
        turma.setLimite_alunos(turmaRequest.getLimite_alunos());

        return turma;
    }

    public static ResponseTurma toResponse(Turma turma){

        ResponseTurma responseTurma = new ResponseTurma();

        responseTurma.setNome(turma.getNome());
        responseTurma.setNivel(turma.getNivel());
        responseTurma.setLimite_alunos(turma.getLimite_alunos());

        return responseTurma;
    }
}
