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
import school.sptech.back_end_PI.dto.turma.TurmaRequest;
import school.sptech.back_end_PI.entity.Horario;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.entity.Turma;
import school.sptech.back_end_PI.repository.AlunoRepository;
import school.sptech.back_end_PI.repository.HorarioRepository;
import school.sptech.back_end_PI.repository.ProfessorRepository;
import school.sptech.back_end_PI.repository.TurmaRepository;
import school.sptech.back_end_PI.services.TurmaService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TurmaServiceTest {

    @Mock
    private TurmaRepository turmaRepository;

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private HorarioRepository horarioRepository;

    @Mock
    private ProfessorRepository professorRepository;

    @InjectMocks
    private TurmaService turmaService;

    @Nested
    public class SalvarTestes {

        @Test
        @DisplayName("Deve lançar ResponseStatusException quando nome da turma já cadastrado")
        void deveLancarExcecaoQuandoNomeJaCadastrado() {
            TurmaRequest request = new TurmaRequest();
            request.setNome("Turma A");
            request.setHorariosIds(List.of(1L));

            Mockito.when(turmaRepository.existsByNome("Turma A")).thenReturn(true);

            Assertions.assertThrows(ResponseStatusException.class, () -> turmaService.salvar(request));
        }

        @Test
        @DisplayName("Deve lançar ResponseStatusException quando lista de horários é nula")
        void deveLancarExcecaoQuandoHorariosNulos() {
            TurmaRequest request = new TurmaRequest();
            request.setNome("Turma A");
            request.setHorariosIds(null);

            Mockito.when(turmaRepository.existsByNome("Turma A")).thenReturn(false);

            Assertions.assertThrows(ResponseStatusException.class, () -> turmaService.salvar(request));
        }

        @Test
        @DisplayName("Deve lançar ResponseStatusException quando lista de horários é vazia")
        void deveLancarExcecaoQuandoHorariosVazios() {
            TurmaRequest request = new TurmaRequest();
            request.setNome("Turma A");
            request.setHorariosIds(Collections.emptyList());

            Mockito.when(turmaRepository.existsByNome("Turma A")).thenReturn(false);

            Assertions.assertThrows(ResponseStatusException.class, () -> turmaService.salvar(request));
        }

        @Test
        @DisplayName("Deve lançar ResponseStatusException quando IDs de horários são inválidos")
        void deveLancarExcecaoQuandoIdsHorariosInvalidos() {
            TurmaRequest request = new TurmaRequest();
            request.setNome("Turma A");
            request.setHorariosIds(List.of(1L, 2L));

            Mockito.when(turmaRepository.existsByNome("Turma A")).thenReturn(false);
            Mockito.when(horarioRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(new Horario()));

            Assertions.assertThrows(ResponseStatusException.class, () -> turmaService.salvar(request));
        }

        @Test
        @DisplayName("Deve salvar turma com professor nulo por padrão")
        void deveSalvarTurmaComProfessorNulo() {
            TurmaRequest request = new TurmaRequest();
            request.setNome("Turma A");
            request.setNivel("Iniciante");
            request.setLimiteAlunos(10);
            request.setTipo("Grupo");
            request.setHorariosIds(List.of(1L));

            Horario horario = new Horario();
            horario.setId(1L);

            Turma turmaSalva = new Turma();
            turmaSalva.setNome("Turma A");
            turmaSalva.setProfessor(null);

            Mockito.when(turmaRepository.existsByNome("Turma A")).thenReturn(false);
            Mockito.when(horarioRepository.findAllById(List.of(1L))).thenReturn(List.of(horario));
            Mockito.when(turmaRepository.save(Mockito.any(Turma.class))).thenReturn(turmaSalva);

            Turma resultado = turmaService.salvar(request);

            Assertions.assertNull(resultado.getProfessor());
            Mockito.verify(turmaRepository, Mockito.times(1)).save(Mockito.any(Turma.class));
        }
    }

    @Nested
    public class AtualizarTestes {

        @Test
        @DisplayName("Deve lançar ResponseStatusException quando turma não encontrada")
        void deveLancarExcecaoQuandoTurmaNaoEncontrada() {
            TurmaRequest request = new TurmaRequest();
            request.setNome("Turma B");
            request.setHorariosIds(List.of(1L));

            Mockito.when(turmaRepository.findById(1L)).thenReturn(Optional.empty());

            Assertions.assertThrows(ResponseStatusException.class, () -> turmaService.atualizar(1L, request));
        }

        @Test
        @DisplayName("Deve lançar ResponseStatusException quando novo nome já pertence a outra turma")
        void deveLancarExcecaoQuandoNovoNomeJaExiste() {
            Turma turma = new Turma();
            turma.setId(1L);
            turma.setNome("Turma A");

            TurmaRequest request = new TurmaRequest();
            request.setNome("Turma B");
            request.setHorariosIds(List.of(1L));

            Mockito.when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));
            Mockito.when(turmaRepository.existsByNome("Turma B")).thenReturn(true);

            Assertions.assertThrows(ResponseStatusException.class, () -> turmaService.atualizar(1L, request));
        }

        @Test
        @DisplayName("Deve lançar ResponseStatusException quando IDs de horários são inválidos na atualização")
        void deveLancarExcecaoQuandoIdsHorariosInvalidosNaAtualizacao() {
            Turma turma = new Turma();
            turma.setId(1L);
            turma.setNome("Turma A");

            TurmaRequest request = new TurmaRequest();
            request.setNome("Turma A");
            request.setHorariosIds(List.of(1L, 2L));

            Mockito.when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));
            Mockito.when(horarioRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(new Horario()));

            Assertions.assertThrows(ResponseStatusException.class, () -> turmaService.atualizar(1L, request));
        }

        @Test
        @DisplayName("Deve atualizar turma com sucesso")
        void deveAtualizarTurmaComSucesso() {
            Turma turma = new Turma();
            turma.setId(1L);
            turma.setNome("Turma A");

            TurmaRequest request = new TurmaRequest();
            request.setNome("Turma A Atualizada");
            request.setNivel("Avançado");
            request.setLimiteAlunos(15);
            request.setTipo("Grupo");
            request.setHorariosIds(List.of(1L));

            Horario horario = new Horario();
            horario.setId(1L);

            Mockito.when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));
            Mockito.when(turmaRepository.existsByNome("Turma A Atualizada")).thenReturn(false);
            Mockito.when(horarioRepository.findAllById(List.of(1L))).thenReturn(List.of(horario));
            Mockito.when(turmaRepository.save(Mockito.any(Turma.class))).thenReturn(turma);

            Turma resultado = turmaService.atualizar(1L, request);

            Assertions.assertEquals("Turma A Atualizada", resultado.getNome());
        }
    }

    @Nested
    public class DeletarTestes {

        @Test
        @DisplayName("Deve lançar ResponseStatusException quando turma não encontrada para deleção")
        void deveLancarExcecaoQuandoTurmaNaoEncontrada() {
            Mockito.when(turmaRepository.findById(1L)).thenReturn(Optional.empty());

            Assertions.assertThrows(ResponseStatusException.class, () -> turmaService.deletar(1L));
        }

        @Test
        @DisplayName("Deve deletar turma com sucesso")
        void deveDeletarTurmaComSucesso() {
            Turma turma = new Turma();
            turma.setId(1L);

            Mockito.when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));

            Assertions.assertDoesNotThrow(() -> turmaService.deletar(1L));

            Mockito.verify(turmaRepository, Mockito.times(1)).delete(turma);
        }
    }

    @Nested
    public class AdicionarProfessorTestes {

        @Test
        @DisplayName("Deve lançar ResponseStatusException quando turma não encontrada")
        void deveLancarExcecaoQuandoTurmaNaoEncontrada() {
            Mockito.when(turmaRepository.findById(1L)).thenReturn(Optional.empty());

            Assertions.assertThrows(ResponseStatusException.class,
                    () -> turmaService.adicionarProfessor(1L, 1L));
        }

        @Test
        @DisplayName("Deve lançar ResponseStatusException quando professor não encontrado")
        void deveLancarExcecaoQuandoProfessorNaoEncontrado() {
            Turma turma = new Turma();
            turma.setId(1L);

            Mockito.when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));
            Mockito.when(professorRepository.findById(1L)).thenReturn(Optional.empty());

            Assertions.assertThrows(ResponseStatusException.class,
                    () -> turmaService.adicionarProfessor(1L, 1L));
        }

        @Test
        @DisplayName("Deve lançar ResponseStatusException quando professor está inativo")
        void deveLancarExcecaoQuandoProfessorInativo() {
            Turma turma = new Turma();
            turma.setId(1L);

            Professor professor = new Professor();
            professor.setId(1L);
            professor.setAtivo(false);

            Mockito.when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));
            Mockito.when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));

            Assertions.assertThrows(ResponseStatusException.class,
                    () -> turmaService.adicionarProfessor(1L, 1L));
        }

        @Test
        @DisplayName("Deve adicionar professor ativo à turma com sucesso")
        void deveAdicionarProfessorAtivoComSucesso() {
            Turma turma = new Turma();
            turma.setId(1L);

            Professor professor = new Professor();
            professor.setId(1L);
            professor.setAtivo(true);

            Mockito.when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));
            Mockito.when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));
            Mockito.when(turmaRepository.save(turma)).thenReturn(turma);

            Turma resultado = turmaService.adicionarProfessor(1L, 1L);

            Assertions.assertEquals(professor, resultado.getProfessor());
        }
    }

    @Nested
    public class RemoverProfessorTestes {

        @Test
        @DisplayName("Deve lançar ResponseStatusException quando turma não encontrada")
        void deveLancarExcecaoQuandoTurmaNaoEncontrada() {
            Mockito.when(turmaRepository.findById(1L)).thenReturn(Optional.empty());

            Assertions.assertThrows(ResponseStatusException.class,
                    () -> turmaService.removerProfessor(1L));
        }

        @Test
        @DisplayName("Deve lançar ResponseStatusException quando turma não possui professor")
        void deveLancarExcecaoQuandoTurmaSemProfessor() {
            Turma turma = new Turma();
            turma.setId(1L);
            turma.setProfessor(null);

            Mockito.when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));

            Assertions.assertThrows(ResponseStatusException.class,
                    () -> turmaService.removerProfessor(1L));
        }

        @Test
        @DisplayName("Deve remover professor da turma com sucesso")
        void deveRemoverProfessorComSucesso() {
            Professor professor = new Professor();
            professor.setId(1L);

            Turma turma = new Turma();
            turma.setId(1L);
            turma.setProfessor(professor);

            Mockito.when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));
            Mockito.when(turmaRepository.save(turma)).thenReturn(turma);

            Turma resultado = turmaService.removerProfessor(1L);

            Assertions.assertNull(resultado.getProfessor());
        }
    }

    @Nested
    public class ListarTodasTestes {

        @Test
        @DisplayName("Deve retornar todas as turmas")
        void deveRetornarTodasAsTurmas() {
            Turma turma = new Turma();
            turma.setNome("Turma A");

            Mockito.when(turmaRepository.findAll()).thenReturn(List.of(turma));

            List<Turma> resultado = turmaService.listarTodas();

            Assertions.assertEquals(1, resultado.size());
            Assertions.assertEquals("Turma A", resultado.get(0).getNome());
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não houver turmas")
        void deveRetornarListaVaziaQuandoNaoHouverTurmas() {
            Mockito.when(turmaRepository.findAll()).thenReturn(Collections.emptyList());

            List<Turma> resultado = turmaService.listarTodas();

            Assertions.assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    public class BuscarPorIdTestes {

        @Test
        @DisplayName("Deve retornar turma quando encontrada")
        void deveRetornarTurmaQuandoEncontrada() {
            Turma turma = new Turma();
            turma.setId(1L);
            turma.setNome("Turma A");

            Mockito.when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));

            Turma resultado = turmaService.buscarPorId(1L);

            Assertions.assertEquals("Turma A", resultado.getNome());
        }

        @Test
        @DisplayName("Deve lançar ResponseStatusException quando turma não encontrada")
        void deveLancarExcecaoQuandoTurmaNaoEncontrada() {
            Mockito.when(turmaRepository.findById(1L)).thenReturn(Optional.empty());

            Assertions.assertThrows(ResponseStatusException.class, () -> turmaService.buscarPorId(1L));
        }
    }
}
