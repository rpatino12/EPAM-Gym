package com.rpatino12.epam.gym.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TrainingDto {
    @NotBlank
    private String name;
    private String date;
    @NotNull
    private Double duration;
    @NotBlank
    private String trainerUsername;
    @NotBlank
    private String traineeUsername;

    public TrainingDto() {
    }

    public TrainingDto(String name, String date, Double duration, String trainerUsername, String traineeUsername) {
        this.name = name;
        this.date = date;
        this.duration = duration;
        this.trainerUsername = trainerUsername;
        this.traineeUsername = traineeUsername;
    }
}
