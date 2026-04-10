package school.sptech.back_end_PI.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.sptech.back_end_PI.dto.pessoa.RequestPessoa;
import school.sptech.back_end_PI.services.JwtService;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UsuarioController(AuthenticationManager authenticationManager,
                             JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RequestPessoa request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getSenha()
                )
        );

        String token = jwtService.generateToken(authentication);

        return ResponseEntity.ok(token);
    }
}