package com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.StepDTO;

import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Enums.StatusStep;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StepResponseDTO {
    private Integer idStep;
    private String nameStep;
    private Integer idTask;
    private StatusStep statusStep;
}
