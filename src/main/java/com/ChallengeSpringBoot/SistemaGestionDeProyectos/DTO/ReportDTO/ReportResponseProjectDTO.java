package com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ReportDTO;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Reporte de tareas agrupadas por proyecto")
public class ReportResponseProjectDTO {

    @Schema(description = "Nombre del proyecto", example = "Sistema de Ventas")
    String nameProject;

    @Schema(description = "Total de tareas en estado PENDIENTE")
    Integer totalTasksPending;

    @Schema(description = "Total de tareas en estado INICIADA")
    Integer totalTasksIniciated;

    @Schema(description = "Total de tareas en estado COMPLETADA")
    Integer totalTasksCompleted;

    @Schema(description = "Lista de tareas detalladas (con nombre y fecha), según el reporte")
    List<TaskDetailDTO> tasks;

    @Schema(description = "Fecha de inicio de la primera tarea iniciada (usada para ordenamiento)")
    private LocalDate startDate;

    @Schema(description = "Fecha de fin de la primera tarea finalizada (usada para ordenamiento)")
    private LocalDate endDate;

    // -----------------------------------------------
    // Clase interna: detalle de cada tarea
    // -----------------------------------------------
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "Detalle de una tarea individual dentro del reporte de proyecto")
    public static class TaskDetailDTO {

        @Schema(description = "Nombre de la tarea", example = "Diseño de base de datos")
        private String nameTask;

        @Schema(description = "Fecha relevante: fecha de inicio (reporte 4) o fecha de finalización (reporte 5). Formato: dd/MM/yyyy",
                example = "01/05/2026")
        private LocalDate date;
    }
}
