package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Services;

import java.util.List;
import org.springframework.stereotype.Service;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectDTO.ProjectRequestDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectDTO.ProjectResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.UserDTO.UserResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.Project;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.ProjectUser;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.User;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.Task;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.Step;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.Comment;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.ProjectRepository;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.TaskRepository;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.StepRepository;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.CommentRepository;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.ProjectUserRepository;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final StepRepository stepRepository;
    private final CommentRepository commentRepository;
    private final ProjectUserRepository projectUserRepository;
    private final UserRepository userRepository;

    public List<ProjectResponseDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    public ProjectResponseDTO getProjectById(Integer idProject) {
        Project project = projectRepository.findById(idProject)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con id: " + idProject));
        return convertToResponseDTO(project);
    }

    public Project findProjectById(Integer idProject) {
        return projectRepository.findById(idProject)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con id: " + idProject));
    }

    // @Transactional
    // public String deleteProject(Integer idProject) {
    // try {
    // Project project = projectRepository.findById(idProject)
    // .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con id: " +
    // idProject));

    // if (Boolean.FALSE.equals(project.getActive())) {
    // throw new RuntimeException("El proyecto con id " + idProject + " ya se
    // encuentra dado de baja.");
    // }

    // List<Task> tasks = taskRepository.findByProjectIdProject(idProject);

    // if (!tasks.isEmpty()) {
    // List<Comment> comments = commentRepository.findByTaskIn(tasks);
    // comments.forEach(comment -> comment.setActive(false));
    // commentRepository.saveAll(comments);

    // List<Step> steps = stepRepository.findByTaskIn(tasks);
    // steps.forEach(step -> step.setActive(false));
    // stepRepository.saveAll(steps);

    // tasks.forEach(task -> task.setActive(false));
    // taskRepository.saveAll(tasks);
    // }

    // project.setActive(false);
    // projectRepository.save(project);

    // return "Proyecto dado de baja correctamente junto con sus tareas, pasos y
    // comentarios asociados.";
    // } catch (RuntimeException exception) {
    // throw exception;
    // } catch (Exception exception) {
    // throw new RuntimeException("No fue posible dar de baja el proyecto con id: "
    // + idProject, exception);
    // }
    // }

    @Transactional
    public ProjectResponseDTO insertProject(ProjectRequestDTO projectDto) {

        // 1. Validaciones de campos obligatorios
        if (projectDto.getNameProject() == null || projectDto.getNameProject().trim().isEmpty()) {
            throw new RuntimeException("El nombre del proyecto es obligatorio.");
        }

        if (projectDto.getIdOwner() == null) {
            throw new RuntimeException("El ID del propietario es obligatorio.");
        }

        // 2. Control del Propietario (Owner)
        User owner = userRepository.findByIdAndActiveTrue(projectDto.getIdOwner())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + projectDto.getIdOwner()));
        if (!owner.getActive()) {
            throw new RuntimeException("El propietario seleccionado no se encuentra activo.");
        }

        // 3. Guardar el proyecto primero (necesitamos el ID generado)
        Project project = new Project();
        project.setNameProject(projectDto.getNameProject());
        project.setDescription(projectDto.getDescription());
        project.setOwner(owner);
        project.setActive(true);
        Project savedProject = projectRepository.save(project);

        // 4. Control y asignación de usuarios
        if (projectDto.getUserAssigned() != null && !projectDto.getUserAssigned().isEmpty()) {
            saveAssignedUsers(savedProject, projectDto.getUserAssigned());
        }
        return convertToResponseDTO(savedProject);
    }

    /// Actualizacion del proyecto solo campos name y description
    public ProjectResponseDTO updateProject(ProjectRequestDTO projectRequestDTO) {
        Project project = projectRepository.findById(projectRequestDTO.getIdProject())
                .orElseThrow(() -> new RuntimeException(
                        "Proyecto no encontrado con id: " + projectRequestDTO.getIdProject()));

        if (!project.getNameProject().equalsIgnoreCase(projectRequestDTO.getNameProject())) {
            boolean nameProjectExists = projectRepository
                    .existsByNameProjectIgnoreCase(projectRequestDTO.getNameProject());
            if (nameProjectExists) {
                throw new RuntimeException(
                        "Ya existe un proyecto con el nombre: " + projectRequestDTO.getNameProject());
            }
        }

        project.setNameProject(projectRequestDTO.getNameProject());
        project.setDescription(projectRequestDTO.getDescription());

        Project updatedProject = projectRepository.save(project);
        return convertToResponseDTO(updatedProject);
    }

    // Funciones auxiliares
    private ProjectResponseDTO convertToResponseDTO(Project project) {
        UserResponseDTO ownerDTO = convertToUserResponseDTO(project.getOwner());

        // Obtengo los usuarios asignados con ProjectUserService
        List<UserResponseDTO> assignedUsersDTO = projectUserRepository.findByProject(project)
                .stream()
                .map(projectUser -> convertToUserResponseDTO(projectUser.getUser()))
                .toList();

        return new ProjectResponseDTO(
                project.getIdProject(),
                project.getNameProject(),
                project.getDescription(),
                ownerDTO,
                assignedUsersDTO);
    }

    private UserResponseDTO convertToUserResponseDTO(User user) {
        if (user == null)
            return null;
        return new UserResponseDTO(
                null,
                user.getName(),
                user.getEmail());
    }

    public boolean hasActiveProjectsAsOwner(User user) {
        return projectRepository.existsByOwnerAndActiveTrue(user);
    }

    private void saveAssignedUsers(Project project, List<Integer> idUsers) {
        List<ProjectUser> projectUsers = idUsers.stream()
                .map(idUser -> {
                    User user = userRepository.findByIdAndActiveTrue(idUser)
                            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + idUser));

                    ProjectUser projectUser = new ProjectUser();
                    projectUser.setProject(project);
                    projectUser.setUser(user);
                    projectUser.setActive(true);
                    return projectUser;
                })
                .toList();

        projectUserRepository.saveAll(projectUsers);
    }
}
