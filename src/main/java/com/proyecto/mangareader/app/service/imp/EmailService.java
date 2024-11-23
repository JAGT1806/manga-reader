package com.proyecto.mangareader.app.service.imp;

import com.proyecto.mangareader.app.service.IEmailService;
import com.proyecto.mangareader.app.util.MessageUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService {
    private final JavaMailSender mailSender;
    private final MessageUtil messageSource;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendVerificationCode(String to, String code) throws MessagingException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(messageSource.getMessage("email.verified.title"));
        message.setText(messageSource.getMessage("email.verified.text", new Object[]{code}));

        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(messageSource.getMessage("email.forget.password.title"));
        message.setText(messageSource.getMessage("email.forget.password.body", new Object[]{code}));

        mailSender.send(message);
    }

}
