package com.rpatino12.epam.gym.service;

import com.rpatino12.epam.gym.dto.WorkloadDto;
import com.rpatino12.epam.gym.exception.ResourceNotFoundException;
import com.rpatino12.epam.gym.exception.TrainingNullException;
import com.rpatino12.epam.gym.model.Trainee;
import com.rpatino12.epam.gym.model.Trainer;
import com.rpatino12.epam.gym.repo.TrainingRepository;
import com.rpatino12.epam.gym.model.Training;
import jakarta.annotation.PostConstruct;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private static final String TRAINING_QUEUE = "training.save.queue";

    private final JmsTemplate jmsTemplate;

    public TrainingService(TrainingRepository trainingRepository, TraineeService traineeService, TrainerService trainerService, JmsTemplate jmsTemplate) {
        this.trainingRepository = trainingRepository;
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.jmsTemplate = jmsTemplate;
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
                    training.getTrainingDate(),
                    training.getTrainingDuration());
            this.saveWorkload(workloadDto);

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
        log.info("Getting " + username + " trainings");
        return trainingRepository.findTrainingByTraineeUserUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<List<Training>> getByTrainerUsername(String username){
        log.info("Getting " + username + " trainings");
        return trainingRepository.findTrainingByTrainerUserUsername(username);
    }

    @Transactional
    public void saveWorkload(WorkloadDto workloadDto){
        log.info(
                "Saving {} workload summary of trainer {}",
                workloadDto.getTrainingDate(),
                workloadDto.getUsername()
        );
        jmsTemplate.convertAndSend(TRAINING_QUEUE, workloadDto);
    }

    @Transactional
    public String deleteTrainerWorkload(WorkloadDto workloadDto){
        List<Training> trainings = trainingRepository.findByTrainingDateAndTrainerUserUsername(
                workloadDto.getTrainingDate(),
                workloadDto.getUsername()
        );

        if (!trainings.isEmpty()){
            log.info(
                    "Deleting {} workload of trainer {}",
                    workloadDto.getTrainingDate(),
                    workloadDto.getUsername()
            );
            Training training = trainings.get(0);
            workloadDto.setTrainingDuration(training.getTrainingDuration());
            workloadDto.setActionType("DELETE");
            jmsTemplate.convertAndSend(TRAINING_QUEUE, workloadDto);
            return "Delete successful";
        } else {
            log.error("There are no trainings of {} in {}", workloadDto.getUsername(), workloadDto.getTrainingDate());
            throw new ResourceNotFoundException("Training");
        }
    }

    @PostConstruct
    public void init(){
        log.info("Starting TrainingService");
    }
}
