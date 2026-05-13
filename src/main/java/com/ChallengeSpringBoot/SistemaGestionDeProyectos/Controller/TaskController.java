package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Controller;

import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.TaskDTO.TaskRequestDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.TaskDTO.TaskRequestNextStatusDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.TaskDTO.TaskRequestUpdateDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.TaskDTO.TaskResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Services.ITaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final ITaskService taskService;

    // ==========================
    // --- OBTENER TAREAS ---
    // ==========================

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Integer id) {
        TaskResponseDTO taskDTO = taskService.findTaskById(id);
        return ResponseEntity.ok(taskDTO);
    }

    // ==========================
    // --- CREAR TAREA ---
    // ==========================

    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@RequestBody TaskRequestDTO taskRequestDTO) {
        TaskResponseDTO createdTask = taskService.saveTask(taskRequestDTO);
        return ResponseEntity.ok(createdTask);
    }

    // ==========================
    // --- ACTUALIZAR TAREA ---
    // ==========================

    @PutMapping("/update")
    public ResponseEntity<TaskResponseDTO> updateTask(@RequestBody TaskRequestUpdateDTO taskRequestUpdateDTO) {
        TaskResponseDTO updatedTask = taskService.updateTask(taskRequestUpdateDTO);
        return ResponseEntity.ok(updatedTask);
    }

    // ==========================
    // --- CAMBIAR ESTADO ---
    // ==========================

    @PutMapping("/next-status")
    public ResponseEntity<TaskResponseDTO> nextStatusTask(
            @RequestBody TaskRequestNextStatusDTO taskrequestNextStatusDTO) {
        TaskResponseDTO updatedTask = taskService.nextStatusTask(taskrequestNextStatusDTO);
        return ResponseEntity.ok(updatedTask);
    }

    // ==========================
    // --- ELIMINAR TAREA ---
    // ==========================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Integer id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
