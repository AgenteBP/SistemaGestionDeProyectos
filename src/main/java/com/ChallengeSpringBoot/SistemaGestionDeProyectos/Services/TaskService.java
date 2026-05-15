package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Services;

import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.TaskDTO.TaskRequestDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.TaskDTO.TaskRequestNextStatusDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.TaskDTO.TaskRequestUpdateDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.TaskDTO.TaskResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.UserDTO.UserResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Enums.StatusStep;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Enums.StatusTask;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.Comment;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.Project;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.Step;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.Task;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.User;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.CommentRepository;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.ProjectRepository;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.ProjectUserRepository;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.StepRepository;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.TaskRepository;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService implements ITaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final ProjectUserRepository projectUserRepository;
    private final UserRepository userRepository;
    private final StepRepository stepRepository;
    private final CommentRepository commentRepository;

    @Override
    public TaskResponseDTO findTaskById(Integer idTask) {
        Task task = taskRepository.findByIdTaskAndActiveTrue(idTask)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada con id: " + idTask));
        return mapToResponseDTO(task);
    }

    public Task getTaskById(Integer idTask) {
        return taskRepository.findByIdTaskAndActiveTrue(idTask)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada con id: " + idTask));
    }

    @Override
    public List<TaskResponseDTO> findTaskWithNameOrStatus(String nameTask, StatusTask statusTask) {
        List<Task> tasks = taskRepository.findByActiveTrue();

        // Filtro por nombre (parcial, sin distinción de mayúsculas)
        if (nameTask != null && !nameTask.trim().isEmpty()) {
            String filter = nameTask.trim().toLowerCase();
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

        return tasks.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    public List<TaskResponseDTO> findTaskByDateRange(
            LocalDate startDateFrom, LocalDate startDateTo,
            LocalDate endDateFrom, LocalDate endDateTo) {

        // Validar que "desde" no sea posterior a "hasta" en cada rango
        if (startDateFrom != null && startDateTo != null && startDateFrom.isAfter(startDateTo)) {
            throw new RuntimeException(
                    "La fecha de inicio 'desde' no puede ser posterior a la fecha de inicio 'hasta'.");
        }
        if (endDateFrom != null && endDateTo != null && endDateFrom.isAfter(endDateTo)) {
            throw new RuntimeException(
                    "La fecha de fin 'desde' no puede ser posterior a la fecha de fin 'hasta'.");
        }

        List<Task> tasks = taskRepository.findByActiveTrue();

        // --- Filtros de fecha de inicio ---
        // Si se filtra por startDate, las tareas sin fecha (PENDIENTE) quedan excluidas
        boolean filteringByStartDate = startDateFrom != null || startDateTo != null;
        if (filteringByStartDate) {
            tasks = tasks.stream()
                    .filter(t -> t.getStartDate() != null)
                    .toList();
            if (startDateFrom != null) {
                LocalDate from = startDateFrom;
                tasks = tasks.stream()
                        .filter(t -> !t.getStartDate().isBefore(from))
                        .toList();
            }
            if (startDateTo != null) {
                LocalDate to = startDateTo;
                tasks = tasks.stream()
                        .filter(t -> !t.getStartDate().isAfter(to))
                        .toList();
            }
        }

        // --- Filtros de fecha de fin ---
        // Si se filtra por endDate, las tareas sin fecha de fin (no COMPLETADA) quedan
        // excluidas
        boolean filteringByEndDate = endDateFrom != null || endDateTo != null;
        if (filteringByEndDate) {
            tasks = tasks.stream()
                    .filter(t -> t.getEndDate() != null)
                    .toList();
            if (endDateFrom != null) {
                LocalDate from = endDateFrom;
                tasks = tasks.stream()
                        .filter(t -> !t.getEndDate().isBefore(from))
                        .toList();
            }
            if (endDateTo != null) {
                LocalDate to = endDateTo;
                tasks = tasks.stream()
                        .filter(t -> !t.getEndDate().isAfter(to))
                        .toList();
            }
        }

        return tasks.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Transactional
    @Override
    public TaskResponseDTO saveTask(TaskRequestDTO taskRequestDTO) {
        validateTaskCreateRequest(taskRequestDTO);

        Project project = projectRepository.findByIdProjectAndActiveTrue(taskRequestDTO.getIdProject())
                .orElseThrow(() -> new RuntimeException(
                        "Proyecto no encontrado con id: " + taskRequestDTO.getIdProject()));

        User createdBy = findActiveUserById(taskRequestDTO.getIdCreatedBy());
        User assignedUser = findActiveUserById(taskRequestDTO.getIdAssignedUser());

        validateUserBelongsToProject(project, createdBy, "creador");
        validateUserBelongsToProject(project, assignedUser, "asignado");
        validateDifferentUsers(createdBy, assignedUser);

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
    @Override
    public TaskResponseDTO nextStatusTask(TaskRequestNextStatusDTO taskrequestNextStatusDTO) {
        Integer idTask = taskrequestNextStatusDTO.getIdTask();
        Integer idUser = taskrequestNextStatusDTO.getIdUser();

        if (idTask == null) {
            throw new RuntimeException("El ID de la tarea es obligatorio.");
        }

        Task task = getTaskById(idTask);

        if (idUser == null) {
            throw new RuntimeException("El ID del usuario es obligatorio.");
        }

        // Validar que el usuario sea el creador o el asignado
        boolean isCreator = task.getCreatedBy() != null && task.getCreatedBy().getIdUser().equals(idUser);
        boolean isAssigned = task.getAssignedUser() != null && task.getAssignedUser().getIdUser().equals(idUser);

        if (!isCreator && !isAssigned) {
            throw new RuntimeException("El usuario no tiene permisos para cambiar el estado de esta tarea.");
        }

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
                if (taskrequestNextStatusDTO.getDateTask() != null) {
                    task.setStartDate(taskrequestNextStatusDTO.getDateTask());
                } else {
                    throw new RuntimeException("La fecha de inicio es obligatoria.");
                }
                break;

            case INICIADA:
                validateTaskCanBeCompleted(task);
                task.setStatusTask(StatusTask.COMPLETADA);
                if (taskrequestNextStatusDTO.getDateTask() != null) {
                    if (taskrequestNextStatusDTO.getDateTask().isBefore(task.getStartDate())) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        String startDateStr = task.getStartDate().format(formatter);
                        throw new RuntimeException(
                                "La fecha de finalización no puede ser anterior a la fecha de inicio ("
                                        + startDateStr + ").");
                    }
                    task.setEndDate(taskrequestNextStatusDTO.getDateTask());
                } else {
                    throw new RuntimeException("La fecha de fin es obligatoria.");
                }
                break;

            case COMPLETADA:
                throw new RuntimeException("La tarea ya se encuentra en estado COMPLETADA.");

            default:
                throw new RuntimeException("Estado de tarea desconocido.");
        }

        return mapToResponseDTO(taskRepository.save(task));
    }

    @Transactional
    @Override
    public TaskResponseDTO updateTask(Integer idTask, TaskRequestUpdateDTO taskRequestUpdateDTO) {
        Integer idUser = taskRequestUpdateDTO.getIdUser();

        if (idUser == null) {
            throw new RuntimeException("El ID del usuario es obligatorio para realizar la actualización.");
        }

        if (idTask == null) {
            throw new RuntimeException("El ID de la tarea es obligatorio.");
        }

        Task task = getTaskById(idTask);

        boolean isCreator = task.getCreatedBy().getIdUser().equals(idUser);
        boolean isOwner = task.getProject().getOwner().getIdUser().equals(idUser);

        // Solo el creador de la tarea puede modificarla
        if (!isCreator && !isOwner) {
            throw new RuntimeException(
                    "Solo el creador de la tarea o el dueño del proyecto tiene permisos para modificar sus datos.");
        }

        if (!task.getActive()) {
            throw new RuntimeException("No se puede modificar una tarea inactiva.");
        }

        if (task.getProject() == null || !task.getProject().getActive()) {
            throw new RuntimeException("No se puede modificar una tarea de un proyecto inactivo.");
        }

        // Actualizar datos básicos
        if (taskRequestUpdateDTO.getNameTask() != null && !taskRequestUpdateDTO.getNameTask().trim().isEmpty()) {
            // Verificar nombre duplicado en el mismo proyecto, excluyendo la tarea actual
            if (!task.getNameTask().equalsIgnoreCase(taskRequestUpdateDTO.getNameTask()) &&
                    taskRepository.existsByNameTaskIgnoreCaseAndProjectIdProjectAndActiveTrueAndIdTaskNot(
                            taskRequestUpdateDTO.getNameTask(),
                            task.getProject().getIdProject(),
                            task.getIdTask())) {
                throw new RuntimeException(
                        "Ya existe una tarea activa con el nombre '" + taskRequestUpdateDTO.getNameTask()
                                + "' en este proyecto.");
            }
            task.setNameTask(taskRequestUpdateDTO.getNameTask());
        }
        task.setDescription(taskRequestUpdateDTO.getDescription());

        return mapToResponseDTO(taskRepository.save(task));
    }

    @Transactional
    @Override
    public void deleteTask(Integer idTask, Integer idUser) {

        if (idTask == null) {
            throw new RuntimeException("El ID de la tarea es obligatorio.");
        }

        if (idUser == null) {
            throw new RuntimeException("El ID del usuario es obligatorio.");
        }

        Task task = taskRepository.findById(idTask)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada con id: " + idTask));

        boolean isCreator = task.getCreatedBy().getIdUser().equals(idUser);
        boolean isOwner = task.getProject().getOwner().getIdUser().equals(idUser);

        // Solo el creador de la tarea puede modificarla
        if (!isCreator && !isOwner) {
            throw new RuntimeException(
                    "Solo el creador de la tarea o el dueño del proyecto tiene permisos para modificar sus datos.");
        }

        if (!task.getActive()) {
            throw new RuntimeException("La tarea ya se encuentra inactiva.");
        }

        // Baja lógica de la tarea
        task.setActive(false);
        taskRepository.save(task);

        // Baja lógica de los steps relacionados
        List<Step> steps = stepRepository.findByTaskIdTaskAndActiveTrue(idTask);
        steps.forEach(step -> {
            step.setActive(false);
            stepRepository.save(step);
        });

        // Baja lógica de las comunicaciones (comentarios) asociadas
        List<Comment> comments = commentRepository.findByTaskIdTaskAndActiveTrue(idTask);
        comments.forEach(comment -> {
            comment.setActive(false);
            commentRepository.save(comment);
        });
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
                task.getProject().getNameProject(),
                task.getActive());
    }

    @Override
    public boolean hasActiveTasksAssigned(User user) {
        return taskRepository.existsByAssignedUserAndActiveTrue(user);
    }

    /// validador de request
    private void validateTaskCreateRequest(TaskRequestDTO taskRequestDTO) {
        validateTaskUpdateRequest(taskRequestDTO);

        if (taskRequestDTO.getIdProject() == null) {
            throw new RuntimeException("El ID del proyecto es obligatorio.");
        }

        if (taskRequestDTO.getIdCreatedBy() == null) {
            throw new RuntimeException("El ID del usuario creador es obligatorio.");
        }

        if (taskRequestDTO.getIdAssignedUser() == null) {
            throw new RuntimeException("El ID del usuario asignado es obligatorio.");
        }

        if (taskRequestDTO.getNameTask() == null || taskRequestDTO.getNameTask().trim().isEmpty()) {
            throw new RuntimeException("El nombre de la tarea es obligatorio.");
        }

        // Verificar nombre duplicado dentro del proyecto
        if (taskRepository.existsByNameTaskIgnoreCaseAndProjectIdProjectAndActiveTrue(
                taskRequestDTO.getNameTask(), taskRequestDTO.getIdProject())) {
            throw new RuntimeException(
                    "Ya existe una tarea activa con el nombre '" + taskRequestDTO.getNameTask()
                            + "' en este proyecto.");
        }
    }

    private void validateTaskUpdateRequest(TaskRequestDTO taskRequestDTO) {
        if (taskRequestDTO.getNameTask() == null || taskRequestDTO.getNameTask().trim().isEmpty()) {
            throw new RuntimeException("El nombre de la tarea es obligatorio.");
        }
    }

    private void validateDifferentUsers(User createdBy, User assignedUser) {
        if (createdBy.getIdUser().equals(assignedUser.getIdUser())) {
            throw new RuntimeException("El usuario creador y el usuario asignado no pueden ser el mismo.");
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
        return userRepository.findByIdUserAndActiveTrue(idUser)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + idUser));
    }

}
