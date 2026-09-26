package school.sptech.back_end_PI.dto.professor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// DTO do POST /usuarios/login. Só transporta e-mail e senha:
// as permissões do professor vêm do Professor carregado no banco pela autenticação,
// nunca daqui. Por isso este record não implementa UserDetails.
public class ProfessorLoginRequest {

    @NotNull @NotBlank
    private String email;

    @NotNull @NotBlank
    private String senha;

    public ProfessorLoginRequest() {
    }

    public ProfessorLoginRequest(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
