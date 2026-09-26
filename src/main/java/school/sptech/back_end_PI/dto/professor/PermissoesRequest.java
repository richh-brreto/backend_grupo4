package school.sptech.back_end_PI.dto.professor;

import jakarta.validation.constraints.NotNull;

import java.util.List;

// Payload para liberar/revogar telas de um professor.
// Array vazio = remove todos os acessos (a interface some).
public class PermissoesRequest {

    @NotNull(message = "Informe a lista de permissões (pode estar vazia)")
    private List<String> permissoes;

    public PermissoesRequest() {}

    public PermissoesRequest(List<String> permissoes) {
        this.permissoes = permissoes;
    }

    public List<String> getPermissoes() { return permissoes; }
    public void setPermissoes(List<String> permissoes) { this.permissoes = permissoes; }
}
