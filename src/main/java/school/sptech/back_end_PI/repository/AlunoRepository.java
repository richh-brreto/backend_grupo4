package school.sptech.back_end_PI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.back_end_PI.entity.Aluno;

import java.util.List;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    boolean existsAlunoByEmail(String email);

    Aluno findByEmail(String email);

    List<Aluno> findByTurmaId(Integer id);
}