package school.sptech.back_end_PI.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalTime;
import java.util.List;

@JsonPropertyOrder({ "id", "nome", "email", "telefone", "tipo", "horarios" })
public class ProfessorResponse {
    private Integer id;
    private String nome;
    private String email;
    private Integer telefone;
    private List<HorarioProfessorDto> horarios;

    private TipoProfessorResponse tipo;

    public class HorarioProfessorDto {

        private Long id;
        private String diaSemana;
        private LocalTime horaInicio;
        private LocalTime horaFim;

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

    public ProfessorResponse() {}

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getTelefone() { return telefone; }
    public void setTelefone(Integer telefone) { this.telefone = telefone; }

    public TipoProfessorResponse getTipo() { return tipo; }
    public void setTipo(TipoProfessorResponse tipo) { this.tipo = tipo; }

    public List<HorarioProfessorDto> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<HorarioProfessorDto> horarios) {
        this.horarios = horarios;
    }
}