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
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UsersRepository usersRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UsersEntity user = usersRepository.findByEmail(email).orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
        List<GrantedAuthority> authorities = getUserAuthority(user.getRol());
        return buildUserForAuthentication(user, authorities);
    }

    private List<GrantedAuthority> getUserAuthority(RolesEntity userRole) {
        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(userRole.getRol())); // Asignar el rol al usuario
        return new ArrayList<>(roles);
    }

//    private UserDetails buildUserForAuthentication(UsersEntity user, List<GrantedAuthority> authorities) {
//        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
//                true, true, true, true, authorities);
//    }

    private UserDetails buildUserForAuthentication(UsersEntity user, List<GrantedAuthority> authorities) {
        return new CustomUserDetails(user.getIdUser(), user.getUsername(), user.getPassword(), authorities);
    }
}
