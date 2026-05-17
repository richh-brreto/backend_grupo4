package school.sptech.back_end_PI.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "aula")
public class Aula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aula")
    private Integer id;

    @Column(name = "data", nullable = false)
    private LocalDate data;

    @Column(name = "presenca")
    private Boolean presenca;

    @Column(name = "status", length = 45)
    private String status;

    @Column(name = "hora_inicio")
    private LocalTime horaInicio;

    @Column(name = "hora_fim")
    private LocalTime horaFim;

    @ManyToOne
    @JoinColumn(
            name = "contrato_id_contrato",
            nullable = false
    )
    private Contrato contrato;

    public Aula() {
    }

    public Aula(
            Integer id,
            LocalDate data,
            Boolean presenca,
            String status,
            LocalTime horaInicio,
            LocalTime horaFim,
            Contrato contrato
    ) {
        this.id = id;
        this.data = data;
        this.presenca = presenca;
        this.status = status;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.contrato = contrato;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Boolean getPresenca() {
        return presenca;
    }

    public void setPresenca(Boolean presenca) {
        this.presenca = presenca;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }
}