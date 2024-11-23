package com.proyecto.mangareader.app.security.service;

import com.proyecto.mangareader.app.entity.RolesEntity;
import com.proyecto.mangareader.app.entity.UsersEntity;
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
 * Servicio personalizado para la carga de detalles de usuario en el contexto de autenticación.
 * Implementa la interfaz {@link UserDetailsService} de Spring Security para obtener detalles de
 * usuario basados en su correo electrónico.
 * @author Jhon Alexander Gómez Trujillo
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    /**
     * Repositorio de usuarios para realizar consultas a la base de datos.
     */
    @Autowired
    private UsersRepository usersRepository;

    /**
     * Carga los detalles del usuario en función de su correo electrónico.
     *
     * @param email el correo electrónico del usuario
     * @return los detalles del usuario en un objeto {@link UserDetails}
     * @throws UsernameNotFoundException si el usuario no es encontrado en la base de datos
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UsersEntity user = usersRepository.findByEmail(email).orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
        List<GrantedAuthority> authorities = getUserAuthority(user.getRoles());
        return buildUserForAuthentication(user, authorities);
    }

    /**
     * Obtiene los roles y permisos del usuario, transformándolos en una lista de autoridades
     * que puedan ser reconocidas por Spring Security.
     *
     * @param userRoles conjunto de roles asociados al usuario
     * @return una lista de autoridades ({@link GrantedAuthority}) para el usuario
     */
    private List<GrantedAuthority> getUserAuthority(Set<RolesEntity> userRoles) {
        Set<GrantedAuthority> roles = new HashSet<GrantedAuthority>();
        for (RolesEntity role : userRoles) {
            roles.add(new SimpleGrantedAuthority(role.getRol()));
        }
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>(roles);
        return grantedAuthorities;
    }

//    private UserDetails buildUserForAuthentication(UsersEntity user, List<GrantedAuthority> authorities) {
//        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
//                true, true, true, true, authorities);
//    }

    /**
     * Construye los detalles del usuario para la autenticación en el contexto de seguridad de Spring.
     * Crea un objeto {@link CustomUserDetails} con la información relevante del usuario.
     *
     * @param user el usuario de la entidad {@link UsersEntity}
     * @param authorities lista de roles y permisos del usuario
     * @return un objeto {@link UserDetails} con los detalles del usuario para autenticación
     */
    private UserDetails buildUserForAuthentication(UsersEntity user, List<GrantedAuthority> authorities) {
        return new CustomUserDetails(user.getIdUser(), user.getEmail(), user.getPassword(), authorities, user.isEnabled());
    }
}
