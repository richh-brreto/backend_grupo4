package school.sptech.back_end_PI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.back_end_PI.entity.Comunicado;

import java.util.List;

public interface ComunicadoRepository extends JpaRepository<Comunicado, Long> {
    // Desempata pelo id: a coluna DATETIME guarda apenas segundos
    List<Comunicado> findAllByOrderByDataCriacaoDescIdDesc();
}
