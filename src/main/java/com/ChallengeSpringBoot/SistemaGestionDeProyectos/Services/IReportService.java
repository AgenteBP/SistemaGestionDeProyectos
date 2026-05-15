package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Services;

import java.util.List;

import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ReportDTO.ReportResponseProjectDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ReportDTO.ReportResponseTaskDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ReportDTO.ReportResponseUserDTO;

public interface IReportService {

    List<ReportResponseUserDTO> getQuantitiesProjectsUser();

    List<ReportResponseUserDTO> getQuantitiesTasksUser();

    List<ReportResponseProjectDTO> getQuantitiesTaskPendingProject();

    List<ReportResponseProjectDTO> getQuantitiesTaskIniciatedProject();

    List<ReportResponseProjectDTO> getQuantitiesTaskCompletedProject();

    List<ReportResponseTaskDTO> getQuantitiesStepsNotCompletedTasks();

    byte[] createReport();
}
