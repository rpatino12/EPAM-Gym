package com.rpatino12.epam.gym.service;

import com.rpatino12.epam.gym.dto.WorkloadDto;
import com.rpatino12.epam.gym.exception.ResourceNotFoundException;
import com.rpatino12.epam.gym.exception.TrainingNullException;
import com.rpatino12.epam.gym.feignclients.TrainerFeignClient;
import com.rpatino12.epam.gym.model.Trainee;
import com.rpatino12.epam.gym.model.Trainer;
import com.rpatino12.epam.gym.repo.TrainingRepository;
import com.rpatino12.epam.gym.model.Training;
import jakarta.annotation.PostConstruct;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainerFeignClient trainerFeignClient;

    public TrainingService(TrainingRepository trainingRepository, TraineeService traineeService, TrainerService trainerService, TrainerFeignClient trainerFeignClient) {
        this.trainingRepository = trainingRepository;
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainerFeignClient = trainerFeignClient;
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

    public void saveTrainerWorkload(WorkloadDto workloadDto){
        log.info(
                "Saving {}'s workload summary of trainer {}",
                workloadDto.getTrainingDate().getMonth().toString().toLowerCase(),
                workloadDto.getUsername()
        );
        trainerFeignClient.postTrainerWorkload(workloadDto);
    }

    public void updateTrainerWorkload(WorkloadDto workloadDto){
        log.info(
                "Updating {}'s workload summary of trainer {}",
                workloadDto.getTrainingDate().getMonth().toString().toLowerCase(),
                workloadDto.getUsername()
        );
        trainerFeignClient.updateTrainerWorkload(workloadDto);
    }

    public void deleteTrainerWorkload(WorkloadDto workloadDto){
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

        trainerFeignClient.updateTrainerWorkload(workloadDto);
    }

    @PostConstruct
    public void init(){
        log.info("Starting TrainingService");
    }
}
