package school.sptech.back_end_PI.mapper;

import school.sptech.back_end_PI.dto.aula.AulaResponse;
import school.sptech.back_end_PI.dto.aula.LogAulaResponse;
import school.sptech.back_end_PI.entity.Aula;
import school.sptech.back_end_PI.entity.LogAula;

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
        if (aula.getContrato() != null) {
            response.setContratoId(aula.getContrato().getId());
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
