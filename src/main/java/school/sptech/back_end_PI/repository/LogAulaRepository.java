package school.sptech.back_end_PI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.sptech.back_end_PI.entity.Aula;
import school.sptech.back_end_PI.entity.LogAula;

import java.util.List;

@Repository
public interface LogAulaRepository extends JpaRepository<LogAula, Long> {

    List<LogAula> findByAulaIdOrderByDataHoraDesc(Long aulaId);

    void deleteByAula(Aula aula);
}
