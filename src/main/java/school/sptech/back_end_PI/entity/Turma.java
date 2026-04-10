package school.sptech.back_end_PI.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "turma")
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nome;

    @NotBlank
    private String nivel;

    @NotNull
    private Integer limite_alunos;

    @ManyToMany
    @JoinTable(
            name = "aluno_turma",
            joinColumns = @JoinColumn(name = "turma_id"),
            inverseJoinColumns = @JoinColumn(name = "aluno_id")
    )
    private List<Aluno> alunos = new ArrayList<>();



    public Turma() {
    }

    public Turma(Long id, String nome, String nivel, Integer limite_alunos, List<Aluno> alunos) {
        this.id = id;
        this.nome = nome;
        this.nivel = nivel;
        this.limite_alunos = limite_alunos;
        this.alunos = alunos;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getNivel() {
        return nivel;
    }

    public Integer getLimite_alunos() {
        return limite_alunos;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public void setLimite_alunos(Integer limite_alunos) {
        this.limite_alunos = limite_alunos;
    }

    public List<Aluno> getAlunos() {
        return alunos;
    }

    public void setAlunos(List<Aluno> alunos) {
        this.alunos = alunos;
    }

    @Override
    public String toString() {
        return "Turma{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", nivel='" + nivel + '\'' +
                ", limite_alunos=" + limite_alunos +
                ", alunos=" + alunos +
                '}';
    }
}
