package school.sptech.back_end_PI.dto.pessoa;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RequestPessoaDto {

    @NotBlank
    @Size(min = 2, max = 250)
    @Schema(example = "Bruno Oliveira", description = "Representa o nome de um usuário")
    protected String nome;

    @Email
    @Schema(example = "bruno@gmail.com", description = "Representa o email de usuário")
    protected String email;

    @NotBlank
    @Size(min = 11, max = 11)
    @Schema(example = "11940369728", description = "Representa o telefone de um usuário")
    protected String telefone;

    @NotBlank
    @Size(min = 8, max = 50)
    @Schema(example = "Senha123", description = "Representa a senha de um usuário")
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
    public String getTelefone() {
        return telefone;
    }
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    public Integer getSenha() {
        return Senha;
    }
    public void setSenha(Integer senha) {
        Senha = senha;
    }
}
