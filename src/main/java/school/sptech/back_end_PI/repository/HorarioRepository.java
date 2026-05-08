package school.sptech.back_end_PI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.sptech.back_end_PI.entity.Horario;
import school.sptech.back_end_PI.entity.Professor;

import java.util.List;

public interface HorarioRepository extends JpaRepository<Horario, Long> {


}
