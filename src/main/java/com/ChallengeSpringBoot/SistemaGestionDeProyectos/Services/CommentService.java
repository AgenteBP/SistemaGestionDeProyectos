package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Services;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.CommentDTO.CommentRequestDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.CommentDTO.CommentRequestUpdateDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.CommentDTO.CommentResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.Comment;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.Project;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.Task;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.User;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.CommentRepository;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.ProjectUserRepository;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.TaskRepository;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectUserRepository projectUserRepository;

    // Obtener todos los comentarios de una tarea
    @Override
    public List<CommentResponseDTO> getAllCommentsByTask(Integer idTask) {
        if (idTask == null) {
            throw new RuntimeException("El ID de la tarea es obligatorio.");
        }

        // Verificar que la tarea exista y esté activa
        taskRepository.findByIdTaskAndActiveTrue(idTask)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada o inactiva con id: " + idTask));

        return commentRepository.findByTaskIdTaskAndActiveTrue(idTask).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    // Obtener un comentario por ID
    @Override
    public CommentResponseDTO getCommentById(Integer idComment) {
        if (idComment == null) {
            throw new RuntimeException("El ID del comentario es obligatorio.");
        }

        Comment comment = commentRepository.findByIdCommentAndActiveTrue(idComment)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado o inactivo con id: " + idComment));

        return mapToResponseDTO(comment);
    }

    // Guardar comentario
    @Transactional
    @Override
    public CommentResponseDTO saveComment(CommentRequestDTO commentRequestDTO) {
        validateCommentRequest(commentRequestDTO);

        Task task = taskRepository.findByIdTaskAndActiveTrue(commentRequestDTO.getIdTask())
                .orElseThrow(() -> new RuntimeException(
                        "Tarea no encontrada o inactiva con id: " + commentRequestDTO.getIdTask()));

        User author = userRepository.findByIdUserAndActiveTrue(commentRequestDTO.getIdUser())
                .orElseThrow(() -> new RuntimeException(
                        "Usuario no encontrado o inactivo con id: " + commentRequestDTO.getIdUser()));

        validateUserBelongsToProject(task.getProject(), author);

        Comment comment = new Comment();
        comment.setContent(commentRequestDTO.getContent());
        comment.setTask(task);
        comment.setAuthor(author);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setActive(true);

        return mapToResponseDTO(commentRepository.save(comment));
    }

    // Actualizar comentario
    @Transactional
    @Override
    public CommentResponseDTO updateComment(CommentRequestUpdateDTO commentRequestUpdateDTO) {
        if (commentRequestUpdateDTO.getIdComment() == null) {
            throw new RuntimeException("El ID del comentario es obligatorio.");
        }
        if (commentRequestUpdateDTO.getContent() == null || commentRequestUpdateDTO.getContent().trim().isEmpty()) {
            throw new RuntimeException("El contenido del comentario es obligatorio.");
        }
        if (commentRequestUpdateDTO.getIdUser() == null) {
            throw new RuntimeException("El ID del usuario es obligatorio.");
        }

        Comment comment = commentRepository.findByIdCommentAndActiveTrue(commentRequestUpdateDTO.getIdComment())
                .orElseThrow(() -> new RuntimeException(
                        "Comentario no encontrado o inactivo con id: " + commentRequestUpdateDTO.getIdComment()));

        // Validar permisos: autor del comentario o dueño del proyecto
        boolean isAuthor = comment.getAuthor().getIdUser().equals(commentRequestUpdateDTO.getIdUser());
        boolean isProjectOwner = comment.getTask().getProject().getOwner().getIdUser()
                .equals(commentRequestUpdateDTO.getIdUser());

        if (!isAuthor && !isProjectOwner) {
            throw new RuntimeException("No tienes permisos para modificar este comentario.");
        }

        comment.setContent(commentRequestUpdateDTO.getContent());
        return mapToResponseDTO(commentRepository.save(comment));
    }

    // Eliminar comentario
    @Transactional
    @Override
    public void deleteComment(Integer idComment, Integer idUser) {
        if (idComment == null) {
            throw new RuntimeException("El ID del comentario es obligatorio.");
        }
        if (idUser == null) {
            throw new RuntimeException("El ID del usuario es obligatorio.");
        }

        Comment comment = commentRepository.findByIdCommentAndActiveTrue(idComment)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado o inactivo con id: " + idComment));

        // Validar permisos: autor del comentario o dueño del proyecto
        boolean isAuthor = comment.getAuthor().getIdUser().equals(idUser);
        boolean isProjectOwner = comment.getTask().getProject().getOwner().getIdUser().equals(idUser);

        if (!isAuthor && !isProjectOwner) {
            throw new RuntimeException("No tienes permisos para eliminar este comentario.");
        }

        comment.setActive(false);
        commentRepository.save(comment);
    }

    /// Funciones auxiliares
    private void validateCommentRequest(CommentRequestDTO commentRequestDTO) {
        if (commentRequestDTO.getContent() == null || commentRequestDTO.getContent().trim().isEmpty()) {
            throw new RuntimeException("El contenido del comentario es obligatorio.");
        }
        if (commentRequestDTO.getIdTask() == null) {
            throw new RuntimeException("El ID de la tarea es obligatorio.");
        }
        if (commentRequestDTO.getIdUser() == null) {
            throw new RuntimeException("El ID del usuario es obligatorio.");
        }
    }

    private void validateUserBelongsToProject(Project project, User user) {
        boolean isOwner = project.getOwner() != null && project.getOwner().getIdUser().equals(user.getIdUser());
        boolean isAssignedToProject = projectUserRepository
                .existsByProjectIdProjectAndUserIdUserAndActiveTrue(project.getIdProject(), user.getIdUser());

        if (!isOwner && !isAssignedToProject) {
            throw new RuntimeException("El usuario debe pertenecer al proyecto para comentar.");
        }
    }

    private CommentResponseDTO mapToResponseDTO(Comment comment) {
        return new CommentResponseDTO(
                comment.getIdComment(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getTask().getIdTask(),
                comment.getTask().getNameTask(),
                comment.getAuthor().getIdUser(),
                comment.getAuthor().getName(),
                comment.getActive());
    }

}
