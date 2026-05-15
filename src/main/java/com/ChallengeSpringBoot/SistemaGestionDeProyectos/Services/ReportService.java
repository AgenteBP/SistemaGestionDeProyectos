package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Services;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ReportDTO.ReportGeneralDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ReportDTO.ReportResponseProjectDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ReportDTO.ReportResponseTaskDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ReportDTO.ReportResponseUserDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Enums.StatusStep;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Enums.StatusTask;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.Project;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.Task;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.ProjectRepository;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.ProjectUserRepository;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.StepRepository;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.TaskRepository;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.UserRepository;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService implements IReportService {

        private final ProjectRepository projectRepository;
        private final TaskRepository taskRepository;
        private final StepRepository stepRepository;
        private final ProjectUserRepository projectUserRepository;
        private final UserRepository userRepository;

        @Override
        public List<ReportResponseUserDTO> getQuantitiesProjectsUser() {
                return userRepository.findByActiveTrue().stream()
                                .map(user -> {
                                        Integer asOwner = projectRepository.countByOwnerAndActiveTrue(user);
                                        Integer asMember = projectUserRepository.countByUserAndProjectActiveTrue(user);

                                        ReportResponseUserDTO dto = new ReportResponseUserDTO();
                                        dto.setEmailUser(user.getEmail());
                                        dto.setNameUser(user.getName());
                                        dto.setTotalProjectAssigned(asOwner + asMember);
                                        return dto;
                                })
                                .toList();
        }

        @Override
        public List<ReportResponseUserDTO> getQuantitiesTasksUser() {
                return userRepository.findByActiveTrue().stream()
                                .map(user -> {
                                        Integer totalTasks = taskRepository.countByAssignedUserAndActiveTrue(user);

                                        ReportResponseUserDTO dto = new ReportResponseUserDTO();
                                        dto.setEmailUser(user.getEmail());
                                        dto.setNameUser(user.getName());
                                        dto.setTotalTaskAssigned(totalTasks);
                                        return dto;
                                })
                                .toList();
                // Verificar si esta bien que no cuente a los creador de tarea
        }

        public List<ReportResponseProjectDTO> getQuantitiesTaskPendingProject() {
                return projectRepository.findByActiveTrue().stream()
                                .map(project -> {
                                        Integer totalPending = taskRepository
                                                        .countByProjectIdProjectAndStatusTaskAndActiveTrue(
                                                                        project.getIdProject(),
                                                                        StatusTask.PENDIENTE);

                                        ReportResponseProjectDTO dto = new ReportResponseProjectDTO();
                                        dto.setNameProject(project.getNameProject());
                                        dto.setTotalTasksPending(totalPending);
                                        return dto;
                                })
                                .toList();
        }

        @Override
        public List<ReportResponseProjectDTO> getQuantitiesTaskIniciatedProject() {
                return projectRepository.findByActiveTrue().stream()
                                .sorted(Comparator.comparing(this::getFirstStartedTaskDate,
                                                Comparator.nullsLast(Comparator.naturalOrder())))
                                .map(project -> {
                                        List<Task> sorted = taskRepository
                                                        .findByProjectIdProjectAndStatusTaskAndActiveTrueOrderByStartDateAsc(
                                                                        project.getIdProject(), StatusTask.INICIADA);

                                        List<ReportResponseProjectDTO.TaskDetailDTO> taskDetails = sorted.stream()
                                                        .map(t -> new ReportResponseProjectDTO.TaskDetailDTO(
                                                                        t.getNameTask(), t.getStartDate()))
                                                        .toList();

                                        ReportResponseProjectDTO dto = new ReportResponseProjectDTO();
                                        dto.setNameProject(project.getNameProject());
                                        dto.setTotalTasksIniciated(sorted.size());
                                        dto.setTasks(taskDetails);
                                        return dto;
                                })
                                .toList();
        }

        @Override
        public List<ReportResponseProjectDTO> getQuantitiesTaskCompletedProject() {
                return projectRepository.findByActiveTrue().stream()
                                .sorted(Comparator.comparing(this::getFirstCompletedTaskDate,
                                                Comparator.nullsLast(Comparator.naturalOrder())))
                                .map(project -> {
                                        List<Task> sorted = taskRepository
                                                        .findByProjectIdProjectAndStatusTaskAndActiveTrueOrderByEndDateAsc(
                                                                        project.getIdProject(), StatusTask.COMPLETADA);

                                        List<ReportResponseProjectDTO.TaskDetailDTO> taskDetails = sorted.stream()
                                                        .map(t -> new ReportResponseProjectDTO.TaskDetailDTO(
                                                                        t.getNameTask(), t.getEndDate()))
                                                        .toList();

                                        ReportResponseProjectDTO dto = new ReportResponseProjectDTO();
                                        dto.setNameProject(project.getNameProject());
                                        dto.setTotalTasksCompleted(sorted.size());
                                        dto.setTasks(taskDetails);
                                        return dto;
                                })
                                .toList();
        }

        @Override
        public List<ReportResponseTaskDTO> getQuantitiesStepsNotCompletedTasks() {
                return projectRepository.findByActiveTrue().stream()
                                .map(project -> {
                                        List<ReportResponseTaskDTO.TotalStepsNotCompletedTasksDTO> stepsNotCompleted = taskRepository
                                                        .findByProjectIdProjectAndActiveTrue(project.getIdProject())
                                                        .stream()
                                                        .map(task -> {
                                                                Integer pendingSteps = stepRepository
                                                                                .countByTaskAndStatusStepNotAndActiveTrue(
                                                                                                task,
                                                                                                StatusStep.FINALIZADO);

                                                                ReportResponseTaskDTO.TotalStepsNotCompletedTasksDTO dto = new ReportResponseTaskDTO.TotalStepsNotCompletedTasksDTO();
                                                                dto.setIdTask(task.getIdTask());
                                                                dto.setNameTask(task.getNameTask());
                                                                dto.setTotalStepsNotCompleted(pendingSteps);
                                                                return dto;
                                                        })
                                                        .toList();

                                        ReportResponseTaskDTO reportDTO = new ReportResponseTaskDTO();
                                        reportDTO.setNameProject(project.getNameProject());
                                        reportDTO.setTotalStepsNotCompletedTasksDTO(stepsNotCompleted);
                                        return reportDTO;
                                })
                                .toList();
        }

        public byte[] createReport() {
                ReportGeneralDTO report = new ReportGeneralDTO();
                report.setProjectsPerUser(getQuantitiesProjectsUser());
                report.setTasksPerUser(getQuantitiesTasksUser());
                report.setPendingTasksPerProject(getQuantitiesTaskPendingProject());
                report.setStartedTasksPerProject(getQuantitiesTaskIniciatedProject());
                report.setCompletedTasksPerProject(getQuantitiesTaskCompletedProject());
                report.setPendingStepsPerTask(getQuantitiesStepsNotCompletedTasks());

                try {
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        Document document = new Document(PageSize.A4, 36, 36, 54, 36);
                        PdfWriter.getInstance(document, outputStream);
                        document.open();
                        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                        // Fuentes
                        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
                        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD);
                        Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
                        Font cellFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);

                        // Colores
                        BaseColor headerColor = new BaseColor(63, 84, 186);
                        BaseColor rowColor = new BaseColor(235, 237, 250);

                        // Título principal
                        Paragraph title = new Paragraph("Reporte General del Sistema", titleFont);
                        title.setAlignment(Element.ALIGN_CENTER);
                        title.setSpacingAfter(20);
                        document.add(title);

                        // -----------------------------------------------
                        // Item 1 — Proyectos por usuario
                        // -----------------------------------------------
                        document.add(createSectionTitle("1. Cantidad de proyectos asignados por usuario", sectionFont));
                        PdfPTable table1 = new PdfPTable(3);
                        table1.setWidthPercentage(100);
                        table1.setWidths(new float[] { 3f, 4f, 2f });

                        addTableHeader(table1, headerFont, headerColor,
                                        "Email", "Nombre", "Total Proyectos");

                        boolean alternate1 = false;
                        for (ReportResponseUserDTO dto : report.getProjectsPerUser()) {
                                BaseColor bg = alternate1 ? rowColor : BaseColor.WHITE;
                                addTableRow(table1, cellFont, bg,
                                                dto.getEmailUser(),
                                                dto.getNameUser(),
                                                String.valueOf(dto.getTotalProjectAssigned()));
                                alternate1 = !alternate1;
                        }
                        document.add(table1);
                        document.add(new Paragraph(" "));

                        // -----------------------------------------------
                        // Item 2 — Tareas por usuario
                        // -----------------------------------------------
                        document.add(createSectionTitle("2. Cantidad de tareas asignadas por usuario", sectionFont));
                        PdfPTable table2 = new PdfPTable(3);
                        table2.setWidthPercentage(100);
                        table2.setWidths(new float[] { 3f, 4f, 2f });

                        addTableHeader(table2, headerFont, headerColor,
                                        "Email", "Nombre", "Total Tareas");

                        boolean alternate2 = false;
                        for (ReportResponseUserDTO dto : report.getTasksPerUser()) {
                                BaseColor bg = alternate2 ? rowColor : BaseColor.WHITE;
                                addTableRow(table2, cellFont, bg,
                                                dto.getEmailUser(),
                                                dto.getNameUser(),
                                                String.valueOf(dto.getTotalTaskAssigned()));
                                alternate2 = !alternate2;
                        }
                        document.add(table2);
                        document.add(new Paragraph(" "));

                        // -----------------------------------------------
                        // Item 3 — Tareas pendientes por proyecto
                        // -----------------------------------------------
                        document.add(createSectionTitle("3. Cantidad de tareas pendientes por proyecto", sectionFont));
                        PdfPTable table3 = new PdfPTable(2);
                        table3.setWidthPercentage(100);
                        table3.setWidths(new float[] { 5f, 2f });

                        addTableHeader(table3, headerFont, headerColor,
                                        "Proyecto", "Tareas Pendientes");

                        boolean alternate3 = false;
                        for (ReportResponseProjectDTO dto : report.getPendingTasksPerProject()) {
                                BaseColor bg = alternate3 ? rowColor : BaseColor.WHITE;
                                addTableRow(table3, cellFont, bg,
                                                dto.getNameProject(),
                                                String.valueOf(dto.getTotalTasksPending()));
                                alternate3 = !alternate3;
                        }
                        document.add(table3);
                        document.add(new Paragraph(" "));

                        // -----------------------------------------------
                        // Item 4 — Tareas iniciadas por proyecto (una fila por tarea)
                        // -----------------------------------------------
                        document.add(createSectionTitle(
                                        "4. Tareas iniciadas por proyecto (ordenadas por fecha de inicio)",
                                        sectionFont));
                        PdfPTable table4 = new PdfPTable(3);
                        table4.setWidthPercentage(100);
                        table4.setWidths(new float[] { 3.5f, 4f, 2f });

                        addTableHeader(table4, headerFont, headerColor,
                                        "Proyecto", "Tarea", "Fecha Inicio");

                        Font subtotalFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
                        BaseColor subtotalColor = new BaseColor(200, 210, 240);

                        boolean alternate4 = false;
                        for (ReportResponseProjectDTO dto4 : report.getStartedTasksPerProject()) {
                                if (dto4.getTasks() == null || dto4.getTasks().isEmpty()) {
                                        BaseColor bg = alternate4 ? rowColor : BaseColor.WHITE;
                                        addTableRow(table4, cellFont, bg, dto4.getNameProject(), "-", "-");
                                        alternate4 = !alternate4;
                                } else {
                                        for (ReportResponseProjectDTO.TaskDetailDTO task : dto4.getTasks()) {
                                                BaseColor bg = alternate4 ? rowColor : BaseColor.WHITE;
                                                String startDate = task.getDate() != null
                                                                ? task.getDate().format(dateFormatter)
                                                                : "-";
                                                addTableRow(table4, cellFont, bg,
                                                                dto4.getNameProject(),
                                                                task.getNameTask(),
                                                                startDate);
                                                alternate4 = !alternate4;
                                        }
                                }
                                // Fila de subtotal
                                int totalInic = dto4.getTotalTasksIniciated() != null ? dto4.getTotalTasksIniciated() : 0;
                                PdfPCell subtotalCell = new PdfPCell(
                                                new Phrase("Total: " + totalInic + " tarea(s) iniciada(s)",
                                                                subtotalFont));
                                subtotalCell.setColspan(3);
                                subtotalCell.setBackgroundColor(subtotalColor);
                                subtotalCell.setPadding(5);
                                subtotalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                table4.addCell(subtotalCell);
                        }
                        document.add(table4);
                        document.add(new Paragraph(" "));

                        // -----------------------------------------------
                        // Item 5 — Tareas completadas por proyecto (una fila por tarea)
                        // -----------------------------------------------
                        document.add(createSectionTitle(
                                        "5. Tareas finalizadas por proyecto (ordenadas por fecha de finalización)",
                                        sectionFont));
                        PdfPTable table5 = new PdfPTable(3);
                        table5.setWidthPercentage(100);
                        table5.setWidths(new float[] { 3.5f, 4f, 2f });

                        addTableHeader(table5, headerFont, headerColor,
                                        "Proyecto", "Tarea", "Fecha Finalización");

                        boolean alternate5 = false;
                        for (ReportResponseProjectDTO dto5 : report.getCompletedTasksPerProject()) {
                                if (dto5.getTasks() == null || dto5.getTasks().isEmpty()) {
                                        BaseColor bg = alternate5 ? rowColor : BaseColor.WHITE;
                                        addTableRow(table5, cellFont, bg, dto5.getNameProject(), "-", "-");
                                        alternate5 = !alternate5;
                                } else {
                                        for (ReportResponseProjectDTO.TaskDetailDTO task : dto5.getTasks()) {
                                                BaseColor bg = alternate5 ? rowColor : BaseColor.WHITE;
                                                String endDate = task.getDate() != null
                                                                ? task.getDate().format(dateFormatter)
                                                                : "-";
                                                addTableRow(table5, cellFont, bg,
                                                                dto5.getNameProject(),
                                                                task.getNameTask(),
                                                                endDate);
                                                alternate5 = !alternate5;
                                        }
                                }
                                // Fila de subtotal
                                int totalComp = dto5.getTotalTasksCompleted() != null ? dto5.getTotalTasksCompleted() : 0;
                                PdfPCell subtotalCell5 = new PdfPCell(
                                                new Phrase("Total: " + totalComp + " tarea(s) finalizada(s)",
                                                                subtotalFont));
                                subtotalCell5.setColspan(3);
                                subtotalCell5.setBackgroundColor(subtotalColor);
                                subtotalCell5.setPadding(5);
                                subtotalCell5.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                table5.addCell(subtotalCell5);
                        }
                        document.add(table5);
                        document.add(new Paragraph(" "));

                        // -----------------------------------------------
                        // Item 6 — Pasos pendientes por tarea (sin columna ID)
                        // -----------------------------------------------
                        document.add(createSectionTitle("6. Cantidad de pasos pendientes por tarea", sectionFont));
                        PdfPTable table6 = new PdfPTable(3);
                        table6.setWidthPercentage(100);
                        table6.setWidths(new float[] { 3f, 5f, 2f });

                        addTableHeader(table6, headerFont, headerColor,
                                        "Proyecto", "Nombre Tarea", "Pasos Pendientes");

                        boolean alternate6 = false;
                        for (ReportResponseTaskDTO projectReport : report.getPendingStepsPerTask()) {
                                for (ReportResponseTaskDTO.TotalStepsNotCompletedTasksDTO dto : projectReport
                                                .getTotalStepsNotCompletedTasksDTO()) {
                                        BaseColor bg = alternate6 ? rowColor : BaseColor.WHITE;
                                        addTableRow(table6, cellFont, bg,
                                                        projectReport.getNameProject(),
                                                        dto.getNameTask(),
                                                        String.valueOf(dto.getTotalStepsNotCompleted()));
                                        alternate6 = !alternate6;
                                }
                        }
                        document.add(table6);

                        document.close();
                        return outputStream.toByteArray();

                } catch (Exception e) {
                        throw new RuntimeException("Error al generar el reporte PDF: " + e.getMessage());
                }

        }

        /// Funciones auxiliares
        private LocalDate getFirstStartedTaskDate(Project project) {
                return taskRepository
                                .findByProjectIdProjectAndStatusTaskAndActiveTrueOrderByStartDateAsc(
                                                project.getIdProject(),
                                                StatusTask.INICIADA)
                                .stream()
                                .map(Task::getStartDate)
                                .filter(date -> date != null)
                                .findFirst()
                                .orElse(null);
        }

        private LocalDate getFirstCompletedTaskDate(Project project) {
                return taskRepository
                                .findByProjectIdProjectAndStatusTaskAndActiveTrueOrderByEndDateAsc(
                                                project.getIdProject(),
                                                StatusTask.COMPLETADA)
                                .stream()
                                .map(Task::getEndDate)
                                .filter(date -> date != null)
                                .findFirst()
                                .orElse(null);
        }

        /// pdf
        private Paragraph createSectionTitle(String text, Font font) {
                Paragraph section = new Paragraph(text, font);
                section.setSpacingBefore(10);
                section.setSpacingAfter(8);
                return section;
        }

        private void addTableHeader(PdfPTable table, Font font, BaseColor color, String... headers) {
                for (String header : headers) {
                        PdfPCell cell = new PdfPCell(new Phrase(header, font));
                        cell.setBackgroundColor(color);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setPadding(6);
                        table.addCell(cell);
                }
        }

        private void addTableRow(PdfPTable table, Font font, BaseColor color, String... values) {
                for (String value : values) {
                        PdfPCell cell = new PdfPCell(new Phrase(value != null ? value : "-", font));
                        cell.setBackgroundColor(color);
                        cell.setPadding(5);
                        table.addCell(cell);
                }
        }

}
