package school.sptech.back_end_PI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.back_end_PI.entity.Pessoa;

import java.util.Optional;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
    Optional<Pessoa> findByEmail(String email);
}