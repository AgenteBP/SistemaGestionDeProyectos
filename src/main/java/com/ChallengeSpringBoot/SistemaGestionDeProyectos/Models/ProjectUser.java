package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "project_users")
public class ProjectUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_project_user")
    private Integer idProjectUser;

    @ManyToOne
    @JoinColumn(name = "id_project", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @JsonProperty("active")
    @Column(name = "active")
    private Boolean active = true;

}
