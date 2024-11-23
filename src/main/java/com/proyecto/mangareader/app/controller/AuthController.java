package com.proyecto.mangareader.app.controller;

import com.proyecto.mangareader.app.request.auth.LoginRequest;
import com.proyecto.mangareader.app.request.auth.RegisterRequest;
import com.proyecto.mangareader.app.request.auth.ResendValidatedRequest;
import com.proyecto.mangareader.app.request.auth.VerificationRequest;
import com.proyecto.mangareader.app.request.users.ForgotPasswordRequest;
import com.proyecto.mangareader.app.request.users.ResetPasswordRequest;
import com.proyecto.mangareader.app.responses.error.ErrorResponse;
import com.proyecto.mangareader.app.responses.ok.OkResponse;
import com.proyecto.mangareader.app.responses.user.UserResponse;
import com.proyecto.mangareader.app.service.IAuthService;
import com.proyecto.mangareader.app.service.IUsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * Controlador para la autenticación de usuarios en la aplicación de Manga Reader.
 * Ofrece endpoints para iniciar sesión, obtener la sesión actual y validar tokens.
 * @author Jhon Alexander Gómez Trujillo
 */
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final IAuthService authService;
    private final IUsersService usersService;

    /**
     * Inicia sesión con las credenciales proporcionadas en el cuerpo de la solicitud.
     *
     * @param loginRequest objeto {@link LoginRequest} con el email y la contraseña del usuario.
     * @return un {@link ResponseEntity} que contiene la respuesta de inicio de sesión con un token de autenticación,
     * o un mensaje de error en caso de fallar la autenticación.
     */
    @Operation(summary = "Iniciar sesión de usuario")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
            return authService.login(loginRequest.getEmail(), loginRequest.getPassword());
    }

    @Operation(summary = "Registar un nuevo usuario", description = "Crea un nuevo usuario deshabilitado en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "usuario creado exitosamente",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de usuarios inválidos",
                    content=@Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content=@Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) throws MessagingException {
        UserResponse response = usersService.registerUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @Operation(summary = "Habilita usuario", description = "Verifica la creación y habilita al usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "usuario creado exitosamente",
                    content = @Content(schema = @Schema(implementation = OkResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de usuarios inválidos",
                    content=@Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content=@Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/verify")
    public ResponseEntity<OkResponse> verify(@RequestBody VerificationRequest request) {
        usersService.verifyEmail(request);
        OkResponse response = new OkResponse();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Operation(summary = "Contraseña olvidada", description = "Envía un código para recuperar la contraseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Envío del código exitosamente",
                    content = @Content(schema = @Schema(implementation = OkResponse.class))),
            @ApiResponse(responseCode = "400", description = "Email no registrado o no existente",
                    content=@Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content=@Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/forgot-password")
    public OkResponse forgotPassword(@RequestBody ForgotPasswordRequest request) throws MessagingException {
        usersService.forgotPassword(request);
        return new OkResponse();
    }

    @Operation(summary = "Cambiar contraseña", description = "Permite cambiar la contraseña por medio de un código")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña modificada exitosamente",
                    content = @Content(schema = @Schema(implementation = OkResponse.class))),
            @ApiResponse(responseCode = "400", description = "Código inválido",
                    content=@Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content=@Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/reset-password")
    public OkResponse resetPassword(@RequestBody ResetPasswordRequest request) {
        usersService.resetPassword(request);
        return new OkResponse();
    }

    @Operation(summary = "Reenviar código de verificación", description = "Reenvía el código de confirmación de registro en el aplicativo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Correo enviado exitosamente",
                    content = @Content(schema = @Schema(implementation = OkResponse.class))),
            @ApiResponse(responseCode = "400", description = "Cuenta no encontrada",
                    content=@Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content=@Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/resend-validate")
    public OkResponse resendValidated(@RequestBody ResendValidatedRequest request) throws MessagingException {
        usersService.resendValidatedEmail(request);
        return new OkResponse();
    }

}
