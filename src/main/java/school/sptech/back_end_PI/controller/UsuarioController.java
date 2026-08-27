package school.sptech.back_end_PI.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import school.sptech.back_end_PI.dto.professor.ProfessorLoginRequest;
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
        cookie.setSecure(!"dev".equalsIgnoreCase(System.getProperty("spring.profiles.active")));
        cookie.setPath("/");
        cookie.setMaxAge(5 * 60);

        response.addCookie(cookie);

        return ResponseEntity.ok("Login realizado com sucesso - Token gerado");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("authToken", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(!"dev".equalsIgnoreCase(System.getProperty("spring.profiles.active")));
        ResponseCookie springCookie = ResponseCookie.from("authToken", null) // response cookie defini os atributos, eai coloca como cookie padrão
                .maxAge(0)
                .path("/")
                .httpOnly(true)
                .secure(!"dev".equalsIgnoreCase(System.getProperty("spring.profiles.active")))
                .sameSite("Lax")
                .build();
        cookie.setValue(springCookie.toString()); // converte o response cookie pra string, e dps adiciona como cookie padrão
        response.addCookie(cookie);
        response.setHeader("Set-Cookie", springCookie.toString()); // ou adiciona um set cookie diretamente
        return ResponseEntity.ok("Logout realizado com sucesso");
    }
}