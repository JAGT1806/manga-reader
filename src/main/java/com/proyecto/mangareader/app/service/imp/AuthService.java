package com.proyecto.mangareader.app.service.imp;

import com.proyecto.mangareader.app.dto.out.OutUsersDTO;
import com.proyecto.mangareader.app.entity.UsersEntity;
import com.proyecto.mangareader.app.exceptions.AuthenticationFailedException;
import com.proyecto.mangareader.app.exceptions.InvalidTokenException;
import com.proyecto.mangareader.app.repository.UsersRepository;
import com.proyecto.mangareader.app.responses.auth.LoginResponse;
import com.proyecto.mangareader.app.responses.session.SessionResponse;
import com.proyecto.mangareader.app.responses.user.UserResponse;
import com.proyecto.mangareader.app.security.JwtUtil;
import com.proyecto.mangareader.app.security.entity.CustomUserDetails;
import com.proyecto.mangareader.app.security.service.CustomUserDetailsService;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UsersRepository usersRepository;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public LoginResponse login(String email, String password) {
        try {
            // Autenticar al usuario
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            // Obtener los detalles del usuario autenticado
            CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);

            // Generar el token JWT
            String jwt = jwtUtil.generateToken(userDetails);

            // Obtener el usuario y crear la respuesta
            UsersEntity user = usersRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            return new LoginResponse(
                    converToDTO(user),
                    jwt,
                    "Bearer",
                    userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .toList()
            );
        } catch (AuthenticationException e) {
            throw new AuthenticationFailedException("Credenciales inválidas");
        }
    }

    public void logout(String token) {
        // Aquí podrías implementar una lista negra de tokens si deseas invalidar tokens específicos
        SecurityContextHolder.clearContext();
    }

    private OutUsersDTO converToDTO(UsersEntity user) {
        OutUsersDTO dto = new OutUsersDTO();
        dto.setIdUser(user.getIdUser());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRol(user.getRol().getRol());
        if(user.getDateCreate() != null) {
            dto.setDateCreated(LocalDate.from(user.getDateCreate()));
        }
        if(user.getDateModified() != null) {
            dto.setDateModified(LocalDate.from(user.getDateModified()));
        }
        return dto;
    }

    public SessionResponse getCurrentSession(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new InvalidTokenException("Token inválido o expirado");
        }

        String email = jwtUtil.extractUsername(token);
        UsersEntity user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        SessionResponse sessionResponse = new SessionResponse();
        sessionResponse.setUser(converToDTO(user));
        sessionResponse.setLoginTime(LocalDateTime.now()); // O podrías extraer el tiempo de creación del token
        sessionResponse.setToken(token);
        sessionResponse.setAuthorities(userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList());
        sessionResponse.setActive(true);
        return sessionResponse;
    }

    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

}