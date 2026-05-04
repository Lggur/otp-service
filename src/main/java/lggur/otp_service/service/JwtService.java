package lggur.otp_service.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private final SecretKey key = Keys.hmacShaKeyFor(
            "super-secret-key-super-secret-key-123456".getBytes()
    );

    public String generateToken(Long userId, String role) {
        log.debug("Generating JWT for userId={}, role={}", userId, role);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600_000))
                .signWith(key)
                .compact();
    }

    public Long extractUserId(String token) {
        Long userId = Long.valueOf(
                Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
                        .getSubject()
        );
        log.debug("Extracted userId={} from JWT", userId);
        return userId;
    }
}