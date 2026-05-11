package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.Step;

@Repository
public interface StepRepository extends JpaRepository<Step, Integer> {

    List<Step> findByTaskIdTaskAndActiveTrue(Integer idTask);
}
