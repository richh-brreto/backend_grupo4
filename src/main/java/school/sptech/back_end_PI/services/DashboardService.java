package school.sptech.back_end_PI.services;

import org.springframework.stereotype.Service;
import school.sptech.back_end_PI.dto.dashboard.DashboardProfessorItem;
import school.sptech.back_end_PI.dto.dashboard.DashboardResponse;
import school.sptech.back_end_PI.entity.Aula;
import school.sptech.back_end_PI.entity.Contrato;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.repository.AulaRepository;
import school.sptech.back_end_PI.repository.ProfessorRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final ProfessorRepository professorRepository;
    private final AulaRepository aulaRepository;

    private static final double DEFAULT_CAPACITY_HOURS = 40.0;

    public DashboardService(ProfessorRepository professorRepository, AulaRepository aulaRepository) {
        this.professorRepository = professorRepository;
        this.aulaRepository = aulaRepository;
    }

    public DashboardResponse montarDashboardProfessores(LocalDate startDate, LocalDate endDate) {
        // se as datas não forem passadas, define o intervalo da semana atual (Segunda a Domingo)
        LocalDate inicio = (startDate != null) ? startDate : LocalDate.now().with(java.time.temporal.WeekFields.of(java.util.Locale.getDefault()).dayOfWeek(), 1);
        LocalDate fim = (endDate != null) ? endDate : inicio.plusDays(6);

        List<Professor> professores = professorRepository.findAll();

        List<Aula> aulasDoPeriodo = aulaRepository.findByDataBetween(inicio, fim);

        Map<Long, List<Aula>> aulasByProfessor = aulasDoPeriodo.stream()
                .filter(aula -> aula.getContrato() != null && aula.getContrato().getProfessor() != null)
                .collect(Collectors.groupingBy(aula -> aula.getContrato().getProfessor().getId()));

        List<DashboardProfessorItem> detalhes = professores.stream().map(p -> {
            long pid = p.getId();

            List<Aula> minhasAulas = aulasByProfessor.getOrDefault(pid, List.of());

            int aulasCount = minhasAulas.size();

            double totalHorasOcupadas = minhasAulas.stream()
                    .filter(a -> a.getHoraInicio() != null && a.getHoraFim() != null)
                    .mapToDouble(a -> ChronoUnit.MINUTES.between(a.getHoraInicio(), a.getHoraFim()) / 60.0)
                    .sum();

            double horasSemanais = 0.0;
            if (p.getHorarios() != null && !p.getHorarios().isEmpty()) {
                horasSemanais = p.getHorarios().stream()
                        .mapToDouble(h -> ChronoUnit.MINUTES.between(h.getHoraInicio(), h.getHoraFim()) / 60.0)
                        .sum();
            } else {
                horasSemanais = DEFAULT_CAPACITY_HOURS;
            }

            // "horasLivres" = Disponibilidade cadastrada - Horas já alocadas em aulas reais
            double horasLivres = Math.max(0.0, horasSemanais - totalHorasOcupadas);

            String status = computeStatus(totalHorasOcupadas, horasSemanais);

            return new DashboardProfessorItem(pid, p.getNome(), aulasCount, round(horasSemanais), round(horasLivres), status);
        }).collect(Collectors.toList());

        int totalProfessores = professores.size();

        int totalTurmas = detalhes.stream().mapToInt(DashboardProfessorItem::getAulasCount).sum();

        DoubleSummaryStatistics stats = detalhes.stream().mapToDouble(DashboardProfessorItem::getHorasLivres).summaryStatistics();
        double totalHorasLivres = stats.getSum();

        int professoresSobrecarregados = (int) detalhes.stream().filter(d -> d.getStatus().equals("Sobrecarregado")).count();

        return new DashboardResponse(
                totalProfessores,
                totalTurmas,
                round(totalHorasLivres),
                professoresSobrecarregados,
                detalhes
        );
    }

    private static String computeStatus(double horasOcupadas, double capacidadeTotal) {
        if (capacidadeTotal == 0) return "Subutilizado";
        if (horasOcupadas > capacidadeTotal) return "Sobrecarregado";
        if (horasOcupadas >= 0.75 * capacidadeTotal) return "Equilibrado";
        return "Subutilizado";
    }

    private static double round(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}