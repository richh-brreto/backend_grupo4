package school.sptech.back_end_PI.services;

import org.springframework.stereotype.Service;
import school.sptech.back_end_PI.dto.dashboard.DashboardProfessorItem;
import school.sptech.back_end_PI.dto.dashboard.DashboardResponse;
import school.sptech.back_end_PI.entity.Contrato;
import school.sptech.back_end_PI.entity.Horario;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.entity.Turma;
import school.sptech.back_end_PI.repository.ContratoRepository;
import school.sptech.back_end_PI.repository.ProfessorRepository;
import school.sptech.back_end_PI.repository.TurmaRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final ProfessorRepository professorRepository;
    private final TurmaRepository turmaRepository;
    private final ContratoRepository contratoRepository;

    // Default weekly capacity per professor (hours). Can be replaced later by DB column.
    private static final double DEFAULT_CAPACITY_HOURS = 40.0;

    public DashboardService(ProfessorRepository professorRepository, TurmaRepository turmaRepository, ContratoRepository contratoRepository) {
        this.professorRepository = professorRepository;
        this.turmaRepository = turmaRepository;
        this.contratoRepository = contratoRepository;
    }

    public DashboardResponse montarDashboardProfessores(LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();

        boolean useDateFilter = startDate != null && endDate != null;

        List<Professor> professores = professorRepository.findAll();

        List<Long> professorIds = professores.stream().map(Professor::getId).collect(Collectors.toList());

        // Load turmas assigned to these professors in bulk
        List<Turma> turmas = professorIds.isEmpty() ? List.of() : turmaRepository.findByProfessorIdIn(professorIds);

        // Map turmas by professor id
        Map<Long, List<Turma>> turmasByProfessor = turmas.stream()
                .filter(t -> t.getProfessor() != null)
                .collect(Collectors.groupingBy(t -> t.getProfessor().getId()));

        // Load contratos for these professors in bulk
        List<Contrato> contratosProf = professorIds.isEmpty() ? List.of() : contratoRepository.findByProfessorIdIn(professorIds);

        // Also load contratos for the turmas (group contracts) to determine if a turma is active in the period
        List<Long> turmaIds = turmas.stream().map(Turma::getId).collect(Collectors.toList());
        List<Contrato> contratosTurmas = turmaIds.isEmpty() ? List.of() : contratoRepository.findByTurmaIdIn(turmaIds);

        // Group contracts for quick lookup
        Map<Long, List<Contrato>> contratosByProfessor = contratosProf.stream().collect(Collectors.groupingBy(c -> c.getProfessor() != null ? c.getProfessor().getId() : -1L));
        Map<Long, List<Contrato>> contratosByTurma = contratosTurmas.stream().collect(Collectors.groupingBy(c -> c.getTurma() != null ? c.getTurma().getId() : -1L));

        List<DashboardProfessorItem> detalhes = professores.stream().map(p -> {
            long pid = p.getId();

            // turmas assigned
            List<Turma> minhasTurmas = turmasByProfessor.getOrDefault(pid, List.of());

            // Determine which turmas actually contribute based on date filter / active contracts
            double horasFromTurmas = 0.0;
            for (Turma t : minhasTurmas) {
                List<Contrato> contratosDaTurma = contratosByTurma.getOrDefault(t.getId(), List.of());
                boolean includeTurma = false;
                if (useDateFilter) {
                    for (Contrato c : contratosDaTurma) {
                        if (overlaps(c.getDataInicio(), c.getDataFim(), startDate, endDate)) {
                            includeTurma = true;
                            break;
                        }
                    }
                } else {
                    // default behavior: include turma if any contrato has dataFim >= today
                    for (Contrato c : contratosDaTurma) {
                        if (!c.getDataFim().isBefore(today)) {
                            includeTurma = true;
                            break;
                        }
                    }
                }

                if (includeTurma && t.getHorarios() != null) {
                    horasFromTurmas += horasFromHorarios(t.getHorarios());
                }
            }

            // Individual contracts for this professor
            double horasFromContratosIndividuais = 0.0;
            List<Contrato> meusContratos = contratosByProfessor.getOrDefault(pid, List.of()).stream()
                    .filter(c -> c.getProfessor() != null && c.getProfessor().getId().equals(pid))
                    .collect(Collectors.toList());

            for (Contrato c : meusContratos) {
                boolean includeContrato = false;
                if (useDateFilter) {
                    if (overlaps(c.getDataInicio(), c.getDataFim(), startDate, endDate)) includeContrato = true;
                } else {
                    if (!c.getDataFim().isBefore(today)) includeContrato = true;
                }

                if (includeContrato && c.getHorarios() != null) {
                    horasFromContratosIndividuais += horasFromHorarios(c.getHorarios());
                }
            }

            double horasSemanais = horasFromTurmas + horasFromContratosIndividuais;
            double capacity = DEFAULT_CAPACITY_HOURS;
            double horasLivres = Math.max(0.0, capacity - horasSemanais);

            String status = computeStatus(horasSemanais, capacity);

            return new DashboardProfessorItem(pid, p.getNome(), minhasTurmas.size(), round(horasSemanais), round(horasLivres), status);
        }).collect(Collectors.toList());

        // Totals
        int totalProfessores = professores.size();
        int totalTurmas = turmaRepository.findAll().size();
        DoubleSummaryStatistics stats = detalhes.stream().mapToDouble(DashboardProfessorItem::getHorasLivres).summaryStatistics();
        double totalHorasLivres = stats.getSum();
        int sobrecarregados = (int) detalhes.stream().filter(d -> d.getHorasSemanais() > DEFAULT_CAPACITY_HOURS).count();

        return new DashboardResponse(totalProfessores, totalTurmas, round(totalHorasLivres), sobrecarregados, detalhes);
    }

    private static boolean overlaps(LocalDate aStart, LocalDate aEnd, LocalDate bStart, LocalDate bEnd) {
        return (aStart == null || bEnd == null || !aStart.isAfter(bEnd)) && (aEnd == null || bStart == null || !aEnd.isBefore(bStart));
    }

    private static double horasFromHorarios(List<Horario> horarios) {
        return horarios.stream()
                .mapToDouble(h -> ChronoUnit.MINUTES.between(h.getHoraInicio(), h.getHoraFim()) / 60.0)
                .sum();
    }

    private static String computeStatus(double horas, double capacity) {
        if (horas > capacity) return "Sobrecarregado";
        if (horas >= 0.75 * capacity) return "Equilibrado";
        return "Subutilizado";
    }

    private static double round(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}

