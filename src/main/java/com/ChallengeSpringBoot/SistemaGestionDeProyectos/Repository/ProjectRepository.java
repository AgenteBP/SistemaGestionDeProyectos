package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.Project;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.User;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {

    Optional<Project> findByIdProjectAndActiveTrue(Integer idProject);

    boolean existsByNameProjectIgnoreCase(String nameProject);

    boolean existsByNameProjectIgnoreCaseAndActiveTrue(String nameProject);

    boolean existsByNameProjectIgnoreCaseAndActiveTrueAndIdProjectNot(String nameProject, Integer idProject);

    boolean existsByOwnerAndActiveTrue(User owner);

    Integer countByOwnerAndActiveTrue(User user);

    List<Project> findByActiveTrue();

    @Query("SELECT p.owner.idUser, COUNT(p) FROM Project p WHERE p.active = true GROUP BY p.owner.idUser")
    List<Object[]> countProjectsByOwner();
}
