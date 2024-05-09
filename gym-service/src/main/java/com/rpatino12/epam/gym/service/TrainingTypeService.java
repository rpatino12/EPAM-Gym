package com.rpatino12.epam.gym.service;

import com.rpatino12.epam.gym.dto.TrainingTypeDto;
import com.rpatino12.epam.gym.model.TrainingType;
import com.rpatino12.epam.gym.repo.TrainingTypeRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class TrainingTypeService {
    private final TrainingTypeRepository trainingTypeRepository;

    public TrainingTypeService(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @Transactional(readOnly = true)
    public List<TrainingTypeDto> getAll(){
        Iterable<TrainingType> trainingTypes = trainingTypeRepository.findAll();
        List<TrainingTypeDto> trainingTypeDtos = new ArrayList<>();

        trainingTypes.forEach(trainingType -> {
            TrainingTypeDto trainingTypeDto = new TrainingTypeDto();
            trainingTypeDto.setTrainingTypeId(trainingType.getTrainingTypeId());
            trainingTypeDto.setTrainingType(trainingType.getTrainingTypeName().toString());
            trainingTypeDtos.add(trainingTypeDto);
        });

        trainingTypeDtos.sort(new Comparator<TrainingTypeDto>() {
            @Override
            public int compare(TrainingTypeDto o1, TrainingTypeDto o2) {
                return o1.getTrainingTypeId().compareTo(o2.getTrainingTypeId());
            }
        });
        log.info("Getting all training types");
        return trainingTypeDtos;
    }
}
