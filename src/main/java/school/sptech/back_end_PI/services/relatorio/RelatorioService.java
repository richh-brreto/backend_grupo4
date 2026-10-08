package school.sptech.back_end_PI.services.relatorio;

import org.springframework.stereotype.Service;
import school.sptech.back_end_PI.dto.csv.CsvSnapshot;
import school.sptech.back_end_PI.dto.dashboard.DashboardProfessorItem;
import school.sptech.back_end_PI.dto.dashboard.DashboardResponse;
import school.sptech.back_end_PI.services.DashboardService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

@Service
public class RelatorioService {

    private final DashboardService dashboardService;

    public RelatorioService(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    private LocalDate resolverDataInicio(LocalDate startDate) {
        if (startDate != null) {
            return startDate;
        }
        return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    private LocalDate resolverDataFim(LocalDate endDate) {
        return endDate != null ? endDate : LocalDate.now();
    }

    private String montarCsv(DashboardResponse dashboard, LocalDate inicio, LocalDate fim) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StringBuilder sb = new StringBuilder();

        sb.append("Relatorio da Dashboard;Periodo;").append(inicio.format(fmt))
          .append(" a ").append(fim.format(fmt)).append("\n\n");

        sb.append("Total de Professores;").append(dashboard.getTotalProfessores()).append("\n");
        sb.append("Total de Aulas;").append(dashboard.getTotalAulas()).append("\n");
        sb.append("Total de Horas Livres;").append(fmtNumero(dashboard.getTotalHorasLivres())).append("\n");
        sb.append("Professores Sobrecarregados;").append(dashboard.getProfessoresSobrecarregados()).append("\n\n");

        sb.append("Professor;Aulas no Periodo;Horas Semanais;Horas Livres;Status\n");
        for (DashboardProfessorItem p : dashboard.getDetalhes()) {
            sb.append(escapar(p.getNome())).append(";")
              .append(p.getAulasCount()).append(";")
              .append(fmtNumero(p.getHorasSemanais())).append(";")
              .append(fmtNumero(p.getHorasLivres())).append(";")
              .append(escapar(p.getStatus())).append("\n");
        }

        return sb.toString();
    }

    private String fmtNumero(Double valor) {
        if (valor == null) {
            return "";
        }
        return String.format(java.util.Locale.forLanguageTag("pt-BR"), "%.1f", valor);
    }

    private String escapar(String valor) {
        if (valor == null) {
            return "";
        }
        if (valor.contains(";") || valor.contains("\"") || valor.contains("\n")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }

    public CsvSnapshot extrairCsvPadrao() {
        return extrairCsv(resolverDataInicio(null), resolverDataFim(null));
    }

    public CsvSnapshot extrairCsv(LocalDate inicio, LocalDate fim) {
        DashboardResponse dashboard = dashboardService.montarDashboardProfessores(inicio, fim);
        return new CsvSnapshot(montarCsv(dashboard, inicio, fim), inicio, fim);
    }
}

