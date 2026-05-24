package school.sptech.back_end_PI.dto.contrato;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import school.sptech.back_end_PI.dto.aluno.AlunoResponse;
import school.sptech.back_end_PI.dto.professor.ProfessorResponse;
import school.sptech.back_end_PI.dto.turma.TurmaResponse;
import school.sptech.back_end_PI.dto.horario.HorarioResponse;

import java.time.LocalDate;
import java.util.List;

@JsonPropertyOrder({ "id", "tipo", "dataInicio", "dataFim", "aluno", "turma", "professor", "horarios" })
public class ContratoResponse {

    private Long id;
    private String tipo;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private AlunoResponse aluno;
    private TurmaResponse turma;
    private ProfessorResponse professor;
    private List<HorarioResponse> horarios;

    // Construtor Padrão
    public ContratoResponse() {}

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public AlunoResponse getAluno() {
        return aluno;
    }

    public void setAluno(AlunoResponse aluno) {
        this.aluno = aluno;
    }

    public ProfessorResponse getProfessor() {
        return professor;
    }

    public void setProfessor(ProfessorResponse professor) {
        this.professor = professor;
    }

    public TurmaResponse getTurma() {
        return turma;
    }

    public void setTurma(TurmaResponse turma) {
        this.turma = turma;
    }

    public List<HorarioResponse> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<HorarioResponse> horarios) {
        this.horarios = horarios;
    }
}