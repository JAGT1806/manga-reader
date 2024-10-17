package com.proyecto.mangareader.app.service;

import com.proyecto.mangareader.app.dto.in.InUsersDTO;

import com.proyecto.mangareader.app.responses.ok.OkResponse;
import com.proyecto.mangareader.app.responses.user.UserListResponse;
import com.proyecto.mangareader.app.responses.user.UserResponse;

import java.util.List;

public interface IUsersService {
    public UserListResponse getAllUsers(String username, String email, String role, int offset, int limit);

    public UserResponse getUserById(Long idUser);

    public UserResponse saveUser(InUsersDTO inUsersDTO);

    public OkResponse deleteUser(Long id);

    public UserResponse updateUser(Long id, InUsersDTO inUsersDTO);
}
