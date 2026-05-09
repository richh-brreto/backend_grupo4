package school.sptech.back_end_PI.dto;

import java.time.LocalTime;
import java.util.List;

public class AlunoResponse {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String nivel;
    private List<HorarioAlunoDto> horarios;


    public class HorarioAlunoDto {

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


    public AlunoResponse() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public List<HorarioAlunoDto> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<HorarioAlunoDto> horarios) {
        this.horarios = horarios;
    }
}