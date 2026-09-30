package school.sptech.back_end_PI.dto.auth;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

// Resposta do login. O token NÃO vai no corpo: ele continua sendo entregue
// apenas no cookie HttpOnly "authToken" (OWASP A01/A02). O front lê o cookie
// automaticamente e usa "permissoes" para filtrar o menu.
@JsonPropertyOrder({"mensagem", "permissoes"})
public class LoginResponse {

    private String mensagem;
    private List<String> permissoes;

    public LoginResponse() {}

    public LoginResponse(String mensagem, List<String> permissoes) {
        this.mensagem = mensagem;
        this.permissoes = permissoes == null ? List.of() : permissoes;
    }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public List<String> getPermissoes() { return permissoes; }
    public void setPermissoes(List<String> permissoes) { this.permissoes = permissoes; }
}
