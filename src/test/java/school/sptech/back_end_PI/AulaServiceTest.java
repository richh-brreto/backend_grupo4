package school.sptech.back_end_PI;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.sptech.back_end_PI.entity.Contrato;
import school.sptech.back_end_PI.entity.Horario;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.repository.AulaRepository;
import school.sptech.back_end_PI.repository.ContratoRepository;
import school.sptech.back_end_PI.services.AulaService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class AulaServiceTest {

    @Mock
    private ContratoRepository contratoRepository;

    @Mock
    private AulaRepository aulaRepository;

    @InjectMocks
    private AulaService aulaService;

    @Nested
    public class GerarAulasMesSubsequenteTestes {

        @Test
        @DisplayName("Deve gerar aulas corretamente para contrato válido")
        void deveGerarAulasCorretamente() {

            Professor professor = new Professor();

            Horario horario = new Horario();
            horario.setDiaSemana("SEGUNDA");
            horario.setHoraInicio(LocalTime.of(10, 0));
            horario.setHoraFim(LocalTime.of(11, 0));

            professor.setHorarios(List.of(horario));

            Contrato contrato = new Contrato(
                    1L,
                    professor,
                    LocalDate.now().minusMonths(1),
                    LocalDate.now().plusMonths(2)
            );

            List<Contrato> contratos = List.of(contrato);

            Mockito.when(contratoRepository.findAll())
                    .thenReturn(contratos);

            Assertions.assertDoesNotThrow(() ->
                    aulaService.gerarAulasMesSubsequente()
            );

            Mockito.verify(aulaRepository, Mockito.times(1))
                    .saveAll(Mockito.anyList());
        }

        @Test
        @DisplayName("Deve salvar lista vazia quando não houver contratos")
        void deveSalvarListaVaziaQuandoNaoHouverContratos() {

            Mockito.when(contratoRepository.findAll())
                    .thenReturn(Collections.emptyList());

            Assertions.assertDoesNotThrow(() ->
                    aulaService.gerarAulasMesSubsequente()
            );

            Mockito.verify(aulaRepository, Mockito.times(1))
                    .saveAll(Collections.emptyList());
        }

        @Test
        @DisplayName("Não deve gerar aulas quando professor for nulo")
        void naoDeveGerarAulasQuandoProfessorForNulo() {

            Contrato contrato = new Contrato(
                    1L,
                    null,
                    LocalDate.now(),
                    LocalDate.now().plusMonths(1)
            );

            Mockito.when(contratoRepository.findAll())
                    .thenReturn(List.of(contrato));

            Assertions.assertDoesNotThrow(() ->
                    aulaService.gerarAulasMesSubsequente()
            );

            Mockito.verify(aulaRepository, Mockito.times(1))
                    .saveAll(Collections.emptyList());
        }

        @Test
        @DisplayName("Não deve gerar aulas quando horários forem nulos")
        void naoDeveGerarAulasQuandoHorariosForemNulos() {

            Professor professor = new Professor();
            professor.setHorarios(null);

            Contrato contrato = new Contrato(
                    1L,
                    professor,
                    LocalDate.now(),
                    LocalDate.now().plusMonths(1)
            );

            Mockito.when(contratoRepository.findAll())
                    .thenReturn(List.of(contrato));

            Assertions.assertDoesNotThrow(() ->
                    aulaService.gerarAulasMesSubsequente()
            );

            Mockito.verify(aulaRepository, Mockito.times(1))
                    .saveAll(Collections.emptyList());
        }

        @Test
        @DisplayName("Não deve gerar aulas fora do período do contrato")
        void naoDeveGerarAulasForaDoPeriodoContrato() {

            Professor professor = new Professor();

            Horario horario = new Horario();
            horario.setDiaSemana("SEGUNDA");
            horario.setHoraInicio(LocalTime.of(8, 0));
            horario.setHoraFim(LocalTime.of(9, 0));

            professor.setHorarios(List.of(horario));

            Contrato contrato = new Contrato(
                    1L,
                    professor,
                    LocalDate.now().minusYears(2),
                    LocalDate.now().minusYears(1)
            );

            Mockito.when(contratoRepository.findAll())
                    .thenReturn(List.of(contrato));

            Assertions.assertDoesNotThrow(() ->
                    aulaService.gerarAulasMesSubsequente()
            );

            Mockito.verify(aulaRepository, Mockito.times(1))
                    .saveAll(Collections.emptyList());
        }

        @Test
        @DisplayName("Não deve salvar aulas quando dia da semana for inválido")
        void naoDeveSalvarAulasQuandoDiaSemanaForInvalido() {

            Professor professor = new Professor();

            Horario horario = new Horario();
            horario.setDiaSemana("INVALIDO");
            horario.setHoraInicio(LocalTime.of(8, 0));
            horario.setHoraFim(LocalTime.of(9, 0));

            professor.setHorarios(List.of(horario));

            Contrato contrato = new Contrato(
                    1L,
                    professor,
                    LocalDate.now(),
                    LocalDate.now().plusMonths(1)
            );

            Mockito.when(contratoRepository.findAll())
                    .thenReturn(List.of(contrato));

            Assertions.assertDoesNotThrow(() ->
                    aulaService.gerarAulasMesSubsequente()
            );

            Mockito.verify(aulaRepository, Mockito.never())
                    .saveAll(Mockito.anyList());
        }

        @Test
        @DisplayName("Não deve quebrar quando repository lançar exceção")
        void naoDeveQuebrarQuandoRepositoryLancarExcecao() {

            Mockito.when(contratoRepository.findAll())
                    .thenThrow(new RuntimeException("Erro"));

            Assertions.assertDoesNotThrow(() ->
                    aulaService.gerarAulasMesSubsequente()
            );

            Mockito.verify(aulaRepository, Mockito.never())
                    .saveAll(Mockito.anyList());
        }

        @Test
        @DisplayName("Deve funcionar corretamente com múltiplos contratos")
        void deveFuncionarComMultiplosContratos() {

            Professor professor1 = new Professor();
            Professor professor2 = new Professor();

            Horario horario1 = new Horario();
            horario1.setDiaSemana("SEGUNDA");
            horario1.setHoraInicio(LocalTime.of(10, 0));
            horario1.setHoraFim(LocalTime.of(11, 0));

            Horario horario2 = new Horario();
            horario2.setDiaSemana("TERCA");
            horario2.setHoraInicio(LocalTime.of(14, 0));
            horario2.setHoraFim(LocalTime.of(15, 0));

            professor1.setHorarios(List.of(horario1));
            professor2.setHorarios(List.of(horario2));

            Contrato contrato1 = new Contrato(
                    1L,
                    professor1,
                    LocalDate.now().minusMonths(1),
                    LocalDate.now().plusMonths(2)
            );

            Contrato contrato2 = new Contrato(
                    2L,
                    professor2,
                    LocalDate.now().minusMonths(1),
                    LocalDate.now().plusMonths(2)
            );

            List<Contrato> contratos = new ArrayList<>();
            contratos.add(contrato1);
            contratos.add(contrato2);

            Mockito.when(contratoRepository.findAll())
                    .thenReturn(contratos);

            Assertions.assertDoesNotThrow(() ->
                    aulaService.gerarAulasMesSubsequente()
            );

            Mockito.verify(aulaRepository, Mockito.times(1))
                    .saveAll(Mockito.anyList());
        }
    }
}