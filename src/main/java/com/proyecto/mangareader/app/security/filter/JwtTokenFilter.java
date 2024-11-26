package com.proyecto.mangareader.app.security.filter;

import com.proyecto.mangareader.app.security.util.JwtUtil;
import com.proyecto.mangareader.app.util.MessageUtil;
import com.proyecto.mangareader.app.security.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de seguridad JWT para autenticación y autorización de solicitudes HTTP.
 *
 * Responsabilidades:
 * - Interceptar solicitudes entrantes
 * - Validar tokens JWT
 * - Establecer contexto de autenticación de Spring Security
 *
 * @author Jhon Alexander Gómez Trujillo
 * @since 2024
 */
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    /** Utilidad para gestión de tokens JWT. */
    private final JwtUtil jwtUtil;
    /** Servicio para cargar detalles de usuario. */
    private final CustomUserDetailsService userDetailsService;

    private MessageUtil messageUtil;

    /** Constante para el encabezado de autorización. */
    private static final String AUTHORIZATION_HEADER = "Authorization";
    /** Prefijo para tokens Bearer. */
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Procesa y valida el token JWT para cada solicitud entrante.
     *
     * Pasos de procesamiento:
     * - Extraer token del encabezado
     * - Validar token
     * - Cargar detalles de usuario
     * - Verificar estado del usuario
     * - Establecer autenticación en contexto de seguridad
     *
     * @param request Solicitud HTTP entrante
     * @param response Respuesta HTTP
     * @param filterChain Cadena de filtros de la solicitud
     * @throws ServletException Error de procesamiento de solicitud
     * @throws IOException Error de entrada/salida
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtUtil.validateToken(jwt)) {
                String username = jwtUtil.extractUsername(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if(!userDetails.isEnabled()) {
                    throw new IllegalStateException(messageUtil.getMessage("user.not.enabled"));
                }

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("No se puede establecer la autenticación del usuario: {}", e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT del encabezado de autorización.
     *
     * @param request Solicitud HTTP
     * @return Token JWT o null si no está presente
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

}