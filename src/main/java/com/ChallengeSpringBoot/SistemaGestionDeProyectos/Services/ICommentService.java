package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Services;

import java.util.List;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.CommentDTO.CommentRequestDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.CommentDTO.CommentResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.CommentDTO.CommentRequestUpdateDTO;

public interface ICommentService {

    public List<CommentResponseDTO> getAllCommentsByTask(Integer idTask);

    public CommentResponseDTO getCommentById(Integer idComment);

    public CommentResponseDTO saveComment(CommentRequestDTO commentRequestDTO);

    public CommentResponseDTO updateComment(Integer idComment, CommentRequestUpdateDTO commentRequestUpdateDTO);

    public void deleteComment(Integer idComment, Integer idUser);
}
