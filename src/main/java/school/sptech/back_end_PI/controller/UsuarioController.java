package school.sptech.back_end_PI.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.sptech.back_end_PI.dto.ProfessorLoginRequest;
import school.sptech.back_end_PI.services.JwtService;

@RestController
public class UsuarioController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UsuarioController(AuthenticationManager authenticationManager,
                             JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    @io.swagger.v3.oas.annotations.security.SecurityRequirements // Isso remove o cadeado no Swagger
    public ResponseEntity<?> login(@RequestBody ProfessorLoginRequest request, HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getSenha()
                )
        );

        String token = jwtService.generateToken(authentication);

        Cookie cookie = new Cookie("authToken", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(5 * 60);

        response.addCookie(cookie);

        return ResponseEntity.ok("Login realizado com sucesso - Token gerado");
    }
}