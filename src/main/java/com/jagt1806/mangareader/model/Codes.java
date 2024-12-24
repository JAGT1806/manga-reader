package com.jagt1806.mangareader.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "codes")
public class Codes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    private boolean used;
}