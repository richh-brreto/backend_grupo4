package school.sptech.back_end_PI.mapper;

import school.sptech.back_end_PI.dto.AlunoRequest;
import school.sptech.back_end_PI.dto.AlunoResponse;
import school.sptech.back_end_PI.entity.Aluno;

public class AlunoMapper {

    public static Aluno toEntity(AlunoRequest request) {
        if (request == null) return null;

        Aluno aluno = new Aluno();
        aluno.setNome(request.getNome());
        aluno.setEmail(request.getEmail());
        aluno.setTelefone(request.getTelefone());
        aluno.setNivel(request.getNivel());

        return aluno;
    }

    public static AlunoResponse toResponse(Aluno aluno) {
        if (aluno == null) return null;

        return new AlunoResponse(
                aluno.getId(),
                aluno.getNome(),
                aluno.getEmail(),
                aluno.getTelefone(),
                aluno.getNivel()
        );
    }
}