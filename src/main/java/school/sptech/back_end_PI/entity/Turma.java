package school.sptech.back_end_PI.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Entity
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_turma")
    private Long id;

    @NotBlank
    @Column(name = "nome_turma")
    private String nome;

    @NotBlank
    private String nivel;

    @Column(name = "limite_alunos")
    private Integer limiteAlunos;

    private String tipo;

    // Novo mapeamento: Uma turma possui um professor específico (Muitas turmas para um Professor)
    @ManyToOne
    @JoinColumn(name = "professor_id_professor")
    private Professor professor;

    // Novo mapeamento: Tabela N:N de disponibilidade da turma no banco
    @ManyToMany
    @JoinTable(
            name = "disponibilidade_turma",
            joinColumns = @JoinColumn(name = "turma_id_turma"),
            inverseJoinColumns = @JoinColumn(name = "horario_id_horario")
    )
    private List<Horario> horarios;

    // Construtor Padrão
    public Turma() {
    }

    // Getters e Setters Antigos (Mantidos exatamente iguais)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public Integer getLimiteAlunos() {
        return limiteAlunos;
    }

    public void setLimiteAlunos(Integer limiteAlunos) {
        this.limiteAlunos = limiteAlunos;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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