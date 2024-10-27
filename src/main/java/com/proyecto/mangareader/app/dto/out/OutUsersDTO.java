package com.proyecto.mangareader.app.dto.out;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OutUsersDTO {
    private Long idUser;
    private String username;
    private String email;
    private String rol;
    private LocalDate dateCreated;
    private LocalDate dateModified;
    private String image_profile;
}
