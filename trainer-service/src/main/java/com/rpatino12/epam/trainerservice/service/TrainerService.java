package com.rpatino12.epam.trainerservice.service;

import com.rpatino12.epam.trainerservice.dto.TrainerDto;
import com.rpatino12.epam.trainerservice.dto.WorkloadDto;
import com.rpatino12.epam.trainerservice.model.Trainer;
import com.rpatino12.epam.trainerservice.repo.TrainerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TrainerService {
    private final TrainerRepository trainerRepository;
    private static final String TRAINING_QUEUE = "training.save.queue";

    public TrainerService(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    public List<Trainer> getAllTrainers() {
        log.info("Getting the registered monthly workloads of all trainers");
        return trainerRepository.findAll();
    }

    @JmsListener(destination = TRAINING_QUEUE)
    @Transactional
    public void saveTrainer(WorkloadDto workloadDto) {

        Trainer trainer = new Trainer();
        trainer.setUsername(workloadDto.getUsername());
        trainer.setFirstName(workloadDto.getFirstName());
        trainer.setLastName(workloadDto.getLastName());
        trainer.setStatus(workloadDto.isStatus());
        trainer.setMonthlySummary(new HashMap<>());

        if (!trainerRepository.existsByUsername(trainer.getUsername())){
            log.info("Saving workload summary of trainer {}", trainer.getUsername());
            trainerRepository.save(trainer);
            updateMonthlySummary(workloadDto);
        } else {
            updateMonthlySummary(workloadDto);
        }
    }

    @Transactional
    public void updateMonthlySummary(WorkloadDto workloadDto) {
        TrainerDto trainerDto = new TrainerDto();
        trainerDto.setUsername(workloadDto.getUsername());
        trainerDto.setFirstName(workloadDto.getFirstName());
        trainerDto.setLastName(workloadDto.getLastName());
        trainerDto.setStatus(workloadDto.isStatus());
        trainerDto.setTrainingDate(workloadDto.getTrainingDate().toLocalDate());
        trainerDto.setTrainingDuration(workloadDto.getTrainingDuration());
        trainerDto.setActionType(workloadDto.getActionType());

        Optional<Trainer> trainerOptional = trainerRepository.findByUsername(trainerDto.getUsername());
        if(trainerOptional.isPresent()) {
            Trainer trainer = trainerOptional.get();
            String yearMonth = YearMonth.from(trainerDto.getTrainingDate()).toString();

            Double currentDuration = trainer.getMonthlySummary().getOrDefault(yearMonth, 0.0);
            double newDuration;
            if (trainerDto.getActionType().equals("DELETE")){
                newDuration = currentDuration - trainerDto.getTrainingDuration();
                newDuration = (newDuration < 0) ? 0 : newDuration;
            } else {
                newDuration = currentDuration + trainerDto.getTrainingDuration();
            }
            trainer.getMonthlySummary().put(yearMonth, newDuration);

            log.info(
                    "Updating {} workload summary of trainer {}",
                    yearMonth,
                    trainer.getUsername()
            );
            trainerRepository.save(trainer);
        }
    }

    public Double getMonthlySummary(String username, YearMonth yearMonth) {
        log.info("Getting {}'s workload summary of trainer {}", yearMonth.getMonth().toString().toLowerCase(), username);

        Optional<Trainer> trainerOptional = trainerRepository.findByUsername(username);
        if(trainerOptional.isPresent()) {
            Trainer trainer = trainerOptional.get();
            return trainer.getMonthlySummary().getOrDefault(yearMonth.toString(), 0.0);
        }
        return 0.0;
    }
}
