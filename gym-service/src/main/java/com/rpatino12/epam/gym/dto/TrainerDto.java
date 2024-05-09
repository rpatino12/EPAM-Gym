package com.rpatino12.epam.gym.dto;

import com.rpatino12.epam.gym.model.Trainee;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class TrainerDto extends UserDto{
    @NotNull
    private long specializationId;
    private boolean active;

    public TrainerDto() {
    }

    public TrainerDto(String firstName, String lastName, boolean active) {
        super(firstName, lastName);
        this.active = active;
    }

    public TrainerDto(String firstName, String lastName, long specializationId) {
        super(firstName, lastName);
        this.specializationId = specializationId;
    }

    public TrainerDto(String username, String firstName, String lastName, long specializationId, boolean active) {
        super(username, firstName, lastName);
        this.specializationId = specializationId;
        this.active = active;
    }
}
