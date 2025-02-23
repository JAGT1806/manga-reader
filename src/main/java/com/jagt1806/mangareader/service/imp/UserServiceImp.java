package com.jagt1806.mangareader.service.imp;

import com.jagt1806.mangareader.dto.user.UserDTO;
import com.jagt1806.mangareader.exceptions.UniqueException;
import com.jagt1806.mangareader.exceptions.UserNotFoundException;
import com.jagt1806.mangareader.http.request.user.ChangePasswordRequest;
import com.jagt1806.mangareader.http.request.user.UserUpdateRequest;
import com.jagt1806.mangareader.http.response.user.UserListResponse;
import com.jagt1806.mangareader.http.response.user.UserResponse;
import com.jagt1806.mangareader.model.Roles;
import com.jagt1806.mangareader.model.Users;
import com.jagt1806.mangareader.repository.FavoritesRepository;
import com.jagt1806.mangareader.repository.UsersRepository;
import com.jagt1806.mangareader.service.ImgService;
import com.jagt1806.mangareader.service.UserService;
import com.jagt1806.mangareader.util.MessageUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
    private final ImgService imgService;
    private final UsersRepository usersRepository;
    private final FavoritesRepository favoritesRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageUtil messageUtil;

    @Override
    public UserListResponse getUsers(String username, String email, String role, int offset, int limit, Boolean enabled) {
        Pageable pageable = PageRequest.of(offset, limit);
        List<Users> users = usersRepository.findByFilters(username, email, role, enabled, pageable);
        List<UserDTO> userDTOS = users.stream().map(this::mapToUserDTO).toList();
        if(userDTOS.isEmpty()) throw new UserNotFoundException(null);
        return new UserListResponse(userDTOS, offset, limit, usersRepository.countByFilters(username, email, role, enabled));
    }

    @Override
    public UserResponse getUserById(Long id) {
        Users user = usersRepository.findById(id).orElseThrow(() -> new UserNotFoundException(null));
        return new UserResponse(mapToUserDTO(user));
    }

    @Override
    public void updateUser(Long id, UserUpdateRequest request) {
        Users user = usersRepository.findById(id).orElseThrow(() -> new UserNotFoundException(null));
        Optional.ofNullable(request.getUsername()).ifPresent(user::setUsername);
        Optional.ofNullable(request.getProfileImage()).ifPresent(imgId -> user.setProfileImage(imgService.getById(imgId)));
        user.setDateUpdate(LocalDateTime.now());
        try {
            usersRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            if(e.getMessage().contains("users_email_key")) throw new UniqueException(messageUtil.getMessage("user.unique"));
            throw e;
        }
    }

    @Override
    public void changePassword(Long id, ChangePasswordRequest request) {
        Users user = usersRepository.findById(id).orElseThrow(() -> new UserNotFoundException(null));
        if(!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword()))
            throw new IllegalArgumentException(messageUtil.getMessage("user.password.incorrect"));
        if(passwordEncoder.matches(request.getNewPassword(), request.getCurrentPassword()))
            throw new IllegalArgumentException(messageUtil.getMessage("user.password.equals"));
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setDateCreate(LocalDateTime.now());
        usersRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
      if(!usersRepository.existsById(id)) throw new UserNotFoundException(null);
      favoritesRepository.deleteAllByUserId_Id(id);
      usersRepository.deleteById(id);
    }

    private UserDTO mapToUserDTO(Users user){
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isEnabled(),
                user.getRoles().stream().map(Roles::getRol).collect(Collectors.toSet()),
                Optional.ofNullable(user.getDateCreate()).map(LocalDate::from).orElse(null),
                Optional.ofNullable(user.getDateUpdate()).map(LocalDate::from).orElse(null),
                user.getProfileImage().getUrl()
        );
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupInactiveUsers() {
        usersRepository.findByEnabledIsFalse().orElse(Collections.emptyList())
                .stream()
                .filter(user -> Duration.between(user.getDateCreate(), LocalDateTime.now()).toDays() > 10)
                .forEach(usersRepository::delete);
    }
}
