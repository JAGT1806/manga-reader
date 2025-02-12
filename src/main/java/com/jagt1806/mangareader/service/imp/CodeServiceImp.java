package com.jagt1806.mangareader.service.imp;

import com.jagt1806.mangareader.exceptions.UniqueException;
import com.jagt1806.mangareader.model.Codes;
import com.jagt1806.mangareader.model.Users;
import com.jagt1806.mangareader.repository.CodesRepository;
import com.jagt1806.mangareader.service.CodeService;
import com.jagt1806.mangareader.service.EmailService;
import com.jagt1806.mangareader.util.MessageUtil;
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
public class CodeServiceImp implements CodeService {
    private final CodesRepository codesRepository;
    private final MessageUtil messageUtil;
    private final EmailService emailService;

    @Value("${token.verification.expiration:24}")
    private int verificationExpirationHours;
    @Value("${token.password.expiration:1}")
    private int passwordExpirationHours;

    public enum CodeType {
        REGISTRATION,
        PASSWORD_RESET
    }

    @Override
    public void generateCode(Users user, CodeType codeType) throws MessagingException {
        deleteExistingCodes(user, codeType);

        String code;
        if(codeType == CodeType.REGISTRATION) {
            code = String.format("%06d", (int) (Math.random() * 900_000) + 100_000);
        } else {
            code = String.format("%07d", (int) (Math.random() * 9_000_000) + 1_000_000);
        }

        Codes verificationCodes = new Codes();
        verificationCodes.setCode(code);
        verificationCodes.setUser(user);

        int expirationHours = (codeType == CodeType.REGISTRATION)
                ? verificationExpirationHours
                : passwordExpirationHours;

        verificationCodes.setExpiryDate(LocalDateTime.now().plusHours(expirationHours));

        try {
            codesRepository.save(verificationCodes);
        } catch (DataIntegrityViolationException e) {
            if(e.getMessage().contains("codes_code_key")) {
                throw new UniqueException(messageUtil.getMessage("code.unique"));
            }
            throw e;
        }



        if(codeType == CodeType.REGISTRATION) {
            emailService.sendVerificationCode(user.getEmail(), code, verificationCodes.getExpiryDate());
        } else {
            emailService.sendPasswordResetEmail(user.getEmail(), code, verificationCodes.getExpiryDate());
        }
    }

    @Override
    public Users getUserByCode(String code, CodeType codeType) {
        Codes verificationCode = getCode(code);
        validateCode(verificationCode, codeType);
        return verificationCode.getUser();
    }

    @Override
    public Codes getCode(String code) {
        return codesRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException(messageUtil.getMessage("user.code.not.valid")));
    }

    @Override
    public void validateCode(Codes code, CodeType codeType) {
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

    @Override
    @Transactional
    public void useCode(Codes code) {
        code.setUsed(true);
        codesRepository.save(code);
        deleteCode(code);
    }

    private void deleteExistingCodes(Users user, CodeType codeType) {
        List<Codes> existingCodes;
        if (codeType == CodeType.REGISTRATION) {
            existingCodes = codesRepository.findByUserAndCodeLength(user, 6).orElse(new ArrayList<>());
        } else {
            existingCodes = codesRepository.findByUserAndCodeLength(user, 7).orElse(new ArrayList<>());
        }
        codesRepository.deleteAll(existingCodes);
    }

    private void deleteCode(Codes code) {
        codesRepository.delete(code);
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanupExpiredCodes() {
        codesRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }

}