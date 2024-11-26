package com.proyecto.mangareader.app.service;

import jakarta.mail.MessagingException;

/**
 * Interfaz que define los servicios de envío de correos electrónicos.
 * Proporciona métodos para enviar códigos de verificación y correos de restablecimiento de contraseña.
 * @author Jhon Alexander Gómez Trujillo
 * @since 2024
 */
public interface IEmailService {
    /**
     * Envía un código de verificación al correo electrónico especificado.
     *
     * @param to Dirección de correo electrónico del destinatario
     * @param code Código de verificación a enviar
     * @throws MessagingException Si ocurre un error durante el envío del correo electrónico
     */
    void sendVerificationCode(String to, String code) throws MessagingException;
    /**
     * Envía un correo electrónico de restablecimiento de contraseña.
     *
     * @param to Dirección de correo electrónico del destinatario
     * @param code Código de restablecimiento de contraseña
     * @throws MessagingException Si ocurre un error durante el envío del correo electrónico
     */
    void sendPasswordResetEmail(String to, String code) throws MessagingException;
}
