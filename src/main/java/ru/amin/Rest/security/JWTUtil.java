package ru.amin.Rest.security;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class JWTUtil {
    @Value("${jwt_secret}")
    private String secret;

    public String generateToken(String username){

        Date expirationDate=Date.from(ZonedDateTime.now().plusDays(30).toInstant());

        return com.auth0.jwt.JWT.create()
                .withSubject("User details")
                .withClaim("username",username)
                .withIssuedAt(new Date())
                .withIssuer("amin")
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(secret));
    }

    public String validateTokenAndRetrieveClaim(String token) throws JWTVerificationException {
        JWTVerifier verifier=com.auth0.jwt.JWT.require(Algorithm.HMAC256(secret))
                .withSubject("User details")
                .withIssuer("amin")
                .build();

        DecodedJWT jwt=verifier.verify(token);
        return jwt.getClaim("username").asString();
    }
}