package school.sptech.back_end_PI.services;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;
import school.sptech.back_end_PI.dto.turma.RequestTurma;
import school.sptech.back_end_PI.entity.Aluno;
import school.sptech.back_end_PI.entity.Turma;
import school.sptech.back_end_PI.repository.AlunoRepository;
import school.sptech.back_end_PI.repository.TurmaRepository;

@Service
public class TurmaService {

    TurmaRepository turmaRepository;
    AlunoRepository alunoRepository;

    public TurmaService(TurmaRepository turmaRepository, AlunoRepository alunoRepository) {
        this.turmaRepository = turmaRepository;
        this.alunoRepository = alunoRepository;
    }

    public void criarTurma(Turma turma){
        turmaRepository.save(turma);
    }

    public void vincularAluno(Integer turmaId, Integer alunoId) {

        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow();


        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow();

        turma.getAlunos().add(aluno);

        turmaRepository.save(turma);
    }

}
