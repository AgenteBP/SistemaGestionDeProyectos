package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Services;

import java.util.List;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.UserDTO.UserRequestDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.UserDTO.UserRequestUpdateDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.UserDTO.UserResponseDTO;

public interface IUserService {

    UserResponseDTO createUser(UserRequestDTO dto);

    List<UserResponseDTO> getAllUsers();

    List<UserResponseDTO> getAllUsersWithActive();

    UserResponseDTO getUserById(Integer idUser);

    // User findUserById(Integer idUser);

    UserResponseDTO getUserByName(String name);

    // User findUserByName(String name);

    UserResponseDTO getUserByEmail(String email);

    // User findUserByEmail(String email);

    UserResponseDTO updateUser(Integer idUser, UserRequestUpdateDTO dto);

    void deleteUser(Integer idUser);
}
