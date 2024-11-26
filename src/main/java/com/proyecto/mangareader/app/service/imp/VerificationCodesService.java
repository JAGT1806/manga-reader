package com.proyecto.mangareader.app.service.imp;

import com.proyecto.mangareader.app.entity.UsersEntity;
import com.proyecto.mangareader.app.entity.VerificationCodesEntity;
import com.proyecto.mangareader.app.exceptions.UniqueException;
import com.proyecto.mangareader.app.repository.VerificationCodesRepository;
import com.proyecto.mangareader.app.service.IEmailService;
import com.proyecto.mangareader.app.service.IVerificationCodesService;
import com.proyecto.mangareader.app.util.MessageUtil;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para la gestión de códigos de verificación de usuarios.
 *
 * Maneja la generación, validación y limpieza de códigos de verificación
 * para procesos como registro de usuarios y restablecimiento de contraseña.
 * @author Jhon Alexander Gómez Trujillo
 * @since 2024
 */
@Service
@RequiredArgsConstructor
public class VerificationCodesService implements IVerificationCodesService {
    /** Repositorio para operaciones de códigos de verificación */
    private final VerificationCodesRepository codesRepository;
    /** Utilidad para gestión de mensajes */
    private final MessageUtil messageUtil;
    /** Servicio de envío de correos electrónicos */
    private final IEmailService emailService;

    /**
     * Tiempo de expiración de códigos de verificación en horas
     * Valor por defecto: 24 horas
     */
    @Value("${token.verification.expiration:24}")
    private int verificationExpirationHours;

    /**
     * Tiempo de expiración de códigos de restablecimiento de contraseña en horas
     * Valor por defecto: 1 hora
     */
    @Value("${token.password.expiration:1}")
    private int passwordExpirationHours;


    /**
     * Tipos de códigos de verificación.
     */
    public enum CodeType {
        REGISTRATION,
        PASSWORD_RESET
    }

    /**
     * Genera un nuevo código de verificación para un usuario.
     *
     * @param user Usuario para el que se genera el código
     * @param codeType Tipo de código (registro o restablecimiento de contraseña)
     * @throws MessagingException Si hay un error al enviar el correo electrónico
     */
    @Override
    public void generateCode(UsersEntity user, CodeType codeType) throws MessagingException {
        deleteExistingCodes(user, codeType);

        String code;
        if(codeType == CodeType.REGISTRATION) {
            code = String.format("%06d", (int) (Math.random() * 900_000) + 100_000);
        } else {
            code = String.format("%07d", (int) (Math.random() * 9_000_000) + 1_000_000);
        }

        VerificationCodesEntity verificationCodes = new VerificationCodesEntity();
        verificationCodes.setCode(code);
        verificationCodes.setUser(user);

        int expirationHours = (codeType == CodeType.REGISTRATION)
                ? verificationExpirationHours
                : passwordExpirationHours;

        verificationCodes.setExpiryDate(LocalDateTime.now().plusHours(expirationHours));

        try {
            codesRepository.save(verificationCodes);
        } catch (DataIntegrityViolationException e) {
            if(e.getMessage().contains("ukpb2127rkh2td3y1mptij7slba")) {
                throw new UniqueException(messageUtil.getMessage("code.unique"));
            }
            throw e;
        }



        if(codeType == CodeType.REGISTRATION) {
            emailService.sendVerificationCode(user.getEmail(), code);
        } else {
            emailService.sendPasswordResetEmail(user.getEmail(), code);
        }

    }

    /**
     * Recupera el usuario asociado a un código de verificación.
     *
     * @param code Código de verificación
     * @param codeType Tipo de código
     * @return Usuario asociado al código
     * @throws IllegalArgumentException Si el código no es válido
     */
    @Override
    public UsersEntity getUserByCode(String code, CodeType codeType) {
        VerificationCodesEntity verificationCode = getCode(code);
        validateCode(verificationCode, codeType);
        return verificationCode.getUser();
    }

    /**
     * Recupera la entidad de código de verificación.
     *
     * @param code Código de verificación
     * @return Entidad de código de verificación
     * @throws IllegalArgumentException Si el código no existe
     */
    @Override
    public VerificationCodesEntity getCode(String code) {
        return codesRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException(messageUtil.getMessage("user.code.not.valid")));
    }

    /**
     * Valida un código de verificación.
     *
     * @param code Código de verificación a validar
     * @param codeType Tipo de código esperado
     * @throws IllegalArgumentException Si el código no es válido
     */
    @Override
    public void validateCode(VerificationCodesEntity code, CodeType codeType) {
        if (code.isUsed()) {
            deleteCode(code);
            throw new IllegalArgumentException(messageUtil.getMessage("user.code.used"));
        }

        if((codeType == CodeType.REGISTRATION && code.getCode().length() != 6) ||
                (codeType == CodeType.PASSWORD_RESET && code.getCode().length() != 7)) {
            throw new IllegalArgumentException(messageUtil.getMessage("user.code.not.valid"));
        }

        if (LocalDateTime.now().isAfter(code.getExpiryDate())) {
            deleteCode(code);
            throw new IllegalArgumentException(messageUtil.getMessage("user.code.expired"));
        }
    }

    /**
     * Marca un código de verificación como usado.
     *
     * @param code Código de verificación a marcar
     */
    @Override
    @Transactional
    public void useCode(VerificationCodesEntity code) {
        code.setUsed(true);
        codesRepository.save(code);
        deleteCode(code);
    }

    /**
     * Elimina códigos de verificación existentes para un usuario.
     *
     * @param user Usuario
     * @param codeType Tipo de código
     */
    private void deleteExistingCodes(UsersEntity user, CodeType codeType) {
        List<VerificationCodesEntity> existingCodes;
        if (codeType == CodeType.REGISTRATION) {
            existingCodes = codesRepository.findByUserAndCodeLength(user, 6).orElse(new ArrayList<>());
        } else {
            existingCodes = codesRepository.findByUserAndCodeLength(user, 7).orElse(new ArrayList<>());
        }
        codesRepository.deleteAll(existingCodes);
    }

    /**
     * Elimina un código de verificación específico.
     *
     * @param code Código de verificación a eliminar
     */
    private void deleteCode(VerificationCodesEntity code) {
        codesRepository.delete(code);
    }

    /**
     * Tarea programada para limpiar códigos de verificación expirados.
     * Se ejecuta cada hora en punto.
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanupExpiredCodes() {
        codesRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }
}
