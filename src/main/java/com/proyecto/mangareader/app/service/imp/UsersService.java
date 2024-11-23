package com.proyecto.mangareader.app.service.imp;

import com.proyecto.mangareader.app.entity.ImgEntity;
import com.proyecto.mangareader.app.request.auth.RegisterRequest;
import com.proyecto.mangareader.app.dto.users.UsersDTO;
import com.proyecto.mangareader.app.entity.RolesEntity;
import com.proyecto.mangareader.app.entity.UsersEntity;
import com.proyecto.mangareader.app.entity.VerificationCodesEntity;
import com.proyecto.mangareader.app.exceptions.*;
import com.proyecto.mangareader.app.repository.RolesRepository;
import com.proyecto.mangareader.app.repository.UsersRepository;
import com.proyecto.mangareader.app.request.users.ChangePasswordRequest;
import com.proyecto.mangareader.app.request.users.ForgotPasswordRequest;
import com.proyecto.mangareader.app.request.auth.ResendValidatedRequest;
import com.proyecto.mangareader.app.request.users.ResetPasswordRequest;
import com.proyecto.mangareader.app.request.auth.VerificationRequest;
import com.proyecto.mangareader.app.request.users.UpdatedUserRequest;
import com.proyecto.mangareader.app.responses.ok.OkResponse;
import com.proyecto.mangareader.app.responses.user.UserListResponse;
import com.proyecto.mangareader.app.responses.user.UserResponse;
import com.proyecto.mangareader.app.service.IUsersService;
import com.proyecto.mangareader.app.service.IVerificationCodesService;
import com.proyecto.mangareader.app.util.MessageUtil;
import jakarta.mail.MessagingException;
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
public class UsersService implements IUsersService {
    private final ImgService imgService;
    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final IVerificationCodesService codesService;
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
        usersRepository.deleteById(id);
        return new OkResponse();
    }



    @Override
    public UserResponse registerUser(RegisterRequest request) throws MessagingException {
        if (request == null || request.getUsername().isEmpty() ||
                request.getEmail().isEmpty() || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException(messageSource.getMessage("user.null"));
        }


        // Verificar si ya existe un usuario con ese email o username
        if (usersRepository.existsByEmail(request.getEmail())) {
            UsersEntity user = usersRepository.findByEmail(request.getEmail()).orElse(null);
            throw new IllegalArgumentException(messageSource.getMessage("user.unique"));
        }

        UsersEntity user = new UsersEntity();
        setDTOtoUser(user, request, true);
        user.setEnabled(false);

        RolesEntity role = rolesRepository.findByRol("ROLE_USER")
                .orElseThrow(() -> new RoleNotFoundException(null));

        Set<RolesEntity> roles = new HashSet<>();
        roles.add(role);

        user.setRoles(roles);

        user.setProfileImage(imgService.getImg(1L));


        try {
            usersRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            if(e.getMessage().contains("uk6dotkott2kjsp8vw4d0m25fb7")) {
                throw new UniqueException(messageSource.getMessage("user.unique"));
            }
            throw e;
        }

        // Generar y enviar nuevo código
        codesService.generateCode(user, VerificationCodesService.CodeType.REGISTRATION);

        UsersDTO usersDTO = setUserDTO(user);
        return new UserResponse(usersDTO);
    }

    @Override
    @Transactional
    public void verifyEmail(VerificationRequest request) {
        String code = request.getCode();
        VerificationCodesEntity verificationCode = codesService.getCode(code);
        codesService.validateCode(verificationCode, VerificationCodesService.CodeType.REGISTRATION);

        UsersEntity user = verificationCode.getUser();
        user.setEnabled(true);
        usersRepository.save(user);

        // El código se inhabilita automáticamente después de la validación
        codesService.useCode(verificationCode);
    }


    @Override
    public void forgotPassword(ForgotPasswordRequest request) throws MessagingException {
        UsersEntity user = usersRepository.findByEmail(request.getEmail().toLowerCase()).orElseThrow(() -> new UserNotFoundException(null));

        if(!user.isEnabled()) {
            throw new UserNotEnabledException(null);
        }

        codesService.generateCode(user, VerificationCodesService.CodeType.PASSWORD_RESET);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        String code = request.getCode();
        UsersEntity user = codesService.getUserByCode(code, VerificationCodesService.CodeType.PASSWORD_RESET);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        usersRepository.save(user);
    }

    @Override
    public void resendValidatedEmail(ResendValidatedRequest request) throws MessagingException {
        UsersEntity user = usersRepository.findByEmail(request.getEmail().toLowerCase()).orElseThrow(() -> new UserNotFoundException(null));

        if(user.isEnabled()) {
            throw new UserAlreadyEnabledException(null);
        }

        codesService.generateCode(user, VerificationCodesService.CodeType.REGISTRATION);
    }



    // Métodos privados
    private UsersDTO setUserDTO(UsersEntity user) {
        UsersDTO usersDTO = new UsersDTO();
        usersDTO.setIdUser(user.getIdUser());
        usersDTO.setUsername(user.getUsername());
        usersDTO.setEmail(user.getEmail());

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
