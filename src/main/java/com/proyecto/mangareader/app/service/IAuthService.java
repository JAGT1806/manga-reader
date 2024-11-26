package com.proyecto.mangareader.app.service;

import com.proyecto.mangareader.app.request.auth.RegisterRequest;
import com.proyecto.mangareader.app.request.auth.ResendValidatedRequest;
import com.proyecto.mangareader.app.request.auth.VerificationRequest;
import com.proyecto.mangareader.app.request.users.ForgotPasswordRequest;
import com.proyecto.mangareader.app.request.users.ResetPasswordRequest;
import com.proyecto.mangareader.app.responses.auth.LoginResponse;
import com.proyecto.mangareader.app.responses.user.UserResponse;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;

/**
 * Interfaz de servicio de autenticación para la aplicación Manga Reader.
 *
 * Define los contratos para operaciones fundamentales de gestión de usuarios
 * y autenticación, incluyendo inicio de sesión, registro, verificación de correo,
 * recuperación de contraseña y gestión de códigos de verificación.
 *
 * @author Jhon Alexander Gómez Trujillo
 * @since 2024
 */
public interface IAuthService {
    /**
     * Autentica un usuario en el sistema mediante credenciales de correo electrónico y contraseña.
     *
     * Realiza la validación de credenciales, genera token de autenticación
     * y devuelve la información del usuario autenticado.
     *
     * @param email Correo electrónico del usuario
     * @param password Contraseña del usuario
     * @return Respuesta HTTP con detalles de inicio de sesión, incluyendo token JWT
     * @throws org.springframework.security.core.userdetails.UsernameNotFoundException Si las credenciales no son válidas
     * @throws IllegalStateException Si la cuenta de usuario no está habilitada
     */
    ResponseEntity<LoginResponse> login(String email, String password);

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * Crea un nuevo usuario con los datos proporcionados, genera un código de verificación
     * y envía un correo electrónico de confirmación.
     *
     * @param request Solicitud de registro con información del nuevo usuario
     * @return Respuesta con detalles del usuario registrado
     * @throws MessagingException Si ocurre un error al enviar el correo de verificación
     * @throws IllegalArgumentException Si los datos de registro son inválidos
     */
    UserResponse registerUser(RegisterRequest request) throws MessagingException;

    /**
     * Verifica el correo electrónico de un usuario utilizando un código de verificación.
     *
     * Valida el código proporcionado y habilita la cuenta de usuario
     * una vez que la verificación es exitosa.
     *
     * @param request Solicitud de verificación con código proporcionado
     * @throws IllegalArgumentException Si el código de verificación no es válido
     */
    void verifyEmail(VerificationRequest request);

    /**
     * Inicia el proceso de recuperación de contraseña para un usuario.
     *
     * Genera un código de verificación para restablecer la contraseña
     * y envía un correo electrónico con instrucciones.
     *
     * @param request Solicitud de recuperación de contraseña con correo electrónico
     * @throws MessagingException Si ocurre un error al enviar el correo de recuperación
     * @throws com.proyecto.mangareader.app.exceptions.UserNotFoundException Si no se encuentra un usuario con el correo proporcionado
     */
    void forgotPassword(ForgotPasswordRequest request) throws MessagingException;

    /**
     * Restablece la contraseña de un usuario utilizando un código de verificación.
     *
     * Permite al usuario establecer una nueva contraseña después de verificar
     * su identidad mediante un código de recuperación.
     *
     * @param request Solicitud de restablecimiento con código y nueva contraseña
     * @throws IllegalArgumentException Si el código de verificación no es válido
     */
    void resetPassword(ResetPasswordRequest request);

    /**
     * Reenvía el correo de validación a un usuario que aún no ha verificado su cuenta.
     *
     * Genera un nuevo código de verificación y envía un correo electrónico
     * de activación para usuarios pendientes de verificación.
     *
     * @param request Solicitud de reenvío de correo con la dirección de email
     * @throws MessagingException Si ocurre un error al enviar el correo de verificación
     * @throws com.proyecto.mangareader.app.exceptions.UserNotFoundException Si no se encuentra el usuario
     * @throws com.proyecto.mangareader.app.exceptions.UserAlreadyEnabledException Si la cuenta ya está habilitada
     */
    void resendValidatedEmail(ResendValidatedRequest request) throws MessagingException;

}
