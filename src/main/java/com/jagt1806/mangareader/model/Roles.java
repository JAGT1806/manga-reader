package com.jagt1806.mangareader.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jagt1806.mangareader.audit.auxiliary.Auditable;
import com.jagt1806.mangareader.audit.listener.AuditListener;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
@EntityListeners(AuditListener.class)
public class Roles implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String rol;

    @JsonIgnore
    @Override
    public String getTableName() {
        return "roles";
    }
}
