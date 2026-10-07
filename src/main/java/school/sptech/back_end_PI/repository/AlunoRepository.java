package school.sptech.back_end_PI.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.sptech.back_end_PI.entity.Aluno;
import school.sptech.back_end_PI.entity.Turma;

import java.util.List;
import java.util.Optional;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    boolean existsAlunoByEmail(String email);

    boolean existsByCodigoAcesso(String codigoAcesso);

    Optional<Aluno> findByEmail(String email);

    Optional<Aluno> findByEmailIgnoreCase(String email);

    @Modifying
    @Query("UPDATE Aluno a SET a.ativo = true WHERE a.id = :id")
    int reativarPorId(@Param("id") Long id);

    @Query(value = "SELECT * FROM aluno WHERE id_aluno = :id", nativeQuery = true)
    Optional<Aluno> buscarPorIdIgnorandoFiltro(@Param("id") Long id);

    @Modifying
    @Query(value = "UPDATE disponibilidade_aluno SET is_disponivel = :status WHERE aluno_id_aluno = :alunoId AND horario_id_horario IN (:horariosIds)", nativeQuery = true)
    void atualizarDisponibilidadeHorarios(@Param("alunoId") Long alunoId, @Param("horariosIds") List<Long> horariosIds, @Param("status") boolean status);

    @Query(value = "SELECT COUNT(*) FROM disponibilidade_aluno WHERE aluno_id_aluno = :alunoId AND horario_id_horario IN (:horariosIds) AND is_disponivel = false", nativeQuery = true)
    int contarHorariosIndisponiveis(@Param("alunoId") Long alunoId, @Param("horariosIds") List<Long> horariosIds);

    @Query(value = "SELECT a.* FROM aluno a WHERE a.id_aluno = :id AND a.ativo = 1", nativeQuery = true)
    Optional<Aluno> findByIdWithDisponivelHorarios(@Param("id") Long id);

    // nome nulo = sem filtro. A collation do MySQL já ignora acentos e maiúsculas no LIKE
    @Query("SELECT a FROM Aluno a WHERE (:nome IS NULL OR a.nome LIKE CONCAT('%', :nome, '%'))")
    Page<Aluno> buscarAtivos(@Param("nome") String nome, Pageable pageable);

    // Native para escapar do @SQLRestriction, que esconde os inativos de qualquer JPQL
    @Query(value = "SELECT * FROM aluno WHERE ativo = 0 AND (:nome IS NULL OR nome LIKE CONCAT('%', :nome, '%'))",
            countQuery = "SELECT COUNT(*) FROM aluno WHERE ativo = 0 AND (:nome IS NULL OR nome LIKE CONCAT('%', :nome, '%'))",
            nativeQuery = true)
    Page<Aluno> buscarInativos(@Param("nome") String nome, Pageable pageable);

    // Subquery em vez de DISTINCT para que a ordenação e o count da paginação funcionem direto
    @Query("SELECT a FROM Aluno a WHERE a.id IN (SELECT c.aluno.id FROM Contrato c WHERE c.professor.id = :professorId)" +
            " AND (:nome IS NULL OR a.nome LIKE CONCAT('%', :nome, '%'))")
    Page<Aluno> findByProfessorId(@Param("professorId") Long professorId, @Param("nome") String nome, Pageable pageable);
}