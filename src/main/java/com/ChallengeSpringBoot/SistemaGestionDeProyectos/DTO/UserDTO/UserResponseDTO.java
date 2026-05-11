package com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.UserDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Integer idUser;
    private String name;
    private String email;
}
