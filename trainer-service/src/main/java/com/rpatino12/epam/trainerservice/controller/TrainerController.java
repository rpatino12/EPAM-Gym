package com.rpatino12.epam.trainerservice.controller;

import com.rpatino12.epam.trainerservice.dto.TrainerDto;
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
    public ResponseEntity<Trainer> postTrainer(@RequestBody Trainer trainer) {
        log.info("Received POST request to /api/trainers");

        return new ResponseEntity<>(trainerService.saveTrainer(trainer), HttpStatus.OK);
    }


    @PutMapping("/monthly-summary")
    public ResponseEntity<Void> updateMonthlySummary(@RequestBody TrainerDto trainerDto) {
        log.info("Received PUT request to /api/trainers/monthly-summary");

        trainerService.updateMonthlySummary(trainerDto);
        return ResponseEntity.ok().build();
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
