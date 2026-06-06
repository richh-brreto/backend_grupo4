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
import school.sptech.back_end_PI.dto.contrato.ContratoRequest;
import school.sptech.back_end_PI.dto.contrato.ContratoResponse;
import school.sptech.back_end_PI.entity.Aluno;
import school.sptech.back_end_PI.entity.Contrato;
import school.sptech.back_end_PI.entity.Horario;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.entity.Turma;
import school.sptech.back_end_PI.exception.BusinessRuleException;
import school.sptech.back_end_PI.exception.EntityNotFound;
import school.sptech.back_end_PI.repository.AlunoRepository;
import school.sptech.back_end_PI.repository.ContratoRepository;
import school.sptech.back_end_PI.repository.HorarioRepository;
import school.sptech.back_end_PI.repository.ProfessorRepository;
import school.sptech.back_end_PI.repository.TurmaRepository;
import school.sptech.back_end_PI.services.AulaService;
import school.sptech.back_end_PI.services.ContratoService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ContratoServiceTest {

    @Mock
    private ContratoRepository contratoRepository;

    @Mock
    private TurmaRepository turmaRepository;

    @Mock
    private ProfessorRepository professorRepository;

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private HorarioRepository horarioRepository;

    @Mock
    private AulaService aulaService;

    @InjectMocks
    private ContratoService contratoService;

    @Nested
    public class CriarContratoTestes {

        @Test
        @DisplayName("Deve lançar BusinessRuleException quando data de início é após data de fim")
        void deveLancarExcecaoQuandoDataInicioAposDataFim() {
            ContratoRequest request = new ContratoRequest();
            request.setTipo("Grupo");
            request.setDataInicio(LocalDate.now().plusDays(10));
            request.setDataFim(LocalDate.now());

            Assertions.assertThrows(BusinessRuleException.class,
                    () -> contratoService.criarContrato(request));
        }

        @Test
        @DisplayName("Deve lançar BusinessRuleException para tipo de contrato inválido")
        void deveLancarExcecaoParaTipoInvalido() {
            ContratoRequest request = new ContratoRequest();
            request.setTipo("Invalido");
            request.setDataInicio(LocalDate.now());
            request.setDataFim(LocalDate.now().plusMonths(1));

            Assertions.assertThrows(BusinessRuleException.class,
                    () -> contratoService.criarContrato(request));
        }

        @Test
        @DisplayName("Deve lançar EntityNotFound quando aluno não encontrado no contrato em grupo")
        void deveLancarEntityNotFoundQuandoAlunoNaoEncontradoGrupo() {
            ContratoRequest request = new ContratoRequest();
            request.setTipo("Grupo");
            request.setDataInicio(LocalDate.now());
            request.setDataFim(LocalDate.now().plusMonths(1));
            request.setAlunoId(99L);

            Mockito.when(alunoRepository.findById(99L)).thenReturn(Optional.empty());

            Assertions.assertThrows(EntityNotFound.class,
                    () -> contratoService.criarContrato(request));
        }

        @Test
        @DisplayName("Deve lançar BusinessRuleException quando aluno está inativo no contrato em grupo")
        void deveLancarExcecaoQuandoAlunoInativoGrupo() {
            Aluno aluno = new Aluno();
            aluno.setId(1L);
            aluno.setAtivo(false);

            ContratoRequest request = new ContratoRequest();
            request.setTipo("Grupo");
            request.setDataInicio(LocalDate.now());
            request.setDataFim(LocalDate.now().plusMonths(1));
            request.setAlunoId(1L);

            Mockito.when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));

            Assertions.assertThrows(BusinessRuleException.class,
                    () -> contratoService.criarContrato(request));
        }

        @Test
        @DisplayName("Deve lançar BusinessRuleException quando turma atingiu o limite de alunos")
        void deveLancarExcecaoQuandoTurmaLotada() {
            Aluno aluno = new Aluno();
            aluno.setId(1L);
            aluno.setAtivo(true);
            aluno.setHorarios(new ArrayList<>());

            Turma turma = new Turma();
            turma.setId(1L);
            turma.setNome("Turma A");
            turma.setLimiteAlunos(5);
            turma.setHorarios(null);

            ContratoRequest request = new ContratoRequest();
            request.setTipo("Grupo");
            request.setDataInicio(LocalDate.now());
            request.setDataFim(LocalDate.now().plusMonths(1));
            request.setAlunoId(1L);
            request.setTurmaId(1L);

            Mockito.when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
            Mockito.when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));
            Mockito.when(contratoRepository.countByTurmaAndDataFimGreaterThanEqual(turma, request.getDataInicio()))
                    .thenReturn(5L);

            Assertions.assertThrows(BusinessRuleException.class,
                    () -> contratoService.criarContrato(request));
        }

        @Test
        @DisplayName("Deve lançar BusinessRuleException quando já existe contrato em grupo duplicado")
        void deveLancarExcecaoQuandoContratoGrupoDuplicado() {
            Aluno aluno = new Aluno();
            aluno.setId(1L);
            aluno.setAtivo(true);
            aluno.setHorarios(new ArrayList<>());

            Turma turma = new Turma();
            turma.setId(1L);
            turma.setNome("Turma A");
            turma.setLimiteAlunos(10);
            turma.setHorarios(null);

            ContratoRequest request = new ContratoRequest();
            request.setTipo("Grupo");
            request.setDataInicio(LocalDate.now());
            request.setDataFim(LocalDate.now().plusMonths(1));
            request.setAlunoId(1L);
            request.setTurmaId(1L);

            Mockito.when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
            Mockito.when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));
            Mockito.when(contratoRepository.countByTurmaAndDataFimGreaterThanEqual(turma, request.getDataInicio()))
                    .thenReturn(0L);
            Mockito.when(contratoRepository.existsByAlunoAndTurmaAndDataInicioAndDataFim(
                    aluno, turma, request.getDataInicio(), request.getDataFim())).thenReturn(true);

            Assertions.assertThrows(BusinessRuleException.class,
                    () -> contratoService.criarContrato(request));
        }

        @Test
        @DisplayName("Deve criar contrato em grupo com sucesso")
        void deveCriarContratoGrupoComSucesso() {
            Aluno aluno = new Aluno();
            aluno.setId(1L);
            aluno.setAtivo(true);
            aluno.setHorarios(new ArrayList<>());

            Turma turma = new Turma();
            turma.setId(1L);
            turma.setNome("Turma A");
            turma.setLimiteAlunos(10);
            turma.setHorarios(null);

            ContratoRequest request = new ContratoRequest();
            request.setTipo("Grupo");
            request.setDataInicio(LocalDate.now());
            request.setDataFim(LocalDate.now().plusMonths(1));
            request.setAlunoId(1L);
            request.setTurmaId(1L);

            Mockito.when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
            Mockito.when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));
            Mockito.when(contratoRepository.countByTurmaAndDataFimGreaterThanEqual(turma, request.getDataInicio()))
                    .thenReturn(0L);
            Mockito.when(contratoRepository.existsByAlunoAndTurmaAndDataInicioAndDataFim(
                    aluno, turma, request.getDataInicio(), request.getDataFim())).thenReturn(false);

            Assertions.assertDoesNotThrow(() -> contratoService.criarContrato(request));

            Mockito.verify(contratoRepository, Mockito.times(1)).save(Mockito.any(Contrato.class));
            Mockito.verify(aulaService, Mockito.times(1)).gerarAulasParaContrato(Mockito.any(Contrato.class));
        }

        @Test
        @DisplayName("Deve lançar EntityNotFound quando professor não encontrado no contrato individual")
        void deveLancarEntityNotFoundQuandoProfessorNaoEncontradoIndividual() {
            Aluno aluno = new Aluno();
            aluno.setId(1L);
            aluno.setAtivo(true);

            ContratoRequest request = new ContratoRequest();
            request.setTipo("Individual");
            request.setDataInicio(LocalDate.now());
            request.setDataFim(LocalDate.now().plusMonths(1));
            request.setAlunoId(1L);
            request.setProfessorId(99L);
            request.setHorariosIds(List.of(1L));

            Mockito.when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
            Mockito.when(professorRepository.findById(99L)).thenReturn(Optional.empty());

            Assertions.assertThrows(EntityNotFound.class,
                    () -> contratoService.criarContrato(request));
        }

        @Test
        @DisplayName("Deve lançar BusinessRuleException quando horários não informados no contrato individual")
        void deveLancarExcecaoQuandoHorariosNaoInformadosIndividual() {
            Aluno aluno = new Aluno();
            aluno.setId(1L);
            aluno.setAtivo(true);

            Professor professor = new Professor();
            professor.setId(1L);

            ContratoRequest request = new ContratoRequest();
            request.setTipo("Individual");
            request.setDataInicio(LocalDate.now());
            request.setDataFim(LocalDate.now().plusMonths(1));
            request.setAlunoId(1L);
            request.setProfessorId(1L);
            request.setHorariosIds(null);

            Mockito.when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
            Mockito.when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));

            Assertions.assertThrows(BusinessRuleException.class,
                    () -> contratoService.criarContrato(request));
        }

        @Test
        @DisplayName("Deve criar contrato individual com sucesso")
        void deveCriarContratoIndividualComSucesso() {
            Horario horario = new Horario();
            horario.setId(1L);

            Aluno aluno = new Aluno();
            aluno.setId(1L);
            aluno.setAtivo(true);
            aluno.setHorarios(List.of(horario));

            Professor professor = new Professor();
            professor.setId(1L);
            professor.setHorarios(List.of(horario));

            ContratoRequest request = new ContratoRequest();
            request.setTipo("Individual");
            request.setDataInicio(LocalDate.now());
            request.setDataFim(LocalDate.now().plusMonths(1));
            request.setAlunoId(1L);
            request.setProfessorId(1L);
            request.setHorariosIds(List.of(1L));

            Mockito.when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
            Mockito.when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));
            Mockito.when(horarioRepository.findAllById(List.of(1L))).thenReturn(List.of(horario));
            Mockito.when(contratoRepository.existsByAlunoAndProfessorAndDataInicioAndDataFim(
                    aluno, professor, request.getDataInicio(), request.getDataFim())).thenReturn(false);

            Assertions.assertDoesNotThrow(() -> contratoService.criarContrato(request));

            Mockito.verify(contratoRepository, Mockito.times(1)).save(Mockito.any(Contrato.class));
            Mockito.verify(aulaService, Mockito.times(1)).gerarAulasParaContrato(Mockito.any(Contrato.class));
        }
    }

    @Nested
    public class DeletarContratoTestes {

        @Test
        @DisplayName("Deve lançar EntityNotFound quando contrato não encontrado para deleção")
        void deveLancarEntityNotFoundQuandoContratoNaoEncontrado() {
            Mockito.when(contratoRepository.findById(1L)).thenReturn(Optional.empty());

            Assertions.assertThrows(EntityNotFound.class,
                    () -> contratoService.deletarContrato(1L));
        }

        @Test
        @DisplayName("Deve deletar contrato com horários com sucesso")
        void deveDeletarContratoComHorariosComSucesso() {
            Horario horario = new Horario();
            horario.setId(1L);

            Contrato contrato = new Contrato();
            contrato.setId(1L);
            contrato.setHorarios(new ArrayList<>(List.of(horario)));

            Mockito.when(contratoRepository.findById(1L)).thenReturn(Optional.of(contrato));

            Assertions.assertDoesNotThrow(() -> contratoService.deletarContrato(1L));

            Assertions.assertTrue(contrato.getHorarios().isEmpty());
            Mockito.verify(contratoRepository, Mockito.times(1)).delete(contrato);
        }

        @Test
        @DisplayName("Deve deletar contrato sem horários com sucesso")
        void deveDeletarContratoSemHorariosComSucesso() {
            Contrato contrato = new Contrato();
            contrato.setId(1L);
            contrato.setHorarios(null);

            Mockito.when(contratoRepository.findById(1L)).thenReturn(Optional.of(contrato));

            Assertions.assertDoesNotThrow(() -> contratoService.deletarContrato(1L));

            Mockito.verify(contratoRepository, Mockito.times(1)).delete(contrato);
        }
    }

    @Nested
    public class ListarTodosContratosTestes {

        @Test
        @DisplayName("Deve retornar lista de contratos")
        void deveRetornarListaDeContratos() {
            Contrato contrato = new Contrato();
            contrato.setId(1L);
            contrato.setTipo("Grupo");

            Mockito.when(contratoRepository.findAll()).thenReturn(List.of(contrato));

            List<ContratoResponse> resultado = contratoService.listarTodosContratos();

            Assertions.assertEquals(1, resultado.size());
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não houver contratos")
        void deveRetornarListaVaziaQuandoNaoHouverContratos() {
            Mockito.when(contratoRepository.findAll()).thenReturn(Collections.emptyList());

            List<ContratoResponse> resultado = contratoService.listarTodosContratos();

            Assertions.assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    public class BuscarContratoPorIdTestes {

        @Test
        @DisplayName("Deve retornar contrato quando encontrado")
        void deveRetornarContratoQuandoEncontrado() {
            Contrato contrato = new Contrato();
            contrato.setId(1L);
            contrato.setTipo("Individual");

            Mockito.when(contratoRepository.findById(1L)).thenReturn(Optional.of(contrato));

            ContratoResponse resultado = contratoService.buscarContratoPorId(1L);

            Assertions.assertNotNull(resultado);
        }

        @Test
        @DisplayName("Deve lançar EntityNotFound quando contrato não encontrado")
        void deveLancarEntityNotFoundQuandoContratoNaoEncontrado() {
            Mockito.when(contratoRepository.findById(1L)).thenReturn(Optional.empty());

            Assertions.assertThrows(EntityNotFound.class,
                    () -> contratoService.buscarContratoPorId(1L));
        }
    }
}
