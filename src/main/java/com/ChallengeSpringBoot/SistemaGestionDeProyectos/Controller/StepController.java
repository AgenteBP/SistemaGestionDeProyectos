package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.StepDTO.StepRequestDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.DTO.StepDTO.StepResponseDTO;
import com.ChallengeSpringBoot.SistemaGestionDeProyectos.Services.IStepService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/step")
@RequiredArgsConstructor
public class StepController {

    private final IStepService stepService;

    // ==========================
    // --- OBTENER PASOS DE UNA TAREA ---
    // ==========================
    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<StepResponseDTO>> getStepsByTask(@PathVariable Integer taskId) {
        List<StepResponseDTO> steps = stepService.getAllStepsByTask(taskId);
        return ResponseEntity.ok(steps);
    }

    // ==========================
    // --- ACTUALIZAR ESTADO DEL PASO ---
    // ==========================
    @PutMapping("/next-status/{idStep}")
    public ResponseEntity<StepResponseDTO> nextStatusStep(@PathVariable Integer idStep, @RequestParam Integer idUser) {
        StepResponseDTO updatedStep = stepService.nextStatusStep(idStep, idUser);
        return ResponseEntity.ok(updatedStep);
    }

    // ==========================
    // --- ACTUALIZAR NOMBRE DEL PASO ---
    // ==========================
    @PutMapping("/update-name/{idStep}")
    public ResponseEntity<StepResponseDTO> updateNameStep(@PathVariable Integer idStep, @RequestParam String nameStep) {
        StepResponseDTO updatedStep = stepService.updateNameStep(idStep, nameStep);
        return ResponseEntity.ok(updatedStep);
    }

    // ==========================
    // --- CREAR PASO ---
    // ==========================
    @PostMapping
    public ResponseEntity<StepResponseDTO> createStep(@RequestBody StepRequestDTO stepRequestDTO) {
        StepResponseDTO createdStep = stepService.saveStep(stepRequestDTO);
        return ResponseEntity.ok(createdStep);
    }

    // ==========================
    // --- ELIMINAR PASO ---
    // ==========================
    @DeleteMapping("/{idStep}")
    public ResponseEntity<Void> deleteStep(@PathVariable Integer idStep) {
        stepService.deleteStep(idStep);
        return ResponseEntity.noContent().build();
    }

}
