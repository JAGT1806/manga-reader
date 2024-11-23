package com.proyecto.mangareader.app.responses.auth;

import com.proyecto.mangareader.app.dto.users.UsersDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LoginResponse {
    private UsersDTO user;
    private String token;
    private String tokenType;
    private List<String> roles;
}
