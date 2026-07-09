package com.college.expensesplittingengine.Auth;

import com.college.expensesplittingengine.Auth.Imple.UserDetailsImple;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("ExpenseSplittingEngine")
    private String jwtCookie;

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getJwtfromCookie(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookie);

        if (cookie != null) {
            return cookie.getValue();
        }

        return null;
    }

    public ResponseCookie generateJwtCookie(UserDetailsImple userDetails) {

        String jwt = generateTokenFromUsername(userDetails.getUsername());

        return ResponseCookie.from(jwtCookie, jwt)
                .path("/")
                .maxAge(24 * 60 * 60 * 10)
                .httpOnly(true)
                .build();
    }

    public ResponseCookie getCleanJwtCookie() {

        return ResponseCookie.from(jwtCookie, "")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .build();
    }

    public String generateTokenFromUsername(String username) {

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }

    public String getUsernameFromToken(String token) {

        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {

        try {

            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parseSignedClaims(authToken);

            return true;

        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());

        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());

        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());

        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}