package school.sptech.back_end_PI.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sptech.back_end_PI.dto.dashboard.DashboardProfessorItem;
import school.sptech.back_end_PI.dto.dashboard.DashboardResponse;
import school.sptech.back_end_PI.entity.Contrato;
import school.sptech.back_end_PI.entity.Horario;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.repository.ContratoRepository;
import school.sptech.back_end_PI.repository.ProfessorRepository;

import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class DashboardService {

    private final ProfessorRepository professorRepository;
    private final ContratoRepository contratoRepository;

    public DashboardService(ProfessorRepository professorRepository, ContratoRepository contratoRepository) {
        this.professorRepository = professorRepository;
        this.contratoRepository = contratoRepository;
    }

    // Snapshot semanal e independente de data: capacidade do professor
    // (disponibilidade) x carga contratada (grade recorrente das turmas e dos
    // contratos individuais). As aulas se repetem toda semana, então contar as
    // linhas de aula de uma janela específica distorcia o resultado.
    @Transactional(readOnly = true)
    public DashboardResponse montarDashboardProfessores() {
        List<Professor> professores = professorRepository.findAll();
        Map<Long, Set<Horario>> gradePorProfessor = montarGradeSemanalPorProfessor();

        List<DashboardProfessorItem> detalhes = professores.stream()
                .map(professor -> criarItemProfessor(professor, gradePorProfessor.getOrDefault(professor.getId(), Set.of())))
                .toList();

        return construirRespostaDashboard(professores.size(), detalhes);
    }

    private Map<Long, Set<Horario>> montarGradeSemanalPorProfessor() {
        Map<Long, Set<Horario>> grade = new HashMap<>();

        for (Contrato contrato : contratoRepository.findAll()) {
            Professor professor = resolverProfessorDoContrato(contrato);
            if (professor == null) {
                continue;
            }
            // Set por id de Horario: evita contar o mesmo slot duas vezes quando
            // vários alunos compartilham a grade da mesma turma.
            grade.computeIfAbsent(professor.getId(), id -> new LinkedHashSet<>())
                    .addAll(horariosDaGrade(contrato));
        }

        return grade;
    }

    private Professor resolverProfessorDoContrato(Contrato contrato) {
        if (contrato.getTurma() != null) {
            return contrato.getTurma().getProfessor();
        }
        return contrato.getProfessor();
    }

    private List<Horario> horariosDaGrade(Contrato contrato) {
        List<Horario> horarios = contrato.getTurma() != null
                ? contrato.getTurma().getHorarios()
                : contrato.getHorarios();
        return horarios != null ? horarios : List.of();
    }

    private DashboardProfessorItem criarItemProfessor(Professor professor, Set<Horario> gradeSemanal) {
        double capacidade = somarHoras(professor.getHorarios());
        double horasContratadas = somarHoras(gradeSemanal);
        double horasLivres = Math.max(0.0, capacidade - horasContratadas);
        String status = computeStatus(horasContratadas, capacidade);

        return new DashboardProfessorItem(
                professor.getId(),
                professor.getNome(),
                gradeSemanal.size(),
                round(capacidade),
                round(horasLivres),
                status
        );
    }

    private double somarHoras(Collection<Horario> horarios) {
        if (horarios == null) {
            return 0.0;
        }
        return horarios.stream()
                .filter(h -> h != null && h.getHoraInicio() != null && h.getHoraFim() != null)
                .mapToDouble(h -> ChronoUnit.MINUTES.between(h.getHoraInicio(), h.getHoraFim()) / 60.0)
                .sum();
    }

    private DashboardResponse construirRespostaDashboard(int totalProfessores, List<DashboardProfessorItem> detalhes) {
        int totalAulas = detalhes.stream().mapToInt(DashboardProfessorItem::getAulasCount).sum();
        double totalHorasLivres = detalhes.stream().mapToDouble(DashboardProfessorItem::getHorasLivres).sum();
        int professoresSobrecarregados = (int) detalhes.stream()
                .filter(d -> "Sobrecarregado".equals(d.getStatus()))
                .count();

        return new DashboardResponse(
                totalProfessores,
                totalAulas,
                round(totalHorasLivres),
                professoresSobrecarregados,
                detalhes
        );
    }

    private static String computeStatus(double horasContratadas, double capacidade) {
        if (capacidade <= 0) {
            // Sem disponibilidade cadastrada: se tem grade, está sobrecarregado.
            return horasContratadas > 0 ? "Sobrecarregado" : "Subutilizado";
        }
        if (horasContratadas >= 0.80 * capacidade) {
            return "Sobrecarregado";
        }
        if (horasContratadas >= 0.50 * capacidade) {
            return "Equilibrado";
        }
        return "Subutilizado";
    }

    private static double round(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
