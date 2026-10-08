package school.sptech.back_end_PI.dto.csv;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public record ContextoCsv(String fileName, String fileUri, String mimeType,
                          LocalDate periodoInicio, LocalDate periodoFim,
                          LocalDateTime enviadoEm, LocalDateTime expiraEm) {
    public boolean valido() {
        return LocalDateTime.now().isBefore(expiraEm);
    }

    public static ContextoCsv de(UploadedCsv arquivo, CsvSnapshot snapshot) {
        Instant expiraServer = arquivo.expiraEm();
        LocalDateTime expiraEm = expiraServer != null
                ? LocalDateTime.ofInstant(expiraServer, ZoneId.systemDefault()).minusHours(1)
                : LocalDateTime.now().plusHours(47);
        return new ContextoCsv(arquivo.name(), arquivo.uri(), arquivo.mimeType(),
                snapshot.inicio(), snapshot.fim(), LocalDateTime.now(), expiraEm);
    }
}
