package school.sptech.back_end_PI.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import school.sptech.back_end_PI.dto.aula.*;
import school.sptech.back_end_PI.services.AulaService;

import java.util.List;

@RestController
@RequestMapping("/aulas")
@Tag(name = "Aulas", description = "Endpoints para gerenciamento de aulas")
public class AulaController {

    @Autowired
    private AulaService aulaService;

    @PostMapping("/extra")
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<AulaResponse> adicionarAulaExtra(@RequestBody @Valid AulaExtraRequest request) {
        return ResponseEntity.status(201).body(aulaService.adicionarAulaExtra(request));
    }

    @PatchMapping("/{id}/remarcar")
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<AulaResponse> remarcarAula(
            @PathVariable Long id,
            @RequestBody @Valid RemarcarAulaRequest request) {
        return ResponseEntity.ok(aulaService.remarcarAula(id, request));
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<AulaResponse> cancelarAula(
            @PathVariable Long id,
            @RequestBody(required = false) CancelarAulaRequest request) {
        return ResponseEntity.ok(aulaService.cancelarAula(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<Void> deletarAula(@PathVariable Long id) {
        aulaService.deletarAula(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/presenca")
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<AulaResponse> atribuirPresenca(
            @PathVariable Long id,
            @RequestBody @Valid PresencaRequest request) {
        return ResponseEntity.ok(aulaService.atribuirPresenca(id, request));
    }

    @GetMapping("/{id}/logs")
    @PreAuthorize("@accessGuard.canManageAula(#id, authentication)")
    public ResponseEntity<List<LogAulaResponse>> listarLogs(@PathVariable Long id) {
        return ResponseEntity.ok(aulaService.listarLogsPorAula(id));
    }

    @GetMapping("/contrato/{contratoId}")
    @PreAuthorize("@accessGuard.canManageContrato(#contratoId, authentication)")
    public ResponseEntity<List<AulaResponse>> listarPorContrato(@PathVariable Long contratoId) {
        return ResponseEntity.ok(aulaService.listarAulasPorContrato(contratoId));
    }
}
