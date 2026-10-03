package school.sptech.back_end_PI.dto.contrato;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Corpo único da mensagem publicada em contratos.vencimento.queue.
 * Tudo o que o serviço de messaging precisa está aqui dentro: contrato, aluno,
 * professor, turma e o destinatário (professor administrador).
 *
 * senha e codigoAcesso NÃO entram: são credenciais, não dados de notificação.
 */
public record ContratoVencimentoMensagem(
        ContratoDados contrato,
        AlunoDados aluno,
        ProfessorDados professor,
        TurmaDados turma,
        ProfessorDados destinatario,
        long diasParaVencer,
        LocalDate dataAviso,
        LocalDateTime publicadoEm
) {

    // Contrato do tipo "Grupo" não tem professor; o do tipo "Individual" não tem turma.
    // Quem não existir no banco vai como null.

    public record ContratoDados(
            Long id,
            String tipo,
            LocalDate dataInicio,
            LocalDate dataFim
    ) {
    }

    public record AlunoDados(
            Long id,
            String nome,
            String email,
            String telefone,
            String nivel,
            Boolean ativo
    ) {
    }

    public record ProfessorDados(
            Long id,
            String nome,
            String email,
            String telefone,
            Boolean ativo
    ) {
    }

    public record TurmaDados(
            Long id,
            String nome,
            String nivel,
            Integer limiteAlunos,
            String tipo,
            Long professorId
    ) {
    }
}