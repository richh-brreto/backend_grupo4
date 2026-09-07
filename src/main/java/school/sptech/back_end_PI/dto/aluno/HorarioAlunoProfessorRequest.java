package school.sptech.back_end_PI.dto.aluno;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class HorarioAlunoProfessorRequest {

    @NotEmpty
    private List<Long> alunoHorariosIds;

    public List<Long> getAlunoHorariosIds() {
        return alunoHorariosIds;
    }

    public void setAlunoHorariosIds(List<Long> alunoHorariosIds) {
        this.alunoHorariosIds = alunoHorariosIds;
    }
}
