package school.sptech.back_end_PI.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.sptech.back_end_PI.dto.csv.CsvSnapshot;
import school.sptech.back_end_PI.services.relatorio.RelatorioService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@RestController
@RequestMapping("/relatorio")
public class RelatorioController {

    private static final String BOM = "\uFEFF";

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/csv")
    @PreAuthorize("hasAuthority('PERM_TELA_DASHBOARD')")
    public ResponseEntity<byte[]> baixarCsv(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        CsvSnapshot snapshot = (startDate == null && endDate == null)
                ? relatorioService.extrairCsvPadrao()
                : relatorioService.extrairCsv(startDate, endDate);

        byte[] corpo = (BOM + snapshot.csv()).getBytes(StandardCharsets.UTF_8);
        String nomeArquivo = "relatorio-dashboard-" + LocalDate.now() + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + "\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(corpo);
    }
}
