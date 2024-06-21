package com.rpatino12.epam.gym.util;

import com.rpatino12.epam.gym.dto.TraineeResponse;
import com.rpatino12.epam.gym.dto.TrainerItem;
import com.rpatino12.epam.gym.model.Trainee;
import com.rpatino12.epam.gym.model.Trainer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TraineeMapper {

    public TraineeResponse mapToDTO(Trainee trainee){
        TraineeResponse dto = new TraineeResponse();
        dto.setUsername(trainee.getUser().getUsername());
        dto.setFirstName(trainee.getUser().getFirstName());
        dto.setLastName(trainee.getUser().getLastName());
        dto.setDateOfBirth(trainee.getDateOfBirth().toString());
        dto.setAddress(trainee.getAddress());
        dto.setActive(trainee.getUser().getIsActive());

        List<TrainerItem> trainers = getTrainers(trainee);

        dto.setTrainersList(trainers);

        return dto;
    }

    private static List<TrainerItem> getTrainers(Trainee trainee) {
        List<TrainerItem> trainers = new ArrayList<>();
        for (Trainer trainer : trainee.getTrainers()) {
            TrainerItem trainerItem = new TrainerItem();
            trainerItem.setUsername(trainer.getUser().getUsername());
            trainerItem.setFirstName(trainer.getUser().getFirstName());
            trainerItem.setLastName(trainer.getUser().getLastName());
            trainerItem.setSpecialization(trainer.getSpecialization().getTrainingTypeName().name());
            trainers.add(trainerItem);
        }
        return trainers;
    }
}
