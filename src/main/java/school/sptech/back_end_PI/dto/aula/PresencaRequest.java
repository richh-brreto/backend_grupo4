package school.sptech.back_end_PI.dto.aula;

import jakarta.validation.constraints.NotNull;

public class PresencaRequest {

    @NotNull(message = "O valor de presença é obrigatório")
    private Boolean presenca;

    public PresencaRequest() {}

    public Boolean getPresenca() { return presenca; }
    public void setPresenca(Boolean presenca) { this.presenca = presenca; }
}
