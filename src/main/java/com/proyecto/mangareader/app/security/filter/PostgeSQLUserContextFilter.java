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

@Component
public class PostgeSQLUserContextFilter extends OncePerRequestFilter {
    @Autowired
    private DataSource dataSource;

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
