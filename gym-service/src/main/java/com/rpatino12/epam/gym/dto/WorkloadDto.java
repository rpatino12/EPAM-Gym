package com.rpatino12.epam.gym.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class WorkloadDto {
    private String username;
    private String firstName;
    private String lastName;
    private boolean status;
    private LocalDate trainingDate;
    private double trainingDuration;
}
