package school.sptech.back_end_PI.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.back_end_PI.dto.aluno.AlunoRequest;
import school.sptech.back_end_PI.dto.aluno.AlunoResponse;
import school.sptech.back_end_PI.entity.Aluno;
import school.sptech.back_end_PI.mapper.AlunoMapper;
import school.sptech.back_end_PI.services.AlunoService;
import java.util.List;

@RestController
@RequestMapping("/alunos")
public class AlunoController {

    private final AlunoService service;

    public AlunoController(AlunoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AlunoResponse> create(
            @Valid @RequestBody AlunoRequest request) {

        Aluno criado = service.create(request);

        return ResponseEntity.status(201)
                .body(AlunoMapper.toResponse(criado));
    }

    @GetMapping
    public ResponseEntity<List<AlunoResponse>> getAll() {
        List<AlunoResponse> response = service.getAll()
                .stream()
                .map(AlunoMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlunoResponse> getById(@PathVariable Long id) {
        Aluno aluno = service.getById(id);
        return ResponseEntity.ok(AlunoMapper.toResponse(aluno));
    }


    @GetMapping("/turma/{id}")
    public ResponseEntity<List<AlunoResponse>> getByTurmaId(@PathVariable Long id){
        List<Aluno> alunos = service.getByTurmaId(id);
        return ResponseEntity.ok(AlunoMapper.toResponseList(alunos));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativar um aluno (Soft Delete)", description = "Altera o status do aluno para inativo")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlunoResponse> update(@PathVariable Long id, @Valid @RequestBody AlunoRequest request) {

        Aluno atualizado = service.update(id, request);
        return ResponseEntity.ok(AlunoMapper.toResponse(atualizado));
    }

    @PatchMapping("/{id}/reativar")
    @Operation(summary = "Reativar um aluno inativo", description = "Restaura o acesso e o status do aluno para ativo")
    public ResponseEntity<AlunoResponse> reativar(@PathVariable Long id) {
        Aluno alunoReativado = service.reativar(id);
        return ResponseEntity.ok(AlunoMapper.toResponse(alunoReativado));
    }
}