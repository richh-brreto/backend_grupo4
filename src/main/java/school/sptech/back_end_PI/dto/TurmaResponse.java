package school.sptech.back_end_PI.dto;

import java.util.List;

public class TurmaResponse {

    private Long id;
    private String nome;
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
}
