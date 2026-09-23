package school.sptech.back_end_PI.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.sptech.back_end_PI.dto.auth.PrimeiroAcessoRequest;
import school.sptech.back_end_PI.dto.auth.PrimeiroAcessoResponse;
import school.sptech.back_end_PI.services.PrimeiroAcessoService;

@RestController
@Tag(name = "Autenticação", description = "Primeiro acesso e configuração de senha")
public class PrimeiroAcessoController {

    private final PrimeiroAcessoService service;

    public PrimeiroAcessoController(PrimeiroAcessoService service) {
        this.service = service;
    }

    @PostMapping("/first-access")
    @Operation(summary = "Primeiro acesso", description = "Define a senha do usuário (aluno ou professor) usando o código de acesso gerado no cadastro. O código é invalidado após o uso.")
    @io.swagger.v3.oas.annotations.security.SecurityRequirements
    public ResponseEntity<PrimeiroAcessoResponse> primeiroAcesso(
            @Valid @RequestBody PrimeiroAcessoRequest request) {
        return ResponseEntity.ok(service.definirSenha(request));
    }
}