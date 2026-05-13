package school.sptech.back_end_PI.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.back_end_PI.dto.ProfessorRequest;
import school.sptech.back_end_PI.dto.HorarioAlunoProfessorRequest;
import school.sptech.back_end_PI.dto.ProfessorResponse;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.mapper.ProfessorMapper;
import school.sptech.back_end_PI.services.ProfessorService;

import java.util.List;

@RestController
@RequestMapping("/professores")
@Tag(name = "Professores", description = "Operações relacionadas à professores")
public class ProfessorController {

    private final ProfessorService service;
    public ProfessorController(ProfessorService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Cadastrar um professor", description = "Cadastrar um novo professor com um ID único")
    public ResponseEntity<Professor> cadastrar(
            @Parameter(description = "Um professor, contendo seu id, nome, email, telefone, senha e tipo de usuário (no caso: professor)", required = true)
            @Valid @RequestBody ProfessorRequest dto
    ) {
        Professor professorSalvo = service.create(dto);
        return ResponseEntity.status(201).body(professorSalvo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ProfessorResponse>> listar() {
        List<ProfessorResponse> lista = service.findAll();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfessorResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping("/compatibilidade")
    public ResponseEntity<List<ProfessorResponse>> buscarCompativeis(@RequestBody HorarioAlunoProfessorRequest request) {

        List<ProfessorResponse> response = service.buscarCompativeis(request)
                .stream()
                .map(ProfessorMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfessorResponse> atualizar(@PathVariable Integer id, @RequestBody ProfessorRequest request){

        Professor professorAtualizado = service.atualizar(id, request);
        ProfessorResponse response = ProfessorMapper.toResponse(professorAtualizado);

        return ResponseEntity.ok(response);
    }

}
