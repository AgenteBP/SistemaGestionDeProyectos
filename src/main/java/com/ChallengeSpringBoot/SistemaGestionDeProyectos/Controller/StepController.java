package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.StepDTO.StepRequestDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.StepDTO.StepResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Services.IStepService;
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
@RequestMapping("/step")
@RequiredArgsConstructor
@Tag(name = "Pasos", description = "Gestión de pasos asociados a tareas. Los pasos controlan el progreso detallado de cada tarea.")
public class StepController {

        private final IStepService stepService;

        // ==========================
        // --- OBTENER PASOS DE UNA TAREA ---
        // ==========================

        @Operation(summary = "Obtener pasos activos de una tarea", description = "Retorna todos los pasos activos asociados a una tarea específica.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Lista de pasos obtenida correctamente"),
                        @ApiResponse(responseCode = "404", description = "Tarea no encontrada o inactiva", content = @Content)
        })
        @GetMapping("/task/{taskId}")
        public ResponseEntity<List<StepResponseDTO>> getStepsByTask(
                        @Parameter(description = "ID de la tarea", example = "1") @PathVariable Integer taskId) {
                return ResponseEntity.ok(stepService.getAllStepsByTask(taskId));
        }

        // ==========================
        // --- CREAR PASO ---
        // ==========================

        @Operation(summary = "Crear un nuevo paso", description = "Crea un paso en estado PENDIENTE asociado a una tarea. Solo puede crearlo el owner del proyecto, el creador o el usuario asignado a la tarea.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Paso creado correctamente"),
                        @ApiResponse(responseCode = "400", description = "La tarea está completada, inactiva o el usuario no tiene permisos", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Tarea no encontrada", content = @Content)
        })
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del nuevo paso", required = true, content = @Content(schema = @Schema(implementation = StepRequestDTO.class), examples = @ExampleObject(value = """
                        {
                          "nameStep": "Crear diagrama entidad-relación",
                          "idTask": 1,
                          "idUser": 2
                        }
                        """)))
        @PostMapping
        public ResponseEntity<StepResponseDTO> createStep(@RequestBody StepRequestDTO stepRequestDTO) {
                return ResponseEntity.ok(stepService.saveStep(stepRequestDTO));
        }

        // ==========================
        // --- ACTUALIZAR ESTADO DEL PASO ---
        // ==========================

        @Operation(summary = "Avanzar al siguiente estado del paso", description = """
                        Transiciones posibles:
                        - PENDIENTE → INICIADO (la tarea debe estar en estado INICIADA)
                        - INICIADO → FINALIZADO (la tarea debe estar en estado INICIADA)

                        Solo puede hacerlo el owner del proyecto, el creador o el asignado de la tarea.
                        """)
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Estado del paso actualizado correctamente"),
                        @ApiResponse(responseCode = "400", description = "La tarea no está INICIADA, el paso ya está FINALIZADO o el usuario no tiene permisos", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Paso no encontrado", content = @Content)
        })
        @PutMapping("/next-status/{idStep}")
        public ResponseEntity<StepResponseDTO> nextStatusStep(
                        @Parameter(description = "ID del paso", example = "1") @PathVariable Integer idStep,
                        @Parameter(description = "ID del usuario que realiza la acción", example = "2") @RequestParam Integer idUser) {
                return ResponseEntity.ok(stepService.nextStatusStep(idStep, idUser));
        }

        // ==========================
        // --- ACTUALIZAR NOMBRE DEL PASO ---
        // ==========================

        @Operation(summary = "Actualizar el nombre de un paso", description = "Permite renombrar un paso mientras la tarea no esté completada.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Nombre del paso actualizado correctamente"),
                        @ApiResponse(responseCode = "400", description = "La tarea está completada o inactiva", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Paso no encontrado", content = @Content)
        })
        @PutMapping("/update-name/{idStep}")
        public ResponseEntity<StepResponseDTO> updateNameStep(
                        @Parameter(description = "ID del paso", example = "1") @PathVariable Integer idStep,
                        @Parameter(description = "Nuevo nombre del paso", example = "Crear diagrama ER actualizado") @RequestParam String nameStep) {
                return ResponseEntity.ok(stepService.updateNameStep(idStep, nameStep));
        }

        // ==========================
        // --- ELIMINAR PASO ---
        // ==========================

        @Operation(summary = "Eliminar paso (baja lógica)", description = "Desactiva el paso. No se puede eliminar si ya está FINALIZADO o si la tarea está COMPLETADA.")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Paso eliminado correctamente"),
                        @ApiResponse(responseCode = "400", description = "El paso ya está dado de baja, FINALIZADO o la tarea está COMPLETADA. El usuario no tiene permisos para eliminar el paso.", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Paso no encontrado", content = @Content)
        })
        @DeleteMapping("/{idStep}")
        public ResponseEntity<Void> deleteStep(
                        @Parameter(description = "ID del paso a eliminar", example = "1") @PathVariable Integer idStep,
                        @Parameter(description = "ID del usuario que realiza la acción", example = "2") @RequestParam Integer idUser) {
                stepService.deleteStep(idStep, idUser);
                return ResponseEntity.noContent().build();
        }
}
