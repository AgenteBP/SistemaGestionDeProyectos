package com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.TaskDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos requeridos para crear una nueva tarea")
public class TaskRequestDTO {

    @Schema(description = "Nombre descriptivo de la tarea", example = "Diseño de base de datos")
    private String nameTask;

    @Schema(description = "Descripción detallada de la tarea (opcional)", example = "Modelado del esquema relacional del sistema")
    private String description;

    @Schema(description = "ID del proyecto al que pertenece la tarea", example = "1")
    private Integer idProject;

    @Schema(description = "ID del usuario que crea la tarea (debe pertenecer al proyecto)", example = "1")
    private Integer idCreatedBy;

    @Schema(description = "ID del usuario asignado a la tarea (debe pertenecer al proyecto y ser distinto al creador)", example = "2")
    private Integer idAssignedUser;
}
