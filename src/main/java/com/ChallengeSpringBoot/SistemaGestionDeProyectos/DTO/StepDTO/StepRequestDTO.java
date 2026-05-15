package com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.StepDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos requeridos para crear un nuevo paso asociado a una tarea")
public class StepRequestDTO {

    @Schema(description = "Nombre descriptivo del paso", example = "Crear diagrama entidad-relación")
    private String nameStep;

    @Schema(description = "ID de la tarea a la que pertenece el paso", example = "1")
    private Integer idTask;

    @Schema(description = "ID del usuario que crea el paso (debe ser el owner del proyecto, el creador o el asignado de la tarea)", example = "2")
    private Integer idUser;
}
