package com.rpatino12.epam.gym.dto;

import com.rpatino12.epam.gym.model.Trainer;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class TraineeDto extends UserDto{
    private String birthdate;
    private String address;
    private boolean active;

    public TraineeDto() {
    }

    public TraineeDto(String username, String firstName, String lastName, boolean active) {
        super(username, firstName, lastName);
        this.active = active;
    }

    public TraineeDto(String firstName, String lastName, String birthdate, String address) {
        super(firstName, lastName);
        this.birthdate = birthdate;
        this.address = address;
    }

    public TraineeDto(String username, String firstName, String lastName, String birthdate, String address, boolean active) {
        super(username, firstName, lastName);
        this.birthdate = birthdate;
        this.address = address;
        this.active = active;
    }
}
