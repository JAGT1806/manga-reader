package com.proyecto.mangareader.app.security.filter;

import com.proyecto.mangareader.app.security.entity.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;


/**
 * Filtro que establece un contexto de usuario de PostgreSQL para cada solicitud, basado en los detalles del usuario autenticado.
 * Este filtro recupera el ID y el correo del usuario autenticado y aplica estos valores en una sesión de PostgreSQL.
 * @author Jhon Alexander Gómez Trujillo
 */
@Component
public class PostgeSQLUserContextFilter extends OncePerRequestFilter {
    /**
     * DataSource utilizado para obtener conexiones a la base de datos y establecer el contexto del usuario.
     */
    @Autowired
    private DataSource dataSource;

    /**
     * Filtra cada solicitud para establecer un contexto de usuario en PostgreSQL si el usuario está autenticado.
     * Si el usuario está autenticado, recupera el ID y el nombre de usuario y llama al método
     * para configurar el contexto de usuario en PostgreSQL.
     *
     * @param request el HttpServletRequest que se está procesando
     * @param response el HttpServletResponse que se está creando
     * @param filterChain la cadena de filtros para esta solicitud
     * @throws ServletException si ocurre un error en la capa de servlet
     * @throws IOException si se detecta un error de entrada o salida al manejar la solicitud
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                setPostgreSQLUserContext(userDetails.getId(), userDetails.getUsername());
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Establece el contexto de usuario en PostgreSQL para el usuario autenticado.
     * Este método ejecuta una declaración preparada que invoca una función de PostgreSQL para establecer el contexto de usuario actual
     * utilizando el ID de usuario y el nombre de usuario proporcionados.
     *
     * @param userId el ID del usuario autenticado
     * @param username el nombre de usuario del usuario autenticado
     */
    private void setPostgreSQLUserContext(Long userId, String username) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT set_current_user(?, ?)")) {
            statement.setLong(1, userId);
            statement.setString(2, username);
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
