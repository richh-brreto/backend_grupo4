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
import school.sptech.back_end_PI.dto.aula.AulaExtraRequest;
import school.sptech.back_end_PI.dto.aula.CancelarAulaRequest;
import school.sptech.back_end_PI.dto.aula.PresencaRequest;
import school.sptech.back_end_PI.dto.aula.RemarcarAulaRequest;
import school.sptech.back_end_PI.entity.Aula;
import school.sptech.back_end_PI.entity.Contrato;
import school.sptech.back_end_PI.entity.Horario;
import school.sptech.back_end_PI.entity.StatusAula;
import school.sptech.back_end_PI.exception.BusinessRuleException;
import school.sptech.back_end_PI.exception.EntityNotFound;
import school.sptech.back_end_PI.repository.AulaRepository;
import school.sptech.back_end_PI.repository.ContratoRepository;
import school.sptech.back_end_PI.repository.LogAulaRepository;
import school.sptech.back_end_PI.services.AulaService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AulaServiceTest {

    @Mock
    private AulaRepository aulaRepository;

    @Mock
    private LogAulaRepository logAulaRepository;

    @Mock
    private ContratoRepository contratoRepository;

    @InjectMocks
    private AulaService aulaService;

    @Nested
    public class GerarAulasParaContratoTestes {

        @Test
        @DisplayName("Deve gerar aulas corretamente para contrato válido")
        void deveGerarAulasCorretamente() {
            Horario horario = new Horario();
            horario.setDiaSemana("SEGUNDA");
            horario.setHoraInicio(LocalTime.of(10, 0));
            horario.setHoraFim(LocalTime.of(11, 0));

            Contrato contrato = new Contrato();
            contrato.setId(1L);
            contrato.setDataInicio(LocalDate.now().minusMonths(1));
            contrato.setDataFim(LocalDate.now().plusMonths(2));
            contrato.setHorarios(List.of(horario));

            Aula aulaSalva = new Aula();
            aulaSalva.setId(1L);
            aulaSalva.setContrato(contrato);

            Mockito.when(aulaRepository.saveAll(Mockito.anyList())).thenReturn(List.of(aulaSalva));

            Assertions.assertDoesNotThrow(() -> aulaService.gerarAulasParaContrato(contrato));

            Mockito.verify(aulaRepository, Mockito.times(1)).saveAll(Mockito.anyList());
        }

        @Test
        @DisplayName("Deve retornar sem salvar quando horários forem nulos")
        void deveRetornarSemSalvarQuandoHorariosForemNulos() {
            Contrato contrato = new Contrato();
            contrato.setId(1L);
            contrato.setDataInicio(LocalDate.now());
            contrato.setDataFim(LocalDate.now().plusMonths(1));
            contrato.setHorarios(null);

            Assertions.assertDoesNotThrow(() -> aulaService.gerarAulasParaContrato(contrato));

            Mockito.verify(aulaRepository, Mockito.never()).saveAll(Mockito.anyList());
        }

        @Test
        @DisplayName("Deve retornar sem salvar quando lista de horários for vazia")
        void deveRetornarSemSalvarQuandoHorariosForemVazios() {
            Contrato contrato = new Contrato();
            contrato.setId(1L);
            contrato.setDataInicio(LocalDate.now());
            contrato.setDataFim(LocalDate.now().plusMonths(1));
            contrato.setHorarios(List.of());

            Assertions.assertDoesNotThrow(() -> aulaService.gerarAulasParaContrato(contrato));

            Mockito.verify(aulaRepository, Mockito.never()).saveAll(Mockito.anyList());
        }

        @Test
        @DisplayName("Deve lançar BusinessRuleException quando dia da semana for inválido")
        void deveLancarExcecaoQuandoDiaSemanaForInvalido() {
            Horario horario = new Horario();
            horario.setDiaSemana("INVALIDO");
            horario.setHoraInicio(LocalTime.of(8, 0));
            horario.setHoraFim(LocalTime.of(9, 0));

            Contrato contrato = new Contrato();
            contrato.setId(1L);
            contrato.setDataInicio(LocalDate.now());
            contrato.setDataFim(LocalDate.now().plusMonths(1));
            contrato.setHorarios(List.of(horario));

            Assertions.assertThrows(BusinessRuleException.class,
                    () -> aulaService.gerarAulasParaContrato(contrato));
        }

        @Test
        @DisplayName("Deve gerar aulas corretamente com múltiplos horários")
        void deveGerarAulasComMultiplosHorarios() {
            Horario horario1 = new Horario();
            horario1.setDiaSemana("SEGUNDA");
            horario1.setHoraInicio(LocalTime.of(10, 0));
            horario1.setHoraFim(LocalTime.of(11, 0));

            Horario horario2 = new Horario();
            horario2.setDiaSemana("QUARTA");
            horario2.setHoraInicio(LocalTime.of(14, 0));
            horario2.setHoraFim(LocalTime.of(15, 0));

            Contrato contrato = new Contrato();
            contrato.setId(1L);
            contrato.setDataInicio(LocalDate.now().minusMonths(1));
            contrato.setDataFim(LocalDate.now().plusMonths(2));
            contrato.setHorarios(List.of(horario1, horario2));

            Aula aulaSalva = new Aula();
            aulaSalva.setId(1L);
            aulaSalva.setContrato(contrato);

            Mockito.when(aulaRepository.saveAll(Mockito.anyList())).thenReturn(List.of(aulaSalva));

            Assertions.assertDoesNotThrow(() -> aulaService.gerarAulasParaContrato(contrato));

            Mockito.verify(aulaRepository, Mockito.times(1)).saveAll(Mockito.anyList());
        }

        @Test
        @DisplayName("Deve aceitar todos os dias da semana válidos")
        void deveAceitarTodosOsDiasDaSemanaValidos() {
            String[] diasValidos = {"SEGUNDA", "TERCA", "QUARTA", "QUINTA", "SEXTA", "SABADO", "DOMINGO"};

            for (String dia : diasValidos) {
                Horario horario = new Horario();
                horario.setDiaSemana(dia);
                horario.setHoraInicio(LocalTime.of(10, 0));
                horario.setHoraFim(LocalTime.of(11, 0));

                Contrato contrato = new Contrato();
                contrato.setId(1L);
                contrato.setDataInicio(LocalDate.now().minusMonths(1));
                contrato.setDataFim(LocalDate.now().plusMonths(1));
                contrato.setHorarios(List.of(horario));

                Aula aulaSalva = new Aula();
                aulaSalva.setId(1L);
                aulaSalva.setContrato(contrato);

                Mockito.when(aulaRepository.saveAll(Mockito.anyList())).thenReturn(List.of(aulaSalva));

                Assertions.assertDoesNotThrow(() -> aulaService.gerarAulasParaContrato(contrato),
                        "Falhou para o dia: " + dia);
            }
        }
    }

    @Nested
    public class AdicionarAulaExtraTestes {

        @Test
        @DisplayName("Deve adicionar aula extra com sucesso")
        void deveAdicionarAulaExtraComSucesso() {
            Contrato contrato = new Contrato();
            contrato.setId(1L);

            AulaExtraRequest request = new AulaExtraRequest();
            request.setContratoId(1L);
            request.setData(LocalDate.now().plusDays(1));
            request.setHoraInicio(LocalTime.of(10, 0));
            request.setHoraFim(LocalTime.of(11, 0));

            Aula aulaSalva = new Aula();
            aulaSalva.setId(1L);
            aulaSalva.setData(request.getData());
            aulaSalva.setHoraInicio(request.getHoraInicio());
            aulaSalva.setHoraFim(request.getHoraFim());
            aulaSalva.setStatus(StatusAula.EXTRA);
            aulaSalva.setContrato(contrato);

            Mockito.when(contratoRepository.findById(1L)).thenReturn(Optional.of(contrato));
            Mockito.when(aulaRepository.save(Mockito.any(Aula.class))).thenReturn(aulaSalva);

            Assertions.assertDoesNotThrow(() -> aulaService.adicionarAulaExtra(request));

            Mockito.verify(aulaRepository, Mockito.times(1)).save(Mockito.any(Aula.class));
        }

        @Test
        @DisplayName("Deve lançar BusinessRuleException quando hora de início for após hora de fim")
        void deveLancarExcecaoQuandoHoraInicioAposHoraFim() {
            Contrato contrato = new Contrato();
            contrato.setId(1L);

            AulaExtraRequest request = new AulaExtraRequest();
            request.setContratoId(1L);
            request.setData(LocalDate.now().plusDays(1));
            request.setHoraInicio(LocalTime.of(12, 0));
            request.setHoraFim(LocalTime.of(10, 0));

            Mockito.when(contratoRepository.findById(1L)).thenReturn(Optional.of(contrato));

            Assertions.assertThrows(BusinessRuleException.class,
                    () -> aulaService.adicionarAulaExtra(request));
        }

        @Test
        @DisplayName("Deve lançar EntityNotFound quando contrato não encontrado")
        void deveLancarEntityNotFoundQuandoContratoNaoEncontrado() {
            AulaExtraRequest request = new AulaExtraRequest();
            request.setContratoId(99L);
            request.setData(LocalDate.now().plusDays(1));
            request.setHoraInicio(LocalTime.of(10, 0));
            request.setHoraFim(LocalTime.of(11, 0));

            Mockito.when(contratoRepository.findById(99L)).thenReturn(Optional.empty());

            Assertions.assertThrows(EntityNotFound.class,
                    () -> aulaService.adicionarAulaExtra(request));
        }
    }

    @Nested
    public class RemarcarAulaTestes {

        @Test
        @DisplayName("Deve remarcar aula com sucesso")
        void deveRemarcarAulaComSucesso() {
            Aula aula = new Aula();
            aula.setId(1L);
            aula.setData(LocalDate.now());
            aula.setHoraInicio(LocalTime.of(10, 0));
            aula.setHoraFim(LocalTime.of(11, 0));
            aula.setStatus(StatusAula.AGENDADA);

            RemarcarAulaRequest request = new RemarcarAulaRequest();
            request.setNovaData(LocalDate.now().plusDays(1));
            request.setNovaHoraInicio(LocalTime.of(14, 0));
            request.setNovaHoraFim(LocalTime.of(15, 0));

            Aula aulaSalva = new Aula();
            aulaSalva.setId(1L);
            aulaSalva.setStatus(StatusAula.REMARCADA);

            Mockito.when(aulaRepository.findById(1L)).thenReturn(Optional.of(aula));
            Mockito.when(aulaRepository.save(Mockito.any(Aula.class))).thenReturn(aulaSalva);

            Assertions.assertDoesNotThrow(() -> aulaService.remarcarAula(1L, request));

            Mockito.verify(aulaRepository, Mockito.times(1)).save(Mockito.any(Aula.class));
        }

        @Test
        @DisplayName("Deve lançar BusinessRuleException ao remarcar aula cancelada")
        void deveLancarExcecaoAoRemarcarAulaCancelada() {
            Aula aula = new Aula();
            aula.setId(1L);
            aula.setStatus(StatusAula.CANCELADA);

            RemarcarAulaRequest request = new RemarcarAulaRequest();
            request.setNovaData(LocalDate.now().plusDays(1));
            request.setNovaHoraInicio(LocalTime.of(10, 0));
            request.setNovaHoraFim(LocalTime.of(11, 0));

            Mockito.when(aulaRepository.findById(1L)).thenReturn(Optional.of(aula));

            Assertions.assertThrows(BusinessRuleException.class,
                    () -> aulaService.remarcarAula(1L, request));
        }

        @Test
        @DisplayName("Deve lançar BusinessRuleException quando nova hora de início for após nova hora de fim")
        void deveLancarExcecaoQuandoNovaHoraInicioAposNovaHoraFim() {
            Aula aula = new Aula();
            aula.setId(1L);
            aula.setStatus(StatusAula.AGENDADA);

            RemarcarAulaRequest request = new RemarcarAulaRequest();
            request.setNovaData(LocalDate.now().plusDays(1));
            request.setNovaHoraInicio(LocalTime.of(15, 0));
            request.setNovaHoraFim(LocalTime.of(10, 0));

            Mockito.when(aulaRepository.findById(1L)).thenReturn(Optional.of(aula));

            Assertions.assertThrows(BusinessRuleException.class,
                    () -> aulaService.remarcarAula(1L, request));
        }

        @Test
        @DisplayName("Deve lançar EntityNotFound quando aula não encontrada para remarcar")
        void deveLancarEntityNotFoundQuandoAulaNaoEncontrada() {
            RemarcarAulaRequest request = new RemarcarAulaRequest();
            request.setNovaData(LocalDate.now().plusDays(1));
            request.setNovaHoraInicio(LocalTime.of(10, 0));
            request.setNovaHoraFim(LocalTime.of(11, 0));

            Mockito.when(aulaRepository.findById(99L)).thenReturn(Optional.empty());

            Assertions.assertThrows(EntityNotFound.class,
                    () -> aulaService.remarcarAula(99L, request));
        }
    }

    @Nested
    public class CancelarAulaTestes {

        @Test
        @DisplayName("Deve cancelar aula com sucesso")
        void deveCancelarAulaComSucesso() {
            Aula aula = new Aula();
            aula.setId(1L);
            aula.setStatus(StatusAula.AGENDADA);

            CancelarAulaRequest request = new CancelarAulaRequest();
            request.setMotivo("Motivo do cancelamento");

            Aula aulaSalva = new Aula();
            aulaSalva.setId(1L);
            aulaSalva.setStatus(StatusAula.CANCELADA);

            Mockito.when(aulaRepository.findById(1L)).thenReturn(Optional.of(aula));
            Mockito.when(aulaRepository.save(Mockito.any(Aula.class))).thenReturn(aulaSalva);

            Assertions.assertDoesNotThrow(() -> aulaService.cancelarAula(1L, request));

            Mockito.verify(aulaRepository, Mockito.times(1)).save(Mockito.any(Aula.class));
        }

        @Test
        @DisplayName("Deve lançar BusinessRuleException ao cancelar aula já cancelada")
        void deveLancarExcecaoAoCancelarAulaJaCancelada() {
            Aula aula = new Aula();
            aula.setId(1L);
            aula.setStatus(StatusAula.CANCELADA);

            CancelarAulaRequest request = new CancelarAulaRequest();

            Mockito.when(aulaRepository.findById(1L)).thenReturn(Optional.of(aula));

            Assertions.assertThrows(BusinessRuleException.class,
                    () -> aulaService.cancelarAula(1L, request));
        }

        @Test
        @DisplayName("Deve cancelar aula sem motivo informado")
        void deveCancelarAulaSemMotivoInformado() {
            Aula aula = new Aula();
            aula.setId(1L);
            aula.setStatus(StatusAula.AGENDADA);

            Aula aulaSalva = new Aula();
            aulaSalva.setId(1L);
            aulaSalva.setStatus(StatusAula.CANCELADA);

            Mockito.when(aulaRepository.findById(1L)).thenReturn(Optional.of(aula));
            Mockito.when(aulaRepository.save(Mockito.any(Aula.class))).thenReturn(aulaSalva);

            Assertions.assertDoesNotThrow(() -> aulaService.cancelarAula(1L, null));

            Mockito.verify(aulaRepository, Mockito.times(1)).save(Mockito.any(Aula.class));
        }

        @Test
        @DisplayName("Deve lançar EntityNotFound quando aula não encontrada para cancelar")
        void deveLancarEntityNotFoundQuandoAulaNaoEncontrada() {
            Mockito.when(aulaRepository.findById(99L)).thenReturn(Optional.empty());

            Assertions.assertThrows(EntityNotFound.class,
                    () -> aulaService.cancelarAula(99L, new CancelarAulaRequest()));
        }
    }

    @Nested
    public class DeletarAulaTestes {

        @Test
        @DisplayName("Deve deletar aula com sucesso")
        void deveDeletarAulaComSucesso() {
            Aula aula = new Aula();
            aula.setId(1L);

            Mockito.when(aulaRepository.findById(1L)).thenReturn(Optional.of(aula));

            Assertions.assertDoesNotThrow(() -> aulaService.deletarAula(1L));

            Mockito.verify(logAulaRepository, Mockito.times(1)).deleteByAula(aula);
            Mockito.verify(aulaRepository, Mockito.times(1)).delete(aula);
        }

        @Test
        @DisplayName("Deve lançar EntityNotFound quando aula não encontrada para deleção")
        void deveLancarEntityNotFoundQuandoAulaNaoEncontrada() {
            Mockito.when(aulaRepository.findById(99L)).thenReturn(Optional.empty());

            Assertions.assertThrows(EntityNotFound.class,
                    () -> aulaService.deletarAula(99L));
        }
    }

    @Nested
    public class AtribuirPresencaTestes {

        @Test
        @DisplayName("Deve registrar presença com sucesso")
        void deveRegistrarPresencaComSucesso() {
            Aula aula = new Aula();
            aula.setId(1L);
            aula.setStatus(StatusAula.AGENDADA);

            PresencaRequest request = new PresencaRequest();
            request.setPresenca(true);

            Aula aulaSalva = new Aula();
            aulaSalva.setId(1L);
            aulaSalva.setPresenca(true);

            Mockito.when(aulaRepository.findById(1L)).thenReturn(Optional.of(aula));
            Mockito.when(aulaRepository.save(Mockito.any(Aula.class))).thenReturn(aulaSalva);

            Assertions.assertDoesNotThrow(() -> aulaService.atribuirPresenca(1L, request));

            Mockito.verify(aulaRepository, Mockito.times(1)).save(Mockito.any(Aula.class));
        }

        @Test
        @DisplayName("Deve registrar ausência com sucesso")
        void deveRegistrarAusenciaComSucesso() {
            Aula aula = new Aula();
            aula.setId(1L);
            aula.setStatus(StatusAula.AGENDADA);

            PresencaRequest request = new PresencaRequest();
            request.setPresenca(false);

            Aula aulaSalva = new Aula();
            aulaSalva.setId(1L);
            aulaSalva.setPresenca(false);

            Mockito.when(aulaRepository.findById(1L)).thenReturn(Optional.of(aula));
            Mockito.when(aulaRepository.save(Mockito.any(Aula.class))).thenReturn(aulaSalva);

            Assertions.assertDoesNotThrow(() -> aulaService.atribuirPresenca(1L, request));

            Mockito.verify(aulaRepository, Mockito.times(1)).save(Mockito.any(Aula.class));
        }

        @Test
        @DisplayName("Deve lançar BusinessRuleException ao registrar presença em aula cancelada")
        void deveLancarExcecaoAoRegistrarPresencaEmAulaCancelada() {
            Aula aula = new Aula();
            aula.setId(1L);
            aula.setStatus(StatusAula.CANCELADA);

            PresencaRequest request = new PresencaRequest();
            request.setPresenca(true);

            Mockito.when(aulaRepository.findById(1L)).thenReturn(Optional.of(aula));

            Assertions.assertThrows(BusinessRuleException.class,
                    () -> aulaService.atribuirPresenca(1L, request));
        }

        @Test
        @DisplayName("Deve lançar EntityNotFound quando aula não encontrada para atribuir presença")
        void deveLancarEntityNotFoundQuandoAulaNaoEncontrada() {
            PresencaRequest request = new PresencaRequest();
            request.setPresenca(true);

            Mockito.when(aulaRepository.findById(99L)).thenReturn(Optional.empty());

            Assertions.assertThrows(EntityNotFound.class,
                    () -> aulaService.atribuirPresenca(99L, request));
        }
    }

    @Nested
    public class ListarAulasPorContratoTestes {

        @Test
        @DisplayName("Deve retornar lista de aulas por contrato")
        void deveRetornarListaDeAulasPorContrato() {
            Aula aula = new Aula();
            aula.setId(1L);
            aula.setStatus(StatusAula.AGENDADA);

            Mockito.when(contratoRepository.existsById(1L)).thenReturn(true);
            Mockito.when(aulaRepository.findByContratoIdOrderByDataAsc(1L)).thenReturn(List.of(aula));

            Assertions.assertDoesNotThrow(() -> aulaService.listarAulasPorContrato(1L));
        }

        @Test
        @DisplayName("Deve lançar EntityNotFound quando contrato não encontrado")
        void deveLancarEntityNotFoundQuandoContratoNaoEncontrado() {
            Mockito.when(contratoRepository.existsById(99L)).thenReturn(false);

            Assertions.assertThrows(EntityNotFound.class,
                    () -> aulaService.listarAulasPorContrato(99L));
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando contrato não tiver aulas")
        void deveRetornarListaVaziaQuandoContratoNaoTiverAulas() {
            Mockito.when(contratoRepository.existsById(1L)).thenReturn(true);
            Mockito.when(aulaRepository.findByContratoIdOrderByDataAsc(1L)).thenReturn(List.of());

            var resultado = aulaService.listarAulasPorContrato(1L);

            Assertions.assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    public class ListarLogsPorAulaTestes {

        @Test
        @DisplayName("Deve retornar logs da aula com sucesso")
        void deveRetornarLogsDaAulaComSucesso() {
            Aula aula = new Aula();
            aula.setId(1L);

            Mockito.when(aulaRepository.findById(1L)).thenReturn(Optional.of(aula));
            Mockito.when(logAulaRepository.findByAulaIdOrderByDataHoraDesc(1L)).thenReturn(List.of());

            Assertions.assertDoesNotThrow(() -> aulaService.listarLogsPorAula(1L));
        }

        @Test
        @DisplayName("Deve lançar EntityNotFound quando aula não encontrada para listar logs")
        void deveLancarEntityNotFoundQuandoAulaNaoEncontrada() {
            Mockito.when(aulaRepository.findById(99L)).thenReturn(Optional.empty());

            Assertions.assertThrows(EntityNotFound.class,
                    () -> aulaService.listarLogsPorAula(99L));
        }
    }
}
