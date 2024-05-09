package com.rpatino12.epam.trainerservice.service;

import com.rpatino12.epam.trainerservice.dto.TrainerDto;
import com.rpatino12.epam.trainerservice.model.Trainer;
import com.rpatino12.epam.trainerservice.repo.TrainerRepository;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
public class TrainerService {
    private final TrainerRepository trainerRepository;

    public TrainerService(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    public List<Trainer> getAllTrainers() {
        return trainerRepository.findAll();
    }

    public Trainer saveTrainer(Trainer trainer) {
        return trainerRepository.save(trainer);
    }

    public void updateMonthlySummary(TrainerDto trainerDto) {
        Optional<Trainer> trainerOptional = trainerRepository.findByUsername(trainerDto.getUsername());
        if(trainerOptional.isPresent()) {
            Trainer trainer = trainerOptional.get();
            String yearMonth = YearMonth.from(trainerDto.getTrainingDate()).toString();

            Double currentDuration = trainer.getMonthlySummary().getOrDefault(yearMonth, 0.0);
            double newDuration = currentDuration + trainerDto.getTrainingDuration();
            trainer.getMonthlySummary().put(yearMonth, newDuration);

            trainerRepository.save(trainer);
        }
    }

    public Double getMonthlySummary(String username, YearMonth yearMonth) {
        Optional<Trainer> trainerOptional = trainerRepository.findByUsername(username);
        if(trainerOptional.isPresent()) {
            Trainer trainer = trainerOptional.get();
            return trainer.getMonthlySummary().getOrDefault(yearMonth.toString(), 0.0);
        }
        return null;
    }
}
