package school.sptech.back_end_PI.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contrato")
    private Long id;

    private LocalDate dataInicio;

    private LocalDate dataFim;

    @ManyToOne()
    @JoinColumn (name = "turma_id_turma")
    private Turma turma;

    @ManyToOne
    @JoinColumn (name = "professor_id_professor")
    private Professor professor;

    public Contrato(Long id, LocalDate dataInicio, LocalDate dataFim, Turma turma, Professor professor) {
        this.id = id;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.turma = turma;
        this.professor = professor;
    }

    public Contrato() {
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public Turma getTurma() {
        return turma;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }
}
