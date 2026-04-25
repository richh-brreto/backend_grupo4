package school.sptech.back_end_PI.dto.turma;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ResponseTurma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nome;

    @NotBlank
    private String nivel;

    @NotNull
    private Integer limite_alunos;

    public ResponseTurma() {
    }

    public ResponseTurma(Long id, String nome, String nivel, Integer limite_alunos) {
        this.id = id;
        this.nome = nome;
        this.nivel = nivel;
        this.limite_alunos = limite_alunos;
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

}
