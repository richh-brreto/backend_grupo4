package school.sptech.back_end_PI.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import school.sptech.back_end_PI.Exception.BusinessRuleException;
import school.sptech.back_end_PI.dto.AlunoRequest;
import school.sptech.back_end_PI.dto.TurmaRequest;
import school.sptech.back_end_PI.dto.TurmaResponse;
import school.sptech.back_end_PI.entity.Aluno;
import school.sptech.back_end_PI.entity.Turma;
import school.sptech.back_end_PI.mapper.TurmaMapper;
import school.sptech.back_end_PI.repository.AlunoRepository;
import school.sptech.back_end_PI.repository.TurmaRepository;

@Service
public class TurmaService {
    @Autowired
    private TurmaRepository turmaRepository;
    @Autowired
    private AlunoRepository alunoRepository;

    public Turma salvar(TurmaRequest novaTurma) {
        if (turmaRepository.existsByNome(novaTurma.getNome())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Turma já cadastrada com este nome.");
        }

        Turma turmaSalvar = TurmaMapper.toEntity(novaTurma);

        return turmaRepository.save(turmaSalvar);
    }


    public Turma alocar(Long idTurma, Long idAluno) {
        Turma turma = turmaRepository.findById(idTurma)
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada"));

        Aluno aluno = alunoRepository.findById(idAluno)
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado"));

        if (turma.getAlunos().size() >= turma.getLimiteAlunos()) {
            throw new BusinessRuleException("A turma já está cheia! (Limite de alunos batido)");
        }

        aluno.setTurma(turma);
        turma.getAlunos().add(aluno);

        return turmaRepository.save(turma);
    }
}