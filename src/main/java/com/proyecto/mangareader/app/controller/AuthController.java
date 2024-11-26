package com.proyecto.mangareader.app.controller;

import com.proyecto.mangareader.app.request.auth.LoginRequest;
import com.proyecto.mangareader.app.request.auth.RegisterRequest;
import com.proyecto.mangareader.app.request.auth.ResendValidatedRequest;
import com.proyecto.mangareader.app.request.auth.VerificationRequest;
import com.proyecto.mangareader.app.request.users.ForgotPasswordRequest;
import com.proyecto.mangareader.app.request.users.ResetPasswordRequest;
import com.proyecto.mangareader.app.responses.auth.LoginResponse;
import com.proyecto.mangareader.app.responses.error.ErrorResponse;
import com.proyecto.mangareader.app.responses.ok.OkResponse;
import com.proyecto.mangareader.app.responses.user.UserResponse;
import com.proyecto.mangareader.app.service.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * Controlador REST para gestionar la autenticación de usuarios en la aplicación Manga Reader.
 *
 * Este controlador proporciona endpoints para:
 * <ul>
 *   <li>Iniciar sesión de usuarios</li>
 *   <li>Registrar nuevos usuarios</li>
 *   <li>Verificar cuentas de usuario</li>
 *   <li>Gestionar recuperación de contraseña</li>
 *   <li>Reenviar código de validación</li>
 * </ul>
 *
 * @author Jhon Alexander Gómez Trujillo
 * @since 2024
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Endpoints para gestión de autenticación de usuarios")
@AllArgsConstructor
public class AuthController {
    /** Servicio de autenticación para procesar operaciones de usuario.*/
    private final IAuthService authService;

    /**
     * Endpoint para iniciar sesión de usuarios en la aplicación.
     *
     * @param loginRequest Credenciales de inicio de sesión del usuario
     * @return Respuesta con token de autenticación o mensaje de error
     */
    @Operation(
            summary = "Iniciar sesión de usuario",
            description = "Autentica un usuario con email y contraseña"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Inicio de sesión exitoso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenciales inválidas",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
            return authService.login(loginRequest.getEmail().toLowerCase(), loginRequest.getPassword());
    }

    /**
     * Endpoint para registrar un nuevo usuario en el sistema.
     *
     * @param request Detalles de registro del usuario
     * @return Respuesta con información del usuario creado
     * @throws MessagingException Si hay un error en el envío de correo electrónico
     */
    @Operation(
            summary = "Registrar nuevo usuario",
            description = "Crea un nuevo usuario deshabilitado en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario creado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de usuario inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) throws MessagingException {
        UserResponse response = authService.registerUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    /**
     * Endpoint para verificar y habilitar un usuario recién registrado.
     *
     * @param request Solicitud de verificación con código de activación
     * @return Respuesta de confirmación de verificación
     */
    @Operation(
            summary = "Verificar usuario",
            description = "Verifica la cuenta de usuario mediante código de activación"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario verificado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OkResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Código de verificación inválido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/verify")
    public ResponseEntity<OkResponse> verify(
            @Parameter(description = "Solicitud de verificación", required = true)
            @RequestBody VerificationRequest request) {
        authService.verifyEmail(request);
        return new ResponseEntity<>(new OkResponse(), HttpStatus.OK);
    }


    /**
     * Endpoint para solicitar recuperación de contraseña.
     *
     * @param request Solicitud con email para recuperación de contraseña
     * @return Respuesta de confirmación de envío de código
     * @throws MessagingException Si hay un error en el envío de correo electrónico
     */
    @Operation(
            summary = "Recuperar contraseña",
            description = "Envía un código de recuperación al correo electrónico"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Código de recuperación enviado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OkResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Email no registrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/forgot-password")
    public OkResponse forgotPassword(@RequestBody ForgotPasswordRequest request) throws MessagingException {
        authService.forgotPassword(request);
        return new OkResponse();
    }

    /**
     * Endpoint para restablecer la contraseña utilizando un código de recuperación.
     *
     * @param request Solicitud con código y nueva contraseña
     * @return Respuesta de confirmación de restablecimiento
     */
    @Operation(
            summary = "Restablecer contraseña",
            description = "Cambia la contraseña usando un código de recuperación"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Contraseña restablecida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OkResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Código de recuperación inválido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/reset-password")
    public OkResponse resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return new OkResponse();
    }


    /**
     * Endpoint para reenviar código de verificación de registro.
     *
     * @param request Solicitud para reenviar código de validación
     * @return Respuesta de confirmación de reenvío
     * @throws MessagingException Si hay un error en el envío de correo electrónico
     */
    @Operation(
            summary = "Reenviar código de verificación",
            description = "Reenvía el código de confirmación de registro"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Código reenviado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OkResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Cuenta no encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/resend-validate")
    public OkResponse resendValidated(@RequestBody ResendValidatedRequest request) throws MessagingException {
        authService.resendValidatedEmail(request);
        return new OkResponse();
    }

}