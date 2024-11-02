package com.proyecto.mangareader.app.controller;

import com.proyecto.mangareader.app.dto.in.InLoginDTO;
import com.proyecto.mangareader.app.responses.error.ErrorResponse;
import com.proyecto.mangareader.app.responses.session.SessionResponse;
import com.proyecto.mangareader.app.responses.user.UserResponse;
import com.proyecto.mangareader.app.security.JwtUtil;
import com.proyecto.mangareader.app.service.imp.AuthService;
import io.swagger.v3.oas.annotations.Operation;
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
    public ResponseEntity login(@RequestBody InLoginDTO loginRequest) {
        try {
            UserResponse response = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Credenciales inválidas", HttpStatus.UNAUTHORIZED.value(), LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity logout() {
        try {
            authService.logout();
            return ResponseEntity.ok().body("Sesión cerrada exitosamente");
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Error al cerrar sesión", HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/session")
    public ResponseEntity getCurrentSession() {
        try {
            SessionResponse response = authService.getCurrentSession();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("No hay sesión activa", HttpStatus.UNAUTHORIZED.value(), LocalDateTime.now());
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/validate-token")
    public ResponseEntity<String> validateToken(@RequestParam("tokenValue") String tokenValue) {
        try {
            // Validate the token without "Bearer " prefix
            String username = JwtUtil.extractUsername(tokenValue);
            return ResponseEntity.ok("Token valid for user: " + username);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }
}
