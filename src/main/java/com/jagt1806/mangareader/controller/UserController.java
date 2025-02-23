package com.jagt1806.mangareader.controller;

import com.jagt1806.mangareader.http.request.user.ChangePasswordRequest;
import com.jagt1806.mangareader.http.request.user.UserUpdateRequest;
import com.jagt1806.mangareader.http.response.ok.OkResponse;
import com.jagt1806.mangareader.http.response.user.UserListResponse;
import com.jagt1806.mangareader.http.response.user.UserResponse;
import com.jagt1806.mangareader.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "Users", description = "Buscar información de los usuarios")
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserListResponse> getUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @Parameter(in = ParameterIn.QUERY, description = "Filtrar por roll",
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

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/edit")
    public ResponseEntity<OkResponse> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        userService.updateUser(id, request);
        return ResponseEntity.ok(new OkResponse());
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<OkResponse> updatePassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request);
        return ResponseEntity.ok(new OkResponse());
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<OkResponse> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new OkResponse());
    }
}
