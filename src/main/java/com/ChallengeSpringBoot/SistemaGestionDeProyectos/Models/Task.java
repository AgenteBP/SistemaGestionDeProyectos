package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Enums.StatusTask;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tasks")
public class Task {

    @JsonProperty("id_task")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_task")
    private Integer idTask;

    @JsonProperty("name_task")
    @Column(name = "name_task", nullable = false, length = 100)
    private String nameTask;

    @JsonProperty("description")
    @Column(name = "description", length = 300)
    private String description;

    @JsonProperty("start_date")
    @Column(name = "start_date", nullable = true)
    private Date startDate;

    @JsonProperty("end_date")
    @Column(name = "end_date", nullable = true)
    private Date endDate;

    @Enumerated(EnumType.STRING)
    @JsonProperty("status_task")
    @Column(name = "status_task", nullable = false)
    private StatusTask statusTask;

    @ManyToOne
    @JoinColumn(name = "id_created_by", nullable = false)
    private User createdBy; // usuario creador de la tarea

    @ManyToOne
    @JoinColumn(name = "id_assigned_user", nullable = false)
    private User assignedUser; // usuario asignado a la tarea

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_project", referencedColumnName = "id_project", nullable = false)
    private Project project;

    @JsonProperty("active")
    @Column(name = "active")
    private Boolean active = true;
}
