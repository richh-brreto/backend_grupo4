package school.sptech.back_end_PI.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.sptech.back_end_PI.dto.RequestPessoa;
import school.sptech.back_end_PI.entity.Pessoa;
import school.sptech.back_end_PI.repository.PessoaRepository;
import school.sptech.back_end_PI.services.JwtService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PessoaRepository repository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RequestPessoa request) {

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
