package school.sptech.back_end_PI.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.sptech.back_end_PI.dto.contrato.ContratoVencimentoMensagem;
import school.sptech.back_end_PI.dto.contrato.ContratoVencimentoMensagem.AlunoDados;
import school.sptech.back_end_PI.dto.contrato.ContratoVencimentoMensagem.ContratoDados;
import school.sptech.back_end_PI.dto.contrato.ContratoVencimentoMensagem.ProfessorDados;
import school.sptech.back_end_PI.dto.contrato.ContratoVencimentoMensagem.TurmaDados;
import school.sptech.back_end_PI.entity.Aluno;
import school.sptech.back_end_PI.entity.Contrato;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.entity.Turma;
import school.sptech.back_end_PI.repository.ContratoRepository;
import school.sptech.back_end_PI.repository.ProfessorRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Job que lê os contratos que vencem na janela configurada e publica o aviso na fila do RabbitMQ.
 *
 * Seguro para réplicas: cada contrato passa por uma RESERVA ATÔMICA (UPDATE condicional) antes de ser
 * publicado. Só a instância que conseguir marcar a linha publica; as demais recebem 0 linhas afetadas e pulam.
 * Não há @Transactional no método de propósito — a publicação (I/O de rede) acontece fora de transação,
 * para não segurar transação/lock do banco durante a chamada ao broker.
 */
@Component
public class ContratoVencimentoScheduler {

    private static final Logger log = LoggerFactory.getLogger(ContratoVencimentoScheduler.class);

    @Autowired
    private ContratoRepository contratoRepository;

    @Autowired
    private ContratoVencimentoPublisher publisher;

    @Autowired
    private ProfessorRepository professorRepository;

    @Value("${contratos.vencimento.dias-antecedencia:30}")
    private int diasAntecedencia;

    @Value("${contratos.vencimento.retry-minutos:60}")
    private int minutosParaReabrir;

    // Professor administrador: é quem recebe o aviso (bloco "destinatario" da mensagem)
    @Value("${contratos.vencimento.professor-administrador-id:1}")
    private Long idProfessorAdministrador;

    @Scheduled(cron = "${contratos.vencimento.cron:0 */2 * * * *}")
    public void publicarContratosVencendo() {
        LocalDateTime agora = LocalDateTime.now();
        LocalDate hoje = agora.toLocalDate();
        LocalDate limite = hoje.plusDays(diasAntecedencia);
        // Reserva mais antiga que isso é considerada abandonada (instância caiu no meio da publicação)
        LocalDateTime limiteReabertura = agora.minusMinutes(minutosParaReabrir);

        // Sem o administrador não há para quem entregar o aviso, então nada é publicado neste ciclo
        Professor administrador = professorRepository.findById(idProfessorAdministrador).orElse(null);
        if (administrador == null) {
            log.error("Professor administrador (id {}) não encontrado. Nenhum aviso de vencimento será publicado neste ciclo.",
                    idProfessorAdministrador);
            return;
        }

        List<Contrato> candidatos = contratoRepository
                .findPendentesNotificacaoVencimento(hoje, limite, limiteReabertura);
        if (candidatos.isEmpty()) {
            log.info("Nenhum contrato pendente de notificação entre {} e {}.", hoje, limite);
            return;
        }

        int publicados = 0;
        int jaReservadosPorOutraInstancia = 0;
        for (Contrato contrato : candidatos) {
            int linhas = contratoRepository
                    .reservarNotificacaoVencimento(contrato.getId(), agora, limiteReabertura);
            if (linhas == 0) {
                jaReservadosPorOutraInstancia++;
                continue;
            }
            try {
                publisher.publicar(toMensagem(contrato, hoje, agora, administrador));
                publicados++;
            } catch (Exception e) {
                contratoRepository.liberarNotificacaoVencimento(contrato.getId());
                log.error("Falha ao publicar o contrato {} na fila de vencimento. Reserva liberada.", contrato.getId(), e);
            }
        }

        log.info("Job de vencimento finalizado: {} publicado(s), {} já tratados por outra instância, {} candidato(s).",
                publicados, jaReservadosPorOutraInstancia, candidatos.size());
    }

    private ContratoVencimentoMensagem toMensagem(Contrato contrato, LocalDate hoje,
                                                  LocalDateTime publicadoEm, Professor administrador) {
        return new ContratoVencimentoMensagem(
                toContratoDados(contrato),
                toAlunoDados(contrato.getAluno()),
                toProfessorDados(contrato.getProfessor()),
                toTurmaDados(contrato.getTurma()),
                toProfessorDados(administrador),
                ChronoUnit.DAYS.between(hoje, contrato.getDataFim()),
                hoje,
                publicadoEm
        );
    }

    private ContratoDados toContratoDados(Contrato contrato) {
        return new ContratoDados(
                contrato.getId(),
                contrato.getTipo(),
                contrato.getDataInicio(),
                contrato.getDataFim()
        );
    }

    private AlunoDados toAlunoDados(Aluno aluno) {
        if (aluno == null) return null;

        return new AlunoDados(
                aluno.getId(),
                aluno.getNome(),
                aluno.getEmail(),
                aluno.getTelefone(),
                aluno.getNivel(),
                aluno.getAtivo()
        );
    }

    private ProfessorDados toProfessorDados(Professor professor) {
        if (professor == null) return null;

        return new ProfessorDados(
                professor.getId(),
                professor.getNome(),
                professor.getEmail(),
                professor.getTelefone(),
                professor.getAtivo()
        );
    }

    private TurmaDados toTurmaDados(Turma turma) {
        if (turma == null) return null;

        return new TurmaDados(
                turma.getId(),
                turma.getNome(),
                turma.getNivel(),
                turma.getLimiteAlunos(),
                turma.getTipo(),
                turma.getProfessor() != null ? turma.getProfessor().getId() : null
        );
    }
}
