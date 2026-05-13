package com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectUserDTO;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUserRequestDTO {
    Integer idProject;
    // Integer idUser;
    Integer idOwner;
    List<Integer> idUsers;
}
