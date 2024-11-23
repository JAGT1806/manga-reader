package com.proyecto.mangareader.app.responses.session;

import com.proyecto.mangareader.app.dto.users.UsersDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SessionResponse {
    private UsersDTO user;
    private LocalDateTime loginTime;
    private String sessionId;
    private List<String> authorities;
    private String token;
    private boolean active;
}
