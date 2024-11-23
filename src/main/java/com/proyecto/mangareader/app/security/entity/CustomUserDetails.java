package com.proyecto.mangareader.app.security.entity;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Clase de detalles de usuario personalizada que extiende la clase {@link User} de Spring Security.
 * Incluye información adicional sobre el usuario, como el ID y el estado de habilitación.
 * @author Jhon Alexander Gómez Trujillo
 */
@Getter
public class CustomUserDetails extends User {
    /**
     * Identificador único del usuario.
     */
    private final Long id;
    /**
     * Estado que indica si el usuario está habilitado.
     */
    private final boolean enabled;

    /**
     * Constructor de la clase {@code CustomUserDetails}.
     *
     * @param id el identificador único del usuario
     * @param username el nombre de usuario para autenticación
     * @param password la contraseña del usuario
     * @param authorities las autoridades (roles) concedidas al usuario
     * @param enabled el estado de habilitación del usuario
     */
    public CustomUserDetails(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities, boolean enabled) {
        super(username, password, authorities);
        this.id = id;
        this.enabled = enabled;
    }
}
