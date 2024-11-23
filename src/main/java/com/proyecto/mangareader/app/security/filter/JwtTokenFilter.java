package com.proyecto.mangareader.app.security.filter;

import com.proyecto.mangareader.app.security.util.JwtUtil;
import com.proyecto.mangareader.app.security.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que intercepta cada solicitud HTTP para validar y procesar un token JWT en el encabezado de autorización.
 * Si el token es válido, establece la autenticación en el contexto de seguridad de Spring.
 * @author Jhon Alexander Gómez Trujillo
 */
@Component
@AllArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    /**
     * Utilidad para la generación y validación de tokens JWT.
     */
    private final JwtUtil jwtUtil;
    /**
     * Servicio de detalles del usuario para cargar información del usuario autenticado.
     */
    private final CustomUserDetailsService userDetailsService;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Filtra cada solicitud para validar el token JWT presente en el encabezado de autorización.
     * Si el token es válido y el usuario está habilitado, establece la autenticación en el contexto de seguridad de Spring.
     *
     * @param request el HttpServletRequest que contiene la solicitud del cliente
     * @param response el HttpServletResponse para la respuesta al cliente
     * @param filterChain la cadena de filtros para esta solicitud
     * @throws ServletException si ocurre un error en el procesamiento de la solicitud
     * @throws IOException si se detecta un error de entrada o salida al manejar la solicitud
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
                    throw new IllegalStateException("Usuario no validado");
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
     * Obtiene el token JWT de la solicitud HTTP.
     * Verifica que el encabezado contenga el prefijo "Bearer" y extrae el token.
     *
     * @param request el HttpServletRequest que contiene la solicitud del cliente
     * @return el token JWT si está presente y tiene el formato correcto; null en caso contrario
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * Excluye rutas específicas del filtrado de autenticación con JWT.
     * Las rutas relacionadas con documentación de API y ciertas rutas de autenticación no requieren autenticación.
     *
     * @param request el HttpServletRequest que contiene la solicitud del cliente
     * @return true si la ruta debe excluirse del filtrado, false en caso contrario
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api-docsss") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/doc/swagger-ui") ||
                path.startsWith("/swagger-resources") ||
                path.startsWith("/webjars") ||
                path.startsWith("/api/auth") ||
                path.equals("/api/user/forgot-password") ||
                path.equals("/api/user/reset-password") ||
                path.equals("/api/img") ||
                path.startsWith("/api/manga");
    }
}