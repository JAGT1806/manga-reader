package com.proyecto.mangareader.app.dto.out;

import lombok.Data;

@Data
public class OutUsersDTO {
    private Long idUser;
    private String username;
    private String email;
    private String rol;
}
