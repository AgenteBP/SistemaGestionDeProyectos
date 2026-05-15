package com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.TaskDTO;

import java.time.LocalDate;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.UserDTO.UserResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Enums.StatusTask;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDTO {
    private Integer idTask;
    private String nameTask;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private StatusTask statusTask;
    private UserResponseDTO createdBy;
    private UserResponseDTO assignedUser;
    private Integer idProject;
    private String projectName;
    private Boolean active;
}
