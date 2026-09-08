package school.sptech.back_end_PI.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.sptech.back_end_PI.dto.horario.HorarioResponse;
import school.sptech.back_end_PI.services.HorarioService;

import java.util.List;

@RestController
@RequestMapping("/horarios")
@Tag(name = "Horários", description = "Consulta dos horários fixos disponíveis para vínculo com professores")
public class HorarioController {

    private final HorarioService service;

    public HorarioController(HorarioService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasRole('COODENADOR')")
    @Operation(summary = "Listar todos os horários", description = "Retorna todos os blocos de horário cadastrados, ordenados por dia da semana e hora de início")
    public ResponseEntity<List<HorarioResponse>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }
}
