package com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectDTO;

import java.util.List;

import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.UserDTO.UserResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponseDTO {
    private Integer idProject;
    private String nameProject;
    private String description;
    private UserResponseDTO owner;
    private List<UserResponseDTO> userAssigned;
    private Boolean active;
}
