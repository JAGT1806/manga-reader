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
 * Servicio de utilidad para gestión integral de tokens JWT.
 *
 * Características principales:
 * - Generación de tokens
 * - Validación de tokens
 * - Extracción de claims
 * - Manejo de configuraciones de seguridad
 *
 * @author Jhon Alexander Gómez Trujillo
 */
@Component
public class JwtUtil {
    /** Clave secreta para firma de tokens, configurable por propiedades. */
    @Value("${jwt.secret:defaultSecretKey}")
    private String secretKey;
    /** Tiempo de expiración del token en milisegundos. */
    @Value("${jwt.expiration:864000000}") // 10 days in milliseconds
    private long jwtExpiration;

    /** Llave criptográfica para firmado de tokens. */
    private Key key;

    /**
     * Inicializa la llave criptográfica tras la construcción del bean.
     * Convierte la clave secreta en un formato seguro para firmado.
     */
    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(Base64.getEncoder().encodeToString(secretKey.getBytes()));
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Genera token JWT con información de usuario.
     *
     * Incluye:
     * - Roles del usuario
     * - ID de usuario
     * - Estado de habilitación
     *
     * @param userDetails Detalles del usuario
     * @return Token JWT generado
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
     * Crea token JWT firmado con claims específicos.
     *
     * @param claims Información adicional del token
     * @param subject Identificador principal (username)
     * @return Token JWT firmado
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
     * Extrae el nombre de usuario del token.
     *
     * @param token Token JWT
     * @return Nombre de usuario
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae la fecha de expiración del token JWT.
     *
     * @param token el token JWT
     * @return Fecha de expiración del token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae un reclamo específico del token JWT usando una función de resolución de reclamos.
     *
     * @param token el token JWT
     * @param claimsResolver la función que define qué reclamo extraer
     * @return Claim extraído
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae todos los reclamos del token JWT.
     *
     * @param token el token JWT
     * @return Claims del token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Valida la integridad y vigencia del token.
     *
     * @param token Token JWT a validar
     * @return Estado de validez del token
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
