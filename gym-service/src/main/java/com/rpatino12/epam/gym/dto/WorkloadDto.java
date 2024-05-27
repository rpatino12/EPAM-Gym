package com.rpatino12.epam.gym.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.rpatino12.epam.gym.util.SqlDateDeserializer;
import lombok.Data;

import java.io.Serializable;
import java.sql.Date;

@Data
public class WorkloadDto implements Serializable {
    private String username;
    private String firstName;
    private String lastName;
    private boolean status;
    @JsonDeserialize(using = SqlDateDeserializer.class)
    private Date trainingDate;
    private double trainingDuration;
    private String actionType = "ADD";

    public WorkloadDto() {
    }

    public WorkloadDto(String username, String firstName, String lastName, boolean status, Date trainingDate, double trainingDuration) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
    }
}
