package com.proyecto.mangareader.app.service.imp;

import com.proyecto.mangareader.app.entity.ImgEntity;
import com.proyecto.mangareader.app.repository.FavoritesRepository;
import com.proyecto.mangareader.app.request.auth.RegisterRequest;
import com.proyecto.mangareader.app.dto.users.UsersDTO;
import com.proyecto.mangareader.app.entity.RolesEntity;
import com.proyecto.mangareader.app.entity.UsersEntity;
import com.proyecto.mangareader.app.exceptions.*;
import com.proyecto.mangareader.app.repository.UsersRepository;
import com.proyecto.mangareader.app.request.users.ChangePasswordRequest;
import com.proyecto.mangareader.app.request.users.UpdatedUserRequest;
import com.proyecto.mangareader.app.responses.ok.OkResponse;
import com.proyecto.mangareader.app.responses.user.UserListResponse;
import com.proyecto.mangareader.app.responses.user.UserResponse;
import com.proyecto.mangareader.app.service.IImgService;
import com.proyecto.mangareader.app.service.IUsersService;
import com.proyecto.mangareader.app.util.MessageUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UsersService implements IUsersService {
    private final IImgService imgService;
    private final UsersRepository usersRepository;
    private final FavoritesRepository favoritesRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageUtil messageSource;

    @Override
    public UserListResponse getAllUsers(String username, String email, String role, int offset, int limit, Boolean enabled) {
        Pageable pageable = PageRequest.of(offset, limit);

        List<UsersEntity> users = usersRepository.findByFilters(username, email, role, enabled, pageable);

        List<UsersDTO> usersDTO = users.stream().map(this::setUserDTO).toList();
        if (usersDTO.isEmpty() || (role != null && usersDTO.getFirst() == null)) {
            throw new UserNotFoundException(null);
        }
        Long total = usersRepository.countByFilters(username, email, role, enabled);

        return new UserListResponse(usersDTO, offset, limit,total);
    }

    @Override
    public UserResponse getUserById(Long idUser) {
        UsersEntity user = usersRepository.findById(idUser).orElse(null);

        if(user == null) {
            throw new UserNotFoundException(null);
        }

        UsersDTO usersDTO = setUserDTO(user);
        return new UserResponse(usersDTO);

    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdatedUserRequest request) {
        if (id == null || request == null) {
            throw new IllegalArgumentException(messageSource.getMessage("error.null"));
        }
        UsersEntity user = usersRepository.findById(id).orElseThrow( () -> new UserNotFoundException(null) );

        if(request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if(request.getProfileImage() != null) {
            ImgEntity img = imgService.getImg(request.getProfileImage());
            user.setProfileImage(img);
        }

        if((request.getProfileImage() != null && request.getUsername() != null) &&
                (!user.getUsername().equals(request.getUsername()) && !user.getProfileImage().getId().equals(request.getProfileImage()))) {
            user.setDateModified(LocalDateTime.now());

        }

        try {
            usersRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            if(e.getMessage().contains("uk6dotkott2kjsp8vw4d0m25fb7")) {
                throw new UniqueException(messageSource.getMessage("user.unique"));
            }
            throw e;
        }

        UsersDTO userDTO = setUserDTO(user);
        return new UserResponse(userDTO);
    }

    @Override
    @Transactional
    public void changePassword(Long id, ChangePasswordRequest request) {
        UsersEntity user = usersRepository.findById(id).orElseThrow(() -> new UserNotFoundException(null));

        if(!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException(messageSource.getMessage("user.password.incorrect"));
        }

        validateNewPassword(request.getNewPassword());

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException(messageSource.getMessage("user.password.equals"));
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setDateModified(LocalDateTime.now());

        usersRepository.save(user);

    }

    private void validateNewPassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException(messageSource.getMessage("user.password.invalid"));
        }
    }


    @Override
    public OkResponse deleteUser(Long id) {
        if(!usersRepository.existsById(id)) {
            throw new UserNotFoundException(null);
        }
            // Eliminar todos los registros relacionados
            favoritesRepository.deleteAllByUserId_IdUser(id);

            // Finalmente eliminar el usuario
            usersRepository.deleteById(id);

        return new OkResponse();
    }


    // Métodos privados
    private UsersDTO setUserDTO(UsersEntity user) {
        UsersDTO usersDTO = new UsersDTO();
        usersDTO.setIdUser(user.getIdUser());
        usersDTO.setUsername(user.getUsername());
        usersDTO.setEmail(user.getEmail());
        usersDTO.setEnabled(user.isEnabled());

        Set<RolesEntity> userRoles = user.getRoles();
        Set<String> roles = new HashSet<>();

        for (RolesEntity role : userRoles) {
            roles.add(role.getRol());
        }

        usersDTO.setRol(roles);
        if (user.getDateCreate() != null) {
            usersDTO.setDateCreated(LocalDate.from(user.getDateCreate()));
        }

        if (user.getDateModified() != null) {
            usersDTO.setDateModified(LocalDate.from(user.getDateModified()));
        }

        usersDTO.setImageProfile(user.getProfileImage().getUrl());

        return usersDTO;
    }

    private void setDTOtoUser(UsersEntity user, RegisterRequest request, boolean isNew) {
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if (isNew) {
            user.setDateCreate(LocalDateTime.now());
        } else {
            user.setDateModified(LocalDateTime.now());
        }
    }

    // Tarea programada para eliminar usuario no validado
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupInactiveUsers() {
        List<UsersEntity> inactiveUsers = usersRepository.findByEnabledFalse().orElse(new ArrayList<>());

        for(UsersEntity user : inactiveUsers) {
            long daySinceCreation = Duration.between(user.getDateCreate(), LocalDateTime.now()).toDays();
            if (daySinceCreation > 10) {
                usersRepository.delete(user);
            }
        }
    }
}
