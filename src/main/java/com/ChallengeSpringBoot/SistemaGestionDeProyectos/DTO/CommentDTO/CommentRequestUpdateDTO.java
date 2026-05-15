package com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.CommentDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestUpdateDTO {

    // private Integer idComment;
    private String content;
    // private Integer idTask;
    private Integer idUser;
}
