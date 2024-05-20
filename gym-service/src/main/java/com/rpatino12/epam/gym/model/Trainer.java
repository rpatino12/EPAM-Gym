package com.rpatino12.epam.gym.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "TRAINER")
@Getter
@Setter
public class Trainer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRAINER_ID")
    private Long trainerId;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "SPECIALIZATION_ID")
    private TrainingType specialization;

    @ManyToMany(mappedBy = "trainers")
    @JsonIgnoreProperties(value = {"trainers", "trainings"})
    private List<Trainee> trainees;

    @OneToMany(mappedBy = "trainer")
    @JsonManagedReference(value = "trainer")
    @JsonIgnore
    private List<Training> trainings;

    @PrePersist
    public void prePersist() {
        setTypeName();
    }

    @PreUpdate
    public void preUpdate(){
        setTypeName();
    }

    private void setTypeName(){
        if (specialization.getTrainingTypeId() == 1) {
            specialization.setTrainingTypeName(TrainingTypes.Fitness);
        } else if (specialization.getTrainingTypeId() == 2) {
            specialization.setTrainingTypeName(TrainingTypes.Yoga);
        } else if (specialization.getTrainingTypeId() == 3) {
            specialization.setTrainingTypeName(TrainingTypes.Zumba);
        } else if (specialization.getTrainingTypeId() == 4) {
            specialization.setTrainingTypeName(TrainingTypes.Stretching);
        } else if (specialization.getTrainingTypeId() == 5) {
            specialization.setTrainingTypeName(TrainingTypes.Resistance);
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public String toString() {
        return "Trainer{" +
                "trainerId=" + trainerId +
                ", user=" + user +
                ", specialization=" + specialization +
                '}';
    }
}
