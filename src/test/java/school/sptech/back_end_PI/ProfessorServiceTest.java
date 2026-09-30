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
import school.sptech.back_end_PI.entity.Permissao;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.exception.ConflictException;
import school.sptech.back_end_PI.exception.EntityNotFound;
import school.sptech.back_end_PI.repository.HorarioRepository;
import school.sptech.back_end_PI.repository.ProfessorRepository;
import school.sptech.back_end_PI.services.AuditService;
import school.sptech.back_end_PI.services.PermissaoService;
import school.sptech.back_end_PI.services.ProfessorService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class ProfessorServiceTest {

    @Mock
    private ProfessorRepository professorRepository;

    @Mock
    private HorarioRepository horarioRepository;

    @Mock
    private PermissaoService permissaoService;

    @Mock
    private AuditService audit;

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
        @DisplayName("Deve repassar ao PermissaoService as telas do cadastro e salvá-las no professor")
        void deveSalvarTelasLiberadasNoCadastro() {
            ProfessorRequest request = new ProfessorRequest();
            request.setNome("Carlos");
            request.setEmail("carlos@email.com");
            request.setTelefone("11999999999");
            request.setHorariosIds(List.of(1L));
            request.setPermissoes(List.of("TELA_AGENDA"));

            Horario horario = new Horario();
            horario.setId(1L);

            Set<Permissao> telas = new LinkedHashSet<>(List.of(new Permissao(1, "TELA_AGENDA")));

            Mockito.when(professorRepository.existsByEmail("carlos@email.com")).thenReturn(false);
            Mockito.when(horarioRepository.findAllById(List.of(1L))).thenReturn(List.of(horario));
            Mockito.when(permissaoService.resolverPorNomes(List.of("TELA_AGENDA"))).thenReturn(telas);
            Mockito.when(professorRepository.existsByCodigoAcesso(Mockito.anyString())).thenReturn(false);
            Mockito.when(professorRepository.save(Mockito.any(Professor.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

            Professor resultado = professorService.create(request);

            Mockito.verify(permissaoService, Mockito.times(1)).resolverPorNomes(List.of("TELA_AGENDA"));
            Assertions.assertEquals(List.of("TELA_AGENDA"), resultado.nomesPermissoes());
        }

        @Test
        @DisplayName("Deve lançar exceção quando horários não são encontrados")
        void deveLancarExcecaoQuandoHorariosNaoEncontrados() {
            ProfessorRequest request = new ProfessorRequest();
            request.setEmail("prof@email.com");
            request.setHorariosIds(List.of(1L));

            Mockito.when(professorRepository.existsByEmail("prof@email.com")).thenReturn(false);
            Mockito.when(horarioRepository.findAllById(List.of(1L))).thenReturn(Collections.emptyList());

            Assertions.assertThrows(Exception.class, () -> professorService.create(request));
        }

        @Test
        @DisplayName("Deve criar professor com sucesso")
        void deveCriarProfessorComSucesso() {
            ProfessorRequest request = new ProfessorRequest();
            request.setNome("Carlos");
            request.setEmail("carlos@email.com");
            request.setTelefone("119999999");
            request.setHorariosIds(List.of(1L));

            Horario horario = new Horario();
            horario.setId(1L);

            Professor professorSalvo = new Professor();
            professorSalvo.setNome("Carlos");

            Mockito.when(professorRepository.existsByEmail("carlos@email.com")).thenReturn(false);
            Mockito.when(horarioRepository.findAllById(List.of(1L))).thenReturn(List.of(horario));
            Mockito.when(professorRepository.existsByCodigoAcesso(Mockito.anyString())).thenReturn(false);
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

            Mockito.when(professorRepository.findById(1L)).thenReturn(Optional.empty());

            Assertions.assertThrows(EntityNotFound.class, () -> professorService.atualizar(1L, request));
        }

        @Test
        @DisplayName("Deve lançar ConflictException quando novo email já pertence a outro professor")
        void deveLancarConflictExceptionQuandoNovoEmailJaCadastrado() {
            Professor professor = new Professor();
            professor.setId(1L);
            professor.setEmail("prof@email.com");

            ProfessorRequest request = new ProfessorRequest();
            request.setEmail("outro@email.com");

            Mockito.when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));
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

            ProfessorRequest request = new ProfessorRequest();
            request.setNome("Carlos Atualizado");
            request.setEmail("prof@email.com");
            request.setTelefone("119999999");

            Mockito.when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));
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

            Horario novoHorario = new Horario();
            novoHorario.setId(2L);

            ProfessorRequest request = new ProfessorRequest();
            request.setNome("Carlos");
            request.setEmail("prof@email.com");
            request.setTelefone("119999999");
            request.setHorariosIds(List.of(2L));

            Mockito.when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));
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

    @Nested
    public class PermissoesTestes {

        private ProfessorRequest requestComPermissoes() {
            ProfessorRequest request = new ProfessorRequest();
            request.setNome("Carlos");
            request.setEmail("carlos@email.com");
            request.setTelefone("119999999");
            request.setHorariosIds(List.of(1L));
            request.setPermissoes(List.of("TELA_AGENDA", "TELA_ALUNOS"));
            return request;
        }

        @Test
        @DisplayName("Deve gravar as telas selecionadas no cadastro do professor")
        void deveGravarPermissoesNoCadastro() {
            Permissao agenda = new Permissao(1, "TELA_AGENDA");
            Permissao alunos = new Permissao(2, "TELA_ALUNOS");

            Horario horario = new Horario();
            horario.setId(1L);

            Set<Permissao> liberadas = new LinkedHashSet<>(List.of(agenda, alunos));

            Mockito.when(professorRepository.existsByEmail("carlos@email.com")).thenReturn(false);
            Mockito.when(horarioRepository.findAllById(List.of(1L))).thenReturn(List.of(horario));
            Mockito.when(permissaoService.resolverPorNomes(List.of("TELA_AGENDA", "TELA_ALUNOS")))
                    .thenReturn(liberadas);
            Mockito.when(professorRepository.save(Mockito.any(Professor.class)))
                    .thenAnswer(invocacao -> invocacao.getArgument(0));

            Professor resultado = professorService.create(requestComPermissoes());

            Assertions.assertEquals(2, resultado.getPermissoes().size());
            Assertions.assertEquals(List.of("TELA_AGENDA", "TELA_ALUNOS"), resultado.nomesPermissoes());
        }

        @Test
        @DisplayName("Deve cadastrar professor sem nenhuma tela quando o payload não vier com permissões")
        void deveCadastrarSemPermissoesQuandoPayloadNaoTiver() {
            ProfessorRequest request = requestComPermissoes();
            request.setPermissoes(null);

            Horario horario = new Horario();
            horario.setId(1L);

            Mockito.when(professorRepository.existsByEmail("carlos@email.com")).thenReturn(false);
            Mockito.when(horarioRepository.findAllById(List.of(1L))).thenReturn(List.of(horario));
            Mockito.when(permissaoService.resolverPorNomes(null)).thenReturn(new LinkedHashSet<>());
            Mockito.when(professorRepository.save(Mockito.any(Professor.class)))
                    .thenAnswer(invocacao -> invocacao.getArgument(0));

            Professor resultado = professorService.create(request);

            Assertions.assertTrue(resultado.getPermissoes().isEmpty());
        }

        @Test
        @DisplayName("Deve substituir as permissões ao chamar atualizarPermissoes")
        void deveSubstituirPermissoesAoAtualizar() {
            Professor professor = new Professor();
            professor.setId(1L);
            professor.setPermissoes(new LinkedHashSet<>(List.of(new Permissao(1, "TELA_GERAL"))));

            Set<Permissao> novas = new LinkedHashSet<>(List.of(new Permissao(3, "TELA_TURMAS")));

            Mockito.when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));
            Mockito.when(permissaoService.resolverPorNomes(List.of("TELA_TURMAS"))).thenReturn(novas);
            Mockito.when(professorRepository.save(Mockito.any(Professor.class)))
                    .thenAnswer(invocacao -> invocacao.getArgument(0));

            Professor resultado = professorService.atualizarPermissoes(1L, List.of("TELA_TURMAS"));

            Assertions.assertEquals(List.of("TELA_TURMAS"), resultado.nomesPermissoes());
        }

        @Test
        @DisplayName("Deve revogar todas as telas quando atualizarPermissoes recebe lista vazia")
        void deveRevogarTodasAsTelas() {
            Professor professor = new Professor();
            professor.setId(1L);
            professor.setPermissoes(new LinkedHashSet<>(List.of(new Permissao(1, "TELA_GERAL"))));

            Mockito.when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));
            Mockito.when(permissaoService.resolverPorNomes(List.of())).thenReturn(new LinkedHashSet<>());
            Mockito.when(professorRepository.save(Mockito.any(Professor.class)))
                    .thenAnswer(invocacao -> invocacao.getArgument(0));

            Professor resultado = professorService.atualizarPermissoes(1L, List.of());

            Assertions.assertTrue(resultado.nomesPermissoes().isEmpty());
        }

        @Test
        @DisplayName("Deve manter as permissões atuais quando atualizar não receber a lista")
        void deveManterPermissoesQuandoAtualizarNaoReceberLista() {
            ProfessorRequest request = new ProfessorRequest();
            request.setNome("Carlos");
            request.setEmail("carlos@email.com");
            request.setTelefone("119999999");
            request.setPermissoes(null);

            Professor professor = new Professor();
            professor.setId(1L);
            professor.setEmail("carlos@email.com");
            professor.setPermissoes(new LinkedHashSet<>(List.of(new Permissao(1, "TELA_GERAL"))));

            Mockito.when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));
            Mockito.when(professorRepository.save(Mockito.any(Professor.class)))
                    .thenAnswer(invocacao -> invocacao.getArgument(0));

            Professor resultado = professorService.atualizar(1L, request);

            Assertions.assertEquals(List.of("TELA_GERAL"), resultado.nomesPermissoes());
            Mockito.verify(permissaoService, Mockito.never()).resolverPorNomes(Mockito.any());
        }
    }
}
