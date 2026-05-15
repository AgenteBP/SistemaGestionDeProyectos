package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Services;

import java.util.List;
import org.springframework.stereotype.Service;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectDTO.ProjectRequestDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectDTO.ProjectResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectDTO.ProjectResponseWithTaskDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.TaskDTO.TaskResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.UserDTO.UserResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Enums.StatusTask;
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
public class ProjectService implements IProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final StepRepository stepRepository;
    private final CommentRepository commentRepository;
    private final ProjectUserRepository projectUserRepository;
    private final UserRepository userRepository;

    @Override
    public List<ProjectResponseDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Override
    public ProjectResponseDTO getProjectById(Integer idProject) {
        Project project = projectRepository.findById(idProject)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con id: " + idProject));
        return convertToResponseDTO(project);
    }

    @Override
    public Project findProjectById(Integer idProject) {
        return projectRepository.findById(idProject)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con id: " + idProject));
    }

    /// Obtener proyecto con tareas con filtros
    @Override
    public ProjectResponseWithTaskDTO getProjectByIdWithTask(Integer idProject, String nameTask,
            StatusTask statusTask) {
        Project project = findProjectById(idProject);

        if (!project.getActive()) {
            throw new RuntimeException("El proyecto se encuentra inactivo.");
        }

        List<Task> tasks = taskRepository.findByProjectIdProjectAndActiveTrue(idProject);

        // Filtro por nombre (insensible a mayúsculas/minúsculas)
        if (nameTask != null && !nameTask.trim().isEmpty()) {
            String filter = nameTask.toLowerCase();
            tasks = tasks.stream()
                    .filter(t -> t.getNameTask().toLowerCase().contains(filter))
                    .toList();
        }

        // Filtro por estado
        if (statusTask != null) {
            tasks = tasks.stream()
                    .filter(t -> t.getStatusTask() == statusTask)
                    .toList();
        }

        List<TaskResponseDTO> taskDTOs = tasks.stream()
                .map(this::mapTaskToResponseDTO)
                .toList();

        return ProjectResponseWithTaskDTO.builder()
                .idProject(project.getIdProject())
                .nameProject(project.getNameProject())
                .descriptionProject(project.getDescription())
                .idUserOwner(project.getOwner().getIdUser())
                .nameUserOwner(project.getOwner().getName())
                .tasks(taskDTOs)
                .build();
    }

    private TaskResponseDTO mapTaskToResponseDTO(Task task) {
        UserResponseDTO createdBy = task.getCreatedBy() != null
                ? new UserResponseDTO(task.getCreatedBy().getIdUser(), task.getCreatedBy().getName(),
                        task.getCreatedBy().getEmail())
                : null;

        UserResponseDTO assignedUser = task.getAssignedUser() != null
                ? new UserResponseDTO(task.getAssignedUser().getIdUser(), task.getAssignedUser().getName(),
                        task.getAssignedUser().getEmail())
                : null;

        return new TaskResponseDTO(
                task.getIdTask(),
                task.getNameTask(),
                task.getDescription(),
                task.getStartDate(),
                task.getEndDate(),
                task.getStatusTask(),
                createdBy,
                assignedUser,
                task.getProject().getIdProject(),
                task.getProject().getNameProject(),
                task.getActive());
    }

    @Transactional
    @Override
    public ProjectResponseDTO insertProject(ProjectRequestDTO projectDto) {

        // 1. Validaciones de campos obligatorios
        if (projectDto.getNameProject() == null || projectDto.getNameProject().trim().isEmpty()) {
            throw new RuntimeException("El nombre del proyecto es obligatorio.");
        }

        if (projectDto.getIdOwner() == null) {
            throw new RuntimeException("El ID del propietario es obligatorio.");
        }

        // 2. Control del Propietario (Owner)
        User owner = userRepository.findByIdUserAndActiveTrue(projectDto.getIdOwner())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + projectDto.getIdOwner()));
        if (!owner.getActive()) {
            throw new RuntimeException("El usuario owner seleccionado no se encuentra activo.");
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
    @Override
    public ProjectResponseDTO updateProject(Integer idProject, ProjectRequestDTO projectRequestDTO) {
        if (idProject == null) {
            throw new RuntimeException("El ID del proyecto es obligatorio.");
        }

        if (projectRequestDTO.getIdOwner() == null) {
            throw new RuntimeException("El ID del propietario es obligatorio para actualizar el proyecto.");
        }

        Project project = projectRepository.findById(idProject)
                .orElseThrow(() -> new RuntimeException(
                        "Proyecto no encontrado con id: " + idProject));

        if (!project.getActive()) {
            throw new RuntimeException("No se puede modificar un proyecto inactivo.");
        }

        if (!project.getOwner().getIdUser().equals(projectRequestDTO.getIdOwner())) {
            throw new RuntimeException("Solo el propietario del proyecto puede actualizarlo.");
        }

        if (projectRequestDTO.getNameProject() != null && !projectRequestDTO.getNameProject().trim().isEmpty()
                && !project.getNameProject().equalsIgnoreCase(projectRequestDTO.getNameProject())) {
            boolean nameProjectExists = projectRepository
                    .existsByNameProjectIgnoreCase(projectRequestDTO.getNameProject());
            if (nameProjectExists) {
                throw new RuntimeException(
                        "Ya existe un proyecto con el nombre: " + projectRequestDTO.getNameProject());
            }

            project.setNameProject(projectRequestDTO.getNameProject());
        }

        if (projectRequestDTO.getDescription() != null && !projectRequestDTO.getDescription().trim().isEmpty()) {
            project.setDescription(projectRequestDTO.getDescription());
        }

        Project updatedProject = projectRepository.save(project);
        return convertToResponseDTO(updatedProject);
    }

    @Transactional
    @Override
    public void deleteProject(Integer idProject, Integer idUser) {

        if (idProject == null) {
            throw new RuntimeException("El ID del proyecto es obligatorio.");
        }

        if (idUser == null) {
            throw new RuntimeException("El ID del usuario es obligatorio.");
        }

        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + idUser));
        if (!user.getActive()) {
            throw new RuntimeException("El usuario no se encuentra activo.");
        }

        Project project = projectRepository.findById(idProject)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con id: " + idProject));

        if (!project.getActive()) {
            throw new RuntimeException("El proyecto ya se encuentra inactivo.");
        }

        // 1. Validar que el usuario que intenta eliminar sea el owner
        if (!project.getOwner().getIdUser().equals(idUser)) {
            throw new RuntimeException("Solo el propietario del proyecto puede eliminarlo.");
        }

        // 2. Bajas lógicas en cascada

        // Bajas de ProjectUsers
        List<ProjectUser> projectUsers = projectUserRepository.findByProjectIdProjectAndActiveTrue(idProject);
        projectUsers.forEach(pu -> {
            pu.setActive(false);
            projectUserRepository.save(pu);
        });

        // Bajas de Comentarios (de todas las tareas del proyecto)
        List<Comment> comments = commentRepository.findByTaskProjectIdProjectAndActiveTrue(idProject);
        comments.forEach(c -> {
            c.setActive(false);
            commentRepository.save(c);
        });

        // Bajas de Pasos (de todas las tareas del proyecto)
        List<Step> steps = stepRepository.findByTaskProjectIdProjectAndActiveTrue(idProject);
        steps.forEach(s -> {
            s.setActive(false);
            stepRepository.save(s);
        });

        // Bajas de Tareas
        List<Task> tasks = taskRepository.findByProjectIdProjectAndActiveTrue(idProject);
        tasks.forEach(t -> {
            t.setActive(false);
            taskRepository.save(t);
        });

        // Baja del Proyecto
        project.setActive(false);
        projectRepository.save(project);

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
                assignedUsersDTO,
                project.getActive());
    }

    private UserResponseDTO convertToUserResponseDTO(User user) {
        if (user == null)
            return null;
        return new UserResponseDTO(
                user.getIdUser(),
                user.getName(),
                user.getEmail());
    }

    @Override
    public boolean hasActiveProjectsAsOwner(User user) {
        return projectRepository.existsByOwnerAndActiveTrue(user);
    }

    private void saveAssignedUsers(Project project, List<Integer> idUsers) {
        List<ProjectUser> projectUsers = idUsers.stream()
                .map(idUser -> {
                    User user = userRepository.findByIdUserAndActiveTrue(idUser)
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
