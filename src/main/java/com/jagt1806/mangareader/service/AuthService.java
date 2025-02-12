package com.jagt1806.mangareader.service;

import com.jagt1806.mangareader.http.request.auth.*;
import com.jagt1806.mangareader.http.response.auth.LoginResponse;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    void registerUser(RegisterRequest request) throws MessagingException;

    @Transactional
    void activateAccount(ActivateRequest code);

    void forgotPassword(CodeRequest request) throws MessagingException;

    void resetPassword(ResetPasswordRequest request);

    void resendValidatedEmail(CodeRequest request) throws MessagingException;
}
