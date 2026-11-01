package com.devsu.customersservice.domain.entities.valueobjects;

import com.devsu.customersservice.domain.exceptions.InvalidDomainValueException;

public record LastName(String value) {
    public LastName { if (value == null || value.isBlank()) throw new InvalidDomainValueException("lastName"); }
}
