package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Enums.StatusStep;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.Step;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.Task;

@Repository
public interface StepRepository extends JpaRepository<Step, Integer> {

    List<Step> findByTaskIdTaskAndActiveTrue(Integer idTask);

    List<Step> findByTaskProjectIdProjectAndActiveTrue(Integer idProject);

    Integer countByTaskAndStatusStepNotAndActiveTrue(Task task, StatusStep statusStep);

}
