package com.jagt1806.mangareader.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jagt1806.mangareader.audit.auxiliary.Auditable;
import com.jagt1806.mangareader.audit.listener.AuditListener;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "favorites", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "manga_id"})
})
@EntityListeners(AuditListener.class)
public class Favorites implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String mangaId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users userId;

    private String nameManga;

    private String urlImage;

    @JsonIgnore
    @Override
    public String getTableName() {
        return "users";
    }
}