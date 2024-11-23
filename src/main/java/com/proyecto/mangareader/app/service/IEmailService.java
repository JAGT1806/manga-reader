package com.proyecto.mangareader.app.service;

import jakarta.mail.MessagingException;

public interface IEmailService {
    void sendVerificationCode(String to, String code) throws MessagingException;
    void sendPasswordResetEmail(String to, String code) throws MessagingException;
}
