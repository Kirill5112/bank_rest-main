package com.example.bankcards.security;

import com.example.bankcards.entity.User;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    @Value("${secret.key}")
    private String secretKey;

    @Value("${secret.access-token-expiration}")
    private Long tokenExp;

    public String generateToken(String username) {
        User user = new User();
        user.setUsername(username);
        username = user.getUsername();
        Key key = getKeyFromSecret();
        return Jwts.builder()
                .claim("sub", username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + tokenExp)) // 24 часа
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getKeyFromSecret())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {

        try {
            Jwts.parser()
                    .verifyWith(getKeyFromSecret())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private SecretKey getKeyFromSecret() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secretKey)
        );
    }
}