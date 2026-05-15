package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Controller;

import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ReportDTO.ReportResponseProjectDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ReportDTO.ReportResponseTaskDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ReportDTO.ReportResponseUserDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Services.IReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Reportes estadísticos del sistema. Disponibles en formato JSON o PDF descargable.")
public class ReportController {

    private final IReportService reportService;

    @Operation(summary = "Reporte 1: Proyectos asignados por usuario",
            description = "Retorna la cantidad de proyectos asignados a cada usuario (como owner o como miembro).")
    @ApiResponse(responseCode = "200", description = "Reporte generado correctamente")
    @GetMapping("/quantities-projects")
    public List<ReportResponseUserDTO> getQuantitiesProjectsUser() {
        return reportService.getQuantitiesProjectsUser();
    }

    @Operation(summary = "Reporte 2: Tareas asignadas por usuario",
            description = "Retorna la cantidad de tareas activas asignadas a cada usuario.")
    @ApiResponse(responseCode = "200", description = "Reporte generado correctamente")
    @GetMapping("/quantities-tasks")
    public List<ReportResponseUserDTO> getQuantitiesTasksUser() {
        return reportService.getQuantitiesTasksUser();
    }

    @Operation(summary = "Reporte 3: Tareas pendientes por proyecto",
            description = "Retorna la cantidad de tareas en estado PENDIENTE para cada proyecto activo.")
    @ApiResponse(responseCode = "200", description = "Reporte generado correctamente")
    @GetMapping("/tasks-pending-projects")
    public List<ReportResponseProjectDTO> getQuantitiesTaskPendingProject() {
        return reportService.getQuantitiesTaskPendingProject();
    }

    @Operation(summary = "Reporte 4: Tareas iniciadas por proyecto (ordenadas por fecha de inicio)",
            description = "Retorna la cantidad de tareas en estado INICIADA para cada proyecto, ordenadas por la fecha de inicio más temprana.")
    @ApiResponse(responseCode = "200", description = "Reporte generado correctamente")
    @GetMapping("/tasks-iniciated-projects")
    public List<ReportResponseProjectDTO> getQuantitiesTaskIniciatedProject() {
        return reportService.getQuantitiesTaskIniciatedProject();
    }

    @Operation(summary = "Reporte 5: Tareas finalizadas por proyecto (ordenadas por fecha de finalización)",
            description = "Retorna la cantidad de tareas en estado COMPLETADA para cada proyecto, ordenadas por la fecha de finalización más temprana.")
    @ApiResponse(responseCode = "200", description = "Reporte generado correctamente")
    @GetMapping("/tasks-completed-projects")
    public List<ReportResponseProjectDTO> getQuantitiesTaskCompletedProject() {
        return reportService.getQuantitiesTaskCompletedProject();
    }

    @Operation(summary = "Reporte 6: Pasos pendientes por tarea",
            description = "Retorna, agrupado por proyecto, la cantidad de pasos no FINALIZADOS para cada tarea activa.")
    @ApiResponse(responseCode = "200", description = "Reporte generado correctamente")
    @GetMapping("/steps-not-completed-tasks")
    public List<ReportResponseTaskDTO> getQuantitiesStepsNotCompletedTasks() {
        return reportService.getQuantitiesStepsNotCompletedTasks();
    }

    @Operation(summary = "Reporte general en PDF",
            description = "Genera y descarga un archivo PDF con los 6 reportes del sistema presentados en tablas formateadas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PDF generado correctamente",
                    content = @Content(mediaType = "application/pdf")),
            @ApiResponse(responseCode = "500", description = "Error al generar el PDF", content = @Content)
    })
    @GetMapping("/general/pdf")
    public ResponseEntity<byte[]> getGeneralReportPDF() {
        byte[] pdf = reportService.createReport();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_general.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
