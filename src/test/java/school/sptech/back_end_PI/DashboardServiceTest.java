package school.sptech.back_end_PI;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.sptech.back_end_PI.dto.dashboard.DashboardResponse;
import school.sptech.back_end_PI.entity.Contrato;
import school.sptech.back_end_PI.entity.Horario;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.entity.Turma;
import school.sptech.back_end_PI.repository.ContratoRepository;
import school.sptech.back_end_PI.repository.ProfessorRepository;
import school.sptech.back_end_PI.services.DashboardService;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class DashboardServiceTest {

    @Mock
    private ProfessorRepository professorRepository;

    @Mock
    private ContratoRepository contratoRepository;

    @InjectMocks
    private DashboardService dashboardService;

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private Horario horario(Long id, LocalTime inicio, LocalTime fim) {
        Horario horario = new Horario();
        horario.setId(id);
        horario.setHoraInicio(inicio);
        horario.setHoraFim(fim);
        return horario;
    }

    private Professor professor(Long id, Horario... horarios) {
        Professor professor = new Professor();
        professor.setId(id);
        professor.setNome("Professor " + id);
        professor.setHorarios(new ArrayList<>(List.of(horarios)));
        return professor;
    }

    private Turma turma(Long id, Professor professor, Horario... horarios) {
        Turma turma = new Turma();
        turma.setId(id);
        turma.setProfessor(professor);
        turma.setHorarios(new ArrayList<>(List.of(horarios)));
        return turma;
    }

    private Contrato contratoGrupo(Turma turma) {
        Contrato contrato = new Contrato();
        contrato.setTurma(turma);
        return contrato;
    }

    private Contrato contratoIndividual(Professor professor, Horario... horarios) {
        Contrato contrato = new Contrato();
        contrato.setProfessor(professor);
        contrato.setHorarios(new ArrayList<>(List.of(horarios)));
        return contrato;
    }

    // -----------------------------------------------------------------------
    // Testes
    // -----------------------------------------------------------------------

    @Nested
    public class MontarDashboardProfessoresTestes {

        @Test
        @DisplayName("Deve retornar dashboard vazio quando não há professores")
        void deveRetornarDashboardVazioSemProfessores() {
            Mockito.when(professorRepository.findAll()).thenReturn(List.of());
            Mockito.when(contratoRepository.findAll()).thenReturn(List.of());

            DashboardResponse response = dashboardService.montarDashboardProfessores();

            Assertions.assertNotNull(response);
            Assertions.assertEquals(0, response.getTotalProfessores());
            Assertions.assertEquals(0, response.getTotalAulas());
            Assertions.assertEquals(0.0, response.getTotalHorasLivres());
            Assertions.assertTrue(response.getDetalhes().isEmpty());
        }

        @Test
        @DisplayName("Deve classificar como Sobrecarregado quando a grade cobre 80% ou mais da disponibilidade")
        void deveClassificarComoSobrecarregado() {
            Professor professor = professor(1L, horario(10L, LocalTime.of(8, 0), LocalTime.of(10, 0))); // 2h
            Turma turma = turma(1L, professor, horario(10L, LocalTime.of(8, 0), LocalTime.of(10, 0))); // 2h

            Mockito.when(professorRepository.findAll()).thenReturn(List.of(professor));
            Mockito.when(contratoRepository.findAll()).thenReturn(List.of(contratoGrupo(turma)));

            DashboardResponse response = dashboardService.montarDashboardProfessores();

            Assertions.assertEquals("Sobrecarregado", response.getDetalhes().get(0).getStatus());
            Assertions.assertEquals(0.0, response.getDetalhes().get(0).getHorasLivres());
            Assertions.assertEquals(1, response.getProfessoresSobrecarregados());
        }

        @Test
        @DisplayName("Deve classificar como Equilibrado quando a grade cobre entre 50% e 79%")
        void deveClassificarComoEquilibrado() {
            Professor professor = professor(1L, horario(10L, LocalTime.of(8, 0), LocalTime.of(12, 0))); // 4h
            Turma turma = turma(1L, professor, horario(10L, LocalTime.of(8, 0), LocalTime.of(10, 0))); // 2h = 50%

            Mockito.when(professorRepository.findAll()).thenReturn(List.of(professor));
            Mockito.when(contratoRepository.findAll()).thenReturn(List.of(contratoGrupo(turma)));

            DashboardResponse response = dashboardService.montarDashboardProfessores();

            Assertions.assertEquals("Equilibrado", response.getDetalhes().get(0).getStatus());
            Assertions.assertEquals(2.0, response.getDetalhes().get(0).getHorasLivres());
        }

        @Test
        @DisplayName("Deve classificar como Subutilizado quando a grade cobre menos de 50%")
        void deveClassificarComoSubutilizado() {
            Professor professor = professor(1L, horario(10L, LocalTime.of(8, 0), LocalTime.of(12, 0))); // 4h
            Turma turma = turma(1L, professor, horario(10L, LocalTime.of(8, 0), LocalTime.of(9, 0))); // 1h = 25%

            Mockito.when(professorRepository.findAll()).thenReturn(List.of(professor));
            Mockito.when(contratoRepository.findAll()).thenReturn(List.of(contratoGrupo(turma)));

            DashboardResponse response = dashboardService.montarDashboardProfessores();

            Assertions.assertEquals("Subutilizado", response.getDetalhes().get(0).getStatus());
            Assertions.assertEquals(3.0, response.getDetalhes().get(0).getHorasLivres());
        }

        @Test
        @DisplayName("Professor sem disponibilidade mas com grade vira Sobrecarregado com 0h livres")
        void deveMarcarSobrecarregadoQuandoSemDisponibilidadeComGrade() {
            Professor professor = professor(1L); // sem horários -> 0h
            Turma turma = turma(1L, professor, horario(10L, LocalTime.of(8, 0), LocalTime.of(9, 0))); // 1h

            Mockito.when(professorRepository.findAll()).thenReturn(List.of(professor));
            Mockito.when(contratoRepository.findAll()).thenReturn(List.of(contratoGrupo(turma)));

            DashboardResponse response = dashboardService.montarDashboardProfessores();

            Assertions.assertEquals(0.0, response.getDetalhes().get(0).getHorasSemanais());
            Assertions.assertEquals(0.0, response.getDetalhes().get(0).getHorasLivres());
            Assertions.assertEquals("Sobrecarregado", response.getDetalhes().get(0).getStatus());
        }

        @Test
        @DisplayName("Professor sem disponibilidade e sem grade fica Subutilizado com 0h")
        void deveMarcarSubutilizadoQuandoSemDisponibilidadeESemGrade() {
            Professor professor = professor(1L);

            Mockito.when(professorRepository.findAll()).thenReturn(List.of(professor));
            Mockito.when(contratoRepository.findAll()).thenReturn(List.of());

            DashboardResponse response = dashboardService.montarDashboardProfessores();

            Assertions.assertEquals(0.0, response.getDetalhes().get(0).getHorasSemanais());
            Assertions.assertEquals("Subutilizado", response.getDetalhes().get(0).getStatus());
        }

        @Test
        @DisplayName("Contrato de grupo usa o professor e a grade da turma")
        void deveUsarProfessorEGradeDaTurmaNoContratoGrupo() {
            Professor professorDaTurma = professor(1L);
            Professor outroProfessor = professor(2L);
            Turma turma = turma(1L, professorDaTurma, horario(10L, LocalTime.of(8, 0), LocalTime.of(9, 0)));

            Contrato contrato = contratoGrupo(turma);
            contrato.setProfessor(outroProfessor); // campo redundante no grupo: deve ser ignorado

            Mockito.when(professorRepository.findAll()).thenReturn(List.of(professorDaTurma, outroProfessor));
            Mockito.when(contratoRepository.findAll()).thenReturn(List.of(contrato));

            DashboardResponse response = dashboardService.montarDashboardProfessores();

            Assertions.assertEquals(1, response.getDetalhes().get(0).getAulasCount());
            Assertions.assertEquals(0, response.getDetalhes().get(1).getAulasCount());
        }

        @Test
        @DisplayName("Contrato individual usa o professor e a grade do próprio contrato")
        void deveUsarProfessorEGradeDoContratoIndividual() {
            Professor professor = professor(1L, horario(5L, LocalTime.of(10, 0), LocalTime.of(11, 0))); // 1h
            Contrato contrato = contratoIndividual(professor, horario(5L, LocalTime.of(10, 0), LocalTime.of(11, 0)));

            Mockito.when(professorRepository.findAll()).thenReturn(List.of(professor));
            Mockito.when(contratoRepository.findAll()).thenReturn(List.of(contrato));

            DashboardResponse response = dashboardService.montarDashboardProfessores();

            Assertions.assertEquals(1, response.getDetalhes().get(0).getAulasCount());
            Assertions.assertEquals("Sobrecarregado", response.getDetalhes().get(0).getStatus());
        }

        @Test
        @DisplayName("Não deve duplicar horas quando vários alunos compartilham a grade da turma")
        void naoDeveDuplicarHorasDeTurmaCompartilhada() {
            Professor professor = professor(1L, horario(10L, LocalTime.of(8, 0), LocalTime.of(10, 0))); // 2h
            Turma turma = turma(1L, professor, horario(10L, LocalTime.of(8, 0), LocalTime.of(10, 0))); // 2h

            Mockito.when(professorRepository.findAll()).thenReturn(List.of(professor));
            Mockito.when(contratoRepository.findAll())
                    .thenReturn(List.of(contratoGrupo(turma), contratoGrupo(turma)));

            DashboardResponse response = dashboardService.montarDashboardProfessores();

            Assertions.assertEquals(1, response.getDetalhes().get(0).getAulasCount());
            Assertions.assertEquals(0.0, response.getDetalhes().get(0).getHorasLivres());
            Assertions.assertEquals("Sobrecarregado", response.getDetalhes().get(0).getStatus());
        }

        @Test
        @DisplayName("Ignora contrato sem turma e sem professor")
        void deveIgnorarContratoSemProfessorESemTurma() {
            Professor professor = professor(1L);

            Mockito.when(professorRepository.findAll()).thenReturn(List.of(professor));
            Mockito.when(contratoRepository.findAll()).thenReturn(List.of(new Contrato()));

            DashboardResponse response = dashboardService.montarDashboardProfessores();

            Assertions.assertEquals(0, response.getDetalhes().get(0).getAulasCount());
            Assertions.assertEquals(0, response.getTotalAulas());
        }

        @Test
        @DisplayName("Horário da grade sem hora de início/fim conta como slot mas não soma horas")
        void deveContarSlotSemHoras() {
            Professor professor = professor(1L, horario(10L, LocalTime.of(8, 0), LocalTime.of(12, 0))); // 4h
            Horario semHora = new Horario();
            semHora.setId(11L);
            Turma turma = turma(1L, professor, semHora);

            Mockito.when(professorRepository.findAll()).thenReturn(List.of(professor));
            Mockito.when(contratoRepository.findAll()).thenReturn(List.of(contratoGrupo(turma)));

            DashboardResponse response = dashboardService.montarDashboardProfessores();

            Assertions.assertEquals(1, response.getDetalhes().get(0).getAulasCount());
            Assertions.assertEquals(4.0, response.getDetalhes().get(0).getHorasLivres());
            Assertions.assertEquals("Subutilizado", response.getDetalhes().get(0).getStatus());
        }

        @Test
        @DisplayName("Deve somar horas livres de múltiplos professores")
        void deveCalcularHorasLivresComMultiplosProfessores() {
            Professor prof1 = professor(1L, horario(10L, LocalTime.of(8, 0), LocalTime.of(10, 0))); // 2h
            Professor prof2 = professor(2L, horario(11L, LocalTime.of(14, 0), LocalTime.of(16, 0))); // 2h

            Mockito.when(professorRepository.findAll()).thenReturn(List.of(prof1, prof2));
            Mockito.when(contratoRepository.findAll()).thenReturn(List.of());

            DashboardResponse response = dashboardService.montarDashboardProfessores();

            Assertions.assertEquals(2, response.getTotalProfessores());
            Assertions.assertEquals(4.0, response.getTotalHorasLivres());
        }
    }
}
