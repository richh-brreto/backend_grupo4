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
import org.springframework.web.server.ResponseStatusException;
import school.sptech.back_end_PI.dto.aluno.AlunoRequest;
import school.sptech.back_end_PI.entity.Aluno;
import school.sptech.back_end_PI.entity.Contrato;
import school.sptech.back_end_PI.entity.Horario;
import school.sptech.back_end_PI.exception.ConflictException;
import school.sptech.back_end_PI.exception.EntityNotFound;
import school.sptech.back_end_PI.repository.AlunoRepository;
import school.sptech.back_end_PI.repository.ContratoRepository;
import school.sptech.back_end_PI.repository.HorarioRepository;
import school.sptech.back_end_PI.repository.ProfessorRepository;
import school.sptech.back_end_PI.repository.TurmaRepository;
import school.sptech.back_end_PI.services.AlunoService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AlunoServiceTest {

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private ProfessorRepository professorRepository;

    @Mock
    private HorarioRepository horarioRepository;

    @Mock
    private TurmaRepository turmaRepository;

    @Mock
    private ContratoRepository contratoRepository;

    @InjectMocks
    private AlunoService alunoService;

    @Nested
    public class GetAllTestes {

        @Test
        @DisplayName("Deve retornar lista de alunos")
        void deveRetornarListaDeAlunos() {
            Aluno aluno = new Aluno();
            aluno.setNome("João");

            Mockito.when(alunoRepository.findAll()).thenReturn(List.of(aluno));

            List<Aluno> resultado = alunoService.getAll();

            Assertions.assertEquals(1, resultado.size());
            Assertions.assertEquals("João", resultado.get(0).getNome());
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não houver alunos")
        void deveRetornarListaVaziaQuandoNaoHouverAlunos() {
            Mockito.when(alunoRepository.findAll()).thenReturn(Collections.emptyList());

            List<Aluno> resultado = alunoService.getAll();

            Assertions.assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    public class GetByIdTestes {

        @Test
        @DisplayName("Deve retornar aluno quando encontrado")
        void deveRetornarAlunoQuandoEncontrado() {
            Aluno aluno = new Aluno();
            aluno.setId(1L);
            aluno.setNome("João");

            Mockito.when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));

            Aluno resultado = alunoService.getById(1L);

            Assertions.assertEquals("João", resultado.getNome());
        }

        @Test
        @DisplayName("Deve lançar EntityNotFound quando aluno não encontrado")
        void deveLancarEntityNotFoundQuandoAlunoNaoEncontrado() {
            Mockito.when(alunoRepository.findById(1L)).thenReturn(Optional.empty());

            Assertions.assertThrows(EntityNotFound.class, () -> alunoService.getById(1L));
        }
    }

    @Nested
    public class GetByTurmaIdTestes {

        @Test
        @DisplayName("Deve lançar EntityNotFound quando turma não encontrada")
        void deveLancarEntityNotFoundQuandoTurmaNaoEncontrada() {
            Mockito.when(turmaRepository.existsById(1L)).thenReturn(false);

            Assertions.assertThrows(EntityNotFound.class, () -> alunoService.getByTurmaId(1L));
        }

        @Test
        @DisplayName("Deve retornar apenas alunos ativos da turma")
        void deveRetornarApenasAlunosAtivosDaTurma() {
            Aluno alunoAtivo = new Aluno();
            alunoAtivo.setAtivo(true);

            Aluno alunoInativo = new Aluno();
            alunoInativo.setAtivo(false);

            Contrato contratoAtivo = new Contrato();
            contratoAtivo.setAluno(alunoAtivo);

            Contrato contratoInativo = new Contrato();
            contratoInativo.setAluno(alunoInativo);

            Mockito.when(turmaRepository.existsById(1L)).thenReturn(true);
            Mockito.when(contratoRepository.findByTurmaId(1L)).thenReturn(List.of(contratoAtivo, contratoInativo));

            List<Aluno> resultado = alunoService.getByTurmaId(1L);

            Assertions.assertEquals(1, resultado.size());
            Assertions.assertTrue(resultado.get(0).getAtivo());
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando turma não possui contratos")
        void deveRetornarListaVaziaQuandoTurmaSemContratos() {
            Mockito.when(turmaRepository.existsById(1L)).thenReturn(true);
            Mockito.when(contratoRepository.findByTurmaId(1L)).thenReturn(Collections.emptyList());

            List<Aluno> resultado = alunoService.getByTurmaId(1L);

            Assertions.assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    public class CreateTestes {

        @Test
        @DisplayName("Deve lançar ConflictException quando email já cadastrado")
        void deveLancarConflictExceptionQuandoEmailJaCadastrado() {
            AlunoRequest request = new AlunoRequest();
            request.setEmail("joao@email.com");

            Mockito.when(alunoRepository.existsAlunoByEmail("joao@email.com")).thenReturn(true);

            Assertions.assertThrows(ConflictException.class, () -> alunoService.create(request));
        }

        @Test
        @DisplayName("Deve lançar exceção quando horários não forem encontrados")
        void deveLancarExcecaoQuandoHorariosNaoEncontrados() {
            AlunoRequest request = new AlunoRequest();
            request.setEmail("joao@email.com");
            request.setHorariosIds(List.of(1L));

            Mockito.when(alunoRepository.existsAlunoByEmail("joao@email.com")).thenReturn(false);
            Mockito.when(horarioRepository.findAllById(List.of(1L))).thenReturn(Collections.emptyList());

            Assertions.assertThrows(Exception.class, () -> alunoService.create(request));
        }

        @Test
        @DisplayName("Deve criar aluno com sucesso")
        void deveCriarAlunoComSucesso() {
            AlunoRequest request = new AlunoRequest();
            request.setNome("João");
            request.setEmail("joao@email.com");
            request.setTelefone("11999999999");
            request.setNivel("Iniciante");
            request.setHorariosIds(List.of(1L));

            Horario horario = new Horario();
            horario.setId(1L);

            Aluno alunoSalvo = new Aluno();
            alunoSalvo.setNome("João");
            alunoSalvo.setEmail("joao@email.com");

            Mockito.when(alunoRepository.existsAlunoByEmail("joao@email.com")).thenReturn(false);
            Mockito.when(horarioRepository.findAllById(List.of(1L))).thenReturn(List.of(horario));
            Mockito.when(alunoRepository.save(Mockito.any(Aluno.class))).thenReturn(alunoSalvo);

            Aluno resultado = alunoService.create(request);

            Assertions.assertEquals("João", resultado.getNome());
            Mockito.verify(alunoRepository, Mockito.times(1)).save(Mockito.any(Aluno.class));
        }
    }

    @Nested
    public class UpdateTestes {

        @Test
        @DisplayName("Deve lançar EntityNotFound quando aluno não encontrado para atualização")
        void deveLancarEntityNotFoundQuandoAlunoNaoEncontrado() {
            AlunoRequest request = new AlunoRequest();
            request.setEmail("joao@email.com");

            Mockito.when(alunoRepository.findById(1L)).thenReturn(Optional.empty());

            Assertions.assertThrows(EntityNotFound.class, () -> alunoService.update(1L, request));
        }

        @Test
        @DisplayName("Deve lançar ConflictException quando novo email já pertence a outro aluno")
        void deveLancarConflictExceptionQuandoNovoEmailJaCadastrado() {
            Aluno aluno = new Aluno();
            aluno.setId(1L);
            aluno.setEmail("joao@email.com");

            AlunoRequest request = new AlunoRequest();
            request.setEmail("outro@email.com");

            Mockito.when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
            Mockito.when(alunoRepository.existsAlunoByEmail("outro@email.com")).thenReturn(true);

            Assertions.assertThrows(ConflictException.class, () -> alunoService.update(1L, request));
        }

        @Test
        @DisplayName("Deve atualizar aluno sem trocar email com sucesso")
        void deveAtualizarAlunoSemTrocarEmailComSucesso() {
            Aluno aluno = new Aluno();
            aluno.setId(1L);
            aluno.setEmail("joao@email.com");

            AlunoRequest request = new AlunoRequest();
            request.setNome("João Atualizado");
            request.setEmail("joao@email.com");
            request.setTelefone("11999999999");
            request.setNivel("Intermediário");

            Mockito.when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
            Mockito.when(alunoRepository.save(Mockito.any(Aluno.class))).thenReturn(aluno);

            Aluno resultado = alunoService.update(1L, request);

            Assertions.assertEquals("João Atualizado", resultado.getNome());
            Mockito.verify(alunoRepository, Mockito.times(1)).save(aluno);
        }

        @Test
        @DisplayName("Deve atualizar horários do aluno quando informados")
        void deveAtualizarHorariosQuandoInformados() {
            Aluno aluno = new Aluno();
            aluno.setId(1L);
            aluno.setEmail("joao@email.com");
            aluno.setHorarios(new ArrayList<>());

            Horario novoHorario = new Horario();
            novoHorario.setId(2L);

            AlunoRequest request = new AlunoRequest();
            request.setNome("João");
            request.setEmail("joao@email.com");
            request.setTelefone("11999999999");
            request.setNivel("Avançado");
            request.setHorariosIds(List.of(2L));

            Mockito.when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
            Mockito.when(horarioRepository.findAllById(List.of(2L))).thenReturn(List.of(novoHorario));
            Mockito.when(alunoRepository.save(Mockito.any(Aluno.class))).thenReturn(aluno);

            alunoService.update(1L, request);

            Assertions.assertEquals(List.of(novoHorario), aluno.getHorarios());
        }
    }

    @Nested
    public class DeleteTestes {

        @Test
        @DisplayName("Deve lançar EntityNotFound quando aluno não encontrado para deleção")
        void deveLancarEntityNotFoundQuandoAlunoNaoEncontrado() {
            Mockito.when(alunoRepository.findById(1L)).thenReturn(Optional.empty());

            Assertions.assertThrows(EntityNotFound.class, () -> alunoService.delete(1L));
        }

        @Test
        @DisplayName("Deve deletar aluno e seus contratos com sucesso")
        void deveDeletarAlunoEContratosComSucesso() {
            Aluno aluno = new Aluno();
            aluno.setId(1L);
            aluno.setHorarios(new ArrayList<>());

            Contrato contrato = new Contrato();
            contrato.setId(10L);

            Mockito.when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
            Mockito.when(contratoRepository.findByAlunoId(1L)).thenReturn(List.of(contrato));

            Assertions.assertDoesNotThrow(() -> alunoService.delete(1L));

            Mockito.verify(contratoRepository, Mockito.times(1)).deleteAll(List.of(contrato));
            Mockito.verify(alunoRepository, Mockito.times(1)).delete(aluno);
        }
    }

    @Nested
    public class ReativarTestes {

        @Test
        @DisplayName("Deve lançar EntityNotFound quando aluno não encontrado para reativação")
        void deveLancarEntityNotFoundQuandoAlunoNaoEncontrado() {
            Mockito.when(alunoRepository.buscarPorIdIgnorandoFiltro(1L)).thenReturn(Optional.empty());

            Assertions.assertThrows(EntityNotFound.class, () -> alunoService.reativar(1L));
        }

        @Test
        @DisplayName("Deve lançar ResponseStatusException quando aluno já está ativo")
        void deveLancarExcecaoQuandoAlunoJaEstaAtivo() {
            Aluno aluno = new Aluno();
            aluno.setId(1L);
            aluno.setAtivo(true);

            Mockito.when(alunoRepository.buscarPorIdIgnorandoFiltro(1L)).thenReturn(Optional.of(aluno));

            Assertions.assertThrows(ResponseStatusException.class, () -> alunoService.reativar(1L));
        }

        @Test
        @DisplayName("Deve reativar aluno inativo com sucesso")
        void deveReativarAlunoInativoComSucesso() {
            Aluno aluno = new Aluno();
            aluno.setId(1L);
            aluno.setAtivo(false);

            Mockito.when(alunoRepository.buscarPorIdIgnorandoFiltro(1L)).thenReturn(Optional.of(aluno));

            Aluno resultado = alunoService.reativar(1L);

            Assertions.assertTrue(resultado.getAtivo());
            Mockito.verify(alunoRepository, Mockito.times(1)).reativarPorId(1L);
        }
    }
}
