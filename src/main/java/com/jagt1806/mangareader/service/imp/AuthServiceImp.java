package com.jagt1806.mangareader.service.imp;

import com.jagt1806.mangareader.exceptions.*;
import com.jagt1806.mangareader.http.request.auth.*;
import com.jagt1806.mangareader.model.Codes;
import com.jagt1806.mangareader.model.Img;
import com.jagt1806.mangareader.model.Roles;
import com.jagt1806.mangareader.model.Users;
import com.jagt1806.mangareader.repository.RolesRepository;
import com.jagt1806.mangareader.repository.UsersRepository;
import com.jagt1806.mangareader.http.response.auth.LoginResponse;
import com.jagt1806.mangareader.http.response.img.ImgListResponse;
import com.jagt1806.mangareader.security.model.CustomUserDetails;
import com.jagt1806.mangareader.security.service.CustomUsersDetailsService;
import com.jagt1806.mangareader.security.util.JwtUtil;
import com.jagt1806.mangareader.service.AuthService;
import com.jagt1806.mangareader.service.CodeService;
import com.jagt1806.mangareader.service.ImgService;
import com.jagt1806.mangareader.util.MessageUtil;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {
  private final RolesRepository rolesRepository;
  private final UsersRepository usersRepository;
  private final CodeService codeService;
  private final ImgService imgService;
  private final JwtUtil jwtUtil;
  private final MessageUtil messageUtil;
  private final CustomUsersDetailsService usersDetailsService;
  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;

  @Value("${app.roles}")
  private String[] roles;
  private static final int USER_ROLE_INDEX = 1;

  @Override
  public LoginResponse login(LoginRequest request) {
    Users user = usersRepository.findByEmail(request.getEmail().toLowerCase())
        .orElseThrow(() -> new UserNotFoundException(messageUtil.getMessage("user.not.found")));

    if (!user.isEnabled())
      throw new IllegalStateException(messageUtil.getMessage("user.not.enabled"));

    authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail().toLowerCase(), request.getPassword()));
    CustomUserDetails userDetails = (CustomUserDetails) usersDetailsService
        .loadUserByUsername(request.getEmail().toLowerCase());
    String jwt = jwtUtil.generateToken(userDetails);

    return new LoginResponse("Bearer", jwt, userDetails.getId());
  }

  @Override
  public void registerUser(RegisterRequest request) throws MessagingException {
    if (request == null || request.getUsername().isEmpty() ||
        request.getEmail().isEmpty() || request.getPassword().isEmpty())
      throw new IllegalArgumentException(messageUtil.getMessage("user.null"));

    if (usersRepository.existsByEmail((request.getEmail())))
      throw new IllegalArgumentException(messageUtil.getMessage("user.unique"));

    Users user = new Users();
    user.setUsername(request.getUsername());
    user.setEmail(request.getEmail().toLowerCase());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setDateCreate(LocalDateTime.now());
    user.setEnabled(false);

    Roles role = rolesRepository.findByRol(roles[USER_ROLE_INDEX])
        .orElseThrow(() -> new RoleNotFoundException(null));
    Set<Roles> roles = new HashSet<>();
    roles.add(role);
    user.setRoles(roles);

    ImgListResponse imgResponse = imgService.getAllImg(0, 1);
    Img img = imgResponse.getData().getFirst();
    user.setProfileImage(img);

    try {
      usersRepository.save(user);
    } catch (DataIntegrityViolationException e) {
      if (e.getMessage().contains("users_email_key")) {
        throw new UniqueException(messageUtil.getMessage("user.unique"));
      }
      throw e;
    }

    codeService.generateCode(user, CodeServiceImp.CodeType.REGISTRATION);
  }

  @Transactional
  @Override
  public void activateAccount(ActivateRequest request) {
    Codes codes = codeService.getCode(request.getCode());
    codeService.validateCode(codes, CodeServiceImp.CodeType.REGISTRATION);

    Users user = codes.getUser();
    user.setEnabled(true);
    usersRepository.save(user);

    codeService.useCode(codes);
  }

  @Override
  public void forgotPassword(CodeRequest request) throws MessagingException {
    Users user = usersRepository.findByEmail(request.getEmail().toLowerCase())
        .orElseThrow(() -> new UserNotFoundException(null));

    if (!user.isEnabled()) {
      throw new UserNotEnabledException(null);
    }
    codeService.generateCode(user, CodeServiceImp.CodeType.PASSWORD_RESET);
  }

  @Override
  public void resetPassword(ResetPasswordRequest request) {
    String code = request.getCode();
    Users user = codeService.getUserByCode(code, CodeServiceImp.CodeType.PASSWORD_RESET);
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setDateUpdate(LocalDateTime.now());
    usersRepository.save(user);
  }

  @Override
  public void resendValidatedEmail(CodeRequest request) throws MessagingException {
    Users user = usersRepository.findByEmail(request.getEmail().toLowerCase())
        .orElseThrow(() -> new UserNotFoundException(null));

    if (user.isEnabled()) {
      throw new UserAlreadyEnabledException(null);
    }

    codeService.generateCode(user, CodeServiceImp.CodeType.REGISTRATION);
  }
}
