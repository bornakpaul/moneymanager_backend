package com.apptechlab.moneymanager.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final Key signingKey;
    private final long jwtValidityMillis;
    private final long clockSkewSeconds;

    public JwtUtil(
            @Value("${security.jwt.secret}") String base64Secret,
            @Value("${security.jwt.validity}") long jwtValidityMillis,
            @Value("${security.jwt.clock-skew-seconds}") long clockSkewSeconds
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.jwtValidityMillis = jwtValidityMillis;
        this.clockSkewSeconds = clockSkewSeconds;
    }


    public String generateToken(String username){
        return generateToken(Map.of(), username);
    }

    public String generateToken(Map<String, Object> extraClaims,String username){
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtValidityMillis);

        return Jwts.builder().setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String refreshToken(String token){
        Claims claims = extractAllClaims(token);
        claims.remove(Claims.ISSUED_AT);
        claims.remove(Claims.EXPIRATION);

        return generateToken(claims, claims.getSubject());
    }

    public boolean isTokenValid(String token, String expectedUsername){
        try {
            final String username = extractUsername(token);
            return (username.equals(expectedUsername) && !isTokenExpired(token));
        } catch (JwtException e){
            return false;
        }
    }

    public boolean isTokenExpired(String token){
        Date exp = getExpiration(token);
        return exp.before(new Date());
    }

    public  String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public Date getIssuedAt(String token){
        return extractClaim(token, Claims::getIssuedAt);
    }

    public Date getExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }


    public <T> T extractClaim(String token, Function<Claims,T> resolver){
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    public Claims extractAllClaims(String token){
        JwtParser parser = Jwts.parserBuilder()
                .requireAudience((String) null)
                .setSigningKey(signingKey)
                .setAllowedClockSkewSeconds(clockSkewSeconds)
                .build();
        return parser.parseClaimsJws(token).getBody();
    }

    public Key getSigningKey() {
        return signingKey;
    }

    public Long getJwtValidityMillis() {
        return jwtValidityMillis;
    }

    public Duration getValidity(){
        return Duration.ofMillis(jwtValidityMillis);
    }
}
