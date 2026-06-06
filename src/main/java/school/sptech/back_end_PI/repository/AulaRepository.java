package school.sptech.back_end_PI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.sptech.back_end_PI.entity.Aula;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AulaRepository extends JpaRepository<Aula, Long> {
    List<Aula> findByDataBetween(LocalDate startDate, LocalDate endDate);
    List<Aula> findByContratoIdOrderByDataAsc(Long contratoId);
}
