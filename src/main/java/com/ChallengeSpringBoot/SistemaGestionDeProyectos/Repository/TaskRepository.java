package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    boolean existsByNameTaskIgnoreCaseAndProjectIdProjectAndActiveTrue(String nameTask, Integer idProject);

    boolean existsByNameTaskIgnoreCaseAndProjectIdProjectAndActiveTrueAndIdTaskNot(String nameTask, Integer idProject,
            Integer idTask);

    @Query("SELECT t.assignedUser.idUser, COUNT(t) FROM Task t WHERE t.active = true GROUP BY t.assignedUser.idUser")
    List<Object[]> countTasksByAssignedUser();

    @Query("SELECT t.project.idProject, COUNT(t) FROM Task t WHERE t.statusTask = :status AND t.active = true GROUP BY t.project.idProject")
    List<Object[]> countTasksByProjectAndStatus(@Param("status") StatusTask status);

    @Query("SELECT t FROM Task t WHERE t.statusTask = :status AND t.active = true AND t.project.active = true")
    List<Task> findByStatusAndActiveProject(@Param("status") StatusTask status);
}
