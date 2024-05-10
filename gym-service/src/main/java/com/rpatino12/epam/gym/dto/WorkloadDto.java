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

    public WorkloadDto() {
    }

    public WorkloadDto(String username, String firstName, String lastName, boolean status, LocalDate trainingDate, double trainingDuration) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
    }
}
