package school.sptech.back_end_PI.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotNull;
import school.sptech.back_end_PI.entity.Turma;
import java.time.LocalDate;


@JsonPropertyOrder({ "id", "dataInicio", "dataFim", "professor", "turma" })
public class ContratoResponse {

    private Long id;

    @NotNull
    private LocalDate dataInicio;

    @NotNull
    private LocalDate dataFim;

    private ProfessorResponse professor;

    private Turma turma;

    public ContratoResponse() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }

    public ProfessorResponse getProfessor() { return professor; }
    public void setProfessor(ProfessorResponse professor) { this.professor = professor; }

    public Turma getTurma() { return turma; }
    public void setTurma(Turma turma) { this.turma = turma; }
}
