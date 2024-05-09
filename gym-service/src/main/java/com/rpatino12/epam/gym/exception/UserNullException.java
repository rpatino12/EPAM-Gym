package com.rpatino12.epam.gym.exception;

public class UserNullException extends RuntimeException {

    public UserNullException() {
    }

    public UserNullException(String message) {
        super(message);
    }
}
