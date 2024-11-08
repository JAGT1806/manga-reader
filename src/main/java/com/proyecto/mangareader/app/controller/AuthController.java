package com.proyecto.mangareader.app.controller;

import com.proyecto.mangareader.app.dto.in.InLoginDTO;
import com.proyecto.mangareader.app.exceptions.AuthenticationFailedException;
import com.proyecto.mangareader.app.exceptions.InvalidTokenException;
import com.proyecto.mangareader.app.responses.auth.LoginResponse;
import com.proyecto.mangareader.app.responses.error.ErrorResponse;
import com.proyecto.mangareader.app.responses.message.MessageResponse;
import com.proyecto.mangareader.app.responses.session.SessionResponse;
import com.proyecto.mangareader.app.responses.user.UserResponse;
import com.proyecto.mangareader.app.security.JwtUtil;
import com.proyecto.mangareader.app.service.imp.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Iniciar sesión de usuario")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid InLoginDTO loginRequest) {
        try {
            LoginResponse response = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(response);
        } catch (AuthenticationFailedException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    "Credenciales inválidas",
                    HttpStatus.UNAUTHORIZED.value(),
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

//    @PostMapping("/logout")
//    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
//        try {
//            if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                String token = authHeader.substring(7);
//                authService.logout(token);
//            }
//            return ResponseEntity.ok().body(new MessageResponse("Sesión cerrada exitosamente"));
//        } catch (Exception e) {
//            ErrorResponse errorResponse = new ErrorResponse(
//                    "Error al cerrar sesión",
//                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                    LocalDateTime.now()
//            );
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//    }

    @GetMapping("/session")
    public ResponseEntity<?> getCurrentSession(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new InvalidTokenException("Token no proporcionado o formato inválido");
            }
            String token = authHeader.substring(7);
            SessionResponse response = authService.getCurrentSession(token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    e.getMessage(),
                    HttpStatus.UNAUTHORIZED.value(),
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new InvalidTokenException("Token no proporcionado o formato inválido");
            }
            String token = authHeader.substring(7);
            boolean isValid = authService.validateToken(token);
            if (isValid) {
                return ResponseEntity.ok(new MessageResponse("Token válido"));
            } else {
                throw new InvalidTokenException("Token inválido");
            }
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    e.getMessage(),
                    HttpStatus.UNAUTHORIZED.value(),
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

}
