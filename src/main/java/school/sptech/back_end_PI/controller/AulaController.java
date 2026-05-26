package school.sptech.back_end_PI.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.back_end_PI.dto.aula.*;
import school.sptech.back_end_PI.services.AulaService;

import java.util.List;

@RestController
@RequestMapping("/aulas")
public class AulaController {

    @Autowired
    private AulaService aulaService;

    @PostMapping("/extra")
    public ResponseEntity<AulaResponse> adicionarAulaExtra(@RequestBody @Valid AulaExtraRequest request) {
        return ResponseEntity.status(201).body(aulaService.adicionarAulaExtra(request));
    }

    @PatchMapping("/{id}/remarcar")
    public ResponseEntity<AulaResponse> remarcarAula(
            @PathVariable Long id,
            @RequestBody @Valid RemarcarAulaRequest request) {
        return ResponseEntity.ok(aulaService.remarcarAula(id, request));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<AulaResponse> cancelarAula(
            @PathVariable Long id,
            @RequestBody(required = false) CancelarAulaRequest request) {
        return ResponseEntity.ok(aulaService.cancelarAula(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAula(@PathVariable Long id) {
        aulaService.deletarAula(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/presenca")
    public ResponseEntity<AulaResponse> atribuirPresenca(
            @PathVariable Long id,
            @RequestBody @Valid PresencaRequest request) {
        return ResponseEntity.ok(aulaService.atribuirPresenca(id, request));
    }

    @GetMapping("/{id}/logs")
    public ResponseEntity<List<LogAulaResponse>> listarLogs(@PathVariable Long id) {
        return ResponseEntity.ok(aulaService.listarLogsPorAula(id));
    }

    @GetMapping("/contrato/{contratoId}")
    public ResponseEntity<List<AulaResponse>> listarPorContrato(@PathVariable Long contratoId) {
        return ResponseEntity.ok(aulaService.listarAulasPorContrato(contratoId));
    }
}
