package com.rpatino12.epam.gym.service;

import com.rpatino12.epam.gym.dto.TrainingTypeDto;
import com.rpatino12.epam.gym.model.TrainingType;
import com.rpatino12.epam.gym.model.TrainingTypes;
import com.rpatino12.epam.gym.repo.TrainingTypeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TrainingTypeServiceTest {

    @InjectMocks
    private TrainingTypeService trainingTypeService;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Test
    @DisplayName("getAll() return all trainingTypes list")
    void getAll() {
        List<TrainingType> trainingTypesList = getMockTrainingTypes();
        Mockito.doReturn(trainingTypesList).when(trainingTypeRepository).findAll();

        List<TrainingTypeDto> trainingTypeDtos = trainingTypeService.getAll();
        assertEquals(5, trainingTypeDtos.size());
        assertEquals(TrainingTypes.Fitness.toString(), trainingTypeDtos.get(0).getTrainingType());
    }

    private List<TrainingType> getMockTrainingTypes(){
        List<TrainingType> trainingTypes = new ArrayList<>();
        trainingTypes.add(new TrainingType(1L, TrainingTypes.Fitness));
        trainingTypes.add(new TrainingType(2L, TrainingTypes.Yoga));
        trainingTypes.add(new TrainingType(3L, TrainingTypes.Zumba));
        trainingTypes.add(new TrainingType(4L, TrainingTypes.Stretching));
        trainingTypes.add(new TrainingType(5L, TrainingTypes.Resistance));

        return trainingTypes;
    }
}