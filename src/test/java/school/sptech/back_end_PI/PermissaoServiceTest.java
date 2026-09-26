package school.sptech.back_end_PI;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.sptech.back_end_PI.dto.permissao.PermissaoResponse;
import school.sptech.back_end_PI.entity.Permissao;
import school.sptech.back_end_PI.exception.BusinessRuleException;
import school.sptech.back_end_PI.repository.PermissaoRepository;
import school.sptech.back_end_PI.services.PermissaoService;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class PermissaoServiceTest {

    @Mock
    private PermissaoRepository permissaoRepository;

    @InjectMocks
    private PermissaoService permissaoService;

    @Test
    @DisplayName("Deve listar o catálogo de telas")
    void deveListarCatalogo() {
        Mockito.when(permissaoRepository.findAllByOrderByNomeAsc())
                .thenReturn(List.of(new Permissao(1, "TELA_AGENDA"), new Permissao(2, "TELA_ALUNOS")));

        List<PermissaoResponse> resultado = permissaoService.findAll();

        Assertions.assertEquals(2, resultado.size());
        Assertions.assertEquals("TELA_AGENDA", resultado.get(0).getNome());
    }

    @Test
    @DisplayName("Deve resolver nomes de tela informados no cadastro")
    void deveResolverNomes() {
        Mockito.when(permissaoRepository.findByNomeIgnoreCaseIn(Set.of("TELA_AGENDA", "TELA_ALUNOS")))
                .thenReturn(List.of(new Permissao(1, "TELA_AGENDA"), new Permissao(2, "TELA_ALUNOS")));

        Set<Permissao> resultado = permissaoService.resolverPorNomes(List.of("TELA_AGENDA", "TELA_ALUNOS"));

        Assertions.assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("Deve normalizar o nome da tela (caixa e espaços)")
    void deveNormalizarNome() {
        Mockito.when(permissaoRepository.findByNomeIgnoreCaseIn(Set.of("TELA_AGENDA")))
                .thenReturn(List.of(new Permissao(1, "TELA_AGENDA")));

        Set<Permissao> resultado = permissaoService.resolverPorNomes(List.of("  tela_agenda "));

        Assertions.assertEquals(1, resultado.size());
        Assertions.assertEquals("TELA_AGENDA", resultado.iterator().next().getNome());
    }

    @Test
    @DisplayName("Deve retornar conjunto vazio quando nenhuma tela for informada")
    void deveRetornarVazioSemNomes() {
        Assertions.assertTrue(permissaoService.resolverPorNomes(null).isEmpty());
        Assertions.assertTrue(permissaoService.resolverPorNomes(Collections.emptyList()).isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar tela inexistente em vez de ignorá-la silenciosamente")
    void deveRejeitarTelaInexistente() {
        Mockito.when(permissaoRepository.findByNomeIgnoreCaseIn(Set.of("TELA_AGENDA", "TELA_NAO_EXISTE")))
                .thenReturn(List.of(new Permissao(1, "TELA_AGENDA")));

        BusinessRuleException excecao = Assertions.assertThrows(
                BusinessRuleException.class,
                () -> permissaoService.resolverPorNomes(List.of("TELA_AGENDA", "TELA_NAO_EXISTE"))
        );

        Assertions.assertTrue(excecao.getMessage().contains("TELA_NAO_EXISTE"));
    }
}
