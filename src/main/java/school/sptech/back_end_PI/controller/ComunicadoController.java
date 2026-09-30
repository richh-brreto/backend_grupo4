package school.sptech.back_end_PI.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import school.sptech.back_end_PI.dto.comunicado.ComunicadoRequest;
import school.sptech.back_end_PI.dto.comunicado.ComunicadoResponse;
import school.sptech.back_end_PI.entity.Comunicado;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.mapper.ComunicadoMapper;
import school.sptech.back_end_PI.services.ComunicadoService;

import java.util.List;

@RestController
@RequestMapping("/comunicados")
@Tag(name = "Comunicados", description = "Endpoints para gerenciamento de comunicados")
public class ComunicadoController {

    private final ComunicadoService service;

    public ComunicadoController(ComunicadoService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar comunicados, do mais recente ao mais antigo")
    public ResponseEntity<List<ComunicadoResponse>> listarTodos() {
        return ResponseEntity.ok(ComunicadoMapper.toResponseList(service.listarTodos()));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERM_TELA_GERAL')")
    @Operation(summary = "Cadastrar um novo comunicado")
    public ResponseEntity<ComunicadoResponse> cadastrar(
            @Valid @RequestBody ComunicadoRequest request,
            Authentication authentication) {

        // O autor vem do token autenticado, nunca do corpo da requisição
        Professor autor = authentication.getPrincipal() instanceof Professor professor ? professor : null;
        Comunicado novo = service.salvar(request, autor);
        return ResponseEntity.status(201).body(ComunicadoMapper.toResponse(novo));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_TELA_GERAL')")
    @Operation(summary = "Editar título e texto de um comunicado")
    public ResponseEntity<ComunicadoResponse> editar(
            @PathVariable Long id,
            @Valid @RequestBody ComunicadoRequest request) {

        Comunicado atualizado = service.atualizar(id, request);
        return ResponseEntity.ok(ComunicadoMapper.toResponse(atualizado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_TELA_GERAL')")
    @Operation(summary = "Excluir um comunicado")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
