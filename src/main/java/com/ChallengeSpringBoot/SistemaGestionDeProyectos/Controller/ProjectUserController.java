package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectUserDTO.ProjectUserRequestDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectUserDTO.ProjectUserRequestUpdateDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectUserDTO.ProjectUserResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Services.IProjectUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/project-users")
@RequiredArgsConstructor
public class ProjectUserController {

    private final IProjectUserService projectUserService;

    // ====================================
    // --- OBTENER USUARIOS ASIGNADOS ---
    // ====================================

    @GetMapping("/project/{idProject}")
    public ResponseEntity<List<ProjectUserResponseDTO>> getProjectUsersByProject(@PathVariable Integer idProject) {
        List<ProjectUserResponseDTO> projectUsers = projectUserService.getProjectUsersByProjectDTO(idProject);
        return ResponseEntity.ok(projectUsers);
    }

    // ==========================
    // --- CREAR USUARIO ASIGNADO ---
    // ==========================

    // @PostMapping
    // public ResponseEntity<ProjectUserResponseDTO> saveProjectUser(
    // @RequestBody ProjectUserRequestDTO projectUserRequestDTO) {
    // ProjectUserResponseDTO projectUser =
    // projectUserService.saveProjectUser(projectUserRequestDTO);
    // return ResponseEntity.status(HttpStatus.CREATED).body(projectUser);
    // }

    // ====================================
    // --- CREAR USUARIOS ASIGNADOS (BATCH) ---
    // ====================================

    @PostMapping("/batch")
    public ResponseEntity<ProjectUserResponseDTO> saveAllProjectUsers(
            @RequestBody ProjectUserRequestDTO projectUserRequestDTO) {
        ProjectUserResponseDTO projectUser = projectUserService.saveAllProjectUsers(projectUserRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(projectUser);
    }

    // ==================================
    // --- CAMBIAR USUARIO ASIGNADO ---
    // ==================================

    @PutMapping("/change-user")
    public ResponseEntity<ProjectUserResponseDTO> changeUserAssigned(
            @RequestBody ProjectUserRequestUpdateDTO projectUserRequestUpdateDTO) {
        ProjectUserResponseDTO result = projectUserService.changeUserAssigned(projectUserRequestUpdateDTO);
        return ResponseEntity.ok(result);
    }

    // ===========================
    // --- ELIMINAR USUARIO ASIGNADO ---
    // ===========================

    @DeleteMapping("/project/{idProject}/user/{idUser}/owner/{idOwner}")
    public ResponseEntity<Void> deleteUserAssigned(@PathVariable Integer idProject, @PathVariable Integer idUser,
            @PathVariable Integer idOwner) {
        projectUserService.deleteUserAssigned(idProject, idUser, idOwner);
        return ResponseEntity.noContent().build();
    }
}
