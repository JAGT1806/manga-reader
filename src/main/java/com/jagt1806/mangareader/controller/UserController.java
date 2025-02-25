package com.jagt1806.mangareader.controller;

import com.jagt1806.mangareader.http.request.user.ChangePasswordRequest;
import com.jagt1806.mangareader.http.request.user.UserUpdateRequest;
import com.jagt1806.mangareader.http.response.error.ErrorResponse;
import com.jagt1806.mangareader.http.response.ok.OkResponse;
import com.jagt1806.mangareader.http.response.user.UserListResponse;
import com.jagt1806.mangareader.http.response.user.UserResponse;
import com.jagt1806.mangareader.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@SecurityRequirement(name = "Auth")
@Tag(name = "Users", description = "Buscar información de los usuarios")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Obtener usuarios", description = "Obtiene todos los usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuarios encontrados exitosamente",
                    content=@Content(mediaType = "application/json", schema = @Schema(implementation = UserListResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN",
                    content=@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No se encontraron los usuarios",
                    content=@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('${app.admin.role}')")
    @GetMapping
    public ResponseEntity<UserListResponse> getUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @Parameter(in = ParameterIn.QUERY, description = "Filtrar por rol",
                    allowEmptyValue = true,
                    schema = @Schema(allowableValues = { "ROLE_ADMIN", "ROLE_USER" }))
            @RequestParam(required = false) String role,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "12") int limit,
            @RequestParam(required = false) Boolean enabled
    ) {
        UserListResponse response = userService.getUsers(username, email, role, offset, limit, enabled);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener usuario", description = "Obtiene un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente",
                    content=@Content(mediaType = "application/json", schema = @Schema(implementation = UserListResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN o ser el mismo usuario",
                    content=@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No se encontró el usuario",
                    content=@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('${app.admin.role}') or @userSecurity.isUserAllowed(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Modificar un usuario", description = "Cambiar username y el id del profileName de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Modificación exitosa",
                    content=@Content(mediaType = "application/json", schema = @Schema(implementation = UserListResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN o ser el mismo usuario",
                    content=@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No se encontró el usuario",
                    content=@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('${app.admin.role}') or @userSecurity.isUserAllowed(#id)")
    @PutMapping("/{id}/edit")
    public ResponseEntity<OkResponse> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        userService.updateUser(id, request);
        return ResponseEntity.ok(new OkResponse());
    }

    @Operation(summary = "Cambiar contraseña", description = "Cambias la contraseña de tu cuenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente",
                    content=@Content(mediaType = "application/json", schema = @Schema(implementation = UserListResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN o ser el mismo usuario",
                    content=@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No se encontró el usuario",
                    content=@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('${app.admin.role}') or @userSecurity.isUserAllowed(#id)")
    @PutMapping("/{id}/password")
    public ResponseEntity<OkResponse> updatePassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request);
        return ResponseEntity.ok(new OkResponse());
    }

    @Operation(summary = "Eliminar un usuario", description = "Elimina un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente",
                    content=@Content(mediaType = "application/json", schema = @Schema(implementation = UserListResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN o ser el mismo usuario",
                    content=@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No se encontró el usuario",
                    content=@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('${app.admin.role}') or @userSecurity.isUserAllowed(#id)")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<OkResponse> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new OkResponse());
    }
}
