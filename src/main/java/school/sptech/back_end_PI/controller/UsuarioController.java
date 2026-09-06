package school.sptech.back_end_PI.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import school.sptech.back_end_PI.dto.professor.ProfessorLoginRequest;
import school.sptech.back_end_PI.services.JwtService;

import java.util.Arrays;

@RestController
public class UsuarioController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final Environment environment;

    public UsuarioController(AuthenticationManager authenticationManager,
                             JwtService jwtService,
                             Environment environment) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.environment = environment;
    }

    private boolean useSecureCookie(HttpServletRequest httpRequest) {
        return httpRequest.isSecure()
                || Arrays.asList(environment.getActiveProfiles()).contains("prod");
    }

    @PostMapping("/login")
    @io.swagger.v3.oas.annotations.security.SecurityRequirements // Isso remove o cadeado no Swagger
    public ResponseEntity<?> login(@RequestBody ProfessorLoginRequest request,
                                   HttpServletRequest httpRequest,
                                   HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getSenha()
                )
        );

        String token = jwtService.generateToken(authentication);

        // Secure só faz sentido em HTTPS. Em http://localhost (Bruno/dev) um cookie
        // Secure=true nunca é reenviado pelo client -> requests seguintes caem como anonymous -> 403.
        // Em prod (profile "prod") o Secure é forçado mesmo atrás de proxy TLS.
        boolean secure = useSecureCookie(httpRequest);

        ResponseCookie cookie = ResponseCookie.from("authToken", token)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(jwtService.getExpirationTime() / 1000)
                .sameSite("Strict")
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // Token trafega SOMENTE no cookie HttpOnly, nunca no corpo (OWASP A01/A02).
        // Clients de API (Bruno/Postman) autenticam via cookie jar após o /login.
        return ResponseEntity.ok("Login realizado com sucesso");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest httpRequest, HttpServletResponse response) {
        boolean secure = useSecureCookie(httpRequest);
        ResponseCookie springCookie = ResponseCookie.from("authToken", "")
                .maxAge(0)
                .path("/")
                .httpOnly(true)
                .secure(secure)
                .sameSite("Strict")
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, springCookie.toString());
        return ResponseEntity.ok("Logout realizado com sucesso");
    }
}