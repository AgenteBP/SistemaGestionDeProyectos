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
public class ReportResponseProjectDTO {

    String nameProject;

    Integer totalTasksPending;

    Integer totalTasksIniciated;

    Integer totalTasksCompleted;

    List<TaskDetailDTO> tasks;

    private LocalDate startDate;

    private LocalDate endDate;

    // -----------------------------------------------
    // Clase interna: detalle de cada tarea
    // -----------------------------------------------
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TaskDetailDTO {

        private String nameTask;

        private LocalDate date;
    }
}
