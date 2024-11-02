package com.proyecto.mangareader.app.service.imp;

import com.proyecto.mangareader.app.dto.out.OutUsersDTO;
import com.proyecto.mangareader.app.entity.UsersEntity;
import com.proyecto.mangareader.app.repository.UsersRepository;
import com.proyecto.mangareader.app.responses.session.SessionResponse;
import com.proyecto.mangareader.app.responses.user.UserResponse;
import com.proyecto.mangareader.app.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@AllArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UsersRepository usersRepository;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public UserResponse login(String email, String password) {
        try {
            // Autentucar al usuario
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            // Establecer la autenticación en el contexto de seguridad
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);

            // Crear sesión y almacenar el contexto de seguridad
            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

            // Obtener el usuario y crear la respuesta
            UsersEntity user = usersRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            String token = JwtUtil.generateToken(user.getEmail());
            return new UserResponse(converToDTO(user, token));
        } catch (AuthenticationException e) {
            throw new RuntimeException("Credenciales inválidas");
        }
    }

    public void logout() {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
    }

    private OutUsersDTO converToDTO(UsersEntity user, String token) {
        OutUsersDTO dto = new OutUsersDTO();
        dto.setIdUser(user.getIdUser());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRol(user.getRol().getRol());
        dto.setToken(token);
        if(user.getDateCreate() != null) {
            dto.setDateCreated(LocalDate.from(user.getDateCreate()));
        }
        if(user.getDateModified() != null) {
            dto.setDateModified(LocalDate.from(user.getDateModified()));
        }
        return dto;
    }

    public SessionResponse getCurrentSession() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new RuntimeException("No hay sesion activa");
        }

        HttpSession session = request.getSession(false);

        if(session == null) {
            throw new RuntimeException("No hay sesion activa");
        }

        UsersEntity user = usersRepository.findByEmail(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        SessionResponse sessionResponse = new SessionResponse();
        sessionResponse.setUser(converToDTO(user, null));
        sessionResponse.setLoginTime(LocalDateTime.ofInstant(
                Instant.ofEpochMilli(session.getCreationTime()),
                ZoneId.systemDefault()
        ));
        sessionResponse.setSessionId(session.getId());
        sessionResponse.setAuthorities(authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList());
        sessionResponse.setActive(true);

        return sessionResponse;
    }

}