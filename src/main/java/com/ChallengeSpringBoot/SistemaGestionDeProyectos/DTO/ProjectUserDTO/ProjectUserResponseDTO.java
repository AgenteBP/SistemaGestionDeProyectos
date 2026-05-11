package com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectUserDTO;

import java.util.List;

import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.UserDTO.UserResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUserResponseDTO {
    private Integer idProjectUser;
    private Integer idProject;
    private String nameProject;
    private List<UserResponseDTO> usersAssigned;
    private Boolean active;
}
