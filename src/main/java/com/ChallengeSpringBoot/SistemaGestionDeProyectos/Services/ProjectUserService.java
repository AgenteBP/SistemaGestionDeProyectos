package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectUserDTO.ProjectUserRequestDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectUserDTO.ProjectUserRequestUpdateDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectUserDTO.ProjectUserResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.UserDTO.UserResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.Project;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.ProjectUser;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.User;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.ProjectRepository;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.ProjectUserRepository;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.TaskRepository;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectUserService implements IProjectUserService {

    private final ProjectUserRepository projectUserRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    // @Override
    // public List<ProjectUser> getProjectUsersByProject(Integer idProject) {
    // if (idProject == null) {
    // throw new RuntimeException("El ID del proyecto es obligatorio.");
    // }
    // Project project = getProjectById(idProject);
    // if (!project.getActive()) {
    // throw new RuntimeException("El proyecto se encuentra inactivo.");
    // }
    // return projectUserRepository.findByProject(project);
    // }

    @Override
    public List<ProjectUserResponseDTO> getProjectUsersByProjectDTO(Integer idProject) {
        if (idProject == null) {
            throw new RuntimeException("El ID del proyecto es obligatorio.");
        }
        Project project = getProjectById(idProject);
        if (!project.getActive()) {
            throw new RuntimeException("El proyecto se encuentra inactivo.");
        }
        return projectUserRepository.findByProject(project).stream()
                .map(pu -> convertToResponseDTO(pu, List.of(pu.getUser())))
                .toList();
    }

    @Override
    public List<ProjectUser> getProjectUsersByUser(Integer idUser) {
        User user = getUserById(idUser);
        return projectUserRepository.findByUser(user);
    }

    @Override
    public boolean existsByProjectAndUser(Integer idProject, Integer idUser) {
        return projectUserRepository.existsByProjectIdProjectAndUserIdUserAndActiveTrue(idProject, idUser);
    }

    // @Override
    // public ProjectUserResponseDTO saveProjectUser(ProjectUserRequestDTO
    // projectUserRequestDTO) {
    // if (projectUserRequestDTO.getIdProject() == null) {
    // throw new RuntimeException("El ID del proyecto es obligatorio.");
    // }
    // Project project = getProjectById(projectUserRequestDTO.getIdProject());

    // validateProjectOwner(project, projectUserRequestDTO.getIdOwner());

    // User user = getUserById(projectUserRequestDTO.getIdUser());
    // validateActiveUser(user, projectUserRequestDTO.getIdUser());

    // ProjectUser projectUser = projectUserRepository
    // .findByProjectIdProjectAndUserIdUser(project.getIdProject(),
    // user.getIdUser())
    // .orElse(new ProjectUser());

    // projectUser.setProject(project);
    // projectUser.setUser(user);
    // projectUser.setActive(true);
    // ProjectUser savedProjectUser = projectUserRepository.save(projectUser);

    // return convertToResponseDTO(savedProjectUser, List.of(user));
    // }

    @Override
    public ProjectUserResponseDTO saveAllProjectUsers(ProjectUserRequestDTO dto) {

        // 1. Validaciones básicas
        if (dto.getIdProject() == null || dto.getIdOwner() == null
                || dto.getIdUsers() == null || dto.getIdUsers().isEmpty()) {
            throw new RuntimeException(
                    "Debe proporcionar el ID del proyecto, el ID del propietario y al menos un ID de usuario.");
        }

        // 2. Validar proyecto
        Project project = getProjectById(dto.getIdProject());
        if (!project.getActive()) {
            throw new RuntimeException("No se pueden agregar usuarios a un proyecto inactivo.");
        }

        // 3. Validar que quien agrega sea el owner
        validateProjectOwner(project, dto.getIdOwner());

        // 4. Validar usuarios
        List<User> users = dto.getIdUsers().stream()
                .map(this::getUserById)
                .toList();

        users.forEach(user -> {

            // Usuario activo
            validateActiveUser(user, user.getIdUser());

            // No es el owner
            if (project.getOwner().getIdUser().equals(user.getIdUser())) {
                throw new RuntimeException(
                        "El usuario " + user.getName()
                                + " es el owner del proyecto y no puede agregarse como miembro.");
            }

            // No es ya miembro activo
            boolean isMember = projectUserRepository
                    .existsByProjectIdProjectAndUserIdUserAndActiveTrue(
                            project.getIdProject(), user.getIdUser());
            if (isMember) {
                throw new RuntimeException(
                        "El usuario " + user.getName() + " ya es miembro activo del proyecto.");
            }
        });

        // 5. Crear los ProjectUser
        List<ProjectUser> projectUsers = users.stream()
                .map(user -> {
                    ProjectUser projectUser = projectUserRepository
                            .findByProjectIdProjectAndUserIdUser(
                                    project.getIdProject(), user.getIdUser())
                            .orElse(new ProjectUser());
                    projectUser.setProject(project);
                    projectUser.setUser(user);
                    projectUser.setActive(true);
                    return projectUser;
                })
                .toList();

        List<ProjectUser> savedProjectUsers = projectUserRepository.saveAll(projectUsers);

        Integer idProjectUser = savedProjectUsers.size() == 1
                ? savedProjectUsers.get(0).getIdProjectUser()
                : null;

        return convertToResponseDTO(project, idProjectUser, users, true);
    }

    @Override
    @Transactional
    public ProjectUserResponseDTO changeUserAssigned(Integer idProject,
            ProjectUserRequestUpdateDTO projectUserRequestUpdateDTO) {

        Integer idOldUser = projectUserRequestUpdateDTO.getIdUserAssigned();
        Integer idNewUser = projectUserRequestUpdateDTO.getIdNewUser();
        Integer idOwner = projectUserRequestUpdateDTO.getIdOwner();

        if (idProject == null || idOldUser == null || idNewUser == null || idOwner == null) {
            throw new RuntimeException(
                    "Debe proporcionar el id del proyecto, el id del usuario anterior, el nuevo usuario y el id del propietario.");
        }

        Project project = getProjectById(idProject);
        validateProjectOwner(project, idOwner);

        // 1. Validar que el usuario antiguo esté asignado y no tenga tareas activas
        ProjectUser oldProjectUser = projectUserRepository
                .findByProjectIdProjectAndUserIdUserAndActiveTrue(idProject, idOldUser)
                .orElseThrow(() -> new RuntimeException("El usuario a reemplazar no está asignado a este proyecto."));

        boolean hasAssignedTasks = taskRepository.existsByProjectIdProjectAndAssignedUser_IdUserAndActiveTrue(idProject,
                idOldUser);
        boolean hasCreatedTasks = taskRepository.existsByProjectIdProjectAndCreatedBy_IdUserAndActiveTrue(idProject,
                idOldUser);

        if (hasAssignedTasks || hasCreatedTasks) {
            throw new RuntimeException("No se puede cambiar el usuario: tiene tareas activas en este proyecto.");
        }

        // 2. Validar que el nuevo usuario no esté ya asignado
        boolean isAlreadyAssigned = projectUserRepository.existsByProjectIdProjectAndUserIdUserAndActiveTrue(idProject,
                idNewUser);
        if (isAlreadyAssigned) {
            throw new RuntimeException("El nuevo usuario ya está asignado a este proyecto.");
        }

        User newUser = getUserById(idNewUser);
        validateActiveUser(newUser, idNewUser);

        // 3. Efectuar el cambio (Baja lógica del anterior y alta del nuevo)
        oldProjectUser.setActive(false);
        projectUserRepository.save(oldProjectUser);

        ProjectUser newProjectUser = projectUserRepository
                .findByProjectIdProjectAndUserIdUser(idProject, idNewUser)
                .orElse(new ProjectUser());

        newProjectUser.setProject(project);
        newProjectUser.setUser(newUser);
        newProjectUser.setActive(true);
        ProjectUser savedNewUser = projectUserRepository.save(newProjectUser);

        return convertToResponseDTO(savedNewUser, List.of(newUser));
    }

    @Override
    @Transactional
    public void deleteUserAssigned(Integer idProject, Integer idUser, Integer idOwner) {
        if (idProject == null || idUser == null || idOwner == null) {
            throw new RuntimeException(
                    "Debe proporcionar el id del proyecto, el id del usuario y el id del propietario.");
        }

        Project project = getProjectById(idProject);

        // 1. Validar que quien ejecuta sea el owner
        validateProjectOwner(project, idOwner);

        // 2. Buscar la asignación activa
        ProjectUser projectUser = projectUserRepository
                .findByProjectIdProjectAndUserIdUserAndActiveTrue(idProject, idUser)
                .orElseThrow(
                        () -> new RuntimeException("El usuario no está asignado a este proyecto o ya está inactivo."));

        // 3. Validar que el usuario no tenga tareas activas (asignado o creador) en
        // este proyecto
        boolean hasAssignedTasks = taskRepository.existsByProjectIdProjectAndAssignedUser_IdUserAndActiveTrue(idProject,
                idUser);
        boolean hasCreatedTasks = taskRepository.existsByProjectIdProjectAndCreatedBy_IdUserAndActiveTrue(idProject,
                idUser);

        if (hasAssignedTasks || hasCreatedTasks) {
            throw new RuntimeException(
                    "No se puede eliminar la asignación: el usuario tiene tareas activas en este proyecto como creador o asignado.");
        }

        // 4. Baja lógica
        projectUser.setActive(false);
        projectUserRepository.save(projectUser);
    }

    /// Funciones auxiliares
    private ProjectUserResponseDTO convertToResponseDTO(ProjectUser projectUser, List<User> users) {
        return convertToResponseDTO(
                projectUser.getProject(),
                projectUser.getIdProjectUser(),
                users,
                projectUser.getActive());
    }

    /// revisar el idprojectuser
    private ProjectUserResponseDTO convertToResponseDTO(Project project, Integer idProjectUser, List<User> users,
            Boolean active) {
        List<UserResponseDTO> usersAssigned = users.stream()
                .map(user -> new UserResponseDTO(user.getIdUser(), user.getName(), user.getEmail()))
                .toList();

        return new ProjectUserResponseDTO(
                idProjectUser,
                project.getIdProject(),
                project.getNameProject(),
                usersAssigned,
                active);
    }

    private Project getProjectById(Integer idProject) {
        return projectRepository.findById(idProject)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con id: " + idProject));
    }

    private User getUserById(Integer idUser) {
        return userRepository.findByIdUserAndActiveTrue(idUser)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + idUser));
    }

    private void validateActiveUser(User user, Integer idUser) {
        if (!user.getActive()) {
            throw new RuntimeException("El usuario " + user.getName()
                    + " (ID: " + idUser + ") no esta activo y no puede ser asignado.");
        }
    }

    private void validateProjectOwner(Project project, Integer idOwner) {
        if (idOwner == null) {
            throw new RuntimeException("El ID del propietario es obligatorio para realizar esta acción.");
        }
        if (!project.getOwner().getIdUser().equals(idOwner)) {
            throw new RuntimeException("Solo el propietario del proyecto puede agregar usuarios.");
        }
        if (!project.getActive()) {
            throw new RuntimeException("No se pueden agregar usuarios a un proyecto inactivo.");
        }
    }

}
