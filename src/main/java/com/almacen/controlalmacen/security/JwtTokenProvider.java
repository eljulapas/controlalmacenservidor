package com.almacen.controlalmacen.security;

import com.almacen.controlalmacen.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // Utiliza la clase Keys para generar una clave
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    // Generar el token JWT
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getNombre())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 600000))  // 10 minutos
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    // Validar el token JWT
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)  // Usar la clave generada
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
