package com.proyecto.mangareader.app.service.imp;

import com.proyecto.mangareader.app.service.IEmailService;
import com.proyecto.mangareader.app.util.MessageUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Servicio de implementación para el envío de correos electrónicos.
 * Gestiona el envío de códigos de verificación y correos de restablecimiento de contraseña.
 * @author Jhon Alexander Gómez Trujillo
 * @since 2024
 */
@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService {
    /** Servicio de envío de correos electrónicos de Spring. */
    private final JavaMailSender mailSender;
    /** Utilidad para manejar mensajes localizados. */
    private final MessageUtil messageUtil;

    /**
     * Dirección de correo electrónico desde la cual se enviarán los emails.
     * Se configura desde las propiedades de la aplicación.
     */
    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Envía un código de verificación al correo electrónico especificado.
     *
     * @param to Dirección de correo electrónico del destinatario
     * @param code Código de verificación a enviar
     * @throws MessagingException Si ocurre un error durante el envío del correo electrónico
     */
    @Override
    public void sendVerificationCode(String to, String code) throws MessagingException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(messageUtil.getMessage("email.verified.title"));
        message.setText(messageUtil.getMessage("email.verified.text", new Object[]{code}));

        mailSender.send(message);
    }

    /**
     * Envía un correo electrónico de restablecimiento de contraseña.
     *
     * @param to Dirección de correo electrónico del destinatario
     * @param code Código de restablecimiento de contraseña
     */
    public void sendPasswordResetEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(messageUtil.getMessage("email.forget.password.title"));
        message.setText(messageUtil.getMessage("email.forget.password.body", new Object[]{code}));

        mailSender.send(message);
    }

}
