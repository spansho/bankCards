package com.example.bankCards.Repository;

import com.example.bankCards.Services.JwtKeyHolder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;

@Repository
public class JwtTokenRepository {
    private final String secret="U2VjcmV0S2V5MTIzNDU2Nzg5MEFCQ0RFRkdISUpLTE1OT1BRUlNUVVZXWFla";

    public String generateToken(UserDetails user) {
        String role = user.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("USER")
                .replace("ROLE_", "");

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getUsername());
        claims.put("role", role);
        Instant now = Instant.now();
        Instant expired = now.plusSeconds(86400);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expired))
                .signWith(JwtKeyHolder.getKey(),SignatureAlgorithm.HS256)
                .compact();
        //60000
    }

    private SecretKey getSigningKey() {
        return JwtKeyHolder.getKey();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        boolean flag=(username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(JwtKeyHolder.getKey()) // Используем verifyWith вместо setSigningKey
                .build()
                .parseSignedClaims(token)
                .getBody();
    }
}