package school.sptech.back_end_PI.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.back_end_PI.dto.AlunoResponse;
import school.sptech.back_end_PI.dto.TurmaRequest;
import school.sptech.back_end_PI.dto.TurmaResponse;
import school.sptech.back_end_PI.entity.Turma;
import school.sptech.back_end_PI.mapper.TurmaMapper;
import school.sptech.back_end_PI.services.TurmaService;

import java.util.List;

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

    @PatchMapping("/{idTurma}/alocar-aluno/{idAluno}")
    public ResponseEntity<TurmaResponse> alocar(@PathVariable @Valid Long idTurma, @PathVariable @Valid Long idAluno) {
        Turma turmaSalva = service.alocar(idTurma, idAluno);

        TurmaResponse response = TurmaMapper.toResponse(turmaSalva);

        return ResponseEntity.status(200).body(response);
    }
}
