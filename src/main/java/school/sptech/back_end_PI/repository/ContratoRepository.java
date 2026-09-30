package school.sptech.back_end_PI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import school.sptech.back_end_PI.entity.Aluno;
import school.sptech.back_end_PI.entity.Contrato;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.entity.Turma;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContratoRepository extends JpaRepository<Contrato, Long> {

    List<Contrato> findByTurmaId(Long turmaId);

    // Busca contratos por professor
    List<Contrato> findByProfessorId(Long professorId);

    // Busca contratos para um conjunto de professores
    List<Contrato> findByProfessorIdIn(List<Long> professorIds);

    // Busca contratos para um conjunto de turmas
    List<Contrato> findByTurmaIdIn(List<Long> turmaIds);

    // 1. Conta quantos contratos de grupo ativos existem para uma determinada turma
    Long countByTurmaAndDataFimGreaterThanEqual(Turma turma, LocalDate dataInicio);

    // 2. Valida duplicidade em contratos de Grupo (Evita o mesmo aluno na mesma turma no mesmo período)
    boolean existsByAlunoAndTurmaAndDataInicioAndDataFim(Aluno aluno, Turma turma, LocalDate dataInicio, LocalDate dataFim);

    // 3. Valida duplicidade em contratos Individuais (Evita o mesmo aluno com o mesmo professor no mesmo período)
    boolean existsByAlunoAndProfessorAndDataInicioAndDataFim(Aluno aluno, Professor professor, LocalDate dataInicio, LocalDate dataFim);

    boolean existsByAlunoAndTurmaAndDataInicioAndDataFimAndIdNot(Aluno aluno, Turma turma, LocalDate dataInicio, LocalDate dataFim, Long contratoId);

    List<Contrato> findByAlunoId(Long id);

    // AccessGuard ownership checks
    boolean existsByIdAndProfessorId(Long id, Long professorId);

    boolean existsByProfessorIdAndAlunoId(Long professorId, Long alunoId);

    // Candidatos à notificação de vencimento: dentro da janela e sem reserva válida.
    // A reserva é validada de novo no UPDATE atômico, então esta lista pode estar "desatualizada" sem risco.
    @Query("SELECT c FROM Contrato c " +
           "WHERE c.dataFim BETWEEN :inicio AND :fim " +
           "AND (c.vencimentoNotificadoEm IS NULL OR c.vencimentoNotificadoEm < :limiteReabertura) " +
           "ORDER BY c.dataFim ASC")
    List<Contrato> findPendentesNotificacaoVencimento(@Param("inicio") LocalDate inicio,
                                                      @Param("fim") LocalDate fim,
                                                      @Param("limiteReabertura") LocalDateTime limiteReabertura);

    // RESERVA ATÔMICA: só uma instância consegue marcar a linha. UPDATE condicional no banco,
    // sem lock explícito — o WHERE garante que a segunda instância receba 0 linhas afetadas.
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE Contrato c SET c.vencimentoNotificadoEm = :momento " +
           "WHERE c.id = :id " +
           "AND (c.vencimentoNotificadoEm IS NULL OR c.vencimentoNotificadoEm < :limiteReabertura)")
    int reservarNotificacaoVencimento(@Param("id") Long id,
                                      @Param("momento") LocalDateTime momento,
                                      @Param("limiteReabertura") LocalDateTime limiteReabertura);

    // Devolve a reserva quando a publicação falhou, para o contrato ser tentado de novo no próximo ciclo
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE Contrato c SET c.vencimentoNotificadoEm = NULL WHERE c.id = :id AND c.vencimentoNotificadoEm IS NOT NULL")
    int liberarNotificacaoVencimento(@Param("id") Long id);
}
