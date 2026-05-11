package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.Project;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.ProjectUser;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.User;

@Repository
public interface ProjectUserRepository extends JpaRepository<ProjectUser, Integer> {

    List<ProjectUser> findByProject(Project project);

    List<ProjectUser> findByUser(User user);

    boolean existsByProjectAndUser(Project project, User user);

    boolean existsByProjectIdProjectAndUserIdUserAndActiveTrue(Integer idProject, Integer idUser);
}
