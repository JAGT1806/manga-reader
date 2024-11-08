package com.proyecto.mangareader.app.responses.auth;

import com.proyecto.mangareader.app.dto.out.OutUsersDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LoginResponse {
    private OutUsersDTO user;
    private String token;
    private String tokenType;
    private List<String> roles;
}
