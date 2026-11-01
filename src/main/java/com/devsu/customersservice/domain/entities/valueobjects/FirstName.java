package com.devsu.customersservice.domain.entities.valueobjects;

import com.devsu.customersservice.domain.exceptions.InvalidDomainValueException;

public record FirstName(String value) {
    public FirstName { if (value == null || value.isBlank()) throw new InvalidDomainValueException("firstName"); }
}
