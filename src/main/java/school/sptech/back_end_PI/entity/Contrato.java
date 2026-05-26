package school.sptech.back_end_PI.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "contrato")
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contrato")
    private Long id;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    private String tipo;

    @ManyToOne
    @JoinColumn(name = "aluno_id_aluno")
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "turma_id_turma")
    private Turma turma;

    @ManyToOne
    @JoinColumn(name = "professor_id_professor")
    private Professor professor;

    // Relacionamento com a tabela auxiliar de horários particulares
    @ManyToMany
    @JoinTable(
            name = "ids_horario_contrato",
            joinColumns = @JoinColumn(name = "contrato_id_contrato"),
            inverseJoinColumns = @JoinColumn(name = "horario_id_horario")
    )
    private List<Horario> horarios;

    // Construtor Padrão (Obrigatório para o Hibernate)
    public Contrato() {
    }

    // Construtor Completo
    public Contrato(Long id, LocalDate dataInicio, LocalDate dataFim, String tipo, Aluno aluno, Turma turma, Professor professor, List<Horario> horarios) {
        this.id = id;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.tipo = tipo;
        this.aluno = aluno;
        this.turma = turma;
        this.professor = professor;
        this.horarios = horarios;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public Turma getTurma() {
        return turma;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public List<Horario> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<Horario> horarios) {
        this.horarios = horarios;
    }
}
