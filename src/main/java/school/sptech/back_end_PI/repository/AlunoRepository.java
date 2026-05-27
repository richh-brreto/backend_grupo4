package school.sptech.back_end_PI.repository;

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

    @Modifying
    @Query("UPDATE Aluno a SET a.ativo = true WHERE a.id = :id")
    int reativarPorId(@Param("id") Long id);

    @Query(value = "SELECT * FROM aluno WHERE id_aluno = :id", nativeQuery = true)
    Optional<Aluno> buscarPorIdIgnorandoFiltro(@Param("id") Long id);
}