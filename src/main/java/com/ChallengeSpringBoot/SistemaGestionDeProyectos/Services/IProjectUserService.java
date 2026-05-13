package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Services;

import java.util.List;

import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectUserDTO.ProjectUserRequestDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectUserDTO.ProjectUserRequestUpdateDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectUserDTO.ProjectUserResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.ProjectUser;

public interface IProjectUserService {

    // List<ProjectUser> getProjectUsersByProject(Integer idProject);

    List<ProjectUserResponseDTO> getProjectUsersByProjectDTO(Integer idProject);

    List<ProjectUser> getProjectUsersByUser(Integer idUser);

    boolean existsByProjectAndUser(Integer idProject, Integer idUser);

    // ProjectUserResponseDTO saveProjectUser(ProjectUserRequestDTO
    // projectUserRequestDTO);

    ProjectUserResponseDTO saveAllProjectUsers(ProjectUserRequestDTO projectUserRequestDTO);

    void deleteUserAssigned(Integer idProject, Integer idUser, Integer idOwner);

    ProjectUserResponseDTO changeUserAssigned(ProjectUserRequestUpdateDTO projectUserRequestUpdateDTO);
}
