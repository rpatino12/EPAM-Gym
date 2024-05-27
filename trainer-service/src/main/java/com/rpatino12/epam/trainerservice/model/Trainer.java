package com.rpatino12.epam.trainerservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Entity
@Table(name = "TRAINERS")
@Getter
@Setter
public class Trainer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "status")
    private boolean status;

    @ElementCollection
    @MapKeyColumn(name="year_month")
    @Column(name="duration")
    @CollectionTable(name="monthly_summary", joinColumns=@JoinColumn(name="trainer_id"))
    private Map<String, Double> monthlySummary;
}
