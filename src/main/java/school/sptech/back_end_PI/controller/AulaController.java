package school.sptech.back_end_PI.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import school.sptech.back_end_PI.dto.aula.*;
import school.sptech.back_end_PI.security.AccessGuard;
import school.sptech.back_end_PI.services.AulaService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/aulas")
@Tag(name = "Aulas", description = "Endpoints para gerenciamento de aulas")
public class AulaController {

    @Autowired
    private AulaService aulaService;

    @Autowired
    private AccessGuard accessGuard;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar aulas de um período", description = "Coordenador vê todas as aulas; professor vê somente as próprias.")
    public ResponseEntity<List<AulaResponse>> listarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            Authentication authentication) {

        if (accessGuard.podeVerAulas(authentication)) {
            return ResponseEntity.ok(aulaService.listarAulasPorPeriodo(inicio, fim));
        }

        Long professorId = accessGuard.currentProfessorId(authentication);
        if (professorId == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(aulaService.listarAulasDoProfessorPorPeriodo(professorId, inicio, fim));
    }

    @PostMapping("/extra")
    @PreAuthorize("hasAuthority('PERM_TELA_AGENDA')")
    public ResponseEntity<AulaResponse> adicionarAulaExtra(@RequestBody @Valid AulaExtraRequest request) {
        return ResponseEntity.status(201).body(aulaService.adicionarAulaExtra(request));
    }

    @PatchMapping("/{id}/remarcar")
    @PreAuthorize("hasAuthority('PERM_TELA_AGENDA')")
    public ResponseEntity<AulaResponse> remarcarAula(
            @PathVariable Long id,
            @RequestBody @Valid RemarcarAulaRequest request) {
        return ResponseEntity.ok(aulaService.remarcarAula(id, request));
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAuthority('PERM_TELA_AGENDA')")
    public ResponseEntity<AulaResponse> cancelarAula(
            @PathVariable Long id,
            @RequestBody(required = false) CancelarAulaRequest request) {
        return ResponseEntity.ok(aulaService.cancelarAula(id, request));
    }

    @PatchMapping("/turma/{turmaId}/cancelar")
    @PreAuthorize("hasAuthority('PERM_TELA_AGENDA')")
    @Operation(summary = "Cancelar a aula de uma turma", description = "Cancela as aulas de todos os alunos da turma no dia e horário informados.")
    public ResponseEntity<List<AulaResponse>> cancelarAulaTurma(
            @PathVariable Long turmaId,
            @RequestBody @Valid CancelarAulaTurmaRequest request) {
        return ResponseEntity.ok(aulaService.cancelarAulaTurma(turmaId, request));
    }

    @PatchMapping("/turma/{turmaId}/remarcar")
    @PreAuthorize("hasAuthority('PERM_TELA_AGENDA')")
    @Operation(summary = "Remarcar a aula de uma turma", description = "Remarca as aulas de todos os alunos da turma no dia e horário informados.")
    public ResponseEntity<List<AulaResponse>> remarcarAulaTurma(
            @PathVariable Long turmaId,
            @RequestBody @Valid RemarcarAulaTurmaRequest request) {
        return ResponseEntity.ok(aulaService.remarcarAulaTurma(turmaId, request));
    }

    @PatchMapping("/turma/{turmaId}/presenca")
    @PreAuthorize("hasAuthority('PERM_TELA_AGENDA')")
    @Operation(summary = "Registrar presença da aula de uma turma", description = "Alunos informados ficam ausentes; os demais recebem presença.")
    public ResponseEntity<List<AulaResponse>> registrarPresencaTurma(
            @PathVariable Long turmaId,
            @RequestBody @Valid PresencaAulaTurmaRequest request) {
        return ResponseEntity.ok(aulaService.registrarPresencaTurma(turmaId, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_TELA_AGENDA')")
    public ResponseEntity<Void> deletarAula(@PathVariable Long id) {
        aulaService.deletarAula(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/presenca")
    @PreAuthorize("hasAuthority('PERM_TELA_AGENDA')")
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
