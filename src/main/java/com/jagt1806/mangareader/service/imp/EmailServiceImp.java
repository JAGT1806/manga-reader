package com.jagt1806.mangareader.service.imp;

import com.jagt1806.mangareader.service.EmailService;
import com.jagt1806.mangareader.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EmailServiceImp implements EmailService {
    private final JavaMailSender mailSender;
    private final MessageUtil messageUtil;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendVerificationCode(String to, String code, LocalDateTime expiryDate) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(messageUtil.getMessage("email.verified.title"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyyy HH:mm");
        String date = expiryDate.format(formatter);
        message.setText(messageUtil.getMessage("email.verified.text", new Object[]{code, date}));

        mailSender.send(message);
    }

    @Override
    public void sendPasswordResetEmail(String to, String code, LocalDateTime expiryDate) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(messageUtil.getMessage("email.forget.password.title"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyyy HH:mm");
        String date = expiryDate.format(formatter);
        message.setText(messageUtil.getMessage("email.forget.password.body", new Object[]{code, date}));
        mailSender.send(message);
    }
}
