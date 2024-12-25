package com.jagt1806.mangareader.security.service;

import com.jagt1806.mangareader.exceptions.UserNotFoundException;
import com.jagt1806.mangareader.model.Roles;
import com.jagt1806.mangareader.model.Users;
import com.jagt1806.mangareader.repository.UsersRepository;
import com.jagt1806.mangareader.security.model.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUsersDetailsService implements UserDetailsService {
    private UsersRepository usersRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UserNotFoundException {
        Users user = usersRepository.findByEmail(email.toUpperCase()).orElseThrow(() -> new UserNotFoundException(null));

        List<GrantedAuthority> authorities = getUserAuthority(user.getRoles());

        return buildUserForAuthentication(user, authorities);
    }

    private List<GrantedAuthority> getUserAuthority(Set<Roles> userRoles) {
        Set<GrantedAuthority> roles = new HashSet<>();
        for(Roles role : userRoles)
            roles.add(new SimpleGrantedAuthority(role.getRol()));

        return new ArrayList<>(roles);
    }

    private UserDetails buildUserForAuthentication(Users user, List<GrantedAuthority> authorities) {
        return new CustomUserDetails(user.getId(), user.getEmail(), user.getPassword(), authorities, user.isEnabled());
    }
}
