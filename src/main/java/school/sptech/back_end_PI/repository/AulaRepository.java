package school.sptech.back_end_PI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.back_end_PI.entity.Aula;

public interface AulaRepository extends JpaRepository<Aula, Long> {
}