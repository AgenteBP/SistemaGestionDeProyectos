package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Controller;

import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.TaskDTO.TaskResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Services.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // ==========================
    // --- OBTENER TAREAS ---
    // ==========================

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Integer id) {
        TaskResponseDTO taskDTO = taskService.findTaskById(id);
        return ResponseEntity.ok(taskDTO);
    }

    // @GetMapping("/project/{projectId}")
    // public ResponseEntity<List<TaskResponseDTO>> getTasksByProject(@PathVariable
    // Integer projectId) {
    // List<TaskResponseDTO> tasks = taskService.getTasksByProjectId(projectId);
    // return ResponseEntity.ok(tasks);
    // }

}
