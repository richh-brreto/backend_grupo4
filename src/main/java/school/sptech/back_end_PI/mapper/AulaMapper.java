package school.sptech.back_end_PI.mapper;

import school.sptech.back_end_PI.dto.aula.AulaResponse;
import school.sptech.back_end_PI.dto.aula.LogAulaResponse;
import school.sptech.back_end_PI.entity.Aula;
import school.sptech.back_end_PI.entity.Contrato;
import school.sptech.back_end_PI.entity.LogAula;
import school.sptech.back_end_PI.entity.Turma;

public class AulaMapper {

    public static AulaResponse toResponse(Aula aula) {
        if (aula == null) return null;

        AulaResponse response = new AulaResponse();
        response.setId(aula.getId());
        response.setData(aula.getData());
        response.setHoraInicio(aula.getHoraInicio());
        response.setHoraFim(aula.getHoraFim());
        response.setStatus(aula.getStatus());
        response.setPresenca(aula.getPresenca());
        Contrato contrato = aula.getContrato();
        if (contrato != null) {
            response.setContratoId(contrato.getId());

            if (contrato.getAluno() != null) {
                response.setAlunoId(contrato.getAluno().getId());
                response.setAluno(contrato.getAluno().getNome());
            }

            // Contrato individual tem professor próprio; no de grupo o professor é o da turma
            Turma turma = contrato.getTurma();
            if (turma != null) {
                response.setTurmaId(turma.getId());
                response.setTurma(turma.getNome());
            }
            if (contrato.getProfessor() != null) {
                response.setProfessor(contrato.getProfessor().getNome());
            } else if (turma != null && turma.getProfessor() != null) {
                response.setProfessor(turma.getProfessor().getNome());
            }
        }
        return response;
    }

    public static LogAulaResponse toLogResponse(LogAula log) {
        if (log == null) return null;

        LogAulaResponse response = new LogAulaResponse();
        response.setId(log.getId());
        response.setAcao(log.getAcao());
        response.setDescricao(log.getDescricao());
        response.setDataHora(log.getDataHora());
        if (log.getAula() != null) {
            response.setAulaId(log.getAula().getId());
        }
        return response;
    }
}
