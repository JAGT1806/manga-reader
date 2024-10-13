package com.proyecto.mangareader.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "favorites")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoritesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFavorites;

    private String idManga;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UsersEntity userId;

    private String nameManga;
    private String urlImage;
}
