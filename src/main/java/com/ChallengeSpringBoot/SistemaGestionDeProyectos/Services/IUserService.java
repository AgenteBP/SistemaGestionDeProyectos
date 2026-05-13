package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Services;

import java.util.List;

import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.UserDTO.UserRequestDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.UserDTO.UserResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.User;

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

    UserResponseDTO updateUser(Integer idUser, UserRequestDTO dto);

    void deleteUser(Integer idUser);
}
