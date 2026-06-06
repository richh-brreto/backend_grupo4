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
import school.sptech.back_end_PI.entity.*;
import school.sptech.back_end_PI.repository.AulaRepository;
import school.sptech.back_end_PI.repository.ProfessorRepository;
import school.sptech.back_end_PI.services.DashboardService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class DashboardServiceTest {

    @Mock
    private ProfessorRepository professorRepository;

    @Mock
    private AulaRepository aulaRepository;

    @InjectMocks
    private DashboardService dashboardService;

    // -----------------------------------------------------------------------
    // Helpers para montar objetos reutilizáveis
    // -----------------------------------------------------------------------

    private Professor professorComHorario(Long id, LocalTime inicio, LocalTime fim) {
        Horario horario = new Horario();
        horario.setId(id);
        horario.setHoraInicio(inicio);
        horario.setHoraFim(fim);

        Professor professor = new Professor();
        professor.setId(id);
        professor.setNome("Professor " + id);
        professor.setHorarios(List.of(horario));
        return professor;
    }

    private Aula aulaVinculadaAoProfessor(Professor professor, LocalTime inicio, LocalTime fim) {
        Contrato contrato = new Contrato();
        contrato.setProfessor(professor);

        Aula aula = new Aula();
        aula.setHoraInicio(inicio);
        aula.setHoraFim(fim);
        aula.setContrato(contrato);
        return aula;
    }

    // -----------------------------------------------------------------------
    // Testes
    // -----------------------------------------------------------------------

    @Nested
    public class MontarDashboardProfessoresTestes {

        @Test
        @DisplayName("Deve retornar dashboard vazio quando não há professores")
        void deveRetornarDashboardVazioSemProfessores() {
            LocalDate inicio = LocalDate.now();
            LocalDate fim = LocalDate.now().plusDays(6);

            Mockito.when(professorRepository.findAll()).thenReturn(List.of());
            Mockito.when(aulaRepository.findByDataBetween(inicio, fim)).thenReturn(List.of());

            DashboardResponse response = dashboardService.montarDashboardProfessores(inicio, fim);

            Assertions.assertNotNull(response);
            Assertions.assertEquals(0, response.getTotalProfessores());
            Assertions.assertEquals(0, response.getTotalAulas());
            Assertions.assertTrue(response.getDetalhes().isEmpty());
        }

        @Test
        @DisplayName("Deve usar início da semana quando startDate for nulo")
        void deveUsarInicioSemanaQuandoStartDateNulo() {
            LocalDate fim = LocalDate.now().plusDays(6);

            Mockito.when(professorRepository.findAll()).thenReturn(List.of());
            Mockito.when(aulaRepository.findByDataBetween(Mockito.any(LocalDate.class), Mockito.eq(fim)))
                    .thenReturn(List.of());

            DashboardResponse response = dashboardService.montarDashboardProfessores(null, fim);

            Assertions.assertNotNull(response);
        }

        @Test
        @DisplayName("Deve usar início + 6 dias quando endDate for nulo")
        void deveUsarIniciaMaisSeisDiasQuandoEndDateNulo() {
            LocalDate inicio = LocalDate.now().minusDays(3);

            Mockito.when(professorRepository.findAll()).thenReturn(List.of());
            Mockito.when(aulaRepository.findByDataBetween(Mockito.eq(inicio), Mockito.any(LocalDate.class)))
                    .thenReturn(List.of());

            DashboardResponse response = dashboardService.montarDashboardProfessores(inicio, null);

            Assertions.assertNotNull(response);
        }

        @Test
        @DisplayName("Deve usar datas padrão da semana quando ambas as datas forem nulas")
        void deveUsarDatasPadraoQuandoAmbasDatasNulas() {
            Mockito.when(professorRepository.findAll()).thenReturn(List.of());
            Mockito.when(aulaRepository.findByDataBetween(Mockito.any(LocalDate.class), Mockito.any(LocalDate.class)))
                    .thenReturn(List.of());

            DashboardResponse response = dashboardService.montarDashboardProfessores(null, null);

            Assertions.assertNotNull(response);
        }

        @Test
        @DisplayName("Deve classificar professor como Sobrecarregado quando ocupa 80% ou mais da capacidade")
        void deveClassificarProfessorComoSobrecarregado() {
            Professor professor = professorComHorario(1L, LocalTime.of(8, 0), LocalTime.of(10, 0)); // 2h disponíveis

            Aula aula = aulaVinculadaAoProfessor(professor, LocalTime.of(8, 0), LocalTime.of(10, 0)); // 2h ocupadas = 100%

            LocalDate inicio = LocalDate.now();
            LocalDate fim = LocalDate.now().plusDays(6);

            Mockito.when(professorRepository.findAll()).thenReturn(List.of(professor));
            Mockito.when(aulaRepository.findByDataBetween(inicio, fim)).thenReturn(List.of(aula));

            DashboardResponse response = dashboardService.montarDashboardProfessores(inicio, fim);

            Assertions.assertEquals("Sobrecarregado", response.getDetalhes().get(0).getStatus());
            Assertions.assertEquals(1, response.getProfessoresSobrecarregados());
        }

        @Test
        @DisplayName("Deve classificar professor como Equilibrado quando ocupa entre 50% e 79% da capacidade")
        void deveClassificarProfessorComoEquilibrado() {
            Professor professor = professorComHorario(1L, LocalTime.of(8, 0), LocalTime.of(10, 0)); // 2h disponíveis

            Aula aula = aulaVinculadaAoProfessor(professor, LocalTime.of(8, 0), LocalTime.of(9, 0)); // 1h ocupada = 50%

            LocalDate inicio = LocalDate.now();
            LocalDate fim = LocalDate.now().plusDays(6);

            Mockito.when(professorRepository.findAll()).thenReturn(List.of(professor));
            Mockito.when(aulaRepository.findByDataBetween(inicio, fim)).thenReturn(List.of(aula));

            DashboardResponse response = dashboardService.montarDashboardProfessores(inicio, fim);

            Assertions.assertEquals("Equilibrado", response.getDetalhes().get(0).getStatus());
        }

        @Test
        @DisplayName("Deve classificar professor como Subutilizado quando ocupa menos de 50% da capacidade")
        void deveClassificarProfessorComoSubutilizado() {
            Professor professor = professorComHorario(1L, LocalTime.of(8, 0), LocalTime.of(12, 0)); // 4h disponíveis

            Aula aula = aulaVinculadaAoProfessor(professor, LocalTime.of(8, 0), LocalTime.of(9, 0)); // 1h = 25%

            LocalDate inicio = LocalDate.now();
            LocalDate fim = LocalDate.now().plusDays(6);

            Mockito.when(professorRepository.findAll()).thenReturn(List.of(professor));
            Mockito.when(aulaRepository.findByDataBetween(inicio, fim)).thenReturn(List.of(aula));

            DashboardResponse response = dashboardService.montarDashboardProfessores(inicio, fim);

            Assertions.assertEquals("Subutilizado", response.getDetalhes().get(0).getStatus());
        }

        @Test
        @DisplayName("Deve usar capacidade padrão de 40h quando professor tem horários nulos")
        void deveUsarCapacidadePadraoQuandoProfessorComHorariosNulos() {
            Professor professor = new Professor();
            professor.setId(1L);
            professor.setNome("Prof Sem Horário");
            professor.setHorarios(null);

            LocalDate inicio = LocalDate.now();
            LocalDate fim = LocalDate.now().plusDays(6);

            Mockito.when(professorRepository.findAll()).thenReturn(List.of(professor));
            Mockito.when(aulaRepository.findByDataBetween(inicio, fim)).thenReturn(List.of());

            DashboardResponse response = dashboardService.montarDashboardProfessores(inicio, fim);

            Assertions.assertEquals(40.0, response.getDetalhes().get(0).getHorasSemanais());
            Assertions.assertEquals("Subutilizado", response.getDetalhes().get(0).getStatus());
        }

        @Test
        @DisplayName("Deve usar capacidade padrão de 40h quando professor tem lista de horários vazia")
        void deveUsarCapacidadePadraoQuandoProfessorComHorariosVazios() {
            Professor professor = new Professor();
            professor.setId(1L);
            professor.setNome("Prof Lista Vazia");
            professor.setHorarios(new ArrayList<>());

            LocalDate inicio = LocalDate.now();
            LocalDate fim = LocalDate.now().plusDays(6);

            Mockito.when(professorRepository.findAll()).thenReturn(List.of(professor));
            Mockito.when(aulaRepository.findByDataBetween(inicio, fim)).thenReturn(List.of());

            DashboardResponse response = dashboardService.montarDashboardProfessores(inicio, fim);

            Assertions.assertEquals(40.0, response.getDetalhes().get(0).getHorasSemanais());
        }

        @Test
        @DisplayName("Deve classificar como Subutilizado quando capacidade calculada é zero")
        void deveClassificarComoSubutilizadoQuandoCapacidadeZero() {
            Horario horario = new Horario();
            horario.setId(1L);
            horario.setHoraInicio(LocalTime.of(10, 0));
            horario.setHoraFim(LocalTime.of(10, 0)); // 0 minutos = 0h

            Professor professor = new Professor();
            professor.setId(1L);
            professor.setNome("Prof Capacidade Zero");
            professor.setHorarios(List.of(horario));

            LocalDate inicio = LocalDate.now();
            LocalDate fim = LocalDate.now().plusDays(6);

            Mockito.when(professorRepository.findAll()).thenReturn(List.of(professor));
            Mockito.when(aulaRepository.findByDataBetween(inicio, fim)).thenReturn(List.of());

            DashboardResponse response = dashboardService.montarDashboardProfessores(inicio, fim);

            Assertions.assertEquals("Subutilizado", response.getDetalhes().get(0).getStatus());
        }

        @Test
        @DisplayName("Deve obter professor via turma quando aula é de contrato em grupo")
        void deveObterProfessorViaTurmaParaContratoGrupo() {
            Professor professor = new Professor();
            professor.setId(1L);
            professor.setNome("Prof da Turma");
            professor.setHorarios(new ArrayList<>());

            Turma turma = new Turma();
            turma.setId(1L);
            turma.setProfessor(professor);

            Contrato contrato = new Contrato();
            contrato.setTurma(turma);
            contrato.setProfessor(null);

            Aula aula = new Aula();
            aula.setHoraInicio(LocalTime.of(10, 0));
            aula.setHoraFim(LocalTime.of(11, 0));
            aula.setContrato(contrato);

            LocalDate inicio = LocalDate.now();
            LocalDate fim = LocalDate.now().plusDays(6);

            Mockito.when(professorRepository.findAll()).thenReturn(List.of(professor));
            Mockito.when(aulaRepository.findByDataBetween(inicio, fim)).thenReturn(List.of(aula));

            DashboardResponse response = dashboardService.montarDashboardProfessores(inicio, fim);

            Assertions.assertEquals(1, response.getDetalhes().get(0).getAulasCount());
        }

        @Test
        @DisplayName("Deve filtrar aulas cujo contrato é nulo")
        void deveFiltrarAulasComContratoNulo() {
            Professor professor = new Professor();
            professor.setId(1L);
            professor.setNome("Prof");
            professor.setHorarios(new ArrayList<>());

            Aula aulaOrfã = new Aula();
            aulaOrfã.setContrato(null);

            LocalDate inicio = LocalDate.now();
            LocalDate fim = LocalDate.now().plusDays(6);

            Mockito.when(professorRepository.findAll()).thenReturn(List.of(professor));
            Mockito.when(aulaRepository.findByDataBetween(inicio, fim)).thenReturn(List.of(aulaOrfã));

            DashboardResponse response = dashboardService.montarDashboardProfessores(inicio, fim);

            Assertions.assertEquals(0, response.getDetalhes().get(0).getAulasCount());
            Assertions.assertEquals(0, response.getTotalAulas());
        }

        @Test
        @DisplayName("Deve ignorar horas de aulas sem horaInicio ou horaFim no cálculo de ocupação")
        void deveIgnorarAulasSemHoraNoCalculo() {
            Professor professor = professorComHorario(1L, LocalTime.of(8, 0), LocalTime.of(10, 0)); // 2h disponíveis

            Contrato contrato = new Contrato();
            contrato.setProfessor(professor);

            Aula aulaSemHora = new Aula();
            aulaSemHora.setHoraInicio(null);
            aulaSemHora.setHoraFim(null);
            aulaSemHora.setContrato(contrato);

            LocalDate inicio = LocalDate.now();
            LocalDate fim = LocalDate.now().plusDays(6);

            Mockito.when(professorRepository.findAll()).thenReturn(List.of(professor));
            Mockito.when(aulaRepository.findByDataBetween(inicio, fim)).thenReturn(List.of(aulaSemHora));

            DashboardResponse response = dashboardService.montarDashboardProfessores(inicio, fim);

            // Aula é contada mas não ocupa horas → Subutilizado
            Assertions.assertEquals(1, response.getDetalhes().get(0).getAulasCount());
            Assertions.assertEquals("Subutilizado", response.getDetalhes().get(0).getStatus());
        }

        @Test
        @DisplayName("Deve calcular horas livres corretamente com múltiplos professores")
        void deveCalcularHorasLivresComMultiplosProfessores() {
            Professor prof1 = professorComHorario(1L, LocalTime.of(8, 0), LocalTime.of(10, 0)); // 2h
            Professor prof2 = professorComHorario(2L, LocalTime.of(14, 0), LocalTime.of(16, 0)); // 2h

            LocalDate inicio = LocalDate.now();
            LocalDate fim = LocalDate.now().plusDays(6);

            Mockito.when(professorRepository.findAll()).thenReturn(List.of(prof1, prof2));
            Mockito.when(aulaRepository.findByDataBetween(inicio, fim)).thenReturn(List.of());

            DashboardResponse response = dashboardService.montarDashboardProfessores(inicio, fim);

            Assertions.assertEquals(2, response.getTotalProfessores());
            Assertions.assertEquals(4.0, response.getTotalHorasLivres()); // 2h + 2h
        }
    }
}
