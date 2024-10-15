package com.proyecto.mangareader.app.controller;

import com.proyecto.mangareader.app.dto.in.InRolesDTO;
import com.proyecto.mangareader.app.entity.RolesEntity;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
@AllArgsConstructor
@Tag(name="roles", description = "API para gestionar los roles")
public class RolesController {
    private final IRolesService rolesService;

    @Operation(summary = "Obtener roles", description = "Obtiene todos los roles o un rol específico por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles encontrados exitosamente",
                content=@Content(schema = @Schema(implementation = RolesEntity.class))),
            @ApiResponse(responseCode = "404", description = "No se encontraron roles o el rol específico no existe"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity getAll(@RequestParam(required = false) String role) {
        List<RolesEntity> roles = rolesService.getAllRoles(role);
        if(roles.isEmpty() || (role != null && roles.get(0) == null)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @Operation(summary = "Guardar un nuevo rol", description = "Crea un nuevo rol en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rol creado exitosamente",
                    content = @Content(schema = @Schema(implementation = RolesEntity.class))),
            @ApiResponse(responseCode = "400", description = "Datos de rol inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity save(InRolesDTO roleDTO) {
        try {
            RolesEntity role = new RolesEntity();
            role.setRol(roleDTO.getRole());
            RolesEntity savedRole = rolesService.saveRole(role);
            return new ResponseEntity<>(savedRole, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al crar el rol", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Obtener un rol por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol encontrado exitosamente",
                content = @Content(schema = @Schema(implementation = RolesEntity.class))),
            @ApiResponse(responseCode = "404", description = "El rol con el ID proporcionado no existe"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity getById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(rolesService.getById(id), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Eliminar un rol por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "El rol con el ID proporcionado no existe"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        String result = rolesService.deleteRole(id);
        if(id != null) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>("Id no disponible", HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Actualizar un rol")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = RolesEntity.class))),
            @ApiResponse(responseCode = "400", description = "Datos de rol inválidos"),
            @ApiResponse(responseCode = "404", description = "El rol con el ID proporcionado no existe"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable Long id, @RequestBody InRolesDTO updatedRole) {
        try {
            RolesEntity role = rolesService.updateRole(id, updatedRole);
            return new ResponseEntity<>(role, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al actualizar el rol.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
