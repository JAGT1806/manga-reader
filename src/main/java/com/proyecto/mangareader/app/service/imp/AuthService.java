package com.proyecto.mangareader.app.service.imp;

import com.proyecto.mangareader.app.dto.users.UsersDTO;
import com.proyecto.mangareader.app.entity.RolesEntity;
import com.proyecto.mangareader.app.entity.UsersEntity;
import com.proyecto.mangareader.app.entity.VerificationCodesEntity;
import com.proyecto.mangareader.app.exceptions.*;
import com.proyecto.mangareader.app.repository.RolesRepository;
import com.proyecto.mangareader.app.repository.UsersRepository;
import com.proyecto.mangareader.app.request.auth.RegisterRequest;
import com.proyecto.mangareader.app.request.auth.ResendValidatedRequest;
import com.proyecto.mangareader.app.request.auth.VerificationRequest;
import com.proyecto.mangareader.app.request.users.ForgotPasswordRequest;
import com.proyecto.mangareader.app.request.users.ResetPasswordRequest;
import com.proyecto.mangareader.app.responses.auth.LoginResponse;
import com.proyecto.mangareader.app.responses.message.MessageResponse;
import com.proyecto.mangareader.app.responses.user.UserResponse;
import com.proyecto.mangareader.app.security.util.JwtUtil;
import com.proyecto.mangareader.app.security.entity.CustomUserDetails;
import com.proyecto.mangareader.app.security.service.CustomUserDetailsService;
import com.proyecto.mangareader.app.service.IAuthService;
import com.proyecto.mangareader.app.service.IVerificationCodesService;
import com.proyecto.mangareader.app.util.MessageUtil;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Servicio de autenticación para la aplicación Manga Reader.
 * Proporciona funcionalidades de inicio de sesión, verificación de sesión y validación de tokens.
 * @author Jhon Alexander Gómez Trujillo
 */
