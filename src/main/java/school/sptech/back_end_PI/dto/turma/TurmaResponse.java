package school.sptech.back_end_PI.dto.turma;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import school.sptech.back_end_PI.dto.aluno.AlunoResponse;

import java.time.LocalTime;
import java.util.List;

@JsonPropertyOrder({ "id", "nome", "nivel", "tipo", "limiteAlunos", "professorId", "nomeProfessor", "horarios", "alunos" })
public class TurmaResponse {

    private Long id;
    private String nome;
    private String nivel;
    private String tipo;
    private Integer limiteAlunos;

    // Dados do Professor vinculados à turma
    private Long professorId;
    private String nomeProfessor;

    // Listas vinculadas
    private List<HorarioTurmaDto> horarios;
    private List<AlunoResponse> alunos;

    // Classe interna STATIC para os horários da Turma (usada no Mapper)
    public static class HorarioTurmaDto {
        private Long id;
        private String diaSemana;
        private LocalTime horaInicio;
        private LocalTime horaFim;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getDiaSemana() { return diaSemana; }
        public void setDiaSemana(String diaSemana) { this.diaSemana = diaSemana; }
        public LocalTime getHoraInicio() { return horaInicio; }
        public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
        public LocalTime getHoraFim() { return horaFim; }
        public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }
    }

    public TurmaResponse() {
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Integer getLimiteAlunos() { return limiteAlunos; }
    public void setLimiteAlunos(Integer limiteAlunos) { this.limiteAlunos = limiteAlunos; }

    public Long getProfessorId() { return professorId; }
    public void setProfessorId(Long professorId) { this.professorId = professorId; }

    public String getNomeProfessor() { return nomeProfessor; }
    public void setNomeProfessor(String nomeProfessor) { this.nomeProfessor = nomeProfessor; }

    public List<HorarioTurmaDto> getHorarios() { return horarios; }
    public void setHorarios(List<HorarioTurmaDto> horarios) { this.horarios = horarios; }

    public List<AlunoResponse> getAlunos() { return alunos; }
    public void setAlunos(List<AlunoResponse> alunos) { this.alunos = alunos; }
}