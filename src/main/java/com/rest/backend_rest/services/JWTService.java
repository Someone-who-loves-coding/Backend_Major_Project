package com.rest.backend_rest.services;

import com.rest.backend_rest.models.BlacklistedToken;
import com.rest.backend_rest.repositories.BlacklistedTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;

@Service
public class JWTService {

    @Value( "${jwt.secret}")
    private String secret;

    private final BlacklistedTokenRepository repo;

    @Autowired
    public JWTService(BlacklistedTokenRepository repo) {
        this.repo = repo;
    }

    public String generateToken(String username) {

        HashMap<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 30))
                .and()
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        byte[] apiKeySecretBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(apiKeySecretBytes);
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        if (repo.existsByToken(token)) {
            return false; // Token is blacklisted
        }
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public void blacklistToken(String token) {
        if (!repo.existsByToken(token)) {
            repo.save(new BlacklistedToken(token));
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}