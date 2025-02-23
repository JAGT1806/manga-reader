package com.jagt1806.mangareader.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private boolean enabled;
    private Set<String> rol;
    private LocalDate dataCreated;
    private LocalDate dateModified;
    private String imgProfile;
}
