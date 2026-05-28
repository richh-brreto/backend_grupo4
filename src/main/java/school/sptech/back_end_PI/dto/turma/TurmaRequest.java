package school.sptech.back_end_PI.dto.turma;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public class TurmaRequest {
    @NotBlank
    private String nome;
    @NotBlank
    private String nivel;
    @NotNull @Positive
    private Integer limiteAlunos;
    @NotBlank
    private String tipo;
    @NotEmpty
    private List<Long> horariosIds;

    public List<Long> getHorariosIds() { return horariosIds; }

    public void setHorariosIds(List<Long> horariosIds) { this.horariosIds = horariosIds; }

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
}
