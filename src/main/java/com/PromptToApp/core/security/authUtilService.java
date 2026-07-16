package com.PromptToApp.core.security;

import com.PromptToApp.core.CustomExceptionHandling.customUnauthorizedException;
import com.PromptToApp.core.Entity.User;
import com.PromptToApp.core.utils.refreshTokenClaims;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class authUtilService {

    @Value("${security.jwt.secret-key}")
    private String jwtSecret;


    //    returning the jwt secret in key format
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public String createAccessToken(UUID user_id) {

        Map<String, String> claims = new HashMap<>();
        claims.put("userId", user_id.toString());

        return Jwts.builder()
                .claims(claims)
                .subject(user_id.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10)) // 10 mins exp
                .signWith(getSignKey())
                .compact();

    }


    public String createRefreshToken(UUID user_id, UUID refresh_token_id) {

        Map<String, String> claims = new HashMap<>();
        claims.put("refresh_token_id", refresh_token_id.toString());
        claims.put("user_id", user_id.toString());

        return Jwts.builder()
                .claims(claims)
                .subject(user_id.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 20)) // FOR NOW it is 20
                .signWith(getSignKey())
                .compact();

    }


    public UUID decodeAccessToken(String accessToken) {
        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) getSignKey())
                .build()
                .parseSignedClaims(accessToken)
                .getPayload();


        System.out.println("this is the claims " + claims);
        return UUID.fromString(claims.getSubject());

    }


    public refreshTokenClaims decodeRefreshToken(String refreshToken) {

        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) getSignKey())
                .build()
                .parseSignedClaims(refreshToken)
                .getPayload();


        System.out.println("this is the claims " + claims);

        UUID jti = UUID.fromString(claims.get("refresh_token_id", String.class));
        UUID user_id = UUID.fromString(claims.getSubject());


        return refreshTokenClaims.builder().user_id(user_id).jti(jti).build();

    }


    public User getUser() {

        Authentication authObject = SecurityContextHolder.getContext().getAuthentication();

        if (!authObject.isAuthenticated()) {
            throw new customUnauthorizedException("user is unauthorized");
        }

        return (User) authObject.getPrincipal();

    }


    public UUID getUserId() {

        return getUser().getId();

    }
}
