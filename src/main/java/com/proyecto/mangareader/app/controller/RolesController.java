package com.proyecto.mangareader.app.controller;

import com.proyecto.mangareader.app.request.roles.RolesRequest;
import com.proyecto.mangareader.app.entity.RolesEntity;
import com.proyecto.mangareader.app.responses.error.ErrorResponse;
import com.proyecto.mangareader.app.responses.ok.OkResponse;
import com.proyecto.mangareader.app.responses.role.RoleListResponse;
import com.proyecto.mangareader.app.service.IRolesService;
import io.swagger.v3.oas.annotations.Operation;
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
 * Controlador REST para la gestión de roles en el sistema.
 *
 * Este controlador proporciona endpoints para realizar operaciones CRUD sobre roles,
 * con restricciones de seguridad que requieren permisos de administrador.
 *
 * @author Jhon Alexander Gómez Trujillo
 * @since 2024
 */
@RestController
@RequestMapping("/api/role")
@AllArgsConstructor
@Tag(name="Roles", description = "API para gestionar los roles")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class RolesController {
    /** Servicio para la gestión de roles */
    private final IRolesService rolesService;

    /**
     * Obtiene todos los roles del sistema, con posibilidad de filtrar por nombre.
     *
     * Este endpoint permite recuperar la lista completa de roles o filtrar
     * por un rol específico.
     *
     * @param role Nombre del rol para filtrar (opcional)
     * @return Respuesta con la lista de roles encontrados
     *
     * Códigos de respuesta HTTP:
     * - 200: Roles encontrados exitosamente
     * - 403: Acceso denegado (requiere rol ADMIN)
     * - 404: No se encontraron roles
     * - 500: Error interno del servidor
     */
    @Operation(summary = "Obtener roles", description = "Obtiene todos los roles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles encontrados exitosamente",
                    content=@Content(schema = @Schema(implementation = RoleListResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN"),
            @ApiResponse(responseCode = "404", description = "No se encontraron roles o el rol específico no existe",
                    content=@Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content=@Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<RoleListResponse> getAll(@RequestParam(required = false) String role) {
            RoleListResponse response = rolesService.getAllRoles(role);
            return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Crea un nuevo rol en el sistema.
     *
     * Este endpoint permite a los administradores registrar roles adicionales.
     * Requiere validación de datos y permisos específicos.
     *
     * @param roleDTO Datos del nuevo rol a crear
     * @return Respuesta con el rol recién creado
     *
     * Códigos de respuesta HTTP:
     * - 201: Rol creado exitosamente
     * - 400: Datos de rol inválidos
     * - 403: Acceso denegado (requiere rol ADMIN)
     * - 500: Error interno del servidor
     */
    @Operation(summary = "Guardar un nuevo rol", description = "Crea un nuevo rol en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rol creado exitosamente",
                    content = @Content(schema = @Schema(implementation = RolesEntity.class))),
            @ApiResponse(responseCode = "400", description = "Datos de rol inválidos",
                    content=@Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere ROLE_ADMIN"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content=@Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<RolesEntity> createRole(@RequestBody RolesRequest roleDTO) {
        RolesEntity response = rolesService.createRole(roleDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Recupera un rol específico por su identificador único.
     *
     * Permite obtener los detalles completos de un rol mediante su ID.
     *
     * @param id Identificador único del rol
     * @return Respuesta con la información del rol
     *
     * Códigos de respuesta HTTP:
     * - 200: Rol encontrado exitosamente
     * - 403: Acceso denegado (requiere rol ADMIN)
     * - 404: Rol no encontrado
     * - 500: Error interno del servidor
     */
    @Operation(summary = "Obtener un rol por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol encontrado exitosamente",
                    content = @Content(schema = @Schema(implementation = RolesEntity.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere ROL_ADMIN"),
            @ApiResponse(responseCode = "404", description = "El rol con el ID proporcionado no existe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<RolesEntity> getById(@PathVariable Long id) {
        RolesEntity response = rolesService.getById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Elimina un rol del sistema por su identificador.
     *
     * Este método permite la eliminación de un rol, con validaciones
     * adicionales de seguridad para garantizar que solo usuarios
     * autorizados puedan realizar esta acción.
     *
     * @param id Identificador del rol a eliminar
     * @return Respuesta de confirmación de eliminación
     *
     * Códigos de respuesta HTTP:
     * - 200: Rol eliminado exitosamente
     * - 403: Acceso denegado (requiere rol ADMIN)
     * - 404: Rol no encontrado
     * - 500: Error interno del servidor
     */
    @Operation(summary = "Eliminar un rol por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol eliminado exitosamente",
                    content=@Content(schema = @Schema(implementation = OkResponse.class))),
            @ApiResponse(responseCode = "404", description = "El rol con el ID proporcionado no existe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere ROL_ADMIN"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("@userSecurity.isUserAllowedToDeleteRole(#id)")
    public ResponseEntity<OkResponse> delete(@PathVariable Long id) {
        OkResponse response = rolesService.deleteRole(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Actualiza la información de un rol existente.
     *
     * Permite modificar los detalles de un rol previamente registrado,
     * con validaciones de seguridad para garantizar que solo usuarios
     * autorizados puedan realizar cambios.
     *
     * @param id Identificador del rol a actualizar
     * @param updatedRole Nuevos datos del rol
     * @return Respuesta con el rol actualizado
     *
     * Códigos de respuesta HTTP:
     * - 200: Rol actualizado exitosamente
     * - 400: Datos de rol inválidos
     * - 403: Acceso denegado (requiere rol ADMIN)
     * - 404: Rol no encontrado
     * - 500: Error interno del servidor
     */
    @Operation(summary = "Actualizar un rol")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = RolesEntity.class))),
            @ApiResponse(responseCode = "400", description = "Datos de rol inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN"),
            @ApiResponse(responseCode = "404", description = "El rol con el ID proporcionado no existe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("@userSecurity.isUserAllowedToEditRole(#id)")
    public ResponseEntity<RolesEntity> update(@PathVariable Long id, @RequestBody RolesRequest updatedRole) {
        RolesEntity response = rolesService.updateRole(id, updatedRole);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}