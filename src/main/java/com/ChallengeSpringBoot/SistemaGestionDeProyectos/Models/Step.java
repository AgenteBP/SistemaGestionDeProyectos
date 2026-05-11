package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Enums.StatusStep;
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
@Table(name = "steps")
public class Step {

    @JsonProperty("id_step")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_step")
    private Integer idStep;

    @JsonProperty("name_step")
    @Column(name = "name_step")
    private String nameStep;

    @Enumerated(EnumType.STRING)
    @JsonProperty("status_step")
    @Column(name = "status_step")
    private StatusStep statusStep;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_task", referencedColumnName = "id_task", nullable = false)
    private Task task;

    @JsonProperty("active")
    @Column(name = "active")
    private Boolean active = true;

}
