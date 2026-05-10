package school.sptech.back_end_PI.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import school.sptech.back_end_PI.dto.TurmaRequest;
import school.sptech.back_end_PI.entity.Turma;
import school.sptech.back_end_PI.mapper.TurmaMapper;
import school.sptech.back_end_PI.repository.TurmaRepository;

@Service
public class TurmaService {
    @Autowired
    private TurmaRepository turmaRepository;

    public Turma salvar(TurmaRequest novaTurma) {
        if (turmaRepository.existsByNome(novaTurma.getNome())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Turma já cadastrada com este nome.");
        }

        Turma turmaSalvar = TurmaMapper.toEntity(novaTurma);

        return turmaRepository.save(turmaSalvar);
    }
}