package com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ReportDTO;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponseTaskDTO {

    String nameProject;
    List<TotalStepsNotCompletedTasksDTO> totalStepsNotCompletedTasksDTO;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TotalStepsNotCompletedTasksDTO {

        private Integer idTask;
        private String nameTask;
        private Integer totalStepsNotCompleted;

    }

}
