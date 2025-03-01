package com.volodymyrpo.eit.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    // Custom clock if needed; otherwise, you can rely on Jwts.decompressionClock() or system time
    private final Clock clock = new JwtClock();

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration; // in seconds, typically

    /**
     * Generates a fresh token for a given user. You can add any custom claims if needed.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities());
        return doGenerateToken(claims, userDetails.getUsername());
    }

    /**
     * Refreshes the given token by updating 'iat' and 'exp' fields.
     */
    public String refreshToken(String token) {
        // Parse existing claims
        Claims claims = getAllClaimsFromToken(token);
        final Date createdDate = clock.now();
        final Date expirationDate = calculateExpirationDate(createdDate);

        // Update token claims
        claims.put(Claims.ISSUED_AT, createdDate);
        claims.put(Claims.EXPIRATION, expirationDate);

        // Build a new token using updated claims
        return Jwts.builder()
                .claims(claims)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Validates token by comparing username and checking expiration.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Constructs the signing key from the configured secret.
     */
    private Key getSigningKey() {
        // For HMAC-SHA based signature, your secret must be sufficiently long.
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Extract the username (subject) from the JWT token.
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Retrieves the expiration date from the token.
     */
    private Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Generic method to extract any claim from the token by applying a function on the parsed Claims.
     */
    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parses all claims from the token.
     * Uses Jwts.parserBuilder() which is required for JJWT >= 0.10.x
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Checks if the token is expired based on its 'exp' claim.
     */
    private Boolean isTokenExpired(String token) {
        final Date expirationDate = getExpirationDateFromToken(token);
        return expirationDate.before(clock.now());
    }

    /**
     * The actual method that builds and signs the JWT.
     */
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        final Date createdDate = clock.now();
        final Date expirationDate = calculateExpirationDate(createdDate);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(createdDate)
                .expiration(expirationDate)
                // Using SignatureAlgorithm.HS512 with our secret key
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Helper method to calculate expiration date from a base date.
     * `expiration` is assumed to be in seconds in this example; adjust as necessary.
     */
    private Date calculateExpirationDate(Date createdDate) {
        return new Date(createdDate.getTime() + (expiration * 1000));
    }
}
