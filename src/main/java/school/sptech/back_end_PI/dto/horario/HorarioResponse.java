package school.sptech.back_end_PI.dto.horario;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.LocalTime;

@JsonPropertyOrder({ "id", "diaSemana", "horaInicio", "horaFim" })
public class HorarioResponse {

    private Long id;
    private String diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFim;

    // Construtor Padrão
    public HorarioResponse() {}

    // Construtor Utilitário (facilita na hora do Mapper)
    public HorarioResponse(Long id, String diaSemana, LocalTime horaInicio, LocalTime horaFim) {
        this.id = id;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(LocalTime horaFim) {
        this.horaFim = horaFim;
    }
}