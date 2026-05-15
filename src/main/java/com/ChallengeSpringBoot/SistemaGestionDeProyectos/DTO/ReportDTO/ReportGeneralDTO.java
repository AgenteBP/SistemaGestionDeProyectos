package com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ReportDTO;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportGeneralDTO {
    // Item 1 y 2 — por usuario
    private List<ReportResponseUserDTO> projectsPerUser;
    private List<ReportResponseUserDTO> tasksPerUser;

    // Item 3, 4 y 5 — por proyecto
    private List<ReportResponseProjectDTO> pendingTasksPerProject;
    private List<ReportResponseProjectDTO> startedTasksPerProject;
    private List<ReportResponseProjectDTO> completedTasksPerProject;

    // Item 6 — por tarea
    private List<ReportResponseTaskDTO> pendingStepsPerTask;
}
