package school.sptech.back_end_PI;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.sptech.back_end_PI.dto.comunicado.ComunicadoRequest;
import school.sptech.back_end_PI.entity.Comunicado;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.exception.EntityNotFound;
import school.sptech.back_end_PI.repository.ComunicadoRepository;
import school.sptech.back_end_PI.services.AuditService;
import school.sptech.back_end_PI.services.ComunicadoService;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ComunicadoServiceTest {

    @Mock
    private ComunicadoRepository comunicadoRepository;

    @Mock
    private AuditService audit;

    @InjectMocks
    private ComunicadoService comunicadoService;

    private ComunicadoRequest criarRequest(String titulo, String texto) {
        ComunicadoRequest request = new ComunicadoRequest();
        request.setTitulo(titulo);
        request.setTexto(texto);
        return request;
    }

    @Nested
    public class SalvarTestes {

        @Test
        @DisplayName("Deve salvar comunicado com data do servidor, autor e campos sem espaços extras")
        void deveSalvarComunicado() {
            Professor autor = new Professor();
            autor.setId(1L);
            autor.setNome("Coordenador");

            Mockito.when(comunicadoRepository.save(ArgumentMatchers.any(Comunicado.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Comunicado salvo = comunicadoService.salvar(criarRequest("  Aviso  ", " Texto "), autor);

            Assertions.assertEquals("Aviso", salvo.getTitulo());
            Assertions.assertEquals("Texto", salvo.getTexto());
            Assertions.assertEquals(autor, salvo.getAutor());
            Assertions.assertNotNull(salvo.getDataCriacao());
        }
    }

    @Nested
    public class AtualizarTestes {

        @Test
        @DisplayName("Deve lançar EntityNotFound ao atualizar comunicado inexistente")
        void deveLancarExcecaoQuandoNaoExiste() {
            Mockito.when(comunicadoRepository.findById(99L)).thenReturn(Optional.empty());

            Assertions.assertThrows(EntityNotFound.class,
                    () -> comunicadoService.atualizar(99L, criarRequest("Aviso", "Texto")));
        }

        @Test
        @DisplayName("Deve atualizar título e texto mantendo a data de criação")
        void deveAtualizarComunicado() {
            LocalDateTime criadoEm = LocalDateTime.of(2026, 1, 1, 8, 0);
            Comunicado existente = new Comunicado();
            existente.setId(1L);
            existente.setTitulo("Antigo");
            existente.setTexto("Texto antigo");
            existente.setDataCriacao(criadoEm);

            Mockito.when(comunicadoRepository.findById(1L)).thenReturn(Optional.of(existente));
            Mockito.when(comunicadoRepository.save(existente)).thenReturn(existente);

            Comunicado atualizado = comunicadoService.atualizar(1L, criarRequest("Novo", "Texto novo"));

            Assertions.assertEquals("Novo", atualizado.getTitulo());
            Assertions.assertEquals("Texto novo", atualizado.getTexto());
            Assertions.assertEquals(criadoEm, atualizado.getDataCriacao());
        }
    }

    @Nested
    public class DeletarTestes {

        @Test
        @DisplayName("Deve lançar EntityNotFound ao deletar comunicado inexistente")
        void deveLancarExcecaoQuandoNaoExiste() {
            Mockito.when(comunicadoRepository.findById(99L)).thenReturn(Optional.empty());

            Assertions.assertThrows(EntityNotFound.class, () -> comunicadoService.deletar(99L));
            Mockito.verify(comunicadoRepository, Mockito.never()).delete(ArgumentMatchers.any());
        }

        @Test
        @DisplayName("Deve deletar comunicado existente")
        void deveDeletarComunicado() {
            Comunicado existente = new Comunicado();
            existente.setId(1L);
            Mockito.when(comunicadoRepository.findById(1L)).thenReturn(Optional.of(existente));

            comunicadoService.deletar(1L);

            Mockito.verify(comunicadoRepository).delete(existente);
        }
    }
}
