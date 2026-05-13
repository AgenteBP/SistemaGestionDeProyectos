package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Controller;

import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectDTO.ProjectRequestDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectDTO.ProjectResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectDTO.ProjectResponseWithTaskDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Enums.StatusTask;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Services.IProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final IProjectService projectService;

    // ==========================
    // --- OBTENER PROYECTOS ---
    // ==========================

    @GetMapping
    public ResponseEntity<List<ProjectResponseDTO>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> getProjectById(@PathVariable Integer id) {
        ProjectResponseDTO projectDTO = projectService.getProjectById(id);
        return ResponseEntity.ok(projectDTO);
    }

    @GetMapping("/{id}/tasks")
    public ResponseEntity<ProjectResponseWithTaskDTO> getProjectWithTasks(
            @PathVariable Integer id,
            @RequestParam(required = false) String nameTask,
            @RequestParam(required = false) StatusTask statusTask) {
        ProjectResponseWithTaskDTO projectWithTasks = projectService.getProjectByIdWithTask(id, nameTask, statusTask);
        return ResponseEntity.ok(projectWithTasks);
    }

    // ==========================
    // --- CREAR PROYECTO ---
    // ==========================

    @PostMapping
    public ResponseEntity<ProjectResponseDTO> createProject(@RequestBody ProjectRequestDTO projectRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.insertProject(projectRequestDTO));
    }

    // ==========================
    // --- ACTUALIZAR PROYECTO ---
    // ==========================

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> updateProject(@PathVariable Integer id,
            @RequestBody ProjectRequestDTO projectRequestDTO) {

        ProjectResponseDTO updatedProject = projectService.updateProject(id, projectRequestDTO);
        return ResponseEntity.ok(updatedProject);
    }

    // ==========================
    // --- BORRAR PROYECTO ---
    // ==========================

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable Integer id, @RequestParam Integer idUser) {
        String response = projectService.deleteProject(id, idUser);
        return ResponseEntity.ok(response);
    }
}
