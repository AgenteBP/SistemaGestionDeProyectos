package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Services;

import java.util.List;

import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectDTO.ProjectRequestDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectDTO.ProjectResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectDTO.ProjectResponseWithTaskDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Enums.StatusTask;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.Project;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.User;

public interface IProjectService {

    List<ProjectResponseDTO> getAllProjects();

    ProjectResponseDTO getProjectById(Integer idProject);

    Project findProjectById(Integer idProject);

    ProjectResponseWithTaskDTO getProjectByIdWithTask(Integer idProject, String nameTask, StatusTask statusTask);

    ProjectResponseDTO insertProject(ProjectRequestDTO projectDto);

    ProjectResponseDTO updateProject(Integer idProject, ProjectRequestDTO projectRequestDTO);

    boolean hasActiveProjectsAsOwner(User user);

    void deleteProject(Integer idProject, Integer idUser);
}
