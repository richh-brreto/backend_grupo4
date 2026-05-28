package school.sptech.back_end_PI.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;
import school.sptech.back_end_PI.exception.ConflictException;
import school.sptech.back_end_PI.exception.EntityNotFound;
import school.sptech.back_end_PI.dto.professor.ProfessorRequest;
import school.sptech.back_end_PI.dto.aluno.HorarioAlunoProfessorRequest;
import school.sptech.back_end_PI.dto.professor.ProfessorResponse;
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

    public ProfessorResponse findById(Long id) {
        return professorRepository.findById(id)
                .map(ProfessorMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado com ID: " + id));
    }

    @Transactional
    public void delete(Long id) {
        // 1. Busca a entidade completa (Obrigatório para o @SQLDelete funcionar)
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado com ID: " + id));

        // 2. Limpa a lista de horários da tabela associativa disponibilidade_professor
        professor.getHorarios().clear();
        professorRepository.saveAndFlush(professor); // Força a sincronização

        // 3. Executa o delete passando o objeto (O Hibernate interceptará e rodará o UPDATE)
        professorRepository.delete(professor);
    }

    @Transactional
    public Professor atualizar(Long id, ProfessorRequest dto) {
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

    @Transactional
    public Professor reativar(Long id) {
        // 1. Busca o professor ignorando o filtro global para verificar se ele realmente existe
        Professor professor = professorRepository.buscarPorIdIgnorandoFiltro(id)
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado com o ID: " + id));

        if (professor.getAtivo()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este professor já está ativo.");
        }

        // 2. Executa a query de atualização direta (Retorna a quantidade de linhas afetadas)
        professorRepository.reativarPorId(id);

        // 3. Atualiza o objeto na memória apenas para o JSON do Mapper não ir desatualizado
        professor.setAtivo(true);
        return professor;
    }

}
