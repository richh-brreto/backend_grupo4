package school.sptech.back_end_PI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.back_end_PI.entity.Turma;

import java.util.List;

public interface TurmaRepository extends JpaRepository<Turma, Long> {
    boolean existsByNome(String nome);

    // Busca turmas atribuídas a um professor
    List<Turma> findByProfessorId(Long professorId);

    // Busca turmas atribuídas a qualquer professor do conjunto
    List<Turma> findByProfessorIdIn(List<Long> professorIds);
}
