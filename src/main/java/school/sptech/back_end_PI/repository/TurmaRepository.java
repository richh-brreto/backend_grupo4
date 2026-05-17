package school.sptech.back_end_PI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.back_end_PI.entity.Turma;

public interface TurmaRepository extends JpaRepository<Turma, Long> {
    boolean existsByNome(String nome);
}
