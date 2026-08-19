package VV.dev.notificator.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
public class JwtService {

    private final SecretKey secretKey;

    public JwtService(@Value("${jwt.secret-key}") String secretKey) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public Long extractUserId(String token) {
        if (token == null || token.isBlank()) {
            throw new SecurityException("JWT token is missing");
        }

        String clearToken = token.startsWith("Bearer") ? token.substring(7) : token;

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(clearToken)
                    .getPayload();

            Object userId = claims.get("userId");
            if (userId == null) {
                throw new SecurityException("userId claim is missing in JWT");
            }

            return Long.parseLong(userId.toString());
        } catch (Exception e) {
            throw new SecurityException("Invalid JWT token " + e.getMessage());
        }
    }
}
