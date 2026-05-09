package school.sptech.back_end_PI.dto;

import java.util.List;

public class DisponibilidadeRequest {

    private List<Long> horarioIds;

    public List<Long> getHorarioIds() {
        return horarioIds;
    }

    public void setHorarioIds(List<Long> horarioIds) {
        this.horarioIds = horarioIds;
    }
}
