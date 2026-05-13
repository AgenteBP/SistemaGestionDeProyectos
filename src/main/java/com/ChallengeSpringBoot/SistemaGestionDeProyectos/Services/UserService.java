package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Services;

import java.util.List;
import org.springframework.stereotype.Service;

import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.UserDTO.UserRequestDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.UserDTO.UserResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.User;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.ProjectRepository;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.TaskRepository;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    /// Creacion de usuario
    @Override
    public UserResponseDTO createUser(UserRequestDTO dto) {

        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new RuntimeException("El email es obligatorio.");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con el email: " + dto.getEmail());
        }

        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new RuntimeException("El nombre es obligatorio.");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setActive(true);

        return mapToResponseDTO(userRepository.save(user));
    }

    /// Obtener todos los usuarios
    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    /// Obtener todos los usuarios activos
    @Override
    public List<UserResponseDTO> getAllUsersWithActive() {
        return userRepository.findByActiveTrue().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    /// Obtener usuario por id
    @Override
    public UserResponseDTO getUserById(Integer idUser) {
        User user = userRepository.findByIdUserAndActiveTrue(idUser)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + idUser));
        return mapToResponseDTO(user);
    }

    public User findUserById(Integer idUser) {
        return userRepository.findByIdUserAndActiveTrue(idUser)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + idUser));
    }

    @Override
    public UserResponseDTO getUserByName(String name) {
        User user = userRepository.findByNameAndActiveTrue(name)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con nombre: " + name));
        return mapToResponseDTO(user);
    }

    public User findUserByName(String name) {
        return userRepository.findByNameAndActiveTrue(name)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con nombre: " + name));
    }

    @Override
    public UserResponseDTO getUserByEmail(String email) {
        return mapToResponseDTO(findUserByEmail(email));
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmailAndActiveTrue(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
    }

    @Override
    public UserResponseDTO updateUser(Integer idUser, UserRequestDTO dto) {
        User user = findUserById(idUser);

        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new RuntimeException("El email es obligatorio.");
        }

        // Validar email único solo si cambió
        if (!user.getEmail().equalsIgnoreCase(dto.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("Ya existe un usuario con el email: " + dto.getEmail());
            }
        }

        // Validar nombre no vacío
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new RuntimeException("El nombre es obligatorio.");
        }

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        return mapToResponseDTO(userRepository.save(user));
    }

    @Override
    public void deleteUser(Integer idUser) {
        User user = findUserById(idUser);

        if (projectRepository.existsByOwnerAndActiveTrue(user)) {
            throw new RuntimeException(
                    "El usuario no se puede eliminar porque es propietario de un proyecto activo. Por favor, reasigne la propiedad del proyecto primero.");
        }

        if (taskRepository.existsByAssignedUserAndActiveTrue(user)) {
            throw new RuntimeException(
                    "El usuario no se puede eliminar porque tiene tareas activas asignadas.");
        }

        user.setActive(false);
        userRepository.save(user);
    }

    /// Funcion auxiliar

    private UserResponseDTO mapToResponseDTO(User user) {
        return new UserResponseDTO(
                user.getIdUser(),
                user.getName(),
                user.getEmail());
    }
}
