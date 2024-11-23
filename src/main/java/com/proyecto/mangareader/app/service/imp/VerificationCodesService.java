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

@Service
@RequiredArgsConstructor
public class VerificationCodesService implements IVerificationCodesService {
    private final VerificationCodesRepository codesRepository;
    private final MessageUtil messageSource;
    private final IEmailService emailService;

    @Value("${token.verification.expiration:24}")
    private int verificationExpirationHours;

    @Value("${token.password.expiration:1}")
    private int passwordExpirationHours;


    public enum CodeType {
        REGISTRATION,
        PASSWORD_RESET
    }

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
                throw new UniqueException(messageSource.getMessage("code.unique"));
            }
            throw e;
        }



        if(codeType == CodeType.REGISTRATION) {
            emailService.sendVerificationCode(user.getEmail(), code);
        } else {
            emailService.sendPasswordResetEmail(user.getEmail(), code);
        }

    }

    @Override
    public UsersEntity getUserByCode(String code, CodeType codeType) {
        VerificationCodesEntity verificationCode = getCode(code);
        validateCode(verificationCode, codeType);
        return verificationCode.getUser();
    }

    @Override
    public VerificationCodesEntity getCode(String code) {
        return codesRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException(messageSource.getMessage("user.code.not.valid")));
    }

    @Override
    public void validateCode(VerificationCodesEntity code, CodeType codeType) {
        if (code.isUsed()) {
            deleteCode(code);
            throw new IllegalArgumentException(messageSource.getMessage("user.code.used"));
        }

        if((codeType == CodeType.REGISTRATION && code.getCode().length() != 6) ||
                (codeType == CodeType.PASSWORD_RESET && code.getCode().length() != 7)) {
            throw new IllegalArgumentException(messageSource.getMessage("user.code.not.valid"));
        }

        if (LocalDateTime.now().isAfter(code.getExpiryDate())) {
            deleteCode(code);
            throw new IllegalArgumentException(messageSource.getMessage("user.code.expired"));
        }
    }

    @Override
    @Transactional
    public void useCode(VerificationCodesEntity code) {
        code.setUsed(true);
        codesRepository.save(code);
        deleteCode(code);
    }

    private void deleteExistingCodes(UsersEntity user, CodeType codeType) {
        List<VerificationCodesEntity> existingCodes;
        if (codeType == CodeType.REGISTRATION) {
            existingCodes = codesRepository.findByUserAndCodeLength(user, 6).orElse(new ArrayList<>());
        } else {
            existingCodes = codesRepository.findByUserAndCodeLength(user, 7).orElse(new ArrayList<>());
        }
        codesRepository.deleteAll(existingCodes);
    }

    private void deleteCode(VerificationCodesEntity code) {
        codesRepository.delete(code);
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanupExpiredCodes() {
        codesRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }
}
