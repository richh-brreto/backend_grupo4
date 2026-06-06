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
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
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
        LocalDate inicio = mapearDataInicio(startDate);
        LocalDate fim = mapearDataFim(endDate, inicio);

        List<Professor> professores = professorRepository.findAll();
        List<Aula> aulasDoPeriodo = aulaRepository.findByDataBetween(inicio, fim);

        Map<Long, List<Aula>> aulasPorProfessor = agruparAulasPorProfessor(aulasDoPeriodo);
        List<DashboardProfessorItem> detalhes = construirDetalhesProfessores(professores, aulasPorProfessor);

        return construirRespostaDashboard(professores.size(), detalhes);
    }

    private LocalDate mapearDataInicio(LocalDate startDate) {
        if (startDate != null) {
            return startDate;
        }
        return LocalDate.now().with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);
    }

    private LocalDate mapearDataFim(LocalDate endDate, LocalDate inicio) {
        if (endDate != null) {
            return endDate;
        }
        return inicio.plusDays(6);
    }

    private Map<Long, List<Aula>> agruparAulasPorProfessor(List<Aula> aulas) {
        return aulas.stream()
                .filter(aula -> obterProfessorDaAula(aula) != null)
                .collect(Collectors.groupingBy(aula -> obterProfessorDaAula(aula).getId()));
    }

    private Professor obterProfessorDaAula(Aula aula) {
        if (aula == null || aula.getContrato() == null) {
            return null;
        }
        Contrato contrato = aula.getContrato();
        if (contrato.getTurma() != null) {
            return contrato.getTurma().getProfessor();
        }
        return contrato.getProfessor();
    }

    private List<DashboardProfessorItem> construirDetalhesProfessores(List<Professor> professores, Map<Long, List<Aula>> aulasPorProfessor) {
        return professores.stream()
                .map(professor -> criarItemProfessor(professor, aulasPorProfessor.getOrDefault(professor.getId(), List.of())))
                .collect(Collectors.toList());
    }

    private DashboardProfessorItem criarItemProfessor(Professor professor, List<Aula> aulas) {
        double horasSemanais = calcularHorasSemanaisDisponiveis(professor);
        double totalHorasOcupadas = calcularTotalHorasOcupadas(aulas);
        double horasLivres = Math.max(0.0, horasSemanais - totalHorasOcupadas);
        String status = computeStatus(totalHorasOcupadas, horasSemanais);

        return new DashboardProfessorItem(
                professor.getId(),
                professor.getNome(),
                aulas.size(),
                round(horasSemanais),
                round(horasLivres),
                status
        );
    }

    private double calcularHorasSemanaisDisponiveis(Professor professor) {
        if (professor.getHorarios() == null || professor.getHorarios().isEmpty()) {
            return DEFAULT_CAPACITY_HOURS;
        }
        return professor.getHorarios().stream()
                .mapToDouble(h -> ChronoUnit.MINUTES.between(h.getHoraInicio(), h.getHoraFim()) / 60.0)
                .sum();
    }

    private double calcularTotalHorasOcupadas(List<Aula> aulas) {
        return aulas.stream()
                .filter(a -> a.getHoraInicio() != null && a.getHoraFim() != null)
                .mapToDouble(a -> ChronoUnit.MINUTES.between(a.getHoraInicio(), a.getHoraFim()) / 60.0)
                .sum();
    }

    private DashboardResponse construirRespostaDashboard(int totalProfessores, List<DashboardProfessorItem> detalhes) {
        int totalAulasGeral = detalhes.stream().mapToInt(DashboardProfessorItem::getAulasCount).sum();
        double totalHorasLivres = detalhes.stream().mapToDouble(DashboardProfessorItem::getHorasLivres).sum();
        int professoresSobrecarregados = (int) detalhes.stream().filter(d -> d.getStatus().equals("Sobrecarregado")).count();

        return new DashboardResponse(
                totalProfessores,
                totalAulasGeral,
                round(totalHorasLivres),
                professoresSobrecarregados,
                detalhes
        );
    }

    private static String computeStatus(double horasOcupadas, double capacidadeTotal) {
        if (capacidadeTotal == 0) {
            return "Subutilizado";
        }
        if (horasOcupadas >= 0.80 * capacidadeTotal) {
            return "Sobrecarregado";
        }
        if (horasOcupadas >= 0.50 * capacidadeTotal) {
            return "Equilibrado";
        }
        return "Subutilizado";
    }

    private static double round(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}