package com.epam.esm.service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class JwtUtil {
    @Value("${auth.jwt.secret_key}")
    private String secretString;

    @Value("${auth.jwt.validity_time}")
    private int validityTime;

    private Key secretKey;

    @PostConstruct
    protected void initKey() {
        secretKey = Keys.hmacShaKeyFor(secretString.getBytes(UTF_8));
    }

    public String generateJwt(Map<String, Object> claims) {
        Instant expirationInstant = LocalDateTime.now(Clock.systemUTC())
                .plus(validityTime, ChronoUnit.DAYS)
                .toInstant(ZoneOffset.UTC);
        Date expirationTime = Date.from(expirationInstant);

        JwtBuilder builder = Jwts.builder()
                .setExpiration(expirationTime)
                .signWith(secretKey);
        claims.forEach(builder::claim);

        return builder.compact();
    }

    public Map<String, Object> parseToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return new HashMap<>(claims);
        } catch (JwtException e) {
            return Collections.emptyMap();
        }
    }
}
