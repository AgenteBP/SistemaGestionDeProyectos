package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectUserDTO.ProjectUserRequestDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectUserDTO.ProjectUserRequestUpdateDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectUserDTO.ProjectUserResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Services.IProjectUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/project-users")
@RequiredArgsConstructor
@Tag(name = "Asignación de Usuarios a Proyectos", description = "Gestión de usuarios asignados a proyectos (miembros del equipo)")
public class ProjectUserController {

        private final IProjectUserService projectUserService;

        // ====================================
        // --- OBTENER USUARIOS ASIGNADOS ---
        // ====================================

        @Operation(summary = "Obtener usuarios asignados a un proyecto", description = "Retorna la lista de miembros activos asignados al proyecto (no incluye al owner).")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Lista de asignaciones obtenida correctamente"),
                        @ApiResponse(responseCode = "404", description = "Proyecto no encontrado", content = @Content)
        })
        @GetMapping("/project/{idProject}")
        public ResponseEntity<List<ProjectUserResponseDTO>> getProjectUsersByProject(
                        @Parameter(description = "ID del proyecto", example = "1") @PathVariable Integer idProject) {
                return ResponseEntity.ok(projectUserService.getProjectUsersByProjectDTO(idProject));
        }

        // ====================================
        // --- CREAR USUARIOS ASIGNADOS (BATCH) ---
        // ====================================

        @Operation(summary = "Asignar múltiples usuarios a un proyecto", description = "Asigna una lista de usuarios a un proyecto. Solo el owner puede realizar esta operación. Los usuarios ya asignados son ignorados.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Usuarios asignados correctamente"),
                        @ApiResponse(responseCode = "400", description = "El usuario no es el owner o algún usuario no existe", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Proyecto no encontrado", content = @Content)
        })
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Proyecto, owner que autoriza y lista de usuarios a asignar", required = true, content = @Content(schema = @Schema(implementation = ProjectUserRequestDTO.class), examples = @ExampleObject(value = """
                        {
                          "idProject": 1,
                          "idOwner": 1,
                          "idUsers": [2, 3, 4]
                        }
                        """)))
        @PostMapping("/batch")
        public ResponseEntity<ProjectUserResponseDTO> saveAllProjectUsers(
                        @RequestBody ProjectUserRequestDTO projectUserRequestDTO) {
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(projectUserService.saveAllProjectUsers(projectUserRequestDTO));
        }

        // ==================================
        // --- CAMBIAR USUARIO ASIGNADO ---
        // ==================================

        @Operation(summary = "Reemplazar un usuario asignado por otro", description = "Reemplaza un usuario actual del proyecto por uno nuevo. Solo el owner puede realizar este cambio. El nuevo usuario debe existir y no estar ya asignado.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Usuario reemplazado correctamente"),
                        @ApiResponse(responseCode = "400", description = "El usuario no es el owner, el usuario no está asignado o el nuevo ya está en el proyecto", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Proyecto o usuario no encontrado", content = @Content)
        })
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "ID del owner, usuario actual a reemplazar y nuevo usuario", required = true, content = @Content(schema = @Schema(implementation = ProjectUserRequestUpdateDTO.class), examples = @ExampleObject(value = """
                        {
                          "idOwner": 1,
                          "idCurrentUser": 3,
                          "idNewUser": 5
                        }
                        """)))
        @PutMapping("/change-user/{idProject}")
        public ResponseEntity<ProjectUserResponseDTO> changeUserAssigned(
                        @Parameter(description = "ID del proyecto", example = "1") @PathVariable Integer idProject,
                        @RequestBody ProjectUserRequestUpdateDTO projectUserRequestUpdateDTO) {
                return ResponseEntity.ok(projectUserService.changeUserAssigned(idProject, projectUserRequestUpdateDTO));
        }

        // ===========================
        // --- ELIMINAR USUARIO ASIGNADO ---
        // ===========================

        @Operation(summary = "Eliminar un usuario asignado de un proyecto (baja lógica)", description = "Desactiva la asignación del usuario al proyecto. Solo el owner puede realizar esta operación.")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Asignación eliminada correctamente"),
                        @ApiResponse(responseCode = "400", description = """
                                        Error de validación:
                                        - El solicitante no es el owner del proyecto.
                                        - El usuario no está asignado al proyecto.
                                        - El usuario posee tareas activas en el proyecto.
                                        """, content = @Content),
                        @ApiResponse(responseCode = "404", description = "Proyecto, usuario o asignación no encontrada", content = @Content)
        })
        @DeleteMapping("/project/{idProject}/user/{idUser}/owner/{idOwner}")
        public ResponseEntity<Void> deleteUserAssigned(
                        @Parameter(description = "ID del proyecto", example = "1") @PathVariable Integer idProject,
                        @Parameter(description = "ID del usuario a desasignar", example = "3") @PathVariable Integer idUser,
                        @Parameter(description = "ID del owner que autoriza la operación", example = "1") @PathVariable Integer idOwner) {
                projectUserService.deleteUserAssigned(idProject, idUser, idOwner);
                return ResponseEntity.noContent().build();
        }
}
