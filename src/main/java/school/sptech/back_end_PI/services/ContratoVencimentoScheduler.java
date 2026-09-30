package school.sptech.back_end_PI.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.sptech.back_end_PI.dto.contrato.ContratoVencimentoMensagem;
import school.sptech.back_end_PI.entity.Contrato;
import school.sptech.back_end_PI.repository.ContratoRepository;

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

    @Value("${contratos.vencimento.dias-antecedencia:30}")
    private int diasAntecedencia;

    @Value("${contratos.vencimento.retry-minutos:60}")
    private int minutosParaReabrir;

    @Scheduled(cron = "${contratos.vencimento.cron:0 */2 * * * *}")
    public void publicarContratosVencendo() {
        LocalDateTime agora = LocalDateTime.now();
        LocalDate hoje = agora.toLocalDate();
        LocalDate limite = hoje.plusDays(diasAntecedencia);
        // Reserva mais antiga que isso é considerada abandonada (instância caiu no meio da publicação)
        LocalDateTime limiteReabertura = agora.minusMinutes(minutosParaReabrir);

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
                publisher.publicar(toMensagem(contrato, hoje));
                publicados++;
            } catch (Exception e) {
                contratoRepository.liberarNotificacaoVencimento(contrato.getId());
                log.error("Falha ao publicar o contrato {} na fila de vencimento. Reserva liberada.", contrato.getId(), e);
            }
        }

        log.info("Job de vencimento finalizado: {} publicado(s), {} já tratados por outra instância, {} candidato(s).",
                publicados, jaReservadosPorOutraInstancia, candidatos.size());
    }

    private ContratoVencimentoMensagem toMensagem(Contrato contrato, LocalDate hoje) {
        return new ContratoVencimentoMensagem(
                contrato.getId(),
                contrato.getTipo(),
                contrato.getDataInicio(),
                contrato.getDataFim(),
                ChronoUnit.DAYS.between(hoje, contrato.getDataFim()),
                contrato.getAluno() != null ? contrato.getAluno().getNome() : null,
                contrato.getAluno() != null ? contrato.getAluno().getEmail() : null,
                contrato.getProfessor() != null ? contrato.getProfessor().getNome() : null,
                contrato.getTurma() != null ? contrato.getTurma().getNome() : null
        );
    }
}
