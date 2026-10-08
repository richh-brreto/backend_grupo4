package school.sptech.back_end_PI.dto.csv;

import java.time.Instant;

public record UploadedCsv(String name, String uri, String mimeType, Instant expiraEm) {}
