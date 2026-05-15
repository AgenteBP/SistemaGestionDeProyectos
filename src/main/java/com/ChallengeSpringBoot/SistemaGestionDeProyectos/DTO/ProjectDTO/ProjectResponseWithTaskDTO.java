package com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectDTO;

import java.util.List;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.TaskDTO.TaskResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectResponseWithTaskDTO {

    private Integer idProject;
    private String nameProject;
    private String descriptionProject;
    private Integer idUserOwner;
    private String nameUserOwner;
    List<TaskResponseDTO> tasks;
}
