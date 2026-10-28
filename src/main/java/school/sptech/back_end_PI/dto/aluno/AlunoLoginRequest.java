package school.sptech.back_end_PI.dto.aluno;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AlunoLoginRequest {
    @NotNull
    @NotBlank
    private String email;

    @NotNull @NotBlank
    private String senha;

    public AlunoLoginRequest() {
        }

    public AlunoLoginRequest(String email, String senha) {
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
