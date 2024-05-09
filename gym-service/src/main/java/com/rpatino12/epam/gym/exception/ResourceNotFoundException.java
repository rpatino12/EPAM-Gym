package com.rpatino12.epam.gym.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException{
    private String resourceName;
    private String fieldName;
    private Object value;

    public ResourceNotFoundException() {
        super();
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object value) {
        super(String.format("Couldn't find %s with attribute %s='%s'", resourceName, fieldName, value));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.value = value;
    }

    public ResourceNotFoundException(String resourceName) {
        super(String.format("Couldn't find %s objects in the database", resourceName));
        this.resourceName = resourceName;
    }

}
