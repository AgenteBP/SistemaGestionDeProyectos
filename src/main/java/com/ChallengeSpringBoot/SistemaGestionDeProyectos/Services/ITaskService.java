package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Services;

import java.util.List;

import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.TaskDTO.TaskRequestDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.TaskDTO.TaskRequestNextStatusDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.TaskDTO.TaskRequestUpdateDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.TaskDTO.TaskResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Enums.StatusTask;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Models.User;

public interface ITaskService {

    TaskResponseDTO findTaskById(Integer idTask);

    TaskResponseDTO saveTask(TaskRequestDTO taskRequestDTO);

    TaskResponseDTO nextStatusTask(TaskRequestNextStatusDTO requestNextStatusDTO);

    TaskResponseDTO updateTask(Integer idTask, TaskRequestUpdateDTO taskRequestUpdateDTO);

    boolean hasActiveTasksAssigned(User user);

    void deleteTask(Integer idTask, Integer idUser);

    List<TaskResponseDTO> findTaskWithNameOrStatus(String nameTask, StatusTask statusTask);
}
