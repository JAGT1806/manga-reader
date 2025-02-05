package com.jagt1806.mangareader.service;

import java.time.LocalDateTime;

public interface EmailService {
    void sendVerificationCode(String to, String code, LocalDateTime expiryDate);
    void sendPasswordResetEmail(String to, String code, LocalDateTime expiryDate);
}
