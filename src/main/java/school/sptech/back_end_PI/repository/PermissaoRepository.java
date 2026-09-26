package school.sptech.back_end_PI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.back_end_PI.entity.Permissao;

import java.util.Collection;
import java.util.List;

public interface PermissaoRepository extends JpaRepository<Permissao, Integer> {

    // Usado pelo cadastro: resolve as telas marcadas no payload (case-insensitive)
    List<Permissao> findByNomeIgnoreCaseIn(Collection<String> nomes);

    // Catálogo exibido no front
    List<Permissao> findAllByOrderByNomeAsc();
}
