package school.sptech.back_end_PI.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;
import school.sptech.back_end_PI.entity.Pessoa;

import java.util.Date;

@Service
public class JwtService {

    private String SECRET_KEY = "sua_chave_secreta";

    public String generateToken(Pessoa pessoa) {
        return Jwts.builder()
                .setSubject(pessoa.getUsername())
                .claim("role", pessoa.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
