package com.rpatino12.epam.gym.service;

import com.rpatino12.epam.gym.dto.WorkloadDto;
import com.rpatino12.epam.gym.exception.ResourceNotFoundException;
import com.rpatino12.epam.gym.exception.TrainingNullException;
import com.rpatino12.epam.gym.model.Trainee;
import com.rpatino12.epam.gym.model.Trainer;
import com.rpatino12.epam.gym.repo.TrainingRepository;
import com.rpatino12.epam.gym.model.Training;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TrainingService {

    @Autowired
    private RestTemplate restTemplate;

    private final TrainingRepository trainingRepository;
    private final TraineeService traineeService;
    private final TrainerService trainerService;

    public TrainingService(TrainingRepository trainingRepository, TraineeService traineeService, TrainerService trainerService) {
        this.trainingRepository = trainingRepository;
        this.traineeService = traineeService;
        this.trainerService = trainerService;
    }

    // Training Service class should support possibility to create/select Training profile.
    @Transactional
    public boolean save(Training newTraining, String traineeUsername, String trainerUsername){
        if (null == newTraining){
            log.error("Cannot save a null or empty entity");
            throw new TrainingNullException("Training cannot be null");
        }
        Optional<Trainee> trainee = traineeService.getByUsername(traineeUsername);
        Optional<Trainer> trainer = trainerService.getByUsername(trainerUsername);
        if (trainee.isPresent() && trainer.isPresent()){
            newTraining.setTrainee(trainee.get());
            newTraining.setTrainer(trainer.get());
            newTraining.setTrainingType(trainer.get().getSpecialization());
            Training training = trainingRepository.save(newTraining);

            WorkloadDto workloadDto = new WorkloadDto(
                    trainerUsername,
                    trainer.get().getUser().getFirstName(),
                    trainer.get().getUser().getLastName(),
                    trainer.get().getUser().getIsActive(),
                    training.getTrainingDate().toLocalDate(),
                    training.getTrainingDuration());
            this.saveTrainerWorkload(workloadDto);
            this.updateTrainerWorkload(workloadDto);

            log.info("Creating training: " + training);
            return true;
        } else {
            log.info("Trainee/Trainer not found");
            return false;
        }
    }

    @Transactional(readOnly = true)
    public List<Training> getAll(){
        log.info("Getting all trainings");
        List<Training> trainings = trainingRepository.findAll();
        if (trainings.isEmpty()){
            log.error("There are no trainings registered");
            throw new ResourceNotFoundException("Training");
        }
        return trainings;
    }

    @Transactional(readOnly = true)
    public Optional<List<Training>> getByTraineeUsername(String username){
        log.info("Getting trainee " + username + " trainings: ");
        return trainingRepository.findTrainingByTraineeUserUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<List<Training>> getByTrainerUsername(String username){
        log.info("Getting trainer " + username + " trainings: ");
        return trainingRepository.findTrainingByTrainerUserUsername(username);
    }

    public Void saveTrainerWorkload(WorkloadDto workloadDto){
        log.info(
                "Saving {}'s workload summary of trainer {}",
                workloadDto.getTrainingDate().getMonth().toString().toLowerCase(),
                workloadDto.getUsername()
        );
        return restTemplate.postForObject(
                "http://localhost:8081/api/trainers",
                workloadDto,
                Void.class
        );
    }

    public Void updateTrainerWorkload(WorkloadDto workloadDto){
        log.info(
                "Updating {}'s workload summary of trainer {}",
                workloadDto.getTrainingDate().getMonth().toString().toLowerCase(),
                workloadDto.getUsername()
        );
        return restTemplate.exchange(
                "http://localhost:8081/api/trainers/monthly-summary",
                HttpMethod.PUT,
                new HttpEntity<>(workloadDto),
                Void.class
        ).getBody();
    }

    public Void deleteTrainerWorkload(WorkloadDto workloadDto){
        log.info(
                "Deleting {} workload of trainer {}",
                workloadDto.getTrainingDate(),
                workloadDto.getUsername()
        );

        Optional<Training> optionalTraining = trainingRepository.findByTrainingDateAndTrainerUserUsername(
                Date.valueOf(workloadDto.getTrainingDate()),
                workloadDto.getUsername()
        );
        if (optionalTraining.isPresent()){
            workloadDto.setTrainingDuration(optionalTraining.get().getTrainingDuration());
        } else {
            workloadDto.setTrainingDuration(0.0);
        }

        return restTemplate.exchange(
                "http://localhost:8081/api/trainers/monthly-summary",
                HttpMethod.PUT,
                new HttpEntity<>(workloadDto),
                Void.class
        ).getBody();
    }

    @PostConstruct
    public void init(){
        log.info("Starting TrainingService");
    }
}
