package com.rpatino12.epam.gym.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.List;

@Data
@JsonPropertyOrder({"username", "firstName", "lastName", "dateOfBirth", "address", "isActive", "trainersList"})
public class TraineeResponse {
    private String username;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String address;
    @JsonProperty("isActive")
    private boolean isActive;
    private List<TrainerItem> trainersList;
}
