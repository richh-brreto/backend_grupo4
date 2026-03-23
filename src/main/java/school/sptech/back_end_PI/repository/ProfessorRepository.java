package school.sptech.back_end_PI.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.back_end_PI.entity.Professor;

public interface ProfessorRepository extends JpaRepository<Professor, Integer> {

    boolean existsProfessorByEmail(String email);
}
