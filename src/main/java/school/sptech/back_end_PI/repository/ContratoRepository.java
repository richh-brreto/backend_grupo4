package school.sptech.back_end_PI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.back_end_PI.entity.Aula;
import school.sptech.back_end_PI.entity.Contrato;

public interface ContratoRepository extends JpaRepository<Contrato, Integer>  {
}
