package com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.CommentDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Datos requeridos para crear un comentario en una tarea")
public class CommentRequestDTO {

    @Schema(description = "Contenido del comentario", example = "Se completó el diagrama ER, pendiente revisión.")
    private String content;

    @Schema(description = "ID de la tarea donde se agrega el comentario", example = "1")
    private Integer idTask;

    @Schema(description = "ID del usuario que escribe el comentario", example = "2")
    private Integer idUser;
}
