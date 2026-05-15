package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Enums.StatusStep;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.Step;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.Task;

@Repository
public interface StepRepository extends JpaRepository<Step, Integer> {

    List<Step> findByTaskIdTaskAndActiveTrue(Integer idTask);

    List<Step> findByTaskProjectIdProjectAndActiveTrue(Integer idProject);

    Integer countByTaskAndStatusStepNotAndActiveTrue(Task task, StatusStep statusStep);

    boolean existsByNameStepIgnoreCaseAndTaskIdTaskAndActiveTrue(String nameStep, Integer idTask);

    boolean existsByNameStepIgnoreCaseAndTaskIdTaskAndActiveTrueAndIdStepNot(String nameStep, Integer idTask, Integer idStep);

    @Query("SELECT s.task.idTask, COUNT(s) FROM Step s WHERE s.statusStep != :status AND s.active = true GROUP BY s.task.idTask")
    List<Object[]> countStepsNotCompletedByTask(@Param("status") StatusStep status);
}
