package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.Project;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.User;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {

    Optional<Project> findByIdProjectAndActiveTrue(Integer idProject);

    boolean existsByNameProjectIgnoreCase(String nameProject);
    boolean existsByOwnerAndActiveTrue(User owner);
}
