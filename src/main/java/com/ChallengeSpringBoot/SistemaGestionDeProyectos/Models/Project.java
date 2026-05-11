package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "projects")
public class Project {

    @JsonProperty("id_project")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_project")
    private Integer idProject;

    @JsonProperty("name_project")
    @Column(name = "name_project", unique = true)
    private String nameProject;

    @JsonProperty("description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "id_owner", nullable = false)
    private User owner;

    // @ManyToMany
    // @JoinTable(name = "project_users", // Hibernate crea esta tabla física
    // joinColumns = @JoinColumn(name = "id_project"), inverseJoinColumns =
    // @JoinColumn(name = "id_user"))
    // private List<User> userAssigned; // usuarios asignados

    @JsonProperty("active")
    @Column(name = "active")
    private Boolean active = true;
}
