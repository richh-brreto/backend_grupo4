package school.sptech.back_end_PI.dto;

import java.util.List;

public class HorarioAlunoProfessorRequest {

    private List<Long> alunoHorariosIds;

    public List<Long> getAlunoHorariosIds() {
        return alunoHorariosIds;
    }

    public void setAlunoHorariosIds(List<Long> alunoHorariosIds) {
        this.alunoHorariosIds = alunoHorariosIds;
    }
}
