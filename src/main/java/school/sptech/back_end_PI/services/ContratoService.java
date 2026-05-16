package school.sptech.back_end_PI.services;

import org.springframework.stereotype.Service;
import school.sptech.back_end_PI.Exception.BusinessException;
import school.sptech.back_end_PI.Exception.EntityNotFound;
import school.sptech.back_end_PI.dto.ContratoRequest;
import school.sptech.back_end_PI.dto.ContratoResponse;
import school.sptech.back_end_PI.entity.Contrato;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.entity.Turma;
import school.sptech.back_end_PI.mapper.ContratoMapper;
import school.sptech.back_end_PI.repository.ContratoRepository;
import school.sptech.back_end_PI.repository.ProfessorRepository;
import school.sptech.back_end_PI.repository.TurmaRepository;

@Service
public class ContratoService {

    private final TurmaRepository turmaRepository;
    private final ProfessorRepository professorRepository;
    private final ContratoRepository contratoRepository;

    public ContratoService(TurmaRepository turmaRepository, ProfessorRepository professorRepository, ContratoRepository contratoRepository) {
        this.turmaRepository = turmaRepository;
        this.professorRepository = professorRepository;
        this.contratoRepository = contratoRepository;
    }

    public ContratoResponse criarContrato(ContratoRequest request) {

        if (request.getDataInicio().isAfter(request.getDataFim())) {
            throw new BusinessException("Data de início não pode ser maior que a data de fim");
        }

        Turma turma = turmaRepository.findById(request.getTurmaId())
                .orElseThrow(() -> new EntityNotFound("Turma não encontrada"));

        Professor professor = professorRepository.findById(request.getProfessorId())
                .orElseThrow(() -> new EntityNotFound("Professor não encontrado"));

        boolean contratoExistente = contratoRepository
                .existsByTurmaAndProfessorAndDataInicioAndDataFim(
                        turma, professor, request.getDataInicio(), request.getDataFim());

        if (turma.getAlunos().size() >= turma.getLimiteAlunos()) {
            throw new BusinessException("Turma já atingiu o limite de alunos");
        }

        if (contratoExistente) {
            throw new BusinessException("Contrato já existe para essa turma/professor/período");
        }

        // Criação do contrato
        Contrato contrato = new Contrato();
        contrato.setDataInicio(request.getDataInicio());
        contrato.setDataFim(request.getDataFim());
        contrato.setTurma(turma);
        contrato.setProfessor(professor);

        contratoRepository.save(contrato);

        return ContratoMapper.toResponse(contrato);
    }
}
