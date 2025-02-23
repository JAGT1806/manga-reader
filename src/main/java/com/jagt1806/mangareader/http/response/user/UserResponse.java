package com.jagt1806.mangareader.http.response.user;

import com.jagt1806.mangareader.dto.user.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UserDTO data;
}
