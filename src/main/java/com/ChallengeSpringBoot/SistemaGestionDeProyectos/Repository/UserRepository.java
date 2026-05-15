package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findByActiveTrue();

    Optional<User> findByIdUserAndActiveTrue(Integer idUser);

    Optional<User> findByNameAndActiveTrue(String name);

    Optional<User> findByEmailAndActiveTrue(String email);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

}
