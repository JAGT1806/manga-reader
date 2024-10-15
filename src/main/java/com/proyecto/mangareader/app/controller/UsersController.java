package com.proyecto.mangareader.app.controller;

import com.proyecto.mangareader.app.dto.in.InUsersDTO;
import com.proyecto.mangareader.app.dto.out.OutUsersDTO;
import com.proyecto.mangareader.app.entity.RolesEntity;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
@Tag(name="users", description = "API para gestionar los usuarios")
public class UsersController {
    private final IUsersService usersService;

    @GetMapping
    public ResponseEntity<List<OutUsersDTO>> getAllUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @Parameter(in = ParameterIn.QUERY, description = "Filtrar por rol",
                        required = false,
                        allowEmptyValue = true,
                        schema = @Schema(allowableValues = { "Admin", "user"}))
            @RequestParam(required = false) String role) {
        return ResponseEntity.ok(usersService.getAllUsers(username, email, role));
    }

    @Operation(summary = "Guardar un nuevo usuario", description = "Crea un nuevo usuario en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "usuario creado exitosamente",
                    content = @Content(schema = @Schema(implementation = RolesEntity.class))),
            @ApiResponse(responseCode = "400", description = "Datos de usuarios inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity createUser(InUsersDTO inUsersDTO) {
        try {
            OutUsersDTO outUsersDTO = usersService.saveUser(inUsersDTO);

            return new ResponseEntity<>(outUsersDTO, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Operation(summary = "Obtener un usuario por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol encontrado exitosamente",
                    content = @Content(schema = @Schema(implementation = RolesEntity.class))),
            @ApiResponse(responseCode = "404", description = "El rol con el ID proporcionado no existe"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OutUsersDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(usersService.getUserById(id));
    }

    @Operation(summary = "Modificar un usuario por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario modificado exitosamente",
                    content = @Content(schema = @Schema(implementation = RolesEntity.class))),
            @ApiResponse(responseCode = "404", description = "El usuario con el ID proporcionado no existe"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<OutUsersDTO> updateUser(@PathVariable Long id, InUsersDTO inUsersDTO) {
        try{
            OutUsersDTO savedUser = usersService.saveUser(inUsersDTO);
            return new ResponseEntity<>(savedUser, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Modificar un usuario por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario modificado exitosamente",
                    content = @Content(schema = @Schema(implementation = RolesEntity.class))),
            @ApiResponse(responseCode = "404", description = "El usuario con el ID proporcionado no existe"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        String result = usersService.deleteUser(id);
        if (result != null) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
    }



}
