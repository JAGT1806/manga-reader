package com.proyecto.mangareader.app.security.service;

import com.proyecto.mangareader.app.entity.RolesEntity;
import com.proyecto.mangareader.app.entity.UsersEntity;
import com.proyecto.mangareader.app.exceptions.UserNotFoundException;
import com.proyecto.mangareader.app.repository.UsersRepository;
import com.proyecto.mangareader.app.security.entity.CustomUserDetails;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Servicio de autenticación de usuarios personalizado para Spring Security.
 *
 * Responsabilidades:
 * - Cargar detalles de usuario por correo electrónico
 * - Transformar roles a autoridades de seguridad
 * @author Jhon Alexander Gómez Trujillo
 * @since 2024
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    /** Repositorio para consultas de usuarios. */
    @Autowired
    private UsersRepository usersRepository;

    /**
     * Carga detalles de usuario por email.
     *
     * @param email Correo electrónico del usuario
     * @return Detalles de usuario para autenticación
     * @throws UsernameNotFoundException Si usuario no existe
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UserNotFoundException {
        UsersEntity user = usersRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(null));

        List<GrantedAuthority> authorities = getUserAuthority(user.getRoles());
        return buildUserForAuthentication(user, authorities);
    }

    /**
     * Convierte roles de usuario a autoridades de seguridad.
     *
     * @param userRoles Roles del usuario
     * @return Lista de autoridades
     */
    private List<GrantedAuthority> getUserAuthority(Set<RolesEntity> userRoles) {
        Set<GrantedAuthority> roles = new HashSet<>();
        for (RolesEntity role : userRoles) {
            roles.add(new SimpleGrantedAuthority(role.getRol()));
        }
        return new ArrayList<>(roles);
    }

    /**
     * Construye objeto de detalles de usuario para autenticación.
     *
     * @param user Entidad de usuario
     * @param authorities Roles del usuario
     * @return Detalles de usuario personalizados
     */
    private UserDetails buildUserForAuthentication(UsersEntity user, List<GrantedAuthority> authorities) {
        return new CustomUserDetails(user.getIdUser(), user.getEmail(), user.getPassword(), authorities, user.isEnabled());
    }
}
