package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.UserDTO.UserRequestDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.UserDTO.UserRequestUpdateDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.UserDTO.UserResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Services.IUserService;
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
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Operaciones de alta, baja, modificación y consulta de usuarios")
public class UserController {

    private final IUserService userService;

    // ==========================
    // --- OBTENER USUARIOS ---
    // ==========================

    @Operation(summary = "Obtener todos los usuarios", description = "Retorna la lista completa de usuarios, tanto activos como inactivos.")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida correctamente")
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Obtener usuarios activos", description = "Retorna únicamente los usuarios con estado activo.")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios activos obtenida correctamente")
    @GetMapping("/active")
    public ResponseEntity<List<UserResponseDTO>> getActiveUsers() {
        return ResponseEntity.ok(userService.getAllUsersWithActive());
    }

    @Operation(summary = "Obtener usuario por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(
            @Parameter(description = "ID del usuario", example = "1") @PathVariable Integer id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "Obtener usuario por nombre")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @GetMapping("/name/{name}")
    public ResponseEntity<UserResponseDTO> getUserByName(
            @Parameter(description = "Nombre del usuario", example = "Braian") @PathVariable String name) {
        return ResponseEntity.ok(userService.getUserByName(name));
    }

    @Operation(summary = "Obtener usuario por email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(
            @Parameter(description = "Email del usuario", example = "braian@gmail.com") @PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    // ==========================
    // --- CREAR USUARIO ---
    // ==========================

    @Operation(summary = "Crear un nuevo usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o email ya registrado", content = @Content)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del nuevo usuario",
            required = true,
            content = @Content(schema = @Schema(implementation = UserRequestDTO.class),
                    examples = @ExampleObject(value = """
                            {
                              "name": "Braian",
                              "email": "braian@gmail.com"
                            }
                            """)))
    @PostMapping("/save")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserRequestDTO userRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userRequestDTO));
    }

    // ==========================
    // --- ACTUALIZAR USUARIO ---
    // ==========================

    @Operation(summary = "Actualizar datos de un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Nuevos datos del usuario",
            required = true,
            content = @Content(schema = @Schema(implementation = UserRequestUpdateDTO.class),
                    examples = @ExampleObject(value = """
                            {
                              "name": "Braian Actualizado"
                            }
                            """)))
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @Parameter(description = "ID del usuario a actualizar", example = "1") @PathVariable Integer id,
            @RequestBody UserRequestUpdateDTO userRequestUpdateDTO) {
        return ResponseEntity.ok(userService.updateUser(id, userRequestUpdateDTO));
    }

    // ==========================
    // --- BORRAR USUARIO ---
    // ==========================

    @Operation(summary = "Eliminar un usuario (baja lógica)", description = "Desactiva el usuario. No elimina el registro de la base de datos.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "El usuario tiene proyectos o tareas activas asignadas", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID del usuario a eliminar", example = "1") @PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
