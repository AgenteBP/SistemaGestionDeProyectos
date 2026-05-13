package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.CommentDTO.CommentRequestDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.CommentDTO.CommentRequestUpdateDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.CommentDTO.CommentResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Services.ICommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final ICommentService commentService;

    // ==========================
    // --- OBTENER COMENTARIOS DE UNA TAREA ---
    // ==========================
    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByTask(@PathVariable Integer taskId) {
        List<CommentResponseDTO> comments = commentService.getAllCommentsByTask(taskId);
        return ResponseEntity.ok(comments);
    }

    // ==========================
    // --- OBTENER UN COMENTARIO POR ID ---
    // ==========================
    @GetMapping("/{idComment}")
    public ResponseEntity<CommentResponseDTO> getCommentById(@PathVariable Integer idComment) {
        CommentResponseDTO comment = commentService.getCommentById(idComment);
        return ResponseEntity.ok(comment);
    }

    // ==========================
    // --- CREAR COMENTARIO ---
    // ==========================
    @PostMapping
    public ResponseEntity<CommentResponseDTO> createComment(@RequestBody CommentRequestDTO commentRequestDTO) {
        CommentResponseDTO createdComment = commentService.saveComment(commentRequestDTO);
        return ResponseEntity.ok(createdComment);
    }

    // ==========================
    // --- ACTUALIZAR COMENTARIO ---
    // ==========================
    @PutMapping("/update")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @RequestBody CommentRequestUpdateDTO commentRequestUpdateDTO) {
        CommentResponseDTO updatedComment = commentService.updateComment(commentRequestUpdateDTO);
        return ResponseEntity.ok(updatedComment);
    }

    // ==========================
    // --- ELIMINAR COMENTARIO ---
    // ==========================
    @DeleteMapping("/{idComment}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer idComment, @RequestParam Integer idUser) {
        commentService.deleteComment(idComment, idUser);
        return ResponseEntity.noContent().build();
    }

}
