package com.rpatino12.epam.trainerservice.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TrainerDto {
    private String username;
    private String firstName;
    private String lastName;
    private boolean status;
    private LocalDate trainingDate;
    private double trainingDuration;
    private String actionType = "ADD";
}
