package school.sptech.back_end_PI.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import school.sptech.back_end_PI.dto.contrato.ContratoRequest;
import school.sptech.back_end_PI.dto.contrato.ContratoResponse;
import school.sptech.back_end_PI.services.ContratoService;
import school.sptech.back_end_PI.security.AccessGuard;

import java.util.List;

@RestController
@RequestMapping("/contratos")
public class ContratoController {

    private final ContratoService service;
    private final AccessGuard accessGuard;

    public ContratoController(ContratoService service, AccessGuard accessGuard) {
        this.service = service;
        this.accessGuard = accessGuard;
    }

    @PostMapping
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<ContratoResponse> criarContrato(@Valid @RequestBody ContratoRequest request){
        ContratoResponse contratoCriado = service.criarContrato(request);
        return ResponseEntity.status(201).body(contratoCriado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<ContratoResponse> atualizarContrato(@PathVariable Long id, @Valid @RequestBody ContratoRequest request){
        ContratoResponse contratoAtualizado = service.atualizarContrato(id,request);
        return ResponseEntity.status(200).body(contratoAtualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<ContratoResponse> deletarContrato(@PathVariable Long id){
        service.deletarContrato(id);
        return ResponseEntity.status(204).build();
    }

    @GetMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ContratoResponse>> listarContratos(Authentication authentication){
        List<ContratoResponse> response;
        if (accessGuard.isCoordenador(authentication)) {
            response = service.listarTodosContratos();
        } else {
            Long professorId = accessGuard.currentProfessorId(authentication);
            response = professorId == null ? List.of() : service.listarContratosPorProfessor(professorId);
        }
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@accessGuard.canManageContrato(#id, authentication)")
    public ResponseEntity<ContratoResponse> buscarContratoPorId(@PathVariable Long id){
        ContratoResponse response = service.buscarContratoPorId(id);
        return ResponseEntity.status(200).body(response);
    }

}
