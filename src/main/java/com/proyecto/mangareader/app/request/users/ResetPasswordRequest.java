package com.proyecto.mangareader.app.request.users;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String code;
    private String password;
}
