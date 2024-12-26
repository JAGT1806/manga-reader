package com.jagt1806.mangareader.init;

import com.jagt1806.mangareader.exceptions.RoleNotFoundException;
import com.jagt1806.mangareader.model.Img;
import com.jagt1806.mangareader.model.Roles;
import com.jagt1806.mangareader.model.Users;
import com.jagt1806.mangareader.repository.ImgRepository;
import com.jagt1806.mangareader.repository.RolesRepository;
import com.jagt1806.mangareader.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ApplicationInitializer {
    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.role}")
    private String adminRole;

    @Value("${app.roles}")
    private String[] roles;

    @Value("${app.img}")
    private String img;

    private final RolesRepository rolesRepository;
    private final UsersRepository usersRepository;
    private final ImgRepository imgRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void initializeData() {
        initializeRoles();
        initializeImg();
        initializedAdminUser();
    }

    private void initializeRoles() {
        for(String role: roles) {
            rolesRepository.findByRol(role)
                    .orElseGet(() -> rolesRepository.save(new Roles(null, role)));
        }
    }

    private void initializeImg() {
        if(imgRepository.findByUrl(img).isEmpty()) {
            Img img1 = new Img(null, img);
            imgRepository.save(img1);
        }
    }

    private void initializedAdminUser() {
        if(usersRepository.findByEmail(adminEmail).isEmpty()) {
            Img profileImage = imgRepository.findFirstByOrderById().orElse(null);

            Users adminUser = new Users();
            adminUser.setUsername(adminUsername);
            adminUser.setEmail(adminEmail);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setDateCreate(LocalDateTime.now());
            adminUser.setProfileImage(profileImage);
            adminUser.setEnabled(true);

            Roles adminRoleEntity = rolesRepository.findByRol(adminRole)
                    .orElseThrow(() -> new RoleNotFoundException(null));
            adminUser.getRoles().add(adminRoleEntity);

            usersRepository.save(adminUser);
        }
    }
}
