package school.sptech.back_end_PI.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.entity.Turma;

import java.util.List;
import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    boolean existsProfessorByEmail(String email);

    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = {"tipo"})
    Optional<Professor> findByEmail(String email);

    @Query("""
        SELECT DISTINCT p
        FROM Professor p
        JOIN p.horarios h
        WHERE h.id IN :horariosIds
    """)
    List<Professor> buscarProfessoresCompativeis(
            @Param("horariosIds") List<Long> horariosIds
    );

    @Query(value = "SELECT * FROM professor WHERE id_professor = :id", nativeQuery = true)
    Optional<Professor> buscarPorIdIgnorandoFiltro(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Professor p SET p.ativo = true WHERE p.id = :id")
    int reativarPorId(@Param("id") Long id);

    @Modifying
    @Query(value = "UPDATE disponibilidade_professor SET is_disponivel = :status WHERE professor_id_professor = :professorId AND horario_id_horario IN (:horariosIds)", nativeQuery = true)
    void atualizarDisponibilidadeHorarios(@Param("professorId") Long professorId, @Param("horariosIds") List<Long> horariosIds, @Param("status") boolean status);

    @Query(value = "SELECT COUNT(*) FROM disponibilidade_professor WHERE professor_id_professor = :professorId AND horario_id_horario IN (:horariosIds) AND is_disponivel = false", nativeQuery = true)
    int contarHorariosIndisponiveis(@Param("professorId") Long professorId, @Param("horariosIds") List<Long> horariosIds);

    @Query(value = "SELECT p.* FROM professor p WHERE p.id_professor = :id AND p.ativo = 1", nativeQuery = true)
    Optional<Professor> findByIdWithDisponivelHorarios(@Param("id") Long id);

    @Query(value = """

            SELECT p.*
           FROM professor p
           LEFT JOIN contrato c\s
               ON c.professor_id_professor = p.id_professor\s
              AND c.turma_id_turma IS NULL  -- contrato "individual" (sem turma)
           LEFT JOIN turma t\s
               ON t.professor_id_professor = p.id_professor
           WHERE c.id_contrato IS NULL
             AND t.id_turma IS NULL
             AND EXISTS (
                 SELECT 1
                 FROM disponibilidade_professor dp
                 WHERE dp.professor_id_professor = p.id_professor
                   AND dp.is_disponivel = TRUE );
           
            """, nativeQuery = true)
    List<Professor> findProfessoresDisponiveis();

}