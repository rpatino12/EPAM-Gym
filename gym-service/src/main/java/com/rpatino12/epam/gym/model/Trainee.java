package com.rpatino12.epam.gym.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "TRAINEE")
@Getter
@Setter
public class Trainee implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRAINEE_ID")
    private Long traineeId;
    @Column(name = "BIRTHDATE")
    private Date dateOfBirth;
    @Column(name = "ADDRESS")
    private String address;

    @OneToOne(cascade = {CascadeType.REFRESH, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinTable(name="TRAINEE2TRAINER",
            joinColumns=@JoinColumn(name="TRAINEE_ID"),
            inverseJoinColumns=@JoinColumn(name="TRAINER_ID"))
    @JsonIgnoreProperties(value = {"trainees", "trainings"})
    private Set<Trainer> trainers = new HashSet<>();

    @OneToMany(mappedBy = "trainee")
    @JsonManagedReference(value = "trainee")
    @JsonIgnore
    private List<Training> trainings;

    @Override
    public String toString() {
        return "Trainee{" +
                "traineeId=" + traineeId +
                ", dateOfBirth=" + dateOfBirth +
                ", address='" + address + '\'' +
                ", user=" + user +
                '}';
    }
}
