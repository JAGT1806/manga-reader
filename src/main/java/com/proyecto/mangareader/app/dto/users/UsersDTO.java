package com.proyecto.mangareader.app.dto.users;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UsersDTO {
    private Long idUser;
    private String username;
    private String email;
    private Set<String> rol;
    private boolean enabled;
    private LocalDate dateCreated;
    private LocalDate dateModified;
    private String imageProfile;
}
