package school.sptech.back_end_PI.dto.csv;

import java.time.LocalDate;

public record CsvSnapshot(String csv, LocalDate inicio, LocalDate fim) {}
