package school.sptech.back_end_PI.dto.aula;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public class RemarcarAulaRequest {

    @NotNull(message = "A nova data da aula é obrigatória")
    private LocalDate novaData;

    @NotNull(message = "A nova hora de início é obrigatória")
    private LocalTime novaHoraInicio;

    @NotNull(message = "A nova hora de fim é obrigatória")
    private LocalTime novaHoraFim;

    private String motivo;

    public RemarcarAulaRequest() {}

    public LocalDate getNovaData() { return novaData; }
    public void setNovaData(LocalDate novaData) { this.novaData = novaData; }

    public LocalTime getNovaHoraInicio() { return novaHoraInicio; }
    public void setNovaHoraInicio(LocalTime novaHoraInicio) { this.novaHoraInicio = novaHoraInicio; }

    public LocalTime getNovaHoraFim() { return novaHoraFim; }
    public void setNovaHoraFim(LocalTime novaHoraFim) { this.novaHoraFim = novaHoraFim; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
