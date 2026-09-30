package school.sptech.back_end_PI.dto.auth;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

// Dados do usuário autenticado para a interface (sem senha, token ou código de acesso)
// "permissoes" é a lista de telas que o professor pode acessar (tabela professor_permissao).
// Não existe mais "perfil": a autorização vem só das permissões.
@JsonPropertyOrder({"nome", "email", "permissoes"})
public class UsuarioLogadoResponse {

    private String nome;
    private String email;
    private List<String> permissoes;

    public UsuarioLogadoResponse() {}

    public UsuarioLogadoResponse(String nome, String email, List<String> permissoes) {
        this.nome = nome;
        this.email = email;
        this.permissoes = permissoes == null ? List.of() : permissoes;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<String> getPermissoes() { return permissoes; }
    public void setPermissoes(List<String> permissoes) { this.permissoes = permissoes; }
}
