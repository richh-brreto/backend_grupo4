package school.sptech.back_end_PI.dto.contrato;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ContratoRequest {

    @NotNull
    private LocalDate dataInicio;

    @NotNull
    private LocalDate dataFim;

    @NotNull
    private Long TurmaId;

    @NotNull
    private Integer ProfessorId;

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public Long getTurmaId() {
        return TurmaId;
    }

    public Integer getProfessorId() {
        return ProfessorId;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public void setTurmaId(Long turmaId) {
        TurmaId = turmaId;
    }

    public void setProfessorId(Integer professorId) {
        ProfessorId = professorId;
    }
}
