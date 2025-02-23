package com.jagt1806.mangareader.http.response.user;

import com.jagt1806.mangareader.dto.user.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserListResponse {
    private List<UserDTO> data;
    private int offset;
    private int limit;
    private long total;
}
