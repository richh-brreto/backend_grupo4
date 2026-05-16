package school.sptech.back_end_PI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.back_end_PI.entity.Contrato;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.entity.Turma;

import java.time.LocalDate;

public interface ContratoRepository extends JpaRepository<Contrato, Long> {

    boolean existsByTurmaAndProfessorAndDataInicioAndDataFim(
            Turma turma, Professor professor, LocalDate dataInicio, LocalDate dataFim);
}
