package com.rpatino12.epam.gym.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "TRAINING_TYPE")
@Getter
@Setter
public class TrainingType implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRAINING_TYPE_ID")
    private Long trainingTypeId;
    @Column(name = "TRAINING_TYPE_NAME")
    @Enumerated(EnumType.STRING)
    private TrainingTypes trainingTypeName;

    public TrainingType() {
    }

    public TrainingType(Long trainingTypeId, TrainingTypes trainingTypeName) {
        this.trainingTypeId = trainingTypeId;
        this.trainingTypeName = trainingTypeName;
    }

    @Override
    public String toString() {
        return "TrainingType{" +
                "trainingTypeId=" + trainingTypeId +
                ", trainingTypeName='" + trainingTypeName + '\'' +
                '}';
    }
}
