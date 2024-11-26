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
import com.proyecto.mangareader.app.responses.user.UserResponse;
import com.proyecto.mangareader.app.security.util.JwtUtil;
import com.proyecto.mangareader.app.security.entity.CustomUserDetails;
import com.proyecto.mangareader.app.security.service.CustomUserDetailsService;
import com.proyecto.mangareader.app.service.IAuthService;
import com.proyecto.mangareader.app.service.IImgService;
import com.proyecto.mangareader.app.service.IVerificationCodesService;
import com.proyecto.mangareader.app.util.MessageUtil;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
 * Servicio de autenticación para la aplicación Manga Reader que gestiona los procesos
 * de autenticación, registro, verificación de usuarios y gestión de tokens.
 *
 * Esta clase proporciona funcionalidades centrales de seguridad incluyendo:
 * - Inicio de sesión de usuarios
 * - Registro de nuevos usuarios
 * - Verificación de correo electrónico
 * - Recuperación de contraseña
 * - Validación de tokens JWT
 *
 * @author Jhon Alexander Gómez Trujillo
 */
@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    /** Repositorio para operaciones de persistencia de usuarios.*/
    private final UsersRepository usersRepository;
    /** Servicio para gestión de imágenes de perfil. */
    private final IImgService imgService;
    /** Utilidad para generación y validación de tokens JWT. */
    private final JwtUtil jwtUtil;
    /** Servicio personalizado para cargar detalles de usuario. */
    private final CustomUserDetailsService userDetailsService;
    /**Gestor de autenticación para validar credenciales. */
    private final AuthenticationManager authenticationManager;
    /** Repositorio para gestión de roles de usuario. */
    private final RolesRepository rolesRepository;
    /** Codificador de contraseñas para seguridad. */
    private final PasswordEncoder passwordEncoder;
    /** Servicio para generación y gestión de códigos de verificación. */
    private final IVerificationCodesService codesService;
    /** Utilidad para gestión de mensajes localizados. */
    private final MessageUtil messageUtil;

    /**
     * Autentica a un usuario en el sistema utilizando credenciales de correo electrónico y contraseña.
     *
     * @param email Correo electrónico del usuario para iniciar sesión
     * @param password Contraseña del usuario
     * @return Respuesta HTTP con detalles de inicio de sesión, incluyendo token JWT
     * @throws UsernameNotFoundException Si el usuario no existe
     * @throws IllegalStateException Si la cuenta del usuario no está habilitada
     */
    @Override
    public ResponseEntity<LoginResponse> login(String email, String password) {
        
        UsersEntity user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(messageUtil.getMessage("auth.error.user.not.found")));

        if(!user.isEnabled()) {
            throw new IllegalStateException(messageUtil.getMessage("user.not.enabled"));
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

       
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);
        
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

    /**
     * Registra un nuevo usuario en el sistema con los datos proporcionados.
     *
     * @param request Solicitud de registro con información del nuevo usuario
     * @return Respuesta con los detalles del usuario registrado
     * @throws MessagingException Si hay un error al enviar el correo de verificación
     * @throws IllegalArgumentException Si los datos de registro son inválidos
     */
    @Override
    public UserResponse registerUser(RegisterRequest request) throws MessagingException {
        if (request == null || request.getUsername().isEmpty() ||
                request.getEmail().isEmpty() || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException(messageUtil.getMessage("user.null"));
        }


        // Verificar si ya existe un usuario con ese email o username
        if (usersRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(messageUtil.getMessage("user.unique"));
        }

        UsersEntity user = new UsersEntity();
        setDTOtoUser(user, request);
        user.setEnabled(false);

        RolesEntity role = rolesRepository.findByRol("ROLE_USER")
                .orElseThrow(() -> new RoleNotFoundException(null));

        Set<RolesEntity> roles = new HashSet<>();
        roles.add(role);

        user.setRoles(roles);

        user.setProfileImage(imgService.getImg(1L));


        try {
            usersRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            if(e.getMessage().contains("uk6dotkott2kjsp8vw4d0m25fb7")) {
                throw new UniqueException(messageUtil.getMessage("user.unique"));
            }
            throw e;
        }

        // Generar y enviar nuevo código
        codesService.generateCode(user, VerificationCodesService.CodeType.REGISTRATION);

        UsersDTO usersDTO = convertToDTO(user);
        return new UserResponse(usersDTO);
    }

    /**
     * Verifica el correo electrónico de un usuario utilizando un código de verificación.
     *
     * @param request Solicitud de verificación con código proporcionado
     * @throws IllegalStateException Si el código de verificación no es válido
     */
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

    /**
     * Inicia el proceso de recuperación de contraseña para un usuario.
     * Genera un código de verificación para restablecer la contraseña.
     *
     * @param request Solicitud de recuperación con correo electrónico del usuario
     * @throws MessagingException Si hay un error al enviar el correo de recuperación
     * @throws UserNotFoundException Si no se encuentra el usuario
     */
    @Override
    public void forgotPassword(ForgotPasswordRequest request) throws MessagingException {
        UsersEntity user = usersRepository.findByEmail(request.getEmail().toLowerCase()).orElseThrow(() -> new UserNotFoundException(null));

        if(!user.isEnabled()) {
            throw new UserNotEnabledException(null);
        }

        codesService.generateCode(user, VerificationCodesService.CodeType.PASSWORD_RESET);
    }

    /**
     * Restablece la contraseña de un usuario utilizando un código de verificación.
     *
     * @param request Solicitud de restablecimiento con nuevo código y contraseña
     */
    @Override
    public void resetPassword(ResetPasswordRequest request) {
        String code = request.getCode();
        UsersEntity user = codesService.getUserByCode(code, VerificationCodesService.CodeType.PASSWORD_RESET);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        usersRepository.save(user);
    }

    /**
     * Reenvía un correo de validación a un usuario que aún no ha verificado su cuenta.
     *
     * @param request Solicitud de reenvío con correo electrónico
     * @throws MessagingException Si hay un error al enviar el correo
     * @throws UserNotFoundException Si no se encuentra el usuario
     * @throws UserAlreadyEnabledException Si la cuenta ya está habilitada
     */
    @Override
    public void resendValidatedEmail(ResendValidatedRequest request) throws MessagingException {
        UsersEntity user = usersRepository.findByEmail(request.getEmail().toLowerCase()).orElseThrow(() -> new UserNotFoundException(null));

        if(user.isEnabled()) {
            throw new UserAlreadyEnabledException(null);
        }

        codesService.generateCode(user, VerificationCodesService.CodeType.REGISTRATION);
    }

    /**
     * Convierte una entidad de usuario en un DTO de transferencia de datos.
     *
     * @param user Entidad de usuario a convertir
     * @return DTO con información del usuario
     */
    private UsersDTO convertToDTO(UsersEntity user) {
        UsersDTO usersDTO = new UsersDTO();
        usersDTO.setIdUser(user.getIdUser());
        usersDTO.setUsername(user.getUsername());
        usersDTO.setEmail(user.getEmail());
        usersDTO.setEnabled(user.isEnabled());

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

        usersDTO.setImageProfile(user.getProfileImage().getUrl());

        return usersDTO;
    }


    /**
     * Modifica un DTO de transferencia de datos en una entidad de usuario.
     *
     * @param user Entidad de usuario a convertir
     * @param request Solicitud de registro de usuario
     */
    private void setDTOtoUser(UsersEntity user, RegisterRequest request) {
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDateCreate(LocalDateTime.now());
    }


}