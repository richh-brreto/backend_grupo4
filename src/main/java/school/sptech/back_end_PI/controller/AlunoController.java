package school.sptech.back_end_PI.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.back_end_PI.dto.AlunoRequest;
import school.sptech.back_end_PI.dto.AlunoResponse;
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

        Aluno aluno = AlunoMapper.toEntity(request);
        Aluno criado = service.create(aluno);

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

    @PutMapping("/{id}")
    public ResponseEntity<AlunoResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody AlunoRequest request) {

        Aluno aluno = AlunoMapper.toEntity(request);
        Aluno atualizado = service.update(id, aluno);

        return ResponseEntity.ok(AlunoMapper.toResponse(atualizado));
    }
}