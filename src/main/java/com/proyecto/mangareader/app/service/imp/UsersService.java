package com.proyecto.mangareader.app.service.imp;

import com.proyecto.mangareader.app.dto.in.InUsersDTO;
import com.proyecto.mangareader.app.dto.out.OutUsersDTO;
import com.proyecto.mangareader.app.entity.RolesEntity;
import com.proyecto.mangareader.app.entity.UsersEntity;
import com.proyecto.mangareader.app.exceptions.UserNotFoundException;
import com.proyecto.mangareader.app.repository.RolesRepository;
import com.proyecto.mangareader.app.repository.UsersRepository;
import com.proyecto.mangareader.app.responses.ok.OkResponse;
import com.proyecto.mangareader.app.responses.user.UserListResponse;
import com.proyecto.mangareader.app.responses.user.UserResponse;
import com.proyecto.mangareader.app.security.JwtTokenProvider;
import com.proyecto.mangareader.app.service.IUsersService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UsersService implements IUsersService {
    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public UserListResponse getAllUsers(String username, String email, String role, int offset, int limit) {
        // Se define la cantidad de datos y desde donde
        Pageable pageable = PageRequest.of(offset, limit);

        // Se aplica al filtro.
        List<UsersEntity> users = usersRepository.findByFilters(username, email, role, pageable);

        // Se obtiene el listado
        List<OutUsersDTO> usersDTO = users.stream().map(this::setUserDTO).toList();

        if (usersDTO.isEmpty() || (role != null && usersDTO.getFirst() == null)) {
            throw new UserNotFoundException("El usuario no existe");
        }

        Long total = usersRepository.countByFilters(username, email, role);

        return new UserListResponse(usersDTO, offset, limit,total);
    }

    @Override
    public UserResponse getUserById(Long idUser) {
        UsersEntity user = usersRepository.findById(idUser).orElse(null);

        if (user != null) {
            OutUsersDTO outUsersDTO = setUserDTO(user);
            return new UserResponse(outUsersDTO);
        } else {
            throw new UserNotFoundException("Usuario no encontrado");
        }

    }

    @Override
    public UserResponse saveUser(InUsersDTO inUsersDTO) {
        if (inUsersDTO == null || inUsersDTO.getUsername().isEmpty() || inUsersDTO.getEmail().isEmpty() || inUsersDTO.getPassword().isEmpty() || inUsersDTO.getRolId().describeConstable().isEmpty()) {
            throw new IllegalArgumentException("Los datos del usuario no puede ser nulo");
        }
        UsersEntity user = new UsersEntity();

        user.setUsername(inUsersDTO.getUsername());
        user.setEmail(inUsersDTO.getEmail());
        user.setPassword(passwordEncoder.encode(inUsersDTO.getPassword()));
        user.setDateCreate(LocalDateTime.now());


        // Establecer el rol a partir del ID proporcionado
        RolesEntity role = rolesRepository.findById(inUsersDTO.getRolId())
                .orElseThrow(() -> new RuntimeException("No se encontro el rol"));
        user.setRol(role);
        usersRepository.save(user);

        OutUsersDTO outUsersDTO = setUserDTO(user);

        UserResponse userResponse = new UserResponse(outUsersDTO);

        return userResponse;
    }

    @Override
    public OkResponse deleteUser(Long id) {
        UsersEntity user = usersRepository.findById(id).orElse(null);
        if(user != null || usersRepository.existsById(id)) {
            usersRepository.deleteById(id);
            return new OkResponse();
        } else {
            throw new UserNotFoundException("usuario no encontrado");
        }
    }

    @Override
    public UserResponse updateUser(Long id, InUsersDTO inUsersDTO) {
        if (id == null || inUsersDTO == null) {
            throw new IllegalArgumentException("El ID del rol o los datos actualizados no pueden ser nulos.");
        }
        if (!usersRepository.existsById(id)) {
            throw new IllegalArgumentException("El rol con ID " + id + " no existe.");
        }

        UsersEntity user = usersRepository.findById(id).orElse(null);
        if (user != null) {
            user.setUsername(inUsersDTO.getUsername());
            user.setEmail(inUsersDTO.getEmail());
            user.setPassword(inUsersDTO.getPassword());
            user.setDateModified(LocalDateTime.now());


            usersRepository.save(user);

            OutUsersDTO userDTO = setUserDTO(user);

            return new UserResponse(userDTO);
        } else {
            throw new IllegalArgumentException("No se encontró el rol con el ID proporcionado.");
        }
    }

    private OutUsersDTO setUserDTO(@org.jetbrains.annotations.NotNull UsersEntity user) {
        OutUsersDTO outUsersDTO = new OutUsersDTO();
        outUsersDTO.setIdUser(user.getIdUser());
        outUsersDTO.setUsername(user.getUsername());
        outUsersDTO.setEmail(user.getEmail());
        outUsersDTO.setRol(user.getRol().getRol());
        if (user.getDateCreate() != null) {
            outUsersDTO.setDateCreated(LocalDate.from(user.getDateCreate()));
        }

        if (user.getDateModified() != null) {
            outUsersDTO.setDateModified(LocalDate.from(user.getDateModified()));
        }

        return outUsersDTO;
    }

}
