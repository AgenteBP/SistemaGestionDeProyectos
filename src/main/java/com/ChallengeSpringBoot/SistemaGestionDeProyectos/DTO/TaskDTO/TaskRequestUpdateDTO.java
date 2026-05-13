package com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.TaskDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequestUpdateDTO {
    private Integer idTask;
    private String nameTask;
    private String description;
    private Integer idUser;
}
