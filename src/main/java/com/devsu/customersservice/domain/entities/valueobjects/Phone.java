package com.devsu.customersservice.domain.entities.valueobjects;

import com.devsu.customersservice.domain.exceptions.InvalidDomainValueException;

public record Phone(String value) {
    public Phone {
        if (value == null || value.isBlank()) throw new InvalidDomainValueException("phone");
    }
}
