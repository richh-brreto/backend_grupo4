package school.sptech.back_end_PI.dto.aula;

import jakarta.validation.constraints.Size;

public class CancelarAulaTurmaRequest extends AulaTurmaRequest {

    @Size(max = 500, message = "O motivo deve ter no máximo 500 caracteres")
    private String motivo;

    public CancelarAulaTurmaRequest() {}

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
