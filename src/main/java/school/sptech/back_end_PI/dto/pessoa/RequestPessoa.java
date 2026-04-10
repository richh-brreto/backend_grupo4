package school.sptech.back_end_PI.dto.pessoa;


public class RequestPessoa {
    protected String nome;
    protected String email;
    protected Integer telefone;
    protected Integer senha;

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
        return senha;
    }
    public void setSenha(Integer senha) {
        senha = senha;
    }
}
