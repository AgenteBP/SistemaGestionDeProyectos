package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectUserDTO.ProjectUserResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.UserDTO.UserResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.Project;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.ProjectUser;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.User;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.ProjectRepository;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.ProjectUserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectUserService {

    private final ProjectUserRepository projectUserRepository;
    private final ProjectRepository projectRepository;
    private final UserService userService;

    public List<ProjectUser> getProjectUsersByProject(Integer idProject) {
        Project project = getProjectById(idProject);
        return projectUserRepository.findByProject(project);
    }

    public List<ProjectUser> getProjectUsersByUser(Integer idUser) {
        User user = userService.findUserById(idUser);
        return projectUserRepository.findByUser(user);
    }

    public boolean existsByProjectAndUser(Integer idProject, Integer idUser) {
        Project project = getProjectById(idProject);
        User user = userService.findUserById(idUser);
        return projectUserRepository.existsByProjectAndUser(project, user);
    }

    public ProjectUserResponseDTO saveProjectUser(Integer idProject, Integer idUser) {
        Project project = getProjectById(idProject);
        User user = userService.findUserById(idUser);
        validateActiveUser(user, idUser);

        ProjectUser projectUser = new ProjectUser();
        projectUser.setProject(project);
        projectUser.setUser(user);
        ProjectUser savedProjectUser = projectUserRepository.save(projectUser);

        return convertToResponseDTO(savedProjectUser, List.of(user));
    }

    public ProjectUserResponseDTO saveAllProjectUsers(Integer idProject, List<Integer> idUsers) {
        Project project = getProjectById(idProject);
        List<User> users = idUsers.stream()
                .map(userService::findUserById)
                .toList();
        users.forEach(user -> validateActiveUser(user, user.getIdUser()));

        List<ProjectUser> projectUsers = users.stream()
                .map(user -> {
                    ProjectUser projectUser = new ProjectUser();
                    projectUser.setProject(project);
                    projectUser.setUser(user);
                    return projectUser;
                })
                .toList();
        List<ProjectUser> savedProjectUsers = projectUserRepository.saveAll(projectUsers);

        Integer idProjectUser = savedProjectUsers.size() == 1
                ? savedProjectUsers.get(0).getIdProjectUser()
                : null;

        return convertToResponseDTO(project, idProjectUser, users, true);
    }

    // public void deleteProjectUser(Project project, User user) {
    // ProjectUser projectUser = projectUserRepository.findByProjectAndUser(project,
    // user)
    // .orElseThrow(() -> new RuntimeException("La asignación de usuario no
    // existe."));
    // projectUserRepository.delete(projectUser);
    // }

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
                .map(user -> new UserResponseDTO(null, user.getName(), user.getEmail()))
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

    private void validateActiveUser(User user, Integer idUser) {
        if (!user.getActive()) {
            throw new RuntimeException("El usuario " + user.getName()
                    + " (ID: " + idUser + ") no esta activo y no puede ser asignado.");
        }
    }

}
