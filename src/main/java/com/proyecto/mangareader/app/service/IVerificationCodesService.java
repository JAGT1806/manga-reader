package com.proyecto.mangareader.app.service;

import com.proyecto.mangareader.app.entity.UsersEntity;
import com.proyecto.mangareader.app.entity.VerificationCodesEntity;
import com.proyecto.mangareader.app.service.imp.VerificationCodesService;
import jakarta.mail.MessagingException;

/**
 * Interfaz de servicio para la gestión de códigos de verificación en la aplicación Manga Reader.
 *
 * Define las operaciones fundamentales para la generación, validación y uso de códigos
 * de verificación en diferentes procesos como registro, recuperación de contraseña,
 * y otras verificaciones de usuario.
 *
 * @author Jhon Alexander Gómez Trujillo
 * @since 2024
 */
public interface IVerificationCodesService {
    /**
     * Genera un nuevo código de verificación para un usuario específico.
     *
     * Crea un código único asociado a un usuario para un propósito específico
     * (registro, recuperación de contraseña, etc.) y gestiona su envío.
     *
     * @param user Entidad de usuario para quien se genera el código
     * @param codeType Tipo de código de verificación a generar
     * @throws MessagingException Si ocurre un error durante el envío del código (ej. email)
     */
    void generateCode(UsersEntity user, VerificationCodesService.CodeType codeType) throws MessagingException;

    /**
     * Recupera el usuario asociado a un código de verificación específico.
     *
     * Busca y devuelve el usuario vinculado a un código de verificación,
     * verificando que sea del tipo de código esperado.
     *
     * @param code Código de verificación a consultar
     * @param codeType Tipo de código esperado
     * @return Entidad de usuario asociada al código
     * @throws IllegalArgumentException Si el código no es válido o no corresponde al tipo esperado
     */
    UsersEntity getUserByCode(String code, VerificationCodesService.CodeType codeType);

    /**
     * Recupera la entidad de código de verificación completa.
     *
     * Permite obtener todos los detalles de un código de verificación específico.
     *
     * @param code Código de verificación a recuperar
     * @return Entidad de código de verificación
     * @throws IllegalArgumentException Si el código no existe
     */
    VerificationCodesEntity getCode(String code);

    /**
     * Valida un código de verificación para un tipo específico.
     *
     * Realiza comprobaciones de validez del código, como:
     * - Verificar que no esté expirado
     * - Comprobar que no haya sido ya utilizado
     * - Confirmar que corresponde al tipo de código esperado
     *
     * @param code Entidad de código de verificación a validar
     * @param codeType Tipo de código esperado
     * @throws IllegalArgumentException Si el código no es válido
     */
    void validateCode(VerificationCodesEntity code, VerificationCodesService.CodeType codeType);

    /**
     * Marca un código de verificación como utilizado.
     *
     * Invalida el código para prevenir reutilizaciones posteriores.
     * Típicamente llamado después de una verificación exitosa.
     *
     * @param code Entidad de código de verificación a marcar como usado
     */
    void useCode(VerificationCodesEntity code);
}
