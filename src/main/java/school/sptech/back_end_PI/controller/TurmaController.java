package school.sptech.back_end_PI.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.sptech.back_end_PI.dto.TurmaRequest;
import school.sptech.back_end_PI.entity.Turma;
import school.sptech.back_end_PI.services.TurmaService;

@RestController
@RequestMapping("/turmas")
public class TurmaController {

    @Autowired
    private TurmaService service;

    @PostMapping
    public ResponseEntity<Turma> cadastrar(@RequestBody @Valid TurmaRequest dto) {
        Turma turmaSalva = service.salvar(dto);
        return ResponseEntity.status(201).body(turmaSalva);
    }
}
