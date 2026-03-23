package school.sptech.back_end_PI.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.sptech.back_end_PI.dto.RequestPessoa;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.services.ProfessorService;

@RestController
@RequestMapping("/professores")
public class ProfessorController {
    private final ProfessorService service;
    public ProfessorController(ProfessorService service) {
        this.service = service;
    }


    @PostMapping
    public ResponseEntity cadastrar ( @Valid @RequestBody Professor professor){
        return ResponseEntity.status(201).body(service.create(professor));
    }

}
