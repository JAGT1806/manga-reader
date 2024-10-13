package com.proyecto.mangareader.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUser;

    @Column(nullable = false, length = 255)
    private String username;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name="date_create", nullable = false, updatable = false)
    private LocalDateTime dateCreate;

    @Column(name="date_update")
    private LocalDateTime dateModified;

    @ManyToOne
    @JoinColumn(name = "rol_id", nullable = false)
    private RolesEntity rol;
}
