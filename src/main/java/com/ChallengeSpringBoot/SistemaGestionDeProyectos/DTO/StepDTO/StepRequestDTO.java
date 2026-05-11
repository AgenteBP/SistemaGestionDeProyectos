package com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.StepDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StepRequestDTO {

    private String nameStep;
    private Integer idTask;
    private Integer idUser;
}