@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    private final UsersRepository usersRepository;
    private final UsersService usersService;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final IVerificationCodesService codesService;
    private final MessageUtil messageSource;

    /**
     * Inicia sesión de usuario mediante autenticación con email y contraseña.
     *
     * @param email    El correo electrónico del usuario.
     * @param password La contraseña del usuario.
     * @return ResponseEntity que contiene la respuesta de éxito con los datos de sesión o un error si las credenciales son inválidas.
     */
    @Override
    public ResponseEntity<?> login(String email, String password) {
        // Obtener el usuario y crear la respuesta
        UsersEntity user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(messageSource.getMessage("auth.error.user.not.found")));

        if(!user.isEnabled()) {
            throw new UserNotEnabledException(messageSource.getMessage("user.not.enabled"));
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        // Obtener los detalles del usuario autenticado
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);
        // Generar el token JWT
        String jwt = jwtUtil.generateToken(userDetails);

        return new ResponseEntity<>(new LoginResponse(
                convertToDTO(user),
                jwt,
                "Bearer",
                userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
        ), HttpStatus.OK);
    }

    @Override
    public UserResponse registerUser(RegisterRequest request) throws MessagingException {
        if (request == null || request.getUsername().isEmpty() ||
                request.getEmail().isEmpty() || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException(messageSource.getMessage("user.null"));
        }

        // Verificar si ya existe un usuario con ese email o username
        if (usersRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(messageSource.getMessage("user.unique"));
        }

        UsersEntity user = new UsersEntity();
        setDTOtoUser(user, request, true);
        user.setEnabled(false);

        RolesEntity role = rolesRepository.findByRol("ROLE_USER")
                .orElseThrow(() -> new RoleNotFoundException(null));

        Set<RolesEntity> roles = new HashSet<>();
        roles.add(role);

        user.setRoles(roles);

        usersRepository.save(user);

        // Generar y enviar nuevo código
        codesService.generateCode(user, VerificationCodesService.CodeType.REGISTRATION);

        UsersDTO usersDTO = setUserDTO(user);
        return new UserResponse(usersDTO);
    }

    @Override
    @Transactional
    public void verifyEmail(VerificationRequest request) {
        String code = request.getCode();
        VerificationCodesEntity verificationCode = codesService.getCode(code);
        codesService.validateCode(verificationCode, VerificationCodesService.CodeType.REGISTRATION);

        UsersEntity user = verificationCode.getUser();
        user.setEnabled(true);
        usersRepository.save(user);

        // El código se inhabilita automáticamente después de la validación
        codesService.useCode(verificationCode);
    }


    @Override
    public void forgotPassword(ForgotPasswordRequest request) throws MessagingException {
        UsersEntity user = usersRepository.findByEmail(request.getEmail().toLowerCase()).orElseThrow(() -> new UserNotFoundException(null));

        if(!user.isEnabled()) {
            throw new UserNotEnabledException(null);
        }

        codesService.generateCode(user, VerificationCodesService.CodeType.PASSWORD_RESET);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        String code = request.getCode();
        UsersEntity user = codesService.getUserByCode(code, VerificationCodesService.CodeType.PASSWORD_RESET);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        usersRepository.save(user);
    }

    @Override
    public void resendValidatedEmail(ResendValidatedRequest request) throws MessagingException {
        UsersEntity user = usersRepository.findByEmail(request.getEmail().toLowerCase()).orElseThrow(() -> new UserNotFoundException(null));

        if(user.isEnabled()) {
            throw new UserAlreadyEnabledException(null);
        }

        codesService.generateCode(user, VerificationCodesService.CodeType.REGISTRATION);
    }



    /**
     * Convierte una entidad de usuario en un DTO de salida.
     *
     * @param user La entidad de usuario a convertir.
     * @return Un objeto OutUsersDTO con los datos del usuario.
     */
    private UsersDTO convertToDTO(UsersEntity user) {
        UsersDTO dto = new UsersDTO();
        dto.setIdUser(user.getIdUser());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());

        Set<RolesEntity> userRoles = user.getRoles();
        Set<String> roles = new HashSet<>();

        for (RolesEntity role : userRoles) {
            roles.add(role.getRol());
        }

        dto.setRol(roles);
        if(user.getDateCreate() != null) {
            dto.setDateCreated(LocalDate.from(user.getDateCreate()));
        }
        if(user.getDateModified() != null) {
            dto.setDateModified(LocalDate.from(user.getDateModified()));
        }

        dto.setImageProfile(user.getProfileImage().getUrl());

        return dto;
    }

    // Métodos privados
    private UsersDTO setUserDTO(UsersEntity user) {
        UsersDTO usersDTO = new UsersDTO();
        usersDTO.setIdUser(user.getIdUser());
        usersDTO.setUsername(user.getUsername());
        usersDTO.setEmail(user.getEmail());

        Set<RolesEntity> userRoles = user.getRoles();
        Set<String> roles = new HashSet<>();

        for (RolesEntity role : userRoles) {
            roles.add(role.getRol());
        }

        usersDTO.setRol(roles);
        if (user.getDateCreate() != null) {
            usersDTO.setDateCreated(LocalDate.from(user.getDateCreate()));
        }

        if (user.getDateModified() != null) {
            usersDTO.setDateModified(LocalDate.from(user.getDateModified()));
        }



        return usersDTO;
    }

    private void setDTOtoUser(UsersEntity user, RegisterRequest request, boolean isNew) {
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if (isNew) {
            user.setDateCreate(LocalDateTime.now());
        } else {
            user.setDateModified(LocalDateTime.now());
        }
    }


    @Override
    public ResponseEntity<MessageResponse> validateToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidTokenException(messageSource.getMessage("token.null"));
        }
        String token = authHeader.substring(7);
        boolean isValid = jwtUtil.validateToken(token);
        if (isValid) {
            return ResponseEntity.ok(new MessageResponse(messageSource.getMessage("token.valid")));
        } else {
            throw new InvalidTokenException(messageSource.getMessage("token.invalid"));
        }
    }

}