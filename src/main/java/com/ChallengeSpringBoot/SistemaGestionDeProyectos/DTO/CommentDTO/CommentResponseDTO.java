package com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.CommentDTO;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDTO {

    private Integer idComment;
    private String content;
    private LocalDateTime createdAt;
    private Integer idTask;
    private String nameTask;
    private Integer idAuthor;
    private String nameAuthor;
    private Boolean active;

}
