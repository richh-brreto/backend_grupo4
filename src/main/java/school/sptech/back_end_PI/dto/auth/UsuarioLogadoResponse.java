package school.sptech.back_end_PI.dto.auth;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

// Dados do usuário autenticado para a interface (sem senha, token ou código de acesso)
// "permissoes" é a lista de telas que o professor pode acessar (tabela professor_permissao).
// Não existe mais "perfil": a autorização vem só das permissões.
// "tipo" (PROFESSOR ou ALUNO) diz à interface qual escopo de dados esperar.
@JsonPropertyOrder({"nome", "email", "tipo", "permissoes"})
public class UsuarioLogadoResponse {

    private String nome;
    private String email;
    private String tipo;
    private List<String> permissoes;

    public UsuarioLogadoResponse() {}

    public UsuarioLogadoResponse(String nome, String email, List<String> permissoes) {
        this(nome, email, null, permissoes);
    }

    public UsuarioLogadoResponse(String nome, String email, String tipo, List<String> permissoes) {
        this.nome = nome;
        this.email = email;
        this.tipo = tipo;
        this.permissoes = permissoes == null ? List.of() : permissoes;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public List<String> getPermissoes() { return permissoes; }
    public void setPermissoes(List<String> permissoes) { this.permissoes = permissoes; }
}
