package school.sptech.back_end_PI.dto.turma;

import school.sptech.back_end_PI.dto.aluno.AlunoResponse;

import java.util.List;

public class TurmaResponse {

    private Long id;
    private String nome;
    private String nivel;
    private String tipo;
    private Integer limiteAlunos;
    List<AlunoResponse> alunos;

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

    public Integer getLimiteAlunos() {
        return limiteAlunos;
    }

    public void setLimiteAlunos(Integer limiteAlunos) {
        this.limiteAlunos = limiteAlunos;
    }

    public List<AlunoResponse> getAlunos() {
        return alunos;
    }

    public void setAlunos(List<AlunoResponse> alunos) {
        this.alunos = alunos;
    }

    public String getNivel() {
        return nivel;
    }

    public String getTipo() {
        return tipo;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
