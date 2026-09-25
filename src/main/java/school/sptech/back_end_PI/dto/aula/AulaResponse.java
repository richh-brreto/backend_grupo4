package school.sptech.back_end_PI.dto.aula;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import school.sptech.back_end_PI.entity.StatusAula;

import java.time.LocalDate;
import java.time.LocalTime;

@JsonPropertyOrder({"id", "data", "horaInicio", "horaFim", "status", "presenca", "contratoId", "professor", "turmaId", "turma", "alunoId", "aluno"})
public class AulaResponse {

    private Long id;
    private LocalDate data;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private StatusAula status;
    private Boolean presenca;
    private Long contratoId;

    // Nomes exibidos na agenda
    private String professor;
    private Long turmaId;
    private String turma;
    private Long alunoId;
    private String aluno;

    public AulaResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFim() { return horaFim; }
    public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }

    public StatusAula getStatus() { return status; }
    public void setStatus(StatusAula status) { this.status = status; }

    public Boolean getPresenca() { return presenca; }
    public void setPresenca(Boolean presenca) { this.presenca = presenca; }

    public Long getContratoId() { return contratoId; }
    public void setContratoId(Long contratoId) { this.contratoId = contratoId; }

    public String getProfessor() { return professor; }
    public void setProfessor(String professor) { this.professor = professor; }

    public Long getTurmaId() { return turmaId; }
    public void setTurmaId(Long turmaId) { this.turmaId = turmaId; }

    public String getTurma() { return turma; }
    public void setTurma(String turma) { this.turma = turma; }

    public Long getAlunoId() { return alunoId; }
    public void setAlunoId(Long alunoId) { this.alunoId = alunoId; }

    public String getAluno() { return aluno; }
    public void setAluno(String aluno) { this.aluno = aluno; }
}
