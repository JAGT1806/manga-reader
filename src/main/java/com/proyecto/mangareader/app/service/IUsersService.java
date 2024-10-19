package com.proyecto.mangareader.app.service;

import com.proyecto.mangareader.app.dto.in.InUsersDTO;

import com.proyecto.mangareader.app.responses.ok.OkResponse;
import com.proyecto.mangareader.app.responses.user.UserListResponse;
import com.proyecto.mangareader.app.responses.user.UserResponse;


public interface IUsersService {
     UserListResponse getAllUsers(String username, String email, String role, int offset, int limit);

     UserResponse getUserById(Long idUser);

     UserResponse saveUser(InUsersDTO inUsersDTO);

     OkResponse deleteUser(Long id);

     UserResponse updateUser(Long id, InUsersDTO inUsersDTO);

     String loginUser(String email, String password);
}
