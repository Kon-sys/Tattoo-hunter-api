package com.example.authservice.security;
import com.example.authservice.dto.JwtAuthenticationDto;
import com.example.authservice.model.User;
import com.example.authservice.service.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtService {

    private static final Logger LOGGER = LogManager.getLogger(JwtService.class);

    private final UserService userService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public JwtAuthenticationDto generateAuthToken(String login, String role) {
        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto();
        jwtDto.setToken(generateJwtToken(login, role));
        jwtDto.setRefreshToken(generateRefreshJwtToken(login, role));
        return jwtDto;
    }

    public JwtAuthenticationDto refreshToken(JwtAuthenticationDto refreshTokenDto) throws Exception {
        String refreshToken = refreshTokenDto.getRefreshToken();
        if(refreshToken == null || validateToken(refreshToken)) {
            Optional<User> user = userService.findByLogin(getLoginFromToken(refreshToken));
            if(user.isPresent()) {
                return refreshBaseToken(user.get().getLogin(), String.valueOf(user.get().getRole()), refreshToken);
            }
        }
        throw new AuthenticationException("Invalid refresh token");
    }

    public boolean validateToken(String token) {
        try{
            Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        } catch (ExpiredJwtException e) {
            LOGGER.error("Expired JwtException", e);
        } catch (UnsupportedJwtException e) {
            LOGGER.error("Unsupported JwtException", e);
        } catch (MalformedJwtException e) {
            LOGGER.error("Malformed JwtException", e);
        } catch (SecurityException e) {
            LOGGER.error("Security JwtException", e);
        } catch (Exception e) {
            LOGGER.error("Invalid token", e);
        }
        return false;
    }

    public JwtAuthenticationDto refreshBaseToken(String login, String role, String refreshToken) {
        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto();
        jwtDto.setToken(generateJwtToken(login, role));
        jwtDto.setRefreshToken(refreshToken);
        return jwtDto;
    }

    public String getLoginFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    private String generateJwtToken(String login, String role) {
        Date date = Date.from(LocalDateTime.now().plusMinutes(10).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setSubject(login)
                .claim("role", role)
                .setExpiration(date)
                .signWith(getSignInKey())
                .compact();
    }

    private String generateRefreshJwtToken(String login, String role) {
        Date date = Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setSubject(login)
                .claim("role", role)
                .setExpiration(date)
                .signWith(getSignInKey())
                .compact();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}