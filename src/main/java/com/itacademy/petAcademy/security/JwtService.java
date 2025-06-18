package com.itacademy.petAcademy.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.ExpirationInMs}")
    public static long JWT_EXPIRATION;

    @Value("${jwt.Secret}")
    public static String SECRETKEYBASE64STRING;

    private final SecretKey jjwtSigningKey;

    private final UserDetailsService userDetailsService;

    public JwtService(
            @Value("${jwt.Secret}") String SECRETKEYBASE64STRING,
            @Value("${jwt.ExpirationInMs}") long jwtExpiration,
            @Lazy UserDetailsService userDetailsService
    ) {
        this.SECRETKEYBASE64STRING = SECRETKEYBASE64STRING;
        this.userDetailsService = userDetailsService;
        byte[] decodedKeyBytes = Decoders.BASE64.decode(SECRETKEYBASE64STRING);
        this.jjwtSigningKey = Keys.hmacShaKeyFor(decodedKeyBytes);
        this.JWT_EXPIRATION = jwtExpiration;
    }

    public String generateToken(String userName) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("userName", userName);

        return Jwts.builder()
                .setClaims(claims) // Estableix els claims
                .setSubject("User Details") // Subject del token
                .setIssuedAt(new Date(System.currentTimeMillis())) // Data d'emissió
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION)) // Data d'expiració
                .setIssuer("JOB TRACKER APPLICATION") // Emissor
                .signWith(jjwtSigningKey) // Signa amb la clau JJWT
                .compact(); // Converteix a cadena compacta
    }

    public String generateToken(String userName, List<String> roles) {
        // Map<String, Object> claims = new HashMap<>();
        Claims claims = Jwts.claims();//.setSubject(userName);
        claims.put("userName", userName);
        claims.put("roles", roles);

        return Jwts.builder()
                .setClaims(claims) // Estableix els claims
                .setSubject("User Details") // Subject del token
                .setIssuedAt(new Date(System.currentTimeMillis())) // Data d'emissió
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION)) // Data d'expiració
                .setIssuer("JOB TRACKER APPLICATION") // Emissor
                .signWith(jjwtSigningKey) // Signa amb la clau JJWT
                .compact(); // Converteix a cadena compacta
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jjwtSigningKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token) {
        return extractClaim(token, claims -> claims.get("userName", String.class));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}