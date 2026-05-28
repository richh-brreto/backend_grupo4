package school.sptech.back_end_PI.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.back_end_PI.dto.turma.TurmaRequest;
import school.sptech.back_end_PI.dto.turma.TurmaResponse;
import school.sptech.back_end_PI.entity.Turma;
import school.sptech.back_end_PI.mapper.TurmaMapper;
import school.sptech.back_end_PI.services.TurmaService;

import java.util.List;

@RestController
@RequestMapping("/turmas")
@Tag(name = "Turmas", description = "Endpoints para gerenciamento de turmas")
public class TurmaController {

    private final TurmaService service;

    public TurmaController(TurmaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todas as turmas cadastradas")
    public ResponseEntity<List<TurmaResponse>> listarTodas() {
        List<Turma> turmas = service.listarTodas();

        // Mapeia a lista de entidades para uma lista de TurmaResponse
        List<TurmaResponse> resposta = turmas.stream()
                .map(TurmaMapper::toResponse)
                .toList();

        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar os detalhes de uma turma específica pelo ID")
    public ResponseEntity<TurmaResponse> buscarPorId(@PathVariable Long id) {
        Turma turma = service.buscarPorId(id);
        return ResponseEntity.ok(TurmaMapper.toResponse(turma));
    }

    @PostMapping
    @Operation(summary = "Cadastrar uma nova turma", description = "Cria uma turma vinculada a horários, nascendo inicialmente sem professor.")
    public ResponseEntity<TurmaResponse> cadastrar(@Valid @RequestBody TurmaRequest request) {
        Turma novaTurma = service.salvar(request);
        return ResponseEntity.status(201).body(TurmaMapper.toResponse(novaTurma));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar dados cadastrais e horários da turma")
    public ResponseEntity<TurmaResponse> editar(
            @PathVariable Long id,
            @Valid @RequestBody TurmaRequest request) {

        Turma atualizada = service.atualizar(id, request);
        return ResponseEntity.ok(TurmaMapper.toResponse(atualizada));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir permanentemente uma turma")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/professor/{professorId}")
    @Operation(summary = "Vincular ou trocar o professor de uma turma")
    public ResponseEntity<TurmaResponse> adicionarProfessor(
            @PathVariable Long id,
            @PathVariable Long professorId) {

        Turma turmaAtualizada = service.adicionarProfessor(id, professorId);
        return ResponseEntity.ok(TurmaMapper.toResponse(turmaAtualizada));
    }

    @DeleteMapping("/{id}/professor")
    @Operation(summary = "Remover o professor atual da turma (deixá-la sem professor)")
    public ResponseEntity<TurmaResponse> removerProfessor(@PathVariable Long id) {

        Turma turmaAtualizada = service.removerProfessor(id);
        return ResponseEntity.ok(TurmaMapper.toResponse(turmaAtualizada));
    }
}