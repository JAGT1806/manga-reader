package com.jagt1806.mangareader.service;

import com.jagt1806.mangareader.model.Codes;
import com.jagt1806.mangareader.model.Users;
import com.jagt1806.mangareader.service.imp.CodeServiceImp;

public interface CodeService {
    void generateCode(Users user, CodeServiceImp.CodeType codeType);
    Users getUserByCode(String code, CodeServiceImp.CodeType codeType);
    Codes getCode(String code);
    void validateCode(Codes code, CodeServiceImp.CodeType codeType);
    void useCode(Codes code);
}
