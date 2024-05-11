package com.rpatino12.epam.gym.dto;

import lombok.Data;

import java.util.Map;

@Data
public class TrainerMonthlySummary {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private boolean status;
    private Map<String, Double> monthlySummary;
}
