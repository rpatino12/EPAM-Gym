package com.rpatino12.epam.gym.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.List;

@Data
@JsonPropertyOrder({"username", "firstName", "lastName", "specialization", "isActive", "traineesList"})
public class TrainerResponse {
    private String username;
    private String firstName;
    private String lastName;
    private String specialization;
    @JsonProperty("isActive")
    private boolean isActive;
    private List<UserDto> traineesList;
}
