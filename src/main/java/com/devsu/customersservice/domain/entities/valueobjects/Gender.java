package com.devsu.customersservice.domain.entities.valueobjects;

import com.devsu.customersservice.domain.exceptions.InvalidDomainValueException;
import java.util.Set;

public record Gender(String value) {
    private static final Set<String> ALLOWED_VALUES = Set.of("Male", "Female", "Other");

    public static final Gender MALE = new Gender("Male");
    public static final Gender FEMALE = new Gender("Female");
    public static final Gender OTHER = new Gender("Other");

    public Gender {
        if (value == null || !ALLOWED_VALUES.contains(value)) {
            throw new InvalidDomainValueException("gender");
        }
    }
}
