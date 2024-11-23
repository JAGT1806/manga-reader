package com.proyecto.mangareader.app.service;

import com.proyecto.mangareader.app.entity.UsersEntity;
import com.proyecto.mangareader.app.entity.VerificationCodesEntity;
import com.proyecto.mangareader.app.service.imp.VerificationCodesService;
import jakarta.mail.MessagingException;

public interface IVerificationCodesService {
    void generateCode(UsersEntity user, VerificationCodesService.CodeType codeType) throws MessagingException;
    UsersEntity getUserByCode(String code, VerificationCodesService.CodeType codeType);
    VerificationCodesEntity getCode(String code);
    void validateCode(VerificationCodesEntity code, VerificationCodesService.CodeType codeType);
    void useCode(VerificationCodesEntity code);
}
