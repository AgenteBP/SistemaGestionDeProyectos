package com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.UserDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos requeridos para crear un nuevo usuario")
public class UserRequestDTO {

    @Schema(description = "Nombre completo del usuario", example = "Braian García")
    private String name;

    @Schema(description = "Email único del usuario", example = "braian@gmail.com")
    private String email;
}
