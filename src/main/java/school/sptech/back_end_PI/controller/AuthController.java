package school.sptech.back_end_PI.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.sptech.back_end_PI.dto.RequestPessoaDto;
import school.sptech.back_end_PI.entity.Pessoa;
import school.sptech.back_end_PI.repository.PessoaRepository;
import school.sptech.back_end_PI.services.JwtService;

@RestController
@RequestMapping("/auth")
@Tag(name = "Login / Entrar", description = "Operação para logar / entrar")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PessoaRepository repository;

    @PostMapping("/login")
    @Operation(summary = "Logar / entrar", description = "Logar um usuário a partir de um email único e senha")
    public ResponseEntity<?> login(
            @Parameter(description = "Uma Usuário, contendo: nome, email, telefone e senha", required = true)
            @Valid @RequestBody RequestPessoaDto request
    ) {

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getSenha()
                )
        );

        Pessoa pessoa = repository.findByEmail(request.getEmail()).get();

        String token = jwtService.generateToken(pessoa);

        return ResponseEntity.ok(token);
    }
}
