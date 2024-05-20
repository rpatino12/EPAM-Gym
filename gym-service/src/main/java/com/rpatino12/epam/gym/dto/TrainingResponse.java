package com.rpatino12.epam.gym.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrainingResponse {
    private String name;
    private String date;
    private String type;
    private Double duration;
    private String trainerUsername;
    private String traineeUsername;

    public TrainingResponse() {
    }

    public TrainingResponse(String name, String date, String type, Double duration, String trainerUsername, String traineeUsername) {
        this.name = name;
        this.date = date;
        this.type = type;
        this.duration = duration;
        this.trainerUsername = trainerUsername;
        this.traineeUsername = traineeUsername;
    }
}
