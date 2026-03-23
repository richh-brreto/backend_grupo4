package school.sptech.back_end_PI.dto;


import org.jspecify.annotations.Nullable;

public class RequestPessoa {
    protected String nome;
    protected String email;
    protected Integer telefone;
    protected Integer Senha;

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
    public Integer getTelefone() {
        return telefone;
    }
    public void setTelefone(Integer telefone) {
        this.telefone = telefone;
    }
    public Integer getSenha() {
        return Senha;
    }
    public void setSenha(Integer senha) {
        Senha = senha;
    }
}
