package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Controller;

import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectDTO.ProjectRequestDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectDTO.ProjectResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Services.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

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

        projectRequestDTO.setIdProject(id);
        ProjectResponseDTO updatedProject = projectService.updateProject(projectRequestDTO);
        return ResponseEntity.ok(updatedProject);
    }

    // ==========================
    // --- BORRAR PROYECTO ---
    // ==========================

    // @DeleteMapping("/{id}")
    // public ResponseEntity<String> deleteProject(@PathVariable Integer id) {
    // String response = projectService.deleteProject(id);
    // return ResponseEntity.ok(response); // Returning OK because it returns a
    // string message
    // }
}
