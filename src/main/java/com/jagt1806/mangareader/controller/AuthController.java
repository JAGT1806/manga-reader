package com.jagt1806.mangareader.controller;

import com.jagt1806.mangareader.http.request.auth.*;
import com.jagt1806.mangareader.http.response.auth.LoginResponse;
import com.jagt1806.mangareader.http.response.error.ErrorResponse;
import com.jagt1806.mangareader.http.response.ok.OkResponse;
import com.jagt1806.mangareader.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Controlador para gestión de autenticación de usuarios")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Inicio de sesión de usuario", description = "Autentica un usuario con email y contraseña"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Registrar nuevo usuario", description = "Crea un nuevo usuario deshabilitado en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OkResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de usuario inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/register")
    public ResponseEntity<OkResponse> register(@RequestBody RegisterRequest request) {
        authService.registerUser(request);
        return new ResponseEntity<>(new OkResponse(), HttpStatus.CREATED);
    }

    @Operation(summary = "Activar usuario", description = "Activa la cuenta de usuario mediante un código"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario activado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OkResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Código de activación inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/activate")
    public ResponseEntity<OkResponse> activateAccount(
            @RequestBody ActivateRequest request) {
        authService.activateAccount(request);
        return ResponseEntity.ok(new OkResponse());
    }

    @Operation(summary = "Recuperar contraseña", description = "Envía un código de recuperación al correo electrónico"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Código de recuperación enviado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OkResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Email no registrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<OkResponse> forgotPassword(@RequestBody CodeRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(new OkResponse());
    }

    @Operation(summary = "Restablecer contraseña", description = "Cambia la contraseña usando un código de recuperación"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña restablecida exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OkResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Código de recuperación inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/reset-password")
    public ResponseEntity<OkResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(new OkResponse());
    }

    @Operation(summary = "Reenviar código de verificación", description = "Reenvía el código de confirmación de registro"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Código reenviado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OkResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Cuenta no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/resend-activate")
    public ResponseEntity<OkResponse> resendActivate(@RequestBody CodeRequest request) {
        authService.resendValidatedEmail(request);
        return ResponseEntity.ok(new OkResponse());
    }



}
