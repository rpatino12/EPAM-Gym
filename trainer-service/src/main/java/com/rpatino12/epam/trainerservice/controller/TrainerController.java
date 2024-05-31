package com.rpatino12.epam.trainerservice.controller;

import com.rpatino12.epam.trainerservice.dto.WorkloadDto;
import com.rpatino12.epam.trainerservice.model.Trainer;
import com.rpatino12.epam.trainerservice.service.TrainerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/trainers")
@Slf4j
public class TrainerController {
    private final TrainerService trainerService;

    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @GetMapping
    public ResponseEntity<List<Trainer>> getAll() {
        log.info("Received GET request to /api/trainers");

        List<Trainer> trainers = trainerService.getAllTrainers();
        return ResponseEntity.ok(trainers);
    }

    @PostMapping
    public ResponseEntity postTrainer(@RequestBody WorkloadDto workloadDto) {
        log.info("Received POST request to /api/trainers");

        trainerService.saveTrainer(workloadDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{username}/monthly-summary/{yearMonth}")
    public ResponseEntity<Double> getMonthlySummary(
            @PathVariable String username,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {
        log.info("Received GET request to /api/trainers/{}/monthly-summary/{}", username, yearMonth);

        Double monthlySummary = trainerService.getMonthlySummary(username, yearMonth);
        return ResponseEntity.ok(monthlySummary);
    }
}
