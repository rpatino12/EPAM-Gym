package com.rpatino12.epam.gym.feignclients;

import com.rpatino12.epam.gym.dto.TrainerMonthlySummary;
import com.rpatino12.epam.gym.dto.WorkloadDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class TrainerServiceFallback implements TrainerFeignClient{
    @Override
    public List<TrainerMonthlySummary> getAll() {
        log.warn("Couldn't connect with trainer-service application" + "\n" +
                "Implementing circuit breaker to get all trainers workload");
        return new ArrayList<>();
    }

    @Override
    public Double getMonthlySummary(String username, YearMonth yearMonth) {
        log.warn("Couldn't connect with trainer-service application" + "\n" +
                "Implementing circuit breaker to get trainer's monthly summary");
        return 99.99;
    }

    @Override
    public TrainerMonthlySummary postTrainerWorkload(WorkloadDto workloadDto) {
        log.warn("Couldn't connect with trainer-service application" + "\n" +
                "Implementing circuit breaker to save trainer's workload");
        return new TrainerMonthlySummary();
    }

    @Override
    public void updateTrainerWorkload(WorkloadDto workloadDto) {
        log.warn("Couldn't connect with trainer-service application" + "\n" +
                "Implementing circuit breaker to update trainer's workload");
    }
}
