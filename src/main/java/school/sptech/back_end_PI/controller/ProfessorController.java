package school.sptech.back_end_PI.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.sptech.back_end_PI.dto.CreateProfessorRequest;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.services.ProfessorService;

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
            @Valid @RequestBody CreateProfessorRequest dto
    ) {
        Professor professorSalvo = service.create(dto);
        return ResponseEntity.status(201).body(professorSalvo);
    }
}
