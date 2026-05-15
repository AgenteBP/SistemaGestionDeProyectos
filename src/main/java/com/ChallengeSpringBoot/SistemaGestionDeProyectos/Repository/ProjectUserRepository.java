package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.Project;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.ProjectUser;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.User;

@Repository
public interface ProjectUserRepository extends JpaRepository<ProjectUser, Integer> {

    List<ProjectUser> findByProject(Project project);

    List<ProjectUser> findByProjectIdProjectAndActiveTrue(Integer idProject);

    List<ProjectUser> findByUser(User user);

    java.util.Optional<ProjectUser> findByProjectIdProjectAndUserIdUserAndActiveTrue(Integer idProject, Integer idUser);

    boolean existsByProjectAndUser(Project project, User user);

    boolean existsByProjectIdProjectAndUserIdUserAndActiveTrue(Integer idProject, Integer idUser);

    java.util.Optional<ProjectUser> findByProjectIdProjectAndUserIdUser(Integer idProject, Integer idUser);

    Integer countByUserAndProjectActiveTrue(User user);

    @Query("SELECT pu.user.idUser, COUNT(pu) FROM ProjectUser pu WHERE pu.project.active = true AND pu.active = true GROUP BY pu.user.idUser")
    List<Object[]> countProjectsByUserMember();
}
