package school.sptech.back_end_PI.dto.auth;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

// Dados mínimos do usuário autenticado para a interface (sem senha, token ou código de acesso)
@JsonPropertyOrder({"nome", "email", "perfil"})
public class UsuarioLogadoResponse {

    private String nome;
    private String email;
    private String perfil;

    public UsuarioLogadoResponse() {}

    public UsuarioLogadoResponse(String nome, String email, String perfil) {
        this.nome = nome;
        this.email = email;
        this.perfil = perfil;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }
}
