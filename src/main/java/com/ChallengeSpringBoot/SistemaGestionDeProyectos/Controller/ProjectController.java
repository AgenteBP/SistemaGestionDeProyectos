package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Controller;

import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectDTO.ProjectRequestDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectDTO.ProjectResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectDTO.ProjectResponseWithTaskDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Enums.StatusTask;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Services.IProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@Tag(name = "Proyectos", description = "Gestión de proyectos: creación, consulta, actualización y baja lógica con cascada")
public class ProjectController {

        private final IProjectService projectService;

        // ==========================
        // --- OBTENER PROYECTOS ---
        // ==========================

        @Operation(summary = "Obtener todos los proyectos", description = "Retorna la lista completa de proyectos registrados (activos e inactivos).")
        @ApiResponse(responseCode = "200", description = "Lista de proyectos obtenida correctamente")
        @GetMapping
        public ResponseEntity<List<ProjectResponseDTO>> getAllProjects() {
                return ResponseEntity.ok(projectService.getAllProjects());
        }

        @Operation(summary = "Obtener proyecto por ID")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Proyecto encontrado"),
                        @ApiResponse(responseCode = "404", description = "Proyecto no encontrado", content = @Content)
        })
        @GetMapping("/{id}")
        public ResponseEntity<ProjectResponseDTO> getProjectById(
                        @Parameter(description = "ID del proyecto", example = "1") @PathVariable Integer id) {
                return ResponseEntity.ok(projectService.getProjectById(id));
        }

        @Operation(summary = "Obtener proyecto con sus tareas (con filtros)", description = "Retorna el proyecto con su lista de tareas activas. Permite filtrar por nombre de tarea y/o estado.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Proyecto con tareas obtenido correctamente"),
                        @ApiResponse(responseCode = "404", description = "Proyecto no encontrado o inactivo", content = @Content)
        })
        @GetMapping("/{id}/tasks")
        public ResponseEntity<ProjectResponseWithTaskDTO> getProjectWithTasks(
                        @Parameter(description = "ID del proyecto", example = "1") @PathVariable Integer id,
                        @Parameter(description = "Filtro por nombre de tarea (parcial, sin distinción de mayúsculas)", example = "base de datos") @RequestParam(required = false) String nameTask,
                        @Parameter(description = "Filtro por estado de tarea", schema = @Schema(allowableValues = {
                                        "PENDIENTE", "INICIADA",
                                        "COMPLETADA" })) @RequestParam(required = false) StatusTask statusTask) {
                return ResponseEntity.ok(projectService.getProjectByIdWithTask(id, nameTask, statusTask));
        }

        // ==========================
        // --- CREAR PROYECTO ---
        // ==========================

        @Operation(summary = "Crear un nuevo proyecto")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Proyecto creado correctamente"),
                        @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario owner no encontrado", content = @Content)
        })
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del nuevo proyecto. `userAssigned` es opcional.", required = true, content = @Content(schema = @Schema(implementation = ProjectRequestDTO.class), examples = @ExampleObject(value = """
                        {
                          "nameProject": "Sistema de Facturación",
                          "description": "Módulo de facturación electrónica",
                          "idOwner": 1,
                          "userAssigned": [2, 3]
                        }
                        """)))
        @PostMapping
        public ResponseEntity<ProjectResponseDTO> createProject(@RequestBody ProjectRequestDTO projectRequestDTO) {
                return ResponseEntity.status(HttpStatus.CREATED).body(projectService.insertProject(projectRequestDTO));
        }

        // ==========================
        // --- ACTUALIZAR PROYECTO ---
        // ==========================

        @Operation(summary = "Actualizar nombre y descripción del proyecto", description = "Solo el owner del proyecto puede realizar la actualización.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Proyecto actualizado correctamente"),
                        @ApiResponse(responseCode = "400", description = "Solo el owner puede actualizar o datos inválidos", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Proyecto no encontrado", content = @Content)
        })
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos a actualizar. Se debe enviar el idOwner para validar permisos.", required = true, content = @Content(schema = @Schema(implementation = ProjectRequestDTO.class), examples = @ExampleObject(value = """
                        {
                          "nameProject": "Sistema de Facturación v2",
                          "description": "Versión actualizada del módulo",
                          "idOwner": 1
                        }
                        """)))
        @PutMapping("/{id}")
        public ResponseEntity<ProjectResponseDTO> updateProject(
                        @Parameter(description = "ID del proyecto a actualizar", example = "1") @PathVariable Integer id,
                        @RequestBody ProjectRequestDTO projectRequestDTO) {
                return ResponseEntity.ok(projectService.updateProject(id, projectRequestDTO));
        }

        // ==========================
        // --- BORRAR PROYECTO ---
        // ==========================

        @Operation(summary = "Eliminar proyecto (baja lógica en cascada)", description = "Desactiva el proyecto y en cascada todas sus tareas, pasos, comentarios y asignaciones. Solo el owner puede eliminarlo.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Proyecto eliminado correctamente"),
                        @ApiResponse(responseCode = "400", description = "El usuario no es el owner o el proyecto ya está inactivo", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Proyecto o usuario no encontrado", content = @Content)
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteProject(
                        @Parameter(description = "ID del proyecto a eliminar", example = "1") @PathVariable Integer id,
                        @Parameter(description = "ID del usuario que realiza la acción (debe ser el owner)", example = "1") @RequestParam Integer idUser) {
                projectService.deleteProject(id, idUser);
                return ResponseEntity.noContent().build();
        }
}
