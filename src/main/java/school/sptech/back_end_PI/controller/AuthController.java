package school.sptech.back_end_PI.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.sptech.back_end_PI.dto.RequestPessoa;
import school.sptech.back_end_PI.entity.Aluno;
import school.sptech.back_end_PI.entity.Pessoa;
import school.sptech.back_end_PI.entity.Professor;
import school.sptech.back_end_PI.repository.PessoaRepository;
import school.sptech.back_end_PI.services.JwtService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authManager;

    private final JwtService jwtService;

    private final PessoaRepository repository;

    private final PasswordEncoder passwordEncoder;

    public AuthController(
            AuthenticationManager authManager,
            JwtService jwtService,
            PessoaRepository repository,
            PasswordEncoder passwordEncoder
    ) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register/aluno")
    public ResponseEntity<?> registerAluno(@RequestBody RequestPessoa request) {

        if (repository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email já cadastrado");
        }

        Aluno pessoa = new Aluno();
        pessoa.setEmail(request.getEmail());
        pessoa.setSenha(passwordEncoder.encode(request.getSenha().toString()));

        repository.save(pessoa);

        String token = jwtService.generateToken(pessoa);

        return ResponseEntity.ok(token);
    }

    @PostMapping("/register/professor")
    public ResponseEntity<?> registerProfessor(@RequestBody RequestPessoa request) {

        if (repository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email já cadastrado");
        }

        Professor pessoa = new Professor();
        pessoa.setEmail(request.getEmail());
        pessoa.setSenha(passwordEncoder.encode(request.getSenha().toString()));

        repository.save(pessoa);

        String token = jwtService.generateToken(pessoa);

        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RequestPessoa request) {

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getSenha()
                )
        );

        Pessoa pessoa = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String token = jwtService.generateToken(pessoa);

        return ResponseEntity.ok(token);
    }
}
