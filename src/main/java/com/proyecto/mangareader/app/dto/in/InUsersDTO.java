package com.proyecto.mangareader.app.dto.in;

import lombok.Data;

@Data
public class InUsersDTO {
    private String username;
    private String email;
    private String password;
    private Long rolId;
}
