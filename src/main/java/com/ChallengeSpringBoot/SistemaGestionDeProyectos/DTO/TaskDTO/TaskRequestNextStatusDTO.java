package com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.TaskDTO;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Datos para avanzar el estado de una tarea al siguiente")
public class TaskRequestNextStatusDTO {

    @Schema(description = "ID de la tarea a avanzar", example = "1")
    private Integer idTask;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(description = "Fecha requerida según la transición: fecha de inicio (PENDIENTE→INICIADA) o fecha de fin (INICIADA→COMPLETADA). Formato: dd/MM/yyyy", example = "15/05/2026")
    private LocalDate dateTask;

    @Schema(description = "ID del usuario que realiza la acción (debe ser el creador o el asignado)", example = "1")
    private Integer idUser;
}
