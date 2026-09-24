package school.sptech.back_end_PI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import school.sptech.back_end_PI.entity.Aula;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AulaRepository extends JpaRepository<Aula, Long> {
    List<Aula> findByDataBetween(LocalDate startDate, LocalDate endDate);
    List<Aula> findByContratoIdOrderByDataAsc(Long contratoId);
    List<Aula> findByDataBetweenOrderByDataAscHoraInicioAsc(LocalDate inicio, LocalDate fim);

    // Aulas de todos os alunos de uma turma em um mesmo encontro (dia + horário)
    List<Aula> findByContratoTurmaIdAndDataAndHoraInicioAndHoraFim(Long turmaId, LocalDate data,
                                                                   LocalTime horaInicio, LocalTime horaFim);

    // Aulas do professor no período: contrato individual (professor do contrato)
    // ou contrato de grupo (professor da turma)
    @Query("""
           SELECT a FROM Aula a
           JOIN a.contrato c
           LEFT JOIN c.professor p
           LEFT JOIN c.turma t
           LEFT JOIN t.professor tp
           WHERE a.data BETWEEN :inicio AND :fim
             AND (p.id = :professorId OR tp.id = :professorId)
           ORDER BY a.data ASC, a.horaInicio ASC
           """)
    List<Aula> findByProfessorAndPeriodo(@Param("professorId") Long professorId,
                                         @Param("inicio") LocalDate inicio,
                                         @Param("fim") LocalDate fim);

    // AccessGuard ownership check
    boolean existsByIdAndContratoProfessorId(Long id, Long professorId);
}
