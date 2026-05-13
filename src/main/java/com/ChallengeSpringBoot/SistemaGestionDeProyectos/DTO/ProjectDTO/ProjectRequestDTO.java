package com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequestDTO {
    // private Integer idProject;
    private String nameProject;
    private String description;
    private Integer idOwner;
    private List<Integer> userAssigned;
}
