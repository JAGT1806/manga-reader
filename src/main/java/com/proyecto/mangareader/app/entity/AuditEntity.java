package com.proyecto.mangareader.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "audits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // CREATE, UPDATE, DELETE
    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String tableName;

    @Column(columnDefinition = "jsonb")
    private String oldData;

    @Column(columnDefinition = "jsonb")
    private String newData;

    // Fecha y hora de la acción
    @Column(nullable = false)
    private LocalDateTime timestamp;

    // User que hizo la acción
    @Column(nullable = false)
    private Long userId;  // ID del usuario que hizo la acción

    @Column(nullable = false)
    private String username;  // Nombre del usuario (opcional)
}
