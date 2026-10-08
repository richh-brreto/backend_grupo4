package school.sptech.back_end_PI.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.sptech.back_end_PI.dto.csv.UploadedCsv;
import school.sptech.back_end_PI.services.relatorio.RelatorioFileContext;
import school.sptech.back_end_PI.services.relatorio.RelatorioService;
import school.sptech.back_end_PI.services.gemini.GeminiFilesService;
import school.sptech.back_end_PI.services.gemini.GeminiClientProvider;
import school.sptech.back_end_PI.dto.csv.CsvSnapshot;
import school.sptech.back_end_PI.dto.csv.ContextoCsv;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class RelatorioFilesScheduler {
    private static final Logger log = LoggerFactory.getLogger(RelatorioFilesScheduler.class);
    private final AtomicBoolean rodando = new AtomicBoolean(false);

    private final RelatorioService relatorioService;
    private final GeminiFilesService filesService;
    private final RelatorioFileContext context;
    private final GeminiClientProvider clientProvider;

    public RelatorioFilesScheduler(RelatorioService relatorioService, GeminiFilesService filesService,
            RelatorioFileContext context, GeminiClientProvider clientProvider) {
        this.relatorioService = relatorioService;
        this.filesService = filesService;
        this.context = context;
        this.clientProvider = clientProvider;
    }

    @Scheduled(cron = "${relatorio.files.cron:0 5 0 * * *}", zone = "${relatorio.files.zone:America/Sao_Paulo}")
    public void renovarCsv() {
        executarRenovacao();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void aoIniciar() {
        executarRenovacao();
    }

    void executarRenovacao() {
        if (!rodando.compareAndSet(false, true)) {
            return;
        }
        try {
            if (clientProvider.chaveConfigurada()) {
                CsvSnapshot snapshot = relatorioService.extrairCsvPadrao();
                UploadedCsv novo = filesService.uploadCsv(
                        snapshot.csv(), "relatorio-dashboard-" + LocalDate.now());

                ContextoCsv antigo = context.obterAtual().orElse(null);
                ContextoCsv novoContexto = ContextoCsv.de(novo, snapshot);
                context.atualizar(novoContexto);

                if (antigo != null && !antigo.fileName().equals(novo.name())) {
                    try {
                        filesService.delete(antigo.fileName());
                    } catch (Exception e) {
                        log.warn("Não deletou arquivo antigo: {}", e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Falha ao renovar CSV na Files API (banco indisponível?): {}", e.getMessage());
        } finally {
            rodando.set(false);
        }
    }
}