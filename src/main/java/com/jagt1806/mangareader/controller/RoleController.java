package com.jagt1806.mangareader.controller;

import com.jagt1806.mangareader.model.Roles;
import com.jagt1806.mangareader.http.request.role.RoleRequest;
import com.jagt1806.mangareader.http.response.error.ErrorResponse;
import com.jagt1806.mangareader.http.response.ok.OkResponse;
import com.jagt1806.mangareader.http.response.role.RoleListResponse;
import com.jagt1806.mangareader.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/role")
@SecurityRequirement(name = "Auth")
@PreAuthorize("hasRole('${app.admin.role}')")
@Tag(name = "Roles", description = "Gestión de roles")
public class RoleController {
    private final RoleService roleService;

    @Operation(summary = "Obtener roles", description = "Obtiene todos los roles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles encontrados exitosamente",
                    content=@Content(mediaType = "application/json", schema = @Schema(implementation = RoleListResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN"),
            @ApiResponse(responseCode = "404", description = "No se encontraron roles o el rol específico no existe",
                    content=@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<RoleListResponse> getAll(
            @RequestParam(required = false) String role,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "12") int limit) {
        RoleListResponse response = roleService.getAllRoles(role, offset, limit);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Obtener un rol por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol encontrado exitosamente",
                    content = @Content(schema = @Schema(implementation = Roles.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere ROL_ADMIN"),
            @ApiResponse(responseCode = "404", description = "El rol con el ID proporcionado no existe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Roles> getById(@PathVariable Long id) {
        Roles response = roleService.getById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Guardar un nuevo rol", description = "Crea un nuevo rol en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rol creado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Roles.class))),
            @ApiResponse(responseCode = "400", description = "Datos de rol inválidos",
                    content=@Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere ROLE_ADMIN"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content=@Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/create")
    public ResponseEntity<OkResponse> createRole(@RequestBody RoleRequest request) {
        roleService.createRole(request);
        return new ResponseEntity<>(new OkResponse(), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar un rol")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = Roles.class))),
            @ApiResponse(responseCode = "400", description = "Datos de rol inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN"),
            @ApiResponse(responseCode = "404", description = "El rol con el ID proporcionado no existe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("@userSecurity.isUserAllowedToEditRole(#id)")
    @PutMapping("/{id}/update")
    public ResponseEntity<OkResponse> update(@PathVariable Long id, @RequestBody RoleRequest request) {
        roleService.updateRole(id, request);
        return new ResponseEntity<>(new OkResponse(), HttpStatus.OK);
    }

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
    @PreAuthorize("@userSecurity.isUserAllowedToEditRole(#id)")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<OkResponse> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        return new ResponseEntity<>(new OkResponse(), HttpStatus.OK);
    }

}
