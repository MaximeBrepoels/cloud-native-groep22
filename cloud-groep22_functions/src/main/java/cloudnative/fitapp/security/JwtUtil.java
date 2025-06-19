package cloudnative.fitapp.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import cloudnative.fitapp.domain.User;
import java.security.Key;
import java.util.Date;


public class JwtUtil {

    private static final String DEFAULT_SECRET = "HeelGeheimeKeyDieEigenlijkInEenEnvZouMoetenStaan";
    private static final int TOKEN_VALIDITY = 3600 * 1000;
    private final String secretKey = getSecretKey();

    private static String getSecretKey() {
        String envKey = System.getenv("JWT_SECRET_KEY");
        return (envKey == null || envKey.isEmpty()) ? DEFAULT_SECRET : envKey;
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("id", user.getId())
                .claim("name", user.getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}