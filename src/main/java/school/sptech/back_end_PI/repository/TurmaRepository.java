package school.sptech.back_end_PI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import school.sptech.back_end_PI.entity.Turma;

import java.util.List;

public interface TurmaRepository extends JpaRepository<Turma, Long> {
    boolean existsByNome(String nome);

    // Busca turmas atribuídas a um professor
    List<Turma> findByProfessorId(Long professorId);

    // Busca turmas atribuídas a qualquer professor do conjunto
    List<Turma> findByProfessorIdIn(List<Long> professorIds);

    // AccessGuard ownership check
    boolean existsByIdAndProfessorId(Long id, Long professorId);

    // Busca turmas com vagas disponíveis (qtd de alunos < capacidade máxima)
    @Query(value = """
           SELECT t.*
           FROM turma t
           LEFT JOIN contrato c ON c.turma_id_turma = t.id_turma
           GROUP BY t.id_turma, t.limite_alunos
           HAVING COUNT(c.aluno_id_aluno) < t.limite_alunos;
            """, nativeQuery = true)
    List<Turma> findTurmasDisponiveis();
}


