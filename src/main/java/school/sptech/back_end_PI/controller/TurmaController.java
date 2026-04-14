package school.sptech.back_end_PI.controller;


import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.back_end_PI.dto.turma.RequestTurma;
import school.sptech.back_end_PI.dto.turma.ResponseTurma;
import school.sptech.back_end_PI.entity.Turma;
import school.sptech.back_end_PI.mapper.turma.TurmaMapper;
import school.sptech.back_end_PI.services.TurmaService;

@RestController
@RequestMapping("/turmas")
public class TurmaController {

    TurmaService turmaService;

    public TurmaController(TurmaService turmaService) {
        this.turmaService = turmaService;
    }

    @PostMapping
    public ResponseEntity cadastrar(@RequestBody RequestTurma turmaRequest){

        System.out.println("Entrou no controller");
        Turma turma = TurmaMapper.toEntity(turmaRequest);
        turmaService.criarTurma(turma);
        return ResponseEntity.status(201).build();
    }


    @PutMapping("/{turmaId}/alunos/{alunoId}")
    public ResponseEntity<Void> vincularAluno(
            @PathVariable Integer turmaId,
            @PathVariable Integer alunoId) {

        turmaService.vincularAluno(turmaId, alunoId);
        return ResponseEntity.noContent().build();
    }


}
