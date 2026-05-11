package com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.TaskDTO;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestDTO {

    private String nameTask;
    private String description;
    private Integer idProject;
    private Integer idCreatedBy;
    private Integer idAssignedUser;
    // private Date startDate;
}
