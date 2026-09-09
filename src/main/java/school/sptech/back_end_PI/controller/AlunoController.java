package school.sptech.back_end_PI.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import school.sptech.back_end_PI.dto.aluno.AlunoRequest;
import school.sptech.back_end_PI.dto.aluno.AlunoResponse;
import school.sptech.back_end_PI.entity.Aluno;
import school.sptech.back_end_PI.mapper.AlunoMapper;
import school.sptech.back_end_PI.services.AlunoService;
import school.sptech.back_end_PI.security.AccessGuard;

import java.util.List;

@RestController
@RequestMapping("/alunos")
public class AlunoController {

    private final AlunoService service;
    private final AccessGuard accessGuard;

    public AlunoController(AlunoService service, AccessGuard accessGuard) {
        this.service = service;
        this.accessGuard = accessGuard;
    }
    @PostMapping
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<AlunoResponse> create(
            @Valid @RequestBody AlunoRequest request) {

        Aluno criado = service.create(request);

        return ResponseEntity.status(201)
                .body(AlunoMapper.toResponse(criado));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AlunoResponse>> getAll(Authentication authentication) {
        List<Aluno> alunos;
        if (accessGuard.isCoordenador(authentication)) {
            alunos = service.getAll();
        } else {
            Long professorId = accessGuard.currentProfessorId(authentication);
            alunos = professorId == null ? List.of() : service.getByProfessorId(professorId);
        }
        List<AlunoResponse> response = alunos.stream()
                .map(AlunoMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@accessGuard.canManageAluno(#id, authentication)")
    public ResponseEntity<AlunoResponse> getById(@PathVariable Long id) {
        Aluno aluno = service.getById(id);
        return ResponseEntity.ok(AlunoMapper.toResponse(aluno));
    }

    @GetMapping("/disponiveis/{id}")
    @PreAuthorize("@accessGuard.canManageAluno(#id, authentication)")
    public ResponseEntity<AlunoResponse> getAlunoComHorariosDisponiveis(@PathVariable Long id) {
        Aluno aluno = service.buscarPorIdComHorariosDisponiveis(id);
        return ResponseEntity.ok(AlunoMapper.toResponse(aluno));
    }


    @GetMapping("/turma/{id}")
    @PreAuthorize("@accessGuard.canManageTurma(#id, authentication)")
    public ResponseEntity<List<AlunoResponse>> getByTurmaId(@PathVariable Long id){
        List<Aluno> alunos = service.getByTurmaId(id);
        return ResponseEntity.ok(AlunoMapper.toResponseList(alunos));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('COORDENADOR')")
    @Operation(summary = "Inativar um aluno (Soft Delete)", description = "Altera o status do aluno para inativo")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<AlunoResponse> update(@PathVariable Long id, @Valid @RequestBody AlunoRequest request) {

        Aluno atualizado = service.update(id, request);
        return ResponseEntity.ok(AlunoMapper.toResponse(atualizado));
    }

    @PatchMapping("/{id}/reativar")
    @PreAuthorize("hasRole('COORDENADOR')")
    @Operation(summary = "Reativar um aluno inativo", description = "Restaura o acesso e o status do aluno para ativo")
    public ResponseEntity<AlunoResponse> reativar(@PathVariable Long id) {
        Aluno alunoReativado = service.reativar(id);
        return ResponseEntity.ok(AlunoMapper.toResponse(alunoReativado));
    }
}