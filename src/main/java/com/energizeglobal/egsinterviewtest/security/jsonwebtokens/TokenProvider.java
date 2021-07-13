package com.energizeglobal.egsinterviewtest.security.jsonwebtokens;

import com.energizeglobal.egsinterviewtest.config.ApplicationProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class TokenProvider {

    private final Logger log = LoggerFactory.getLogger(TokenProvider.class);

    private static final String AUTHORITIES_KEY = "authorities";

    private final Key key;

    private final JwtParser tokenParser;

    private final long tokenValidityMilliseconds;

    public TokenProvider(ApplicationProperties applicationProperties) {

        byte[] keyBytes;
        String base64Secret = applicationProperties.getSecurity().getAuthentication().getJwt().getBase64Secret();

        keyBytes = Decoders.BASE64.decode(base64Secret);
        key = Keys.hmacShaKeyFor(keyBytes);

        tokenParser = Jwts.parserBuilder().setSigningKey(key).build();

        tokenValidityMilliseconds = 1000 * applicationProperties.getSecurity().getAuthentication().getJwt().getTokenValidityInSeconds();
    }

    public String createToken(Authentication authentication) {

        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        long timeStamp = Instant.now().toEpochMilli();

        Date validity = new Date(timeStamp + this.tokenValidityMilliseconds);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    public Authentication getAuthentication(String token) {

        Claims claims = tokenParser.parseClaimsJws(token).getBody();

        Collection<? extends GrantedAuthority> authorities = Arrays
                .stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .filter(authority -> !authority.trim().isEmpty())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String authToken) {

        try {
            tokenParser.parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException runtimeException) {
            log.info("Invalid JWT token.");
            log.trace("Invalid JWT token trace.", runtimeException);
        }
        return false;
    }
}
