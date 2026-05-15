package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Enums.StatusTask;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.Task;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.User;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

        List<Task> findByProjectIdProject(Integer idProject);

        List<Task> findByProjectIdProjectAndActiveTrue(Integer idProject);

        java.util.Optional<Task> findByIdTaskAndActiveTrue(Integer idTask);

        boolean existsByAssignedUserAndActiveTrue(User user);

        boolean existsByProjectIdProjectAndAssignedUser_IdUserAndActiveTrue(Integer idProject, Integer idUser);

        boolean existsByProjectIdProjectAndCreatedBy_IdUserAndActiveTrue(Integer idProject, Integer idUser);

        Integer countByAssignedUserAndActiveTrue(User user);

        Integer countByProjectIdProjectAndStatusTaskAndActiveTrue(Integer idProject, StatusTask statusTask);

        List<Task> findByProjectIdProjectAndStatusTaskAndActiveTrueOrderByStartDateAsc(Integer idProject,
                        StatusTask statusTask);

        List<Task> findByProjectIdProjectAndStatusTaskAndActiveTrueOrderByEndDateAsc(Integer idProject,
                        StatusTask statusTask);

        List<Task> findByActiveTrue();

}
