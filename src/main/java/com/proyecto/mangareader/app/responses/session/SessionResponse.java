package com.proyecto.mangareader.app.responses.session;

import com.proyecto.mangareader.app.dto.out.OutUsersDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SessionResponse {
    private OutUsersDTO user;
    private LocalDateTime loginTime;
    private String sessionId;
    private List<String> authorities;
    private boolean active;
}
