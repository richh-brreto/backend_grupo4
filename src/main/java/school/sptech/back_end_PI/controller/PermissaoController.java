package school.sptech.back_end_PI.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.sptech.back_end_PI.dto.permissao.PermissaoResponse;
import school.sptech.back_end_PI.services.PermissaoService;

import java.util.List;

@RestController
@RequestMapping("/permissoes")
@Tag(name = "Permissões", description = "Catálogo de telas que podem ser liberadas a um professor")
public class PermissaoController {

    private final PermissaoService service;

    public PermissaoController(PermissaoService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar as telas disponíveis",
            description = "Catálogo lido da tabela permissao; é o que alimenta os checkboxes do cadastro de professor")
    public ResponseEntity<List<PermissaoResponse>> listar() {
        return ResponseEntity.ok(service.findAll());
    }
}
