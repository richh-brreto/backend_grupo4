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
import school.sptech.back_end_PI.entity.Aluno;

import school.sptech.back_end_PI.services.AlunoService;

@RestController
@RequestMapping("/alunos")
@Tag(name = "Alunos", description = "Operações relacionadas à Alunos")
public class AlunoController {

    private final AlunoService service;
    public AlunoController(AlunoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Cadastrar um aluno", description = "Cadastrar um novo aluno com um ID único")
    public ResponseEntity<Aluno> create(
            @Parameter(description = "Um Aluno - que inclui seu id, nome, email, telefone, senha e tipo de usuário (no caso: aluno)", required = true)
            @Valid @RequestBody Aluno aluno
    ){
        return ResponseEntity.status(201).body(service.create(aluno));
    }


}