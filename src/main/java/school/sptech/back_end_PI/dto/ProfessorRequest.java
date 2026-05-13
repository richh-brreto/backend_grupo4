package school.sptech.back_end_PI.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class ProfessorRequest {

    @NotBlank
    @Size(min = 3, max = 45)
    private String nome;

    @NotBlank
    @Email
    private String email;

    @NotNull
    private Integer telefone;

    @NotBlank
    @Size(min = 6)
    private String senha;

    @NotNull
    private Integer idTipoProfessor;

    private List<Long> horariosIds;

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getTelefone() { return telefone; }
    public void setTelefone(Integer telefone) { this.telefone = telefone; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public Integer getIdTipoProfessor() { return idTipoProfessor; }
    public void setIdTipoProfessor(Integer idTipoProfessor) { this.idTipoProfessor = idTipoProfessor; }

    public List<Long> getHorariosIds() {
        return horariosIds;
    }

    public void setHorariosIds(List<Long> horariosIds) {
        this.horariosIds = horariosIds;
    }
}