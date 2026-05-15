package com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.ProjectDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos requeridos para crear o actualizar un proyecto")
public class ProjectRequestDTO {

    @Schema(description = "Nombre único del proyecto", example = "Sistema de Facturación")
    private String nameProject;

    @Schema(description = "Descripción del proyecto (opcional)", example = "Módulo de facturación electrónica")
    private String description;

    @Schema(description = "ID del usuario propietario (owner) del proyecto", example = "1")
    private Integer idOwner;

    @Schema(description = "Lista de IDs de usuarios a asignar como miembros del equipo (opcional)", example = "[2, 3]")
    private List<Integer> userAssigned;
}
