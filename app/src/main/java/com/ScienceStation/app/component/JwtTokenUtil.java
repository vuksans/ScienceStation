package com.ScienceStation.app.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    static final String CLAIM_KEY_USERNAME = "sub";
    static final String CLAIM_KEY_CREATED = "iat";

    @Value("${jwt.secret}")
    private String secret;

    //@Value("$(jwt.expiration}")
    private Long expiration = Long.parseLong("604800");

    private Clock clock = DefaultClock.INSTANCE;

    public String getUserNameFromToken(String token){return getClaimFromToken(token,Claims::getSubject);}

    public Date getIssuedAtDateFromToken(String token){return getClaimFromToken(token,Claims::getIssuedAt);}

    public Date getExpirationDateFromToken(String token){return getClaimFromToken(token,Claims::getExpiration);}


    public<T> T getClaimFromToken(String token, Function<Claims,T> claimsResolver){
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }


    //returns all claims for the user provided that the secret is correct!
    private Claims getAllClaimsFromToken(String token){
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean isTokenExpired(String token){
        final Date expirationDate = getExpirationDateFromToken(token);
        return expirationDate.before(clock.now());
    }

    public String generateToken(UserDetails userDetails){
        Map<String,Object> claims = new HashMap<>();

        String subject = userDetails.getUsername();

        final Date creationDate = clock.now();
        final Date expirationDate =  new Date(creationDate.getTime() + expiration * 1000);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(creationDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails){
        final String username = getUserNameFromToken(token);
        final Date created = getIssuedAtDateFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // ovo mi je malo sumnjivo
    public Boolean canTokenBeRefreshed(String token){
        final Date created = getIssuedAtDateFromToken(token);
        return !isTokenExpired(token);
    }

    public String refreshToken(String token){
        final Date creationDate = clock.now();
        final Date expirationDate = new Date(creationDate.getTime() + expiration * 1000);

        final Claims claims = getAllClaimsFromToken(token);
        claims.setIssuedAt(creationDate);
        claims.setExpiration(expirationDate);

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
}
