package school.sptech.back_end_PI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.sptech.back_end_PI.entity.Aluno;
import school.sptech.back_end_PI.entity.Contrato;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.entity.Turma;

import java.time.LocalDate;
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
}
