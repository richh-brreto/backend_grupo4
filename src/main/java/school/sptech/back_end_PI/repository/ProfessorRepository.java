package school.sptech.back_end_PI.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.sptech.back_end_PI.entity.Aluno;
import school.sptech.back_end_PI.entity.Professor;

import java.util.List;
import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, Integer> {

    boolean existsProfessorByEmail(String email);

    Optional<Professor> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("""
        SELECT DISTINCT p
        FROM Professor p
        JOIN p.horarios h
        WHERE h.id IN :horariosIds
    """)
    List<Professor> buscarProfessoresCompativeis(
            @Param("horariosIds") List<Long> horariosIds
    );
}
