package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.CommentDTO.CommentRequestDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.CommentDTO.CommentRequestUpdateDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.CommentDTO.CommentResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Services.ICommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
@Tag(name = "Comentarios", description = "Gestión de comentarios asociados a tareas")
public class CommentController {

    private final ICommentService commentService;

    // ==========================
    // --- OBTENER COMENTARIOS DE UNA TAREA ---
    // ==========================

    @Operation(summary = "Obtener comentarios activos de una tarea")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de comentarios obtenida correctamente"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada", content = @Content)
    })
    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByTask(
            @Parameter(description = "ID de la tarea", example = "1") @PathVariable Integer taskId) {
        return ResponseEntity.ok(commentService.getAllCommentsByTask(taskId));
    }

    // ==========================
    // --- OBTENER UN COMENTARIO POR ID ---
    // ==========================

    @Operation(summary = "Obtener un comentario por su ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comentario encontrado"),
            @ApiResponse(responseCode = "404", description = "Comentario no encontrado", content = @Content)
    })
    @GetMapping("/{idComment}")
    public ResponseEntity<CommentResponseDTO> getCommentById(
            @Parameter(description = "ID del comentario", example = "1") @PathVariable Integer idComment) {
        return ResponseEntity.ok(commentService.getCommentById(idComment));
    }

    // ==========================
    // --- CREAR COMENTARIO ---
    // ==========================

    @Operation(summary = "Crear un comentario en una tarea")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comentario creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario sin permisos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tarea o usuario no encontrado", content = @Content)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del nuevo comentario",
            required = true,
            content = @Content(schema = @Schema(implementation = CommentRequestDTO.class),
                    examples = @ExampleObject(value = """
                            {
                              "content": "Se completó el diagrama ER, pendiente revisión.",
                              "idTask": 1,
                              "idUser": 2
                            }
                            """)))
    @PostMapping
    public ResponseEntity<CommentResponseDTO> createComment(@RequestBody CommentRequestDTO commentRequestDTO) {
        return ResponseEntity.ok(commentService.saveComment(commentRequestDTO));
    }

    // ==========================
    // --- ACTUALIZAR COMENTARIO ---
    // ==========================

    @Operation(summary = "Actualizar el contenido de un comentario",
            description = "Solo el autor del comentario puede modificarlo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comentario actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "El usuario no es el autor del comentario", content = @Content),
            @ApiResponse(responseCode = "404", description = "Comentario no encontrado", content = @Content)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Nuevo contenido del comentario y ID del usuario que edita",
            required = true,
            content = @Content(schema = @Schema(implementation = CommentRequestUpdateDTO.class),
                    examples = @ExampleObject(value = """
                            {
                              "content": "Diagrama revisado y aprobado.",
                              "idUser": 2
                            }
                            """)))
    @PutMapping("/{idComment}")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @Parameter(description = "ID del comentario a actualizar", example = "1") @PathVariable Integer idComment,
            @RequestBody CommentRequestUpdateDTO commentRequestUpdateDTO) {
        return ResponseEntity.ok(commentService.updateComment(idComment, commentRequestUpdateDTO));
    }

    // ==========================
    // --- ELIMINAR COMENTARIO ---
    // ==========================

    @Operation(summary = "Eliminar comentario (baja lógica)",
            description = "Solo el autor o el owner del proyecto puede eliminar el comentario.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Comentario eliminado correctamente"),
            @ApiResponse(responseCode = "400", description = "El usuario no tiene permisos para eliminar el comentario", content = @Content),
            @ApiResponse(responseCode = "404", description = "Comentario no encontrado", content = @Content)
    })
    @DeleteMapping("/{idComment}")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "ID del comentario a eliminar", example = "1") @PathVariable Integer idComment,
            @Parameter(description = "ID del usuario que realiza la acción", example = "1") @RequestParam Integer idUser) {
        commentService.deleteComment(idComment, idUser);
        return ResponseEntity.noContent().build();
    }
}
