package com.jagt1806.mangareader.service.imp;

import com.jagt1806.mangareader.service.EmailService;
import com.jagt1806.mangareader.util.MessageUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
    public void sendVerificationCode(String to, String code, LocalDateTime expiryDate) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String date = expiryDate.format(formatter);

        String subject = messageUtil.getMessage("email.verified.title");
        String content = generateHtmlContent(
                messageUtil.getMessage("email.verified.text", new Object[]{code, date})
        );

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }

    @Override
    public void sendPasswordResetEmail(String to, String code, LocalDateTime expiryDate) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String date = expiryDate.format(formatter);

        String subject = messageUtil.getMessage("email.forget.password.title");
        String content = generateHtmlContent(
                messageUtil.getMessage("email.forget.password.body", new Object[]{code, date})
        );

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }

    private String generateHtmlContent(String body) {
        String footer = messageUtil.getMessage("email.footer");
        String cr = messageUtil.getMessage("email.cr");
        return String.format(
                "<!DOCTYPE html>" +
                        "<html lang='en'>" +
                        "<head>" +
                        "   <meta charset='UTF-8'>" +
                        "   <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                        "   <style>" +
                        "       body { font-family: 'Arial', sans-serif; background-color: #f9f9f9; margin: 0; padding: 0; }" +
                        "       .email-container { max-width: 600px; margin: 40px auto; background: #ffffff; border-radius: 8px; " +
                        "                         box-shadow: 0 0 15px rgba(0, 0, 0, 0.1); overflow: hidden; }" +
                        "       .email-header { background-color: #4A90E2; color: white; text-align: center; padding: 20px; }" +
                        "       .email-body { padding: 30px; color: #333333; line-height: 1.6; font-size: 16px; }" +
                        "       .email-footer { background-color: #f1f1f1; text-align: center; padding: 10px; font-size: 12px; color: #666; }" +
                        "       .btn { display: inline-block; padding: 10px 20px; color: white; background: #4A90E2; text-decoration: none;" +
                        "               border-radius: 4px; margin-top: 20px; }" +
                        "       .btn:hover { background-color: #357ABD; }" +
                        "   </style>" +
                        "</head>" +
                        "<body>" +
                        "   <div class='email-container'>" +
                        "       <div class='email-header'>" +
                        "           <h1>Mangas World</h1>" +
                        "       </div>" +
                        "       <div class='email-body'>" +
                        "           <p>%s</p>" +
                        "       </div>" +
                        "       <div class='email-footer'>" +
                        "           <p>%s</p>" +
                        "           <p>%s</p>" +
                        "       </div>" +
                        "   </div>" +
                        "</body>" +
                        "</html>",
                body, footer, cr);
    }

}
