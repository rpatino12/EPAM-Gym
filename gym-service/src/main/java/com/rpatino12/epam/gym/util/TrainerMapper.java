package com.rpatino12.epam.gym.util;

import com.rpatino12.epam.gym.dto.TrainerResponse;
import com.rpatino12.epam.gym.dto.UserDto;
import com.rpatino12.epam.gym.model.Trainee;
import com.rpatino12.epam.gym.model.Trainer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TrainerMapper {

    public TrainerResponse mapToDTO(Trainer trainer){
        TrainerResponse dto = new TrainerResponse();
        dto.setUsername(trainer.getUser().getUsername());
        dto.setFirstName(trainer.getUser().getFirstName());
        dto.setLastName(trainer.getUser().getLastName());
        dto.setSpecialization(trainer.getSpecialization().getTrainingTypeName().name());
        dto.setActive(trainer.getUser().getIsActive());

        List<UserDto> trainees = getTrainees(trainer);

        dto.setTraineesList(trainees);

        return dto;
    }

    private static List<UserDto> getTrainees(Trainer trainer) {
        List<UserDto> trainees = new ArrayList<>();
        for (Trainee trainee : trainer.getTrainees()) {
            UserDto traineeItem = new UserDto();
            traineeItem.setUsername(trainee.getUser().getUsername());
            traineeItem.setFirstName(trainee.getUser().getFirstName());
            traineeItem.setLastName(trainee.getUser().getLastName());
            trainees.add(traineeItem);
        }
        return trainees;
    }
}
