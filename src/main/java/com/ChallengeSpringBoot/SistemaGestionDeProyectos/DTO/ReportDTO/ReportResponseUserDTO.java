package com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ReportDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponseUserDTO {

    String emailUser;
    String nameUser;
    Integer totalProjectAssigned;
    Integer totalTaskAssigned;

}
