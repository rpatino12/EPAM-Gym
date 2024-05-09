package com.rpatino12.epam.gym.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateUserDto extends UserLogin{
    private String newPassword;

    public UpdateUserDto() {
    }

    public UpdateUserDto(String username, String password, String newPassword) {
        super(username, password);
        this.newPassword = newPassword;
    }
}
