package school.sptech.back_end_PI.dto.aula;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class PresencaAulaTurmaRequest extends AulaTurmaRequest {

    // Alunos ausentes; os demais alunos da aula recebem presença
    @NotNull(message = "A lista de alunos ausentes é obrigatória (pode ser vazia)")
    @Size(max = 200, message = "A lista de alunos ausentes é grande demais")
    private List<@NotNull Long> alunosAusentesIds;

    public PresencaAulaTurmaRequest() {}

    public List<Long> getAlunosAusentesIds() { return alunosAusentesIds; }
    public void setAlunosAusentesIds(List<Long> alunosAusentesIds) { this.alunosAusentesIds = alunosAusentesIds; }
}
