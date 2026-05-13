package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.StepDTO.StepRequestDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.StepDTO.StepResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Enums.StatusStep;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Enums.StatusTask;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.Step;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.Task;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.StepRepository;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.TaskRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StepService implements IStepService {

    private final StepRepository stepRepository;
    private final TaskRepository taskRepository;

    /// Mostrar todos los pasos de una tarea
    @Override
    public List<StepResponseDTO> getAllStepsByTask(Integer idTask) {
        if (idTask == null) {
            throw new RuntimeException("El ID de la tarea es obligatorio.");
        }

        // Verificamos que la tarea exista y esté activa
        taskRepository.findByIdTaskAndActiveTrue(idTask)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada con id: " + idTask));

        return stepRepository.findByTaskIdTaskAndActiveTrue(idTask).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    /// Guardar un paso
    @Override
    public StepResponseDTO saveStep(StepRequestDTO stepRequestDTO) {
        if (stepRequestDTO.getNameStep() == null || stepRequestDTO.getNameStep().trim().isEmpty()) {
            throw new RuntimeException("El nombre del paso es obligatorio.");
        }
        if (stepRequestDTO.getIdTask() == null) {
            throw new RuntimeException("El ID de la tarea es obligatorio.");
        }
        if (stepRequestDTO.getIdUser() == null) {
            throw new RuntimeException("El ID del usuario es obligatorio.");
        }

        Task task = taskRepository.findByIdTaskAndActiveTrue(stepRequestDTO.getIdTask())
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada con id: " + stepRequestDTO.getIdTask()));

        // 1. Validar que quien crea el paso sea el usuario asignado, el creador de la
        // tarea o el dueño del proyecto
        boolean isOwner = task.getProject().getOwner().getIdUser().equals(stepRequestDTO.getIdUser());
        boolean isCreatedBy = task.getCreatedBy() != null
                && task.getCreatedBy().getIdUser().equals(stepRequestDTO.getIdUser());
        boolean isAssignedUser = task.getAssignedUser() != null
                && task.getAssignedUser().getIdUser().equals(stepRequestDTO.getIdUser());

        if (!isOwner && !isCreatedBy && !isAssignedUser) {
            throw new RuntimeException("El usuario no tiene permisos para agregar pasos a esta tarea.");
        }

        // 2. Validar que la tarea no esté COMPLETADA
        if (task.getStatusTask() == StatusTask.COMPLETADA) {
            throw new RuntimeException("No se pueden agregar pasos a una tarea completada.");
        }

        // 3. Validar que la tarea esté activa
        if (!task.getActive()) {
            throw new RuntimeException("No se pueden agregar pasos a una tarea inactiva.");
        }

        Step step = new Step();
        step.setNameStep(stepRequestDTO.getNameStep());
        step.setTask(task);
        step.setStatusStep(StatusStep.PENDIENTE);
        step.setActive(true);

        return mapToResponseDTO(stepRepository.save(step));
    }

    // public StepResponseDTO updateStatusStep(Integer idStep, StatusStep
    // statusStep) {
    // Step step = stepRepository.findById(idStep)
    // .orElseThrow(() -> new RuntimeException("Paso no encontrado con id: " +
    // idStep));
    // step.setStatusStep(statusStep);
    // return mapToResponseDTO(stepRepository.save(step));
    // }

    /// Actualizar el paso
    @Override
    public StepResponseDTO nextStatusStep(Integer idStep, Integer idUser) {

        if (idStep == null) {
            throw new RuntimeException("El ID del paso es obligatorio.");
        }
        if (idUser == null) {
            throw new RuntimeException("El ID del usuario es obligatorio.");
        }

        Step step = stepRepository.findById(idStep)
                .orElseThrow(() -> new RuntimeException("Paso no encontrado con id: " + idStep));

        // Validar que la tarea esté activa
        if (!step.getTask().getActive()) {
            throw new RuntimeException("No se puede modificar un paso de una tarea inactiva.");
        }

        // Validar que la tarea no esté completada
        if (step.getTask().getStatusTask() == StatusTask.COMPLETADA) {
            throw new RuntimeException("No se puede modificar un paso de una tarea completada.");
        }

        // 3. Validar que quien realiza la acción sea el usuario asignado, el creador de
        // la tarea o el dueño del proyecto
        boolean isOwner = step.getTask().getProject().getOwner().getIdUser().equals(idUser);
        boolean isCreatedBy = step.getTask().getCreatedBy() != null
                && step.getTask().getCreatedBy().getIdUser().equals(idUser);
        boolean isAssignedUser = step.getTask().getAssignedUser() != null
                && step.getTask().getAssignedUser().getIdUser().equals(idUser);

        if (!isOwner && !isCreatedBy && !isAssignedUser) {
            throw new RuntimeException("El usuario no tiene permisos para modificar pasos de esta tarea.");
        }

        switch (step.getStatusStep()) {
            case PENDIENTE:
                // Validar que la tarea esté en estado INICIADA
                if (step.getTask().getStatusTask() != StatusTask.INICIADA) {
                    throw new RuntimeException(
                            "La tarea no está en estado INICIADA, para comenzar este paso la tarea debe estar en estado INICIADA.");
                }
                step.setStatusStep(StatusStep.INICIADO);
                break;
            case INICIADO:
                // Validar que la tarea esté en estado INICIADA
                if (step.getTask().getStatusTask() != StatusTask.INICIADA) {
                    throw new RuntimeException(
                            "La tarea debe estar en estado INICIADA para finalizar este paso.");
                }
                step.setStatusStep(StatusStep.FINALIZADO);
                break;
            case FINALIZADO:
                throw new RuntimeException("El paso ya se encuentra en estado FINALIZADO.");
            default:
                throw new RuntimeException("Estado de paso desconocido.");
        }

        return mapToResponseDTO(stepRepository.save(step));
    }

    // Eliminar un paso
    @Override
    public void deleteStep(Integer idStep) {

        if (idStep == null) {
            throw new RuntimeException("El ID del paso es obligatorio.");
        }

        Step step = stepRepository.findById(idStep)
                .orElseThrow(() -> new RuntimeException("Paso no encontrado con id: " + idStep));

        // Validar que el paso esté activo
        if (!step.getActive()) {
            throw new RuntimeException("El paso ya se encuentra dado de baja.");
        }

        // Validar que la tarea no esté completada
        if (step.getTask().getStatusTask() == StatusTask.COMPLETADA) {
            throw new RuntimeException("No se puede eliminar un paso de una tarea completada.");
        }

        // Validar que el paso no esté finalizado
        if (step.getStatusStep() == StatusStep.FINALIZADO) {
            throw new RuntimeException("No se puede eliminar un paso que ya está finalizado.");
        }

        step.setActive(false);
        stepRepository.save(step);
    }

    /// Actualizar nombre de un paso
    @Override
    public StepResponseDTO updateNameStep(Integer idStep, String nameStep) {
        if (idStep == null) {
            throw new RuntimeException("El ID del paso es obligatorio.");
        }
        if (nameStep == null || nameStep.trim().isEmpty()) {
            throw new RuntimeException("El nombre del paso es obligatorio.");
        }

        Step step = stepRepository.findById(idStep)
                .orElseThrow(() -> new RuntimeException("Paso no encontrado con id: " + idStep));

        // Validar que la tarea esté activa
        if (!step.getTask().getActive()) {
            throw new RuntimeException("No se puede modificar un paso de una tarea inactiva.");
        }

        // Validar que la tarea no esté completada
        if (step.getTask().getStatusTask() == StatusTask.COMPLETADA) {
            throw new RuntimeException("No se puede modificar un paso de una tarea completada.");
        }

        step.setNameStep(nameStep);
        return mapToResponseDTO(stepRepository.save(step));
    }

    /// Funcion auxiliar

    private StepResponseDTO mapToResponseDTO(Step step) {
        return new StepResponseDTO(
                step.getIdStep(),
                step.getNameStep(),
                step.getTask().getIdTask(),
                step.getStatusStep());
    }

}
