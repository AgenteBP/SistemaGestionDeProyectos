package com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectUserDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectUserRequestUpdateDTO {
    // private Integer idProject;
    private Integer idUserAssigned;
    private Integer idOwner;
    private Integer idNewUser;
}
