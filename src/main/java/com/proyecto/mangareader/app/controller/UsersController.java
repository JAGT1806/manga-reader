package com.proyecto.mangareader.app.controller;

import com.proyecto.mangareader.app.request.users.ChangePasswordRequest;
import com.proyecto.mangareader.app.request.users.UpdatedUserRequest;
import com.proyecto.mangareader.app.responses.error.ErrorResponse;
import com.proyecto.mangareader.app.responses.ok.OkResponse;
import com.proyecto.mangareader.app.responses.user.UserListResponse;
import com.proyecto.mangareader.app.responses.user.UserResponse;
import com.proyecto.mangareader.app.service.IUsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestión de usuarios en el sistema.
 *
 * Proporciona endpoints para realizar operaciones CRUD sobre usuarios,
 * con mecanismos de seguridad y autorización implementados.
 *
 * @author Equipo de Desarrollo
 * @version 1.0
 * @since 2024
 */
@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
@Tag(name="Users", description = "API para gestionar los usuarios")
public class UsersController {
    /** Servicio para la gestión de operaciones de usuarios */
    private final IUsersService usersService;

    /**
     * Recupera usuarios con múltiples opciones de filtrado.
     *
     * Permite obtener usuarios filtrando por nombre de usuario, email,
     * rol, estado y con paginación.
     *
     * @param username Filtro opcional por nombre de usuario
     * @param email Filtro opcional por correo electrónico
     * @param role Filtro opcional por rol (ROLE_ADMIN o ROLE_USER)
     * @param offset Número de página para paginación (default 0)
     * @param limit Número de elementos por página (default 10)
     * @param enabled Filtro opcional por estado de habilitación
     * @return Respuesta que contiene la lista de usuarios
     *
     * Códigos de respuesta HTTP:
     * - 200: Usuarios encontrados exitosamente
     * - 404: No se encontraron usuarios
     * - 500: Error interno del servidor
     */
    @Operation(summary = "Obtener usuarios", description = "Obtiene todos los usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuarios encontrados exitosamente",
                    content=@Content(schema = @Schema(implementation = UserListResponse.class))),
            @ApiResponse(responseCode = "404", description = "No se encontraron usuarios",
                    content=@Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content=@Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<UserListResponse> getAllUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @Parameter(in = ParameterIn.QUERY, description = "Filtrar por rol",
                        required = false,
                        allowEmptyValue = true,
                        schema = @Schema(allowableValues = { "ROLE_ADMIN", "ROLE_USER"}))
            @RequestParam(required = false) String role,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit,
            @RequestParam(required = false) Boolean enabled ) {
        UserListResponse response = usersService.getAllUsers(username, email, role, offset, limit, enabled);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Recupera un usuario específico por su identificador.
     *
     * Permite obtener los detalles de un usuario. El acceso está restringido
     * a administradores o al propio usuario.
     *
     * @param id Identificador único del usuario
     * @return Respuesta con la información del usuario
     *
     * Códigos de respuesta HTTP:
     * - 200: Usuario encontrado exitosamente
     * - 404: Usuario no encontrado
     * - 500: Error interno del servidor
     */
    @Operation(summary = "Obtener un usuario por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "El usuario con el ID proporcionado no existe",
                    content=@Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content=@Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isUserAllowedToModify(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse response = usersService.getUserById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Modificar un usuario por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario modificado exitosamente",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Argumentos inválidos",
                    content=@Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "El usuario con el ID proporcionado no existe",
                    content=@Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto con el email",
                    content=@Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content=@Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isUserAllowedToModify(#id)")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UpdatedUserRequest request) {
            UserResponse response = usersService.updateUser(id, request);
            return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Actualiza la información de un usuario existente.
     *
     * Permite modificar los datos de un usuario. El acceso está restringido
     * a administradores o al propio usuario.
     *
     * @param id Identificador del usuario a modificar
     * @param request Datos actualizados del usuario
     * @return Respuesta con el usuario modificado
     *
     * Códigos de respuesta HTTP:
     * - 200: Usuario modificado exitosamente
     * - 400: Argumentos inválidos
     * - 404: Usuario no encontrado
     * - 409: Conflicto con el email
     * - 500: Error interno del servidor
     */
    @Operation(summary = "Cambiar la contraseña de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario modificado exitosamente",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Argumentos inválidos",
                    content=@Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "El usuario con el ID proporcionado no existe",
                    content=@Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content=@Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isUserAllowedToModify(#id)")
    @PutMapping("/{id}/password")
    public ResponseEntity<OkResponse> updatePassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request) {
        usersService.changePassword(id, request);
        return new ResponseEntity<>(new OkResponse(), HttpStatus.OK);
    }

    /**
     * Elimina un usuario del sistema.
     *
     * Permite la eliminación de un usuario. El acceso está restringido
     * a administradores o al propio usuario.
     *
     * @param id Identificador del usuario a eliminar
     * @return Respuesta de confirmación de eliminación
     *
     * Códigos de respuesta HTTP:
     * - 200: Usuario eliminado exitosamente
     * - 404: Usuario no encontrado
     * - 500: Error interno del servidor
     */
    @Operation(summary = "Eliminar un usuario por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario modificado exitosamente",
                    content = @Content(schema = @Schema(implementation = OkResponse.class))),
            @ApiResponse(responseCode = "404", description = "El usuario con el ID proporcionado no existe",
                    content=@Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content=@Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isUserAllowedToModify(#id)")
    public ResponseEntity<OkResponse> deleteUser(@PathVariable Long id) {
        OkResponse response = usersService.deleteUser(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
