package school.sptech.back_end_PI.dto.contrato;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.List;

public class ContratoRequest {

    @NotNull(message = "A data de início é obrigatória")
    private LocalDate dataInicio;

    @NotNull(message = "A data de fim é obrigatória")
    private LocalDate dataFim;

    @NotBlank(message = "O tipo do contrato é obrigatório")
    @Pattern(regexp = "(?i)(Grupo|Individual)", message = "O tipo deve ser 'Grupo' ou 'Individual'")
    private String tipo;

    @NotNull(message = "O ID do aluno é obrigatório")
    private Long alunoId;

    // Não usamos @NotNull aqui, pois será nulo se o tipo for 'Individual'
    private Long turmaId;

    // Não usamos @NotNull aqui, pois será nulo se o tipo for 'Grupo'
    private Long professorId;

    // Lista de IDs de horários para a tabela auxiliar (obrigatória apenas se individual)
    private List<Long> horariosIds;

    // Construtor Padrão
    public ContratoRequest() {
    }

    // Getters e Setters
    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Long getAlunoId() {
        return alunoId;
    }

    public void setAlunoId(Long alunoId) {
        this.alunoId = alunoId;
    }

    public Long getTurmaId() {
        return turmaId;
    }

    public void setTurmaId(Long turmaId) {
        this.turmaId = turmaId;
    }

    public Long getProfessorId() {
        return professorId;
    }

    public void setProfessorId(Long professorId) {
        this.professorId = professorId;
    }

    public List<Long> getHorariosIds() {
        return horariosIds;
    }

    public void setHorariosIds(List<Long> horariosIds) {
        this.horariosIds = horariosIds;
    }
}