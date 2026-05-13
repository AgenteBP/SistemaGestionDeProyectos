package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.Comment;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.Task;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findByTaskIdTaskAndActiveTrue(Integer idTask);

    Optional<Comment> findByIdCommentAndActiveTrue(Integer idComment);

    List<Comment> findByTaskProjectIdProjectAndActiveTrue(Integer idProject);

    List<Comment> findByTaskIn(List<Task> tasks);

}
