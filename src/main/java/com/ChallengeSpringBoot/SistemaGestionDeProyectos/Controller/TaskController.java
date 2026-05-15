package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Controller;

import java.util.List;

import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.TaskDTO.TaskRequestDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.TaskDTO.TaskRequestNextStatusDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.TaskDTO.TaskRequestUpdateDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.TaskDTO.TaskResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Enums.StatusTask;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Services.ITaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Tag(name = "Tareas", description = "Gestión de tareas: creación, consulta, cambio de estado y baja lógica")
public class TaskController {

        private final ITaskService taskService;

        // ==========================
        // --- OBTENER TAREAS ---
        // ==========================

        @Operation(summary = "Obtener tarea por ID")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Tarea encontrada"),
                        @ApiResponse(responseCode = "404", description = "Tarea no encontrada o inactiva", content = @Content)
        })
        @GetMapping("/{id}")
        public ResponseEntity<TaskResponseDTO> getTaskById(
                        @Parameter(description = "ID de la tarea", example = "1") @PathVariable Integer id) {
                return ResponseEntity.ok(taskService.findTaskById(id));
        }

        @Operation(
                        summary = "Buscar tareas con filtros opcionales",
                        description = """
                                        Permite buscar tareas activas combinando filtros opcionales:
                                        - `nameTask`: filtra por nombre (parcial, sin distinción de mayúsculas).
                                        - `statusTask`: filtra por estado (PENDIENTE, INICIADA, COMPLETADA).
                                        - Sin parámetros: retorna todas las tareas activas.
                                        """)
        @ApiResponse(responseCode = "200", description = "Lista de tareas filtrada correctamente")
        @GetMapping("/search")
        public ResponseEntity<List<TaskResponseDTO>> searchTasks(
                        @Parameter(description = "Nombre parcial de la tarea (opcional)", example = "base de datos")
                        @RequestParam(required = false) String nameTask,
                        @Parameter(description = "Estado de la tarea (opcional)",
                                        schema = @Schema(allowableValues = { "PENDIENTE", "INICIADA", "COMPLETADA" }))
                        @RequestParam(required = false) StatusTask statusTask) {
                return ResponseEntity.ok(taskService.findTaskWithNameOrStatus(nameTask, statusTask));
        }

        // ==========================
        // --- CREAR TAREA ---
        // ==========================

        @Operation(summary = "Crear una nueva tarea", description = "La tarea se crea en estado PENDIENTE. El usuario creador y el asignado deben pertenecer al proyecto y ser distintos.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Tarea creada correctamente"),
                        @ApiResponse(responseCode = "400", description = "Datos inválidos, usuario no pertenece al proyecto o usuarios iguales", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Proyecto o usuario no encontrado", content = @Content)
        })
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos de la nueva tarea", required = true, content = @Content(schema = @Schema(implementation = TaskRequestDTO.class), examples = @ExampleObject(value = """
                        {
                          "nameTask": "Diseño de base de datos",
                          "description": "Modelado del esquema relacional del sistema",
                          "idProject": 1,
                          "idCreatedBy": 1,
                          "idAssignedUser": 2
                        }
                        """)))
        @PostMapping
        public ResponseEntity<TaskResponseDTO> createTask(@RequestBody TaskRequestDTO taskRequestDTO) {
                return ResponseEntity.ok(taskService.saveTask(taskRequestDTO));
        }

        // ==========================
        // --- ACTUALIZAR TAREA ---
        // ==========================

        @Operation(summary = "Actualizar nombre y descripción de una tarea", description = "Solo el usuario creador de la tarea o el dueño del proyecto puede modificarla.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Tarea actualizada correctamente"),
                        @ApiResponse(responseCode = "400", description = "El usuario no es el creador de la tarea ni dueño del proyecto", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Tarea no encontrada", content = @Content)
        })

        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos a actualizar. Se requiere idUser para validar permisos.", required = true, content = @Content(schema = @Schema(implementation = TaskRequestUpdateDTO.class), examples = @ExampleObject(value = """
                        {
                          "idUser": 1,
                          "nameTask": "Diseño de BD actualizado",
                          "description": "Descripción actualizada"
                        }
                        """)))
        @PutMapping("/{idTask}")
        public ResponseEntity<TaskResponseDTO> updateTask(
                        @Parameter(description = "ID de la tarea", example = "1") @PathVariable Integer idTask,
                        @RequestBody TaskRequestUpdateDTO taskRequestUpdateDTO) {
                return ResponseEntity.ok(taskService.updateTask(idTask, taskRequestUpdateDTO));
        }

        // ==========================
        // --- CAMBIAR ESTADO ---
        // ==========================

        @Operation(summary = "Avanzar al siguiente estado de la tarea", description = """
                        Transiciones posibles:
                        - PENDIENTE → INICIADA (requiere `dateTask` como fecha de inicio)
                        - INICIADA → COMPLETADA (requiere `dateTask` como fecha de fin; todos los pasos deben estar FINALIZADOS(si tiene pasos, si no tiene pasos puede ser completado igual))

                        Solo el creador o el usuario asignado pueden cambiar el estado.
                        """)
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Estado de tarea actualizado correctamente"),
                        @ApiResponse(responseCode = "400", description = "Pasos pendientes, fecha inválida o usuario sin permisos", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Tarea no encontrada", content = @Content)
        })
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "ID de tarea, ID de usuario y fecha requerida según la transición.", required = true, content = @Content(schema = @Schema(implementation = TaskRequestNextStatusDTO.class), examples = {
                        @ExampleObject(name = "Iniciar tarea", value = """
                                        {
                                          "idTask": 1,
                                          "idUser": 1,
                                          "dateTask": "01/05/2026"
                                        }
                                        """),
                        @ExampleObject(name = "Completar tarea", value = """
                                        {
                                          "idTask": 1,
                                          "idUser": 1,
                                          "dateTask": "15/05/2026"
                                        }
                                        """)
        }))
        @PutMapping("/next-status")
        public ResponseEntity<TaskResponseDTO> nextStatusTask(
                        @RequestBody TaskRequestNextStatusDTO taskrequestNextStatusDTO) {
                return ResponseEntity.ok(taskService.nextStatusTask(taskrequestNextStatusDTO));
        }

        // ==========================
        // --- ELIMINAR TAREA ---
        // ==========================

        @Operation(summary = "Eliminar tarea (baja lógica)", description = "Desactiva la tarea junto con todos sus pasos y comentarios asociados.")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Tarea eliminada correctamente"),
                        @ApiResponse(responseCode = "400", description = "La tarea ya está inactiva o el usuario no es el creador de la tarea ni dueño del proyecto", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Tarea no encontrada", content = @Content)
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteTask(
                        @Parameter(description = "ID de la tarea a eliminar", example = "1") @PathVariable Integer id,
                        @Parameter(description = "ID del usuario", example = "1") @RequestParam Integer idUser) {
                taskService.deleteTask(id, idUser);
                return ResponseEntity.noContent().build();
        }
}
