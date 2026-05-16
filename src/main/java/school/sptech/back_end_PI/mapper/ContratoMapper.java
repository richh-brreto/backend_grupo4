package school.sptech.back_end_PI.mapper;

import school.sptech.back_end_PI.dto.ContratoResponse;
import school.sptech.back_end_PI.dto.ProfessorResponse;
import school.sptech.back_end_PI.entity.Contrato;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.entity.Turma;

public class ContratoMapper {

    public static ContratoResponse toResponse(Contrato contrato) {
        ContratoResponse response = new ContratoResponse();

        response.setId(contrato.getId());
        response.setDataInicio(contrato.getDataInicio());
        response.setDataFim(contrato.getDataFim());

        // Mapeia Professor
        Professor professor = contrato.getProfessor();
        if (professor != null) {
            ProfessorResponse professorResponse = new ProfessorResponse();
            professorResponse.setId(professor.getId());
            professorResponse.setNome(professor.getNome());
            professorResponse.setEmail(professor.getEmail());
            professorResponse.setTelefone(professor.getTelefone());
            // aqui você pode mapear tipo e horários se quiser
            response.setProfessor(professorResponse);
        }

        // Mapeia Turma (Debito tecnico deve ser implementado com for implementado o turmaResponse)
//        Turma turma = contrato.getTurma();
//        if (turma != null) {
//            TurmaResponse turmaResponse = new TurmaResponse();
//            turmaResponse.setId(turma.getId());
//            turmaResponse.setNome(turma.getNomeTurma());
//            turmaResponse.setNivel(turma.getNivel());
//            turmaResponse.setTipo(turma.getTipo());
//            // se quiser incluir alunos, pode mapear aqui também
//            response.setTurma(turmaResponse);
//        }
        response.setTurma(contrato.getTurma());

        return response;
    }
}
