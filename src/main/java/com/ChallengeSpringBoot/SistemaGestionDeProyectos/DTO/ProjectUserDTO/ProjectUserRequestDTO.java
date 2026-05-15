package com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectUserDTO;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para asignar múltiples usuarios a un proyecto")
public class ProjectUserRequestDTO {

    @Schema(description = "ID del proyecto al que se asignan los usuarios", example = "1")
    Integer idProject;

    @Schema(description = "ID del owner del proyecto (quien autoriza la asignación)", example = "1")
    Integer idOwner;

    @Schema(description = "Lista de IDs de usuarios a asignar al proyecto", example = "[2, 3, 4]")
    List<Integer> idUsers;
}
