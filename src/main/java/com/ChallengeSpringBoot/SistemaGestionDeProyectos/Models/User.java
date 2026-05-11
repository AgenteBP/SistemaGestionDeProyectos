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
import jakarta.persistence.Table;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @JsonProperty("id_user")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Integer idUser;

    @JsonProperty("name")
    @Column(name = "name", nullable = false)
    private String name;

    @JsonProperty("email")
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @JsonProperty("active")
    @Column(name = "active")
    private Boolean active = true;

    // Relación con Project (propietario)
    // @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch =
    // FetchType.LAZY)
    // @JsonIgnoreProperties("owner") // Evitar recursión
    // private List<Project> projectsOwned; // proyectos que posee

    // // Relación con Comment (autor)
    // @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch =
    // FetchType.LAZY)
    // @JsonIgnoreProperties("author")
    // private List<Comment> commentsWritten; // comentarios que escribió

    // // Relación con Task (asignado)
    // @ManyToMany(mappedBy = "assignedTo")
    // @JsonIgnoreProperties("assignedTo")
    // private List<Task> tasksAssigned; // tareas asignadas

}
