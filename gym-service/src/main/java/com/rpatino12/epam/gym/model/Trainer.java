package com.rpatino12.epam.gym.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SPECIALIZATION_ID")
    private TrainingType specialization;

    @Transient
    @ManyToMany(mappedBy = "trainers", fetch = FetchType.LAZY)
    private Set<Trainee> trainees = new HashSet<>();

    @Transient
    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Training> trainingsList = new ArrayList<>();

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
