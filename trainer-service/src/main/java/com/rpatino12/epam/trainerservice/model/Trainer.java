package com.rpatino12.epam.trainerservice.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Map;

@Document(collection = "trainers")
@Getter
@Setter
public class Trainer implements Serializable {

    @Id
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private boolean status;
    private Map<String, Double> monthlySummary;
}