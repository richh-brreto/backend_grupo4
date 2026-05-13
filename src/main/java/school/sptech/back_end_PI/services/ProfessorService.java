package school.sptech.back_end_PI.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import school.sptech.back_end_PI.Exception.ConflictException;
import school.sptech.back_end_PI.Exception.EntityNotFound;
import school.sptech.back_end_PI.dto.ProfessorRequest;
import school.sptech.back_end_PI.dto.HorarioAlunoProfessorRequest;
import school.sptech.back_end_PI.dto.ProfessorResponse;
import school.sptech.back_end_PI.entity.Horario;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.entity.TipoProfessor;
import school.sptech.back_end_PI.mapper.ProfessorMapper;
import school.sptech.back_end_PI.repository.HorarioRepository;
import school.sptech.back_end_PI.repository.ProfessorRepository;
import school.sptech.back_end_PI.repository.TipoProfessorRepository;

import java.util.List;

@Service
public class ProfessorService {
    private final ProfessorRepository professorRepository;
    private final TipoProfessorRepository tipoProfessorRepository;
    private final HorarioRepository horarioRepository;

    public ProfessorService(ProfessorRepository professorRepository, TipoProfessorRepository tipoProfessorRepository, HorarioRepository horarioRepository) {
        this.professorRepository = professorRepository;
        this.tipoProfessorRepository = tipoProfessorRepository;
        this.horarioRepository = horarioRepository;
    }

    public Professor create(ProfessorRequest dto) {

        if (professorRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email já cadastrado");
        }

        TipoProfessor tipo = tipoProfessorRepository.findById(dto.getIdTipoProfessor())
                .orElseThrow(() -> new EntityNotFoundException("Tipo não encontrado"));

        List<Horario> horarios = horarioRepository.findAllById(dto.getHorariosIds());

        if (horarios.isEmpty()) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Horários não informados ou inválidos");
        }

        Professor novoProfessor = ProfessorMapper.toEntity(dto, tipo, horarios);

        return professorRepository.save(novoProfessor);
    }

    public List<ProfessorResponse> findAll() {
        return ProfessorMapper.toResponseList(professorRepository.findAll());
    }

    public ProfessorResponse findById(Integer id) {
        return professorRepository.findById(id)
                .map(ProfessorMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado com ID: " + id));
    }

    @Transactional
    public void delete(Integer id) {
        //Dependencia Tecnica, limpar tabela de disponibilidade de professor e de contratos.

        if (!professorRepository.existsById(id)) {
            throw new EntityNotFoundException("Professor não encontrado com ID: " + id);
        }

        professorRepository.deleteById(id);
    }

    @Transactional
    public Professor atualizar(Integer id, ProfessorRequest dto) {
        Professor professorExistente = professorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Professor não encontrado!"));

        TipoProfessor tipo = tipoProfessorRepository.findById(dto.getIdTipoProfessor())
                .orElseThrow(() -> new EntityNotFound("Tipo do professor não encontrado!"));

        if (!professorExistente.getEmail().equals(dto.getEmail()) &&
                professorRepository.existsProfessorByEmail(dto.getEmail())) {
            throw new ConflictException("Email já cadastrado");
        }

        professorExistente.setNome(dto.getNome());
        professorExistente.setEmail(dto.getEmail());
        professorExistente.setTelefone(dto.getTelefone());
        professorExistente.setTipo(tipo);

        if (dto.getHorariosIds() != null && !dto.getHorariosIds().isEmpty()) {
            List<Horario> novosHorarios = horarioRepository.findAllById(dto.getHorariosIds());
            professorExistente.setHorarios(novosHorarios);
        }

        return professorRepository.save(professorExistente);
    }


    public List<Professor> buscarCompativeis(HorarioAlunoProfessorRequest request) {
        return professorRepository.buscarProfessoresCompativeis(request.getAlunoHorariosIds());
    }

}
