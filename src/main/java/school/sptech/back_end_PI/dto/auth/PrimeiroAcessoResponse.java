package school.sptech.back_end_PI.dto.auth;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "mensagem", "email" })
public class PrimeiroAcessoResponse {

    private String mensagem;
    private String email;

    public PrimeiroAcessoResponse() {
    }

    public PrimeiroAcessoResponse(String mensagem, String email) {
        this.mensagem = mensagem;
        this.email = email;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}