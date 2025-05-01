package com.banking.service;

import com.banking.dto.auth.LoginResponse;
import com.banking.model.Token;
import com.banking.model.User;
import com.banking.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.*;


@Service
@RequiredArgsConstructor
public class JwtService {

    private final TokenRepository tokenRepository;

    private final SecretKey encryptionKey;

    @Value("${security.jwt.secret-key}")
    private String signingKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    private String encryptJwt(String jwtToken) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE,encryptionKey);
        byte[] encryptedJwt = cipher.doFinal(jwtToken.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().encodeToString(encryptedJwt);
    }

    public String decryptJwt(String jwtToken) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE,encryptionKey);
        byte[] decryptedJwt = cipher.doFinal(Base64.getUrlDecoder().decode(jwtToken));
        return new String(decryptedJwt,StandardCharsets.UTF_8);
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public LoginResponse generateToken(UserDetails userDetails) throws GeneralSecurityException {
        String bearerToken = generateToken(new HashMap<>(),userDetails);
        System.out.println("JWT build on :" + LocalDateTime.now() + ":" + bearerToken);
        LocalDateTime now  = LocalDateTime.now();
        Token token = Token.builder()
                        .user((User)userDetails)
                        .token(bearerToken)
                        .duration(1)
                        .createdOn(now)
                        .expiresOn(now.plusSeconds(jwtExpiration))
                        .build();
        tokenRepository.save(token);

        String expiresAtStr = now.toString();

        return LoginResponse.builder()
                .email(((User) userDetails).getEmail())
                .token(encryptJwt(bearerToken))
                .expiresAt(expiresAtStr)
                .build();
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    public void revokeAllUserTokens(UserDetails userDetails) {
        List<Token> tokens = tokenRepository.findAllUserValidTokens(userDetails.getUsername());
        tokens.forEach(token -> {
            token.setRevoked(true);
            token.setExpired(true);
        });
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        if(username.equals(userDetails.getUsername()) && !isTokenExpired(token)) {
            Optional<Token> exisingToken = tokenRepository.findByToken(token);
            if(exisingToken.isPresent()){
                boolean revokedAndExpired = exisingToken.get().isRevoked() && exisingToken.get().isExpired();
                return !revokedAndExpired;
            }
        }
        return false;
    }


    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {

        return Jwts
                .parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(signingKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}