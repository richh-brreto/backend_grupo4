package school.sptech.back_end_PI.dto.contrato;

import java.time.LocalDate;

public record ContratoVencimentoMensagem(
        Long contratoId,
        String tipo,
        LocalDate dataInicio,
        LocalDate dataFim,
        long diasParaVencer,
        String alunoNome,
        String alunoEmail,
        String professorNome,
        String turmaNome
) {
}
