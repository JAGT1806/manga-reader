package com.proyecto.mangareader.app.service;

import com.proyecto.mangareader.app.dto.in.InUsersDTO;
import com.proyecto.mangareader.app.dto.out.OutUsersDTO;
import com.proyecto.mangareader.app.entity.UsersEntity;

import java.util.List;

public interface IUsersService {
    public List<OutUsersDTO> getAllUsers(String username, String email, String role);

    public OutUsersDTO getUserById(Long idUser);

    public OutUsersDTO saveUser(InUsersDTO inUsersDTO);

    public String deleteUser(Long id);

    public OutUsersDTO updateUser(Long id, InUsersDTO inUsersDTO);
}
