package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Services;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.TaskDTO.TaskRequestDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.TaskDTO.TaskResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.UserDTO.UserResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Enums.StatusStep;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Enums.StatusTask;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.Project;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.Step;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.Task;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.User;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.ProjectRepository;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.ProjectUserRepository;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.StepRepository;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.TaskRepository;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final ProjectUserRepository projectUserRepository;
    private final UserRepository userRepository;
    private final StepRepository stepRepository;

    public TaskResponseDTO findTaskById(Integer idTask) {
        Task task = taskRepository.findByIdTaskAndActiveTrue(idTask)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada con id: " + idTask));
        return mapToResponseDTO(task);
    }

    public Task getTaskById(Integer idTask) {
        return taskRepository.findByIdTaskAndActiveTrue(idTask)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada con id: " + idTask));
    }

    /// revisar
    // public java.util.List<TaskResponseDTO> getTasksByProjectId(Integer idProject)
    /// {
    // return taskRepository.findByProjectIdProject(idProject).stream()
    // .map(this::mapToResponseDTO)
    // .toList();
    // }

    /// Creacion de tarea
    @Transactional
    public TaskResponseDTO saveTask(TaskRequestDTO taskRequestDTO) {
        validateTaskRequest(taskRequestDTO);

        Project project = projectRepository.findByIdProjectAndActiveTrue(taskRequestDTO.getIdProject())
                .orElseThrow(() -> new RuntimeException(
                        "Proyecto no encontrado con id: " + taskRequestDTO.getIdProject()));

        User createdBy = findActiveUserById(taskRequestDTO.getIdCreatedBy());
        User assignedUser = findActiveUserById(taskRequestDTO.getIdAssignedUser());

        validateUserBelongsToProject(project, createdBy, "creador");
        validateUserBelongsToProject(project, assignedUser, "asignado");

        Task task = new Task();
        task.setNameTask(taskRequestDTO.getNameTask());
        task.setDescription(taskRequestDTO.getDescription());
        task.setStatusTask(StatusTask.PENDIENTE);
        task.setCreatedBy(createdBy);
        task.setAssignedUser(assignedUser);
        task.setProject(project);
        task.setActive(true);

        return mapToResponseDTO(taskRepository.save(task));
    }

    @Transactional
    public TaskResponseDTO nextStatusTask(Integer idTask) {
        return nextStatusTask(idTask, new Date());
    }

    @Transactional
    public TaskResponseDTO nextStatusTask(Integer idTask, Date statusDate) {
        if (idTask == null) {
            throw new RuntimeException("El ID de la tarea es obligatorio.");
        }

        if (statusDate == null) {
            throw new RuntimeException("La fecha de cambio de estado es obligatoria.");
        }

        Task task = getTaskById(idTask);

        if (!task.getActive()) {
            throw new RuntimeException("No se puede modificar una tarea inactiva.");
        }

        if (task.getProject() == null || !task.getProject().getActive()) {
            throw new RuntimeException("No se puede modificar una tarea de un proyecto inactivo.");
        }

        if (task.getStatusTask() == null) {
            throw new RuntimeException("La tarea no tiene un estado valido.");
        }

        switch (task.getStatusTask()) {
            case PENDIENTE:
                task.setStatusTask(StatusTask.INICIADA);
                task.setStartDate(statusDate);
                break;

            case INICIADA:
                validateTaskCanBeCompleted(task);
                task.setStatusTask(StatusTask.COMPLETADA);
                task.setEndDate(statusDate);
                break;

            case COMPLETADA:
                throw new RuntimeException("La tarea ya se encuentra en estado COMPLETADA.");

            default:
                throw new RuntimeException("Estado de tarea desconocido.");
        }

        return mapToResponseDTO(taskRepository.save(task));
    }

    /// Funciones auxiliares

    private TaskResponseDTO mapToResponseDTO(Task task) {
        UserResponseDTO createdBy = null;
        if (task.getCreatedBy() != null) {
            createdBy = new UserResponseDTO(
                    task.getCreatedBy().getIdUser(),
                    task.getCreatedBy().getName(),
                    task.getCreatedBy().getEmail());
        }

        UserResponseDTO assignedUser = null;
        if (task.getAssignedUser() != null) {
            assignedUser = new UserResponseDTO(
                    task.getAssignedUser().getIdUser(),
                    task.getAssignedUser().getName(),
                    task.getAssignedUser().getEmail());
        }

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
                task.getProject().getNameProject());
    }

    public boolean hasActiveTasksAssigned(User user) {
        return taskRepository.existsByAssignedUserAndActiveTrue(user);
    }

    /// validador de request
    private void validateTaskRequest(TaskRequestDTO taskRequestDTO) {
        if (taskRequestDTO.getNameTask() == null || taskRequestDTO.getNameTask().trim().isEmpty()) {
            throw new RuntimeException("El nombre de la tarea es obligatorio.");
        }

        if (taskRequestDTO.getIdProject() == null) {
            throw new RuntimeException("El ID del proyecto es obligatorio.");
        }

        if (taskRequestDTO.getIdCreatedBy() == null) {
            throw new RuntimeException("El ID del usuario creador es obligatorio.");
        }

        if (taskRequestDTO.getIdAssignedUser() == null) {
            throw new RuntimeException("El ID del usuario asignado es obligatorio.");
        }
    }

    private void validateUserBelongsToProject(Project project, User user, String role) {
        boolean isOwner = project.getOwner() != null && project.getOwner().getIdUser().equals(user.getIdUser());
        boolean isAssignedToProject = projectUserRepository
                .existsByProjectIdProjectAndUserIdUserAndActiveTrue(project.getIdProject(), user.getIdUser());

        if (!isOwner && !isAssignedToProject) {
            throw new RuntimeException("El usuario " + role + " debe pertenecer al proyecto.");
        }
    }

    private void validateTaskCanBeCompleted(Task task) {
        List<Step> activeSteps = stepRepository.findByTaskIdTaskAndActiveTrue(task.getIdTask());

        boolean hasUnfinishedSteps = activeSteps.stream()
                .anyMatch(step -> step.getStatusStep() != StatusStep.FINALIZADO);

        if (hasUnfinishedSteps) {
            throw new RuntimeException(
                    "La tarea no puede pasar a COMPLETADA porque tiene pasos pendientes o iniciados.");
        }
    }

    private User findActiveUserById(Integer idUser) {
        return userRepository.findByIdAndActiveTrue(idUser)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + idUser));
    }

}
