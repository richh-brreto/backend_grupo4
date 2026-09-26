package school.sptech.back_end_PI.dto.professor;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.LocalTime;
import java.util.List;

@JsonPropertyOrder({ "id", "nome", "email", "telefone", "senhaDefinida", "codigoAcesso", "ativo", "horarios", "permissoes" })
public class ProfessorResponse {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private Boolean senhaDefinida;
    private String codigoAcesso;
    private Boolean ativo; // Campo adicionado para o JSON
    private List<HorarioProfessorDto> horarios;
    private List<String> permissoes;

    public static class HorarioProfessorDto {
        private Long id;
        private String diaSemana;
        private LocalTime horaInicio;
        private LocalTime horaFim;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getDiaSemana() { return diaSemana; }
        public void setDiaSemana(String diaSemana) { this.diaSemana = diaSemana; }
        public LocalTime getHoraInicio() { return horaInicio; }
        public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
        public LocalTime getHoraFim() { return horaFim; }
        public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }
    }

    public ProfessorResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public Boolean getSenhaDefinida() { return senhaDefinida; }
    public void setSenhaDefinida(Boolean senhaDefinida) { this.senhaDefinida = senhaDefinida; }
    public String getCodigoAcesso() { return codigoAcesso; }
    public void setCodigoAcesso(String codigoAcesso) { this.codigoAcesso = codigoAcesso; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    public List<HorarioProfessorDto> getHorarios() { return horarios; }
    public void setHorarios(List<HorarioProfessorDto> horarios) { this.horarios = horarios; }
    public List<String> getPermissoes() { return permissoes; }
    public void setPermissoes(List<String> permissoes) { this.permissoes = permissoes; }
}