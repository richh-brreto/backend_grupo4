package school.sptech.back_end_PI.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.back_end_PI.entity.Aluno;

public interface AlunoRepository extends JpaRepository<Aluno,Integer> {

    boolean existsAlunoByEmail(String email);

    Aluno findByEmail(String email);
}
