package school.sptech.back_end_PI.dto.aula;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

// Identifica o encontro de uma turma (data e horário atuais das aulas).
// O backend localiza as aulas pelo turmaId + horário; o cliente não envia IDs de aula.
public class AulaTurmaRequest {

    @NotNull(message = "A data da aula é obrigatória")
    private LocalDate data;

    @NotNull(message = "A hora de início é obrigatória")
    private LocalTime horaInicio;

    @NotNull(message = "A hora de fim é obrigatória")
    private LocalTime horaFim;

    public AulaTurmaRequest() {}

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFim() { return horaFim; }
    public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }
}
