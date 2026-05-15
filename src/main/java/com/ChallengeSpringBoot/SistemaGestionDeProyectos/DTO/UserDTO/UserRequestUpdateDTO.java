package com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.UserDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestUpdateDTO {
    private String name;
    private String email;
}
