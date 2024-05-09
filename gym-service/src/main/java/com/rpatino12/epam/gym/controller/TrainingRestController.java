package com.rpatino12.epam.gym.controller;

import com.rpatino12.epam.gym.dto.TrainingDto;
import com.rpatino12.epam.gym.dto.TrainingTypeDto;
import com.rpatino12.epam.gym.model.Trainer;
import com.rpatino12.epam.gym.model.Training;
import com.rpatino12.epam.gym.model.TrainingType;
import com.rpatino12.epam.gym.service.TrainerService;
import com.rpatino12.epam.gym.service.TrainingService;
import com.rpatino12.epam.gym.service.TrainingTypeService;
import com.rpatino12.epam.gym.util.DateUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/trainings")
@Tag(name = "Training Controller", description = "Operations for creating and retrieving trainings")
@Slf4j
public class TrainingRestController {
    private final TrainingService trainingService;
    private final TrainingTypeService trainingTypeService;
    private final TrainerService trainerService;
    private final DateUtils dateUtils;

    public TrainingRestController(TrainingService trainingService, TrainingTypeService trainingTypeService, TrainerService trainerService, DateUtils dateUtils) {
        this.trainingService = trainingService;
        this.trainingTypeService = trainingTypeService;
        this.trainerService = trainerService;
        this.dateUtils = dateUtils;
    }

    // Select training method (GET)
    @GetMapping
    @Operation(summary = "View all trainings")
    public ResponseEntity<List<Training>> getAll(){
        log.info("Received GET request to /api/trainings");

        return new ResponseEntity<>(trainingService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/trainee-username/{username}")
    @Operation(summary = "Retrieve specific training with the supplied trainee username")
    public ResponseEntity<List<Training>> getByTrainee(@PathVariable(name = "username") String username){
        log.info("Received GET request to /api/trainings/trainee-username/{username} with parameter: {}", username);

        List<Training> trainings = trainingService.getByTraineeUsername(username).orElse(new ArrayList<>());
        return trainings.isEmpty() ?
                new ResponseEntity<>(HttpStatus.NOT_FOUND) :
                new ResponseEntity<>(trainings, HttpStatus.OK);
    }

    @GetMapping("/trainer-username/{username}")
    @Operation(summary = "Retrieve specific training with the supplied trainer username")
    public ResponseEntity<List<Training>> getByTrainer(@PathVariable(name = "username") String username){
        log.info("Received GET request to /api/trainings/trainer-username/{username} with parameter: {}", username);

        List<Training> trainings = trainingService.getByTrainerUsername(username).orElse(new ArrayList<>());
        return trainings.isEmpty() ?
                new ResponseEntity<>(HttpStatus.NOT_FOUND) :
                new ResponseEntity<>(trainings, HttpStatus.OK);
    }

    // Create training method (POST)
    @PostMapping("/save")
    @Operation(summary = "Create a new training")
    public ResponseEntity<String> createTraining(@Valid @RequestBody TrainingDto trainingDto){
        log.info("Received POST request to /api/trainings/save");

        Date date = new Date(dateUtils.createDateFromDateString(trainingDto.getDate()).getTime());

        Training training = new Training();
        training.setTrainingName(trainingDto.getName());
        training.setTrainingDate(date);
        training.setTrainingDuration(trainingDto.getDuration());

        boolean isTrainingSaved = trainingService.save(
                training,
                trainingDto.getTraineeUsername(),
                trainingDto.getTrainerUsername()
        );

        if (isTrainingSaved){
            return new ResponseEntity<>("Training created", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Trainee/Trainer not found", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/training-types")
    @Operation(summary = "View all training types")
    public ResponseEntity<List<TrainingTypeDto>> getTrainingTypes(){
        log.info("Received GET request to /api/trainings/training-types");

        return new ResponseEntity<>(trainingTypeService.getAll(), HttpStatus.OK);
    }
}
