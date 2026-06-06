package school.sptech.back_end_PI;

import jakarta.persistence.EntityNotFoundException;
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
import school.sptech.back_end_PI.dto.aluno.HorarioAlunoProfessorRequest;
import school.sptech.back_end_PI.dto.professor.ProfessorRequest;
import school.sptech.back_end_PI.dto.professor.ProfessorResponse;
import school.sptech.back_end_PI.entity.Horario;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.entity.TipoProfessor;
import school.sptech.back_end_PI.exception.ConflictException;
import school.sptech.back_end_PI.exception.EntityNotFound;
import school.sptech.back_end_PI.repository.HorarioRepository;
import school.sptech.back_end_PI.repository.ProfessorRepository;
import school.sptech.back_end_PI.repository.TipoProfessorRepository;
import school.sptech.back_end_PI.services.ProfessorService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProfessorServiceTest {

    @Mock
    private ProfessorRepository professorRepository;

    @Mock
    private TipoProfessorRepository tipoProfessorRepository;

    @Mock
    private HorarioRepository horarioRepository;

    @InjectMocks
    private ProfessorService professorService;

    @Nested
    public class CreateTestes {

        @Test
        @DisplayName("Deve lançar ConflictException quando email já cadastrado")
        void deveLancarConflictExceptionQuandoEmailJaCadastrado() {
            ProfessorRequest request = new ProfessorRequest();
            request.setEmail("prof@email.com");

            Mockito.when(professorRepository.existsByEmail("prof@email.com")).thenReturn(true);

            Assertions.assertThrows(ConflictException.class, () -> professorService.create(request));
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando tipo de professor não encontrado")
        void deveLancarEntityNotFoundExceptionQuandoTipoNaoEncontrado() {
            ProfessorRequest request = new ProfessorRequest();
            request.setEmail("prof@email.com");
            request.setIdTipoProfessor(99);

            Mockito.when(professorRepository.existsByEmail("prof@email.com")).thenReturn(false);
            Mockito.when(tipoProfessorRepository.findById(99)).thenReturn(Optional.empty());

            Assertions.assertThrows(EntityNotFoundException.class, () -> professorService.create(request));
        }

        @Test
        @DisplayName("Deve lançar exceção quando horários não são encontrados")
        void deveLancarExcecaoQuandoHorariosNaoEncontrados() {
            ProfessorRequest request = new ProfessorRequest();
            request.setEmail("prof@email.com");
            request.setIdTipoProfessor(1);
            request.setHorariosIds(List.of(1L));

            TipoProfessor tipo = new TipoProfessor();
            tipo.setId(1);

            Mockito.when(professorRepository.existsByEmail("prof@email.com")).thenReturn(false);
            Mockito.when(tipoProfessorRepository.findById(1)).thenReturn(Optional.of(tipo));
            Mockito.when(horarioRepository.findAllById(List.of(1L))).thenReturn(Collections.emptyList());

            Assertions.assertThrows(Exception.class, () -> professorService.create(request));
        }

        @Test
        @DisplayName("Deve criar professor com sucesso")
        void deveCriarProfessorComSucesso() {
            ProfessorRequest request = new ProfessorRequest();
            request.setNome("Carlos");
            request.setEmail("carlos@email.com");
            request.setTelefone(119999999);
            request.setSenha("senha123");
            request.setIdTipoProfessor(1);
            request.setHorariosIds(List.of(1L));

            TipoProfessor tipo = new TipoProfessor();
            tipo.setId(1);

            Horario horario = new Horario();
            horario.setId(1L);

            Professor professorSalvo = new Professor();
            professorSalvo.setNome("Carlos");

            Mockito.when(professorRepository.existsByEmail("carlos@email.com")).thenReturn(false);
            Mockito.when(tipoProfessorRepository.findById(1)).thenReturn(Optional.of(tipo));
            Mockito.when(horarioRepository.findAllById(List.of(1L))).thenReturn(List.of(horario));
            Mockito.when(professorRepository.save(Mockito.any(Professor.class))).thenReturn(professorSalvo);

            Professor resultado = professorService.create(request);

            Assertions.assertEquals("Carlos", resultado.getNome());
            Mockito.verify(professorRepository, Mockito.times(1)).save(Mockito.any(Professor.class));
        }
    }

    @Nested
    public class FindAllTestes {

        @Test
        @DisplayName("Deve retornar lista de professores")
        void deveRetornarListaDeProfessores() {
            Professor professor = new Professor();
            professor.setNome("Carlos");

            Mockito.when(professorRepository.findAll()).thenReturn(List.of(professor));

            List<ProfessorResponse> resultado = professorService.findAll();

            Assertions.assertFalse(resultado.isEmpty());
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não houver professores")
        void deveRetornarListaVaziaQuandoNaoHouverProfessores() {
            Mockito.when(professorRepository.findAll()).thenReturn(Collections.emptyList());

            List<ProfessorResponse> resultado = professorService.findAll();

            Assertions.assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    public class FindByIdTestes {

        @Test
        @DisplayName("Deve retornar professor quando encontrado")
        void deveRetornarProfessorQuandoEncontrado() {
            Professor professor = new Professor();
            professor.setId(1L);
            professor.setNome("Carlos");

            Mockito.when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));

            ProfessorResponse resultado = professorService.findById(1L);

            Assertions.assertNotNull(resultado);
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando professor não encontrado")
        void deveLancarEntityNotFoundExceptionQuandoProfessorNaoEncontrado() {
            Mockito.when(professorRepository.findById(1L)).thenReturn(Optional.empty());

            Assertions.assertThrows(EntityNotFoundException.class, () -> professorService.findById(1L));
        }
    }

    @Nested
    public class DeleteTestes {

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando professor não encontrado para deleção")
        void deveLancarEntityNotFoundExceptionQuandoProfessorNaoEncontrado() {
            Mockito.when(professorRepository.findById(1L)).thenReturn(Optional.empty());

            Assertions.assertThrows(EntityNotFoundException.class, () -> professorService.delete(1L));
        }

        @Test
        @DisplayName("Deve deletar professor com sucesso")
        void deveDeletarProfessorComSucesso() {
            Professor professor = new Professor();
            professor.setId(1L);
            professor.setHorarios(new ArrayList<>());

            Mockito.when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));
            Mockito.when(professorRepository.saveAndFlush(professor)).thenReturn(professor);

            Assertions.assertDoesNotThrow(() -> professorService.delete(1L));

            Mockito.verify(professorRepository, Mockito.times(1)).delete(professor);
        }
    }

    @Nested
    public class AtualizarTestes {

        @Test
        @DisplayName("Deve lançar EntityNotFound quando professor não encontrado para atualização")
        void deveLancarEntityNotFoundQuandoProfessorNaoEncontrado() {
            ProfessorRequest request = new ProfessorRequest();
            request.setEmail("prof@email.com");
            request.setIdTipoProfessor(1);

            Mockito.when(professorRepository.findById(1L)).thenReturn(Optional.empty());

            Assertions.assertThrows(EntityNotFound.class, () -> professorService.atualizar(1L, request));
        }

        @Test
        @DisplayName("Deve lançar EntityNotFound quando tipo de professor não encontrado na atualização")
        void deveLancarEntityNotFoundQuandoTipoNaoEncontrado() {
            Professor professor = new Professor();
            professor.setId(1L);
            professor.setEmail("prof@email.com");

            ProfessorRequest request = new ProfessorRequest();
            request.setEmail("prof@email.com");
            request.setIdTipoProfessor(99);

            Mockito.when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));
            Mockito.when(tipoProfessorRepository.findById(99)).thenReturn(Optional.empty());

            Assertions.assertThrows(EntityNotFound.class, () -> professorService.atualizar(1L, request));
        }

        @Test
        @DisplayName("Deve lançar ConflictException quando novo email já pertence a outro professor")
        void deveLancarConflictExceptionQuandoNovoEmailJaCadastrado() {
            Professor professor = new Professor();
            professor.setId(1L);
            professor.setEmail("prof@email.com");

            TipoProfessor tipo = new TipoProfessor();
            tipo.setId(1);

            ProfessorRequest request = new ProfessorRequest();
            request.setEmail("outro@email.com");
            request.setIdTipoProfessor(1);

            Mockito.when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));
            Mockito.when(tipoProfessorRepository.findById(1)).thenReturn(Optional.of(tipo));
            Mockito.when(professorRepository.existsProfessorByEmail("outro@email.com")).thenReturn(true);

            Assertions.assertThrows(ConflictException.class, () -> professorService.atualizar(1L, request));
        }

        @Test
        @DisplayName("Deve atualizar professor com sucesso")
        void deveAtualizarProfessorComSucesso() {
            Professor professor = new Professor();
            professor.setId(1L);
            professor.setEmail("prof@email.com");
            professor.setNome("Carlos");

            TipoProfessor tipo = new TipoProfessor();
            tipo.setId(1);

            ProfessorRequest request = new ProfessorRequest();
            request.setNome("Carlos Atualizado");
            request.setEmail("prof@email.com");
            request.setTelefone(119999999);
            request.setSenha("senha123");
            request.setIdTipoProfessor(1);

            Mockito.when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));
            Mockito.when(tipoProfessorRepository.findById(1)).thenReturn(Optional.of(tipo));
            Mockito.when(professorRepository.save(Mockito.any(Professor.class))).thenReturn(professor);

            Professor resultado = professorService.atualizar(1L, request);

            Assertions.assertEquals("Carlos Atualizado", resultado.getNome());
        }

        @Test
        @DisplayName("Deve atualizar horários do professor quando informados")
        void deveAtualizarHorariosQuandoInformados() {
            Professor professor = new Professor();
            professor.setId(1L);
            professor.setEmail("prof@email.com");
            professor.setHorarios(new ArrayList<>());

            TipoProfessor tipo = new TipoProfessor();
            tipo.setId(1);

            Horario novoHorario = new Horario();
            novoHorario.setId(2L);

            ProfessorRequest request = new ProfessorRequest();
            request.setNome("Carlos");
            request.setEmail("prof@email.com");
            request.setTelefone(119999999);
            request.setSenha("senha123");
            request.setIdTipoProfessor(1);
            request.setHorariosIds(List.of(2L));

            Mockito.when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));
            Mockito.when(tipoProfessorRepository.findById(1)).thenReturn(Optional.of(tipo));
            Mockito.when(horarioRepository.findAllById(List.of(2L))).thenReturn(List.of(novoHorario));
            Mockito.when(professorRepository.save(Mockito.any(Professor.class))).thenReturn(professor);

            professorService.atualizar(1L, request);

            Assertions.assertEquals(List.of(novoHorario), professor.getHorarios());
        }
    }

    @Nested
    public class BuscarCompatíveisTestes {

        @Test
        @DisplayName("Deve retornar professores compatíveis com os horários informados")
        void deveRetornarProfessoresCompativeis() {
            HorarioAlunoProfessorRequest request = new HorarioAlunoProfessorRequest();
            request.setAlunoHorariosIds(List.of(1L, 2L));

            Professor professor = new Professor();
            professor.setId(1L);

            Mockito.when(professorRepository.buscarProfessoresCompativeis(List.of(1L, 2L)))
                    .thenReturn(List.of(professor));

            List<Professor> resultado = professorService.buscarCompativeis(request);

            Assertions.assertEquals(1, resultado.size());
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não houver professores compatíveis")
        void deveRetornarListaVaziaQuandoNaoHouverCompativeis() {
            HorarioAlunoProfessorRequest request = new HorarioAlunoProfessorRequest();
            request.setAlunoHorariosIds(List.of(99L));

            Mockito.when(professorRepository.buscarProfessoresCompativeis(List.of(99L)))
                    .thenReturn(Collections.emptyList());

            List<Professor> resultado = professorService.buscarCompativeis(request);

            Assertions.assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    public class ReativarTestes {

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando professor não encontrado para reativação")
        void deveLancarEntityNotFoundExceptionQuandoProfessorNaoEncontrado() {
            Mockito.when(professorRepository.buscarPorIdIgnorandoFiltro(1L)).thenReturn(Optional.empty());

            Assertions.assertThrows(EntityNotFoundException.class, () -> professorService.reativar(1L));
        }

        @Test
        @DisplayName("Deve lançar ResponseStatusException quando professor já está ativo")
        void deveLancarExcecaoQuandoProfessorJaEstaAtivo() {
            Professor professor = new Professor();
            professor.setId(1L);
            professor.setAtivo(true);

            Mockito.when(professorRepository.buscarPorIdIgnorandoFiltro(1L)).thenReturn(Optional.of(professor));

            Assertions.assertThrows(ResponseStatusException.class, () -> professorService.reativar(1L));
        }

        @Test
        @DisplayName("Deve reativar professor inativo com sucesso")
        void deveReativarProfessorInativoComSucesso() {
            Professor professor = new Professor();
            professor.setId(1L);
            professor.setAtivo(false);

            Mockito.when(professorRepository.buscarPorIdIgnorandoFiltro(1L)).thenReturn(Optional.of(professor));

            Professor resultado = professorService.reativar(1L);

            Assertions.assertTrue(resultado.getAtivo());
            Mockito.verify(professorRepository, Mockito.times(1)).reativarPorId(1L);
        }
    }
}
