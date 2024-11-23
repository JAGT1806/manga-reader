package com.proyecto.mangareader.app.service;

import com.proyecto.mangareader.app.request.auth.RegisterRequest;
import com.proyecto.mangareader.app.request.auth.ResendValidatedRequest;
import com.proyecto.mangareader.app.request.auth.VerificationRequest;
import com.proyecto.mangareader.app.request.users.ForgotPasswordRequest;
import com.proyecto.mangareader.app.request.users.ResetPasswordRequest;
import com.proyecto.mangareader.app.responses.user.UserResponse;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;

public interface IAuthService {
    ResponseEntity<?> login(String email, String password);

    UserResponse registerUser(RegisterRequest request) throws MessagingException;

    void verifyEmail(VerificationRequest request);

    void forgotPassword(ForgotPasswordRequest request) throws MessagingException;

    void resetPassword(ResetPasswordRequest request);

    void resendValidatedEmail(ResendValidatedRequest request) throws MessagingException;

    ResponseEntity<?> validateToken(String authHeader);

}
