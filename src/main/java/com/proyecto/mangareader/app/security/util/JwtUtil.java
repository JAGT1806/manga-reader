package com.proyecto.mangareader.app.security.util;

import com.proyecto.mangareader.app.security.entity.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Clase utilitaria para gestionar la generación, validación y extracción de datos de tokens JWT.
 */
@Component
public class JwtUtil {
    /**
     * Clave secreta para firmar el token JWT, inyectada desde propiedades de configuración.
     */
    @Value("${jwt.secret:defaultSecretKey}")
    private String secretKey;
    /**
     * Tiempo de expiración del token JWT, inyectado desde propiedades de configuración.
     */
    @Value("${jwt.expiration:864000000}") // 10 days in milliseconds
    private long jwtExpiration;

    /**
     * Llave para firmar los tokens generados.
     */
    private Key key;

    /**
     * Método inicial para decodificar la clave secreta y generar la llave criptográfica.
     */
    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(Base64.getEncoder().encodeToString(secretKey.getBytes()));
        key = Keys.hmacShaKeyFor(keyBytes);
    }


    /**
     * Genera un token JWT basado en los detalles del usuario.
     *
     * @param userDetails detalles del usuario que serán incluidos en el token
     * @return el token JWT generado
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList());

        if (userDetails instanceof CustomUserDetails) {
            claims.put("userId", ((CustomUserDetails) userDetails).getId());
            claims.put("enabled", ((CustomUserDetails) userDetails).isEnabled());
        }

        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Crea el token JWT con los reclamos y el sujeto especificados.
     *
     * @param claims reclamos o información adicional que se incluirá en el token
     * @param subject el sujeto (generalmente el nombre de usuario) al que se asocia el token
     * @return el token JWT creado
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extrae el nombre de usuario (sujeto) del token JWT.
     *
     * @param token el token JWT
     * @return el nombre de usuario extraído del token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae la fecha de expiración del token JWT.
     *
     * @param token el token JWT
     * @return la fecha de expiración del token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae un reclamo específico del token JWT usando una función de resolución de reclamos.
     *
     * @param token el token JWT
     * @param claimsResolver la función que define qué reclamo extraer
     * @return el reclamo extraído
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae todos los reclamos del token JWT.
     *
     * @param token el token JWT
     * @return todos los reclamos contenidos en el token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Valida el token JWT verificando su firma.
     *
     * @param token el token JWT a validar
     * @return {@code true} si el token es válido, de lo contrario {@code false}
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Verifica si el token JWT ha expirado.
     *
     * @param token el token JWT
     * @return {@code true} si el token ha expirado, de lo contrario {@code false}
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

}
