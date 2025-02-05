package com.jagt1806.mangareader.service;

import com.jagt1806.mangareader.http.request.auth.*;
import com.jagt1806.mangareader.http.response.auth.LoginResponse;
import jakarta.transaction.Transactional;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    void registerUser(RegisterRequest request);

    @Transactional
    void activateAccount(ActivateRequest code);

    void forgotPassword(CodeRequest request);

    void resetPassword(ResetPasswordRequest request);

    void resendValidatedEmail(CodeRequest request);
}
