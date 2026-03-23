package school.sptech.back_end_PI.controller;

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
public class AlunoController {

    private final AlunoService service;
    public AlunoController(AlunoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Aluno> create(@Valid @RequestBody Aluno aluno){
        return ResponseEntity.status(201).body(service.create(aluno));
    }


}