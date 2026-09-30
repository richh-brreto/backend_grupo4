package school.sptech.back_end_PI;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import school.sptech.back_end_PI.entity.Permissao;
import school.sptech.back_end_PI.entity.Professor;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ProfessorAuthoritiesTest {

    @Test
    @DisplayName("Deve expor as telas liberadas como authorities no login")
    void deveExporTelasComoAuthorities() {
        Professor professor = new Professor();
        professor.setPermissoes(new LinkedHashSet<>(List.of(
                new Permissao(1, "TELA_AGENDA"),
                new Permissao(2, "TELA_ALUNOS")
        )));

        List<String> authorities = professor.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Assertions.assertTrue(authorities.contains("PERM_TELA_AGENDA"));
        Assertions.assertTrue(authorities.contains("PERM_TELA_ALUNOS"));
        // Não existe mais papel/tipo: a autorização vem só das telas liberadas
        Assertions.assertTrue(authorities.stream().noneMatch(authority -> authority.startsWith("ROLE_")));
    }

    @Test
    @DisplayName("Deve ficar sem nenhuma authority quando o professor não tem tela liberada")
    void deveFicarSemAuthoritySemTelas() {
        Professor professor = new Professor();

        List<String> authorities = professor.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Assertions.assertTrue(authorities.isEmpty());
    }

    @Test
    @DisplayName("Deve não quebrar quando o professor chega sem lista de permissões")
    void deveTratarPermissoesNulas() {
        Professor professor = new Professor();
        professor.setPermissoes(null);

        Assertions.assertTrue(professor.getAuthorities().isEmpty());
        Assertions.assertTrue(professor.nomesPermissoes().isEmpty());
        Assertions.assertFalse(professor.temTela("TELA_AGENDA"));
    }

    @Test
    @DisplayName("Deve devolver os nomes das telas liberadas em ordem alfabética")
    void deveDevolverNomesOrdenados() {
        Professor professor = new Professor();
        professor.setPermissoes(new LinkedHashSet<>(Set.of(
                new Permissao(3, "TELA_TURMAS"),
                new Permissao(1, "TELA_AGENDA")
        )));

        Assertions.assertEquals(List.of("TELA_AGENDA", "TELA_TURMAS"), professor.nomesPermissoes());
    }
}
