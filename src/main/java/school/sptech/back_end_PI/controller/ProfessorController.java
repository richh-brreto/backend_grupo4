package school.sptech.back_end_PI.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import school.sptech.back_end_PI.dto.professor.ProfessorRequest;
import school.sptech.back_end_PI.dto.aluno.HorarioAlunoProfessorRequest;
import school.sptech.back_end_PI.dto.professor.ProfessorResponse;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.mapper.ProfessorMapper;
import school.sptech.back_end_PI.services.ProfessorService;
import school.sptech.back_end_PI.security.AccessGuard;

import java.util.List;

@RestController
@RequestMapping("/professores")
@Tag(name = "Professores", description = "Operações relacionadas à professores")
public class ProfessorController {

    private final ProfessorService service;
    private final AccessGuard accessGuard;

    public ProfessorController(ProfessorService service, AccessGuard accessGuard) {
        this.service = service;
        this.accessGuard = accessGuard;
    }

    @PostMapping
    @Operation(summary = "Cadastrar um professor", description = "Cadastrar um novo professor com um ID único")
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<ProfessorResponse> cadastrar(
            @Parameter(description = "Um professor, contendo seu id, nome, email, telefone, senha e tipo de usuário (no caso: professor)", required = true)
            @Valid @RequestBody ProfessorRequest dto
    ) {
        Professor professorSalvo = service.create(dto);
        return ResponseEntity.status(201).body(ProfessorMapper.toResponse(professorSalvo));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativar um professor (Soft Delete)", description = "Altera o status do professor para inativo")
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProfessorResponse>> listar(Authentication authentication) {
        if (!accessGuard.isCoordenador(authentication)) {
            Long professorId = accessGuard.currentProfessorId(authentication);
            if (professorId == null) {
                return ResponseEntity.ok(List.of());
            }
            return ResponseEntity.ok(List.of(service.findById(professorId)));
        }
        List<ProfessorResponse> lista = service.findAll();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@accessGuard.canManageProfessor(#id, authentication)")
    public ResponseEntity<ProfessorResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/disponiveis/{id}")
    @PreAuthorize("@accessGuard.canManageProfessor(#id, authentication)")
    public ResponseEntity<ProfessorResponse> getProfessorComHorariosDisponiveis(@PathVariable Long id) {
        Professor professor = service.buscarPorIdComHorariosDisponiveis(id);
        return ResponseEntity.ok(ProfessorMapper.toResponse(professor));
    }

    @PostMapping("/compatibilidade")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProfessorResponse>> buscarCompativeis(@Valid @RequestBody HorarioAlunoProfessorRequest request) {

        List<ProfessorResponse> response = service.buscarCompativeis(request)
                .stream()
                .map(ProfessorMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/disponiveis")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProfessorResponse>> buscarProfessoresDisponiveis(@Valid @RequestBody HorarioAlunoProfessorRequest request) {
        List<ProfessorResponse> response = service.findProfessoresDisponiveis();
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<ProfessorResponse> atualizar(@PathVariable Long id, @Valid @RequestBody ProfessorRequest request){

        Professor professorAtualizado = service.atualizar(id, request);
        ProfessorResponse response = ProfessorMapper.toResponse(professorAtualizado);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/reativar")
    @Operation(summary = "Reativar um professor inativo", description = "Restaura o status ativo e devolve as permissões de login ao professor")
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<ProfessorResponse> reativar(@PathVariable Long id) {
        Professor professorReativado = service.reativar(id);
        return ResponseEntity.ok(ProfessorMapper.toResponse(professorReativado));
    }
}
