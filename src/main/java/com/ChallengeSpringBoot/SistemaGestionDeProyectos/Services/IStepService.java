package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Services;

import java.util.List;

import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.StepDTO.StepRequestDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.StepDTO.StepResponseDTO;

public interface IStepService {

    List<StepResponseDTO> getAllStepsByTask(Integer idTask);

    StepResponseDTO saveStep(StepRequestDTO stepRequestDTO);

    StepResponseDTO nextStatusStep(Integer idStep, Integer idUser);

    void deleteStep(Integer idStep);

    StepResponseDTO updateNameStep(Integer idStep, String nameStep);
}
