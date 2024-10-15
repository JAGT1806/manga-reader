package com.proyecto.mangareader.app.service.imp;

import com.proyecto.mangareader.app.dto.in.InUsersDTO;
import com.proyecto.mangareader.app.dto.out.OutUsersDTO;
import com.proyecto.mangareader.app.entity.RolesEntity;
import com.proyecto.mangareader.app.entity.UsersEntity;
import com.proyecto.mangareader.app.repository.RolesRepository;
import com.proyecto.mangareader.app.repository.UsersRepository;
import com.proyecto.mangareader.app.service.IUsersService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class UsersService implements IUsersService {
    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;

    @Override
    public List<OutUsersDTO> getAllUsers(String username, String email, String role) {
        List<UsersEntity> users = usersRepository.findByFilters(username, email, role);

        return users.stream().map(user -> {
            OutUsersDTO outUsersDTO = new OutUsersDTO();
            outUsersDTO.setIdUser(user.getIdUser());
            outUsersDTO.setUsername(user.getUsername());
            outUsersDTO.setEmail(user.getEmail());
            outUsersDTO.setRol(user.getRol().getRol());

            return outUsersDTO;
        }).toList();
    }

    @Override
    public OutUsersDTO getUserById(Long idUser) {
        UsersEntity user = usersRepository.findById(idUser).orElse(null);
        OutUsersDTO outUsersDTO = new OutUsersDTO();
        if (user != null) {
            outUsersDTO.setIdUser(user.getIdUser());
            outUsersDTO.setUsername(user.getUsername());
            outUsersDTO.setEmail(user.getEmail());
            outUsersDTO.setRol(user.getRol().getRol());

            return outUsersDTO;
        } else {
            return null;
        }

    }

    @Override
    public OutUsersDTO saveUser(InUsersDTO inUsersDTO) {
        if (inUsersDTO == null || inUsersDTO.getUsername().isEmpty() || inUsersDTO.getEmail().isEmpty() || inUsersDTO.getPassword().isEmpty() || inUsersDTO.getRolId().describeConstable().isEmpty()) {
            throw new IllegalArgumentException("Los datos del usuario no puede ser nulo");
        }
        UsersEntity user = new UsersEntity();

        user.setUsername(inUsersDTO.getUsername());
        user.setEmail(inUsersDTO.getEmail());
        user.setPassword(inUsersDTO.getPassword());
        user.setDateCreate(LocalDateTime.now());

        // Establecer el rol a partir del ID proporcionado
        RolesEntity role = rolesRepository.findById(inUsersDTO.getRolId())
                .orElseThrow(() -> new RuntimeException("No se encontro el rol"));
        user.setRol(role);

        OutUsersDTO outUsersDTO = new OutUsersDTO();
        outUsersDTO.setIdUser(user.getIdUser());
        outUsersDTO.setUsername(user.getUsername());
        outUsersDTO.setEmail(user.getEmail());
        if (user.getRol() != null) {
            outUsersDTO.setRol(user.getRol().getRol());
        }

        usersRepository.save(user);
        return outUsersDTO;
    }

    @Override
    public String deleteUser(Long id) {
        UsersEntity user = usersRepository.findById(id).orElse(null);
        if(user != null || usersRepository.existsById(id)) {
            usersRepository.deleteById(id);
            return "Usuario eliminado exitosamente";
        } else {
            return "No se encontro el usuario";
        }
    }

    @Override
    public OutUsersDTO updateUser(Long id, InUsersDTO inUsersDTO) {
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

            OutUsersDTO outUsersDTO = new OutUsersDTO();
            outUsersDTO.setIdUser(user.getIdUser());
            outUsersDTO.setUsername(user.getUsername());
            outUsersDTO.setEmail(user.getEmail());
            if (user.getRol() != null) {
                outUsersDTO.setRol(user.getRol().getRol());
            }

            return outUsersDTO;
        } else {
            throw new IllegalArgumentException("No se encontró el rol con el ID proporcionado.");
        }
    }
}
