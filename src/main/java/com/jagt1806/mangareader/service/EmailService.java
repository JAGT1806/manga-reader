package com.jagt1806.mangareader.service;

import jakarta.mail.MessagingException;

import java.time.LocalDateTime;

public interface EmailService {
    void sendVerificationCode(String to, String code, LocalDateTime expiryDate) throws MessagingException;
    void sendPasswordResetEmail(String to, String code, LocalDateTime expiryDate) throws MessagingException;
}
