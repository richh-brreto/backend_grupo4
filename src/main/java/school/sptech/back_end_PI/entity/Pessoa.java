package school.sptech.back_end_PI.entity;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.*;
import org.springframework.security.core.userdetails.UserDetails;

@MappedSuperclass
public abstract class Pessoa  implements UserDetails {

    @NotNull @NotBlank
    protected String nome;

    @NotNull @NotBlank
    protected String email;

    @NotNull @Positive @Size(max = 11)
    protected String telefone;

    @NotNull @NotBlank
    protected String senha;

    private String tipo;

    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getTelefone() {
        return telefone;
    }
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }
}