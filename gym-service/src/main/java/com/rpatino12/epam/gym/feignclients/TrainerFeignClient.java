package com.rpatino12.epam.gym.feignclients;

import com.rpatino12.epam.gym.dto.TrainerMonthlySummary;
import com.rpatino12.epam.gym.dto.WorkloadDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@FeignClient(name = "trainer-service", url = "http://localhost:8081")
public interface TrainerFeignClient {

    @GetMapping("/api/trainers")
    List<TrainerMonthlySummary> getAll();

    @GetMapping("/api/trainers/{username}/monthly-summary/{yearMonth}")
    Double getMonthlySummary(
            @PathVariable String username,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth);

    @PostMapping("/api/trainers")
    TrainerMonthlySummary postTrainerWorkload(@RequestBody WorkloadDto workloadDto);

    @PutMapping("/api/trainers/monthly-summary")
    void updateTrainerWorkload(@RequestBody WorkloadDto workloadDto);
}
