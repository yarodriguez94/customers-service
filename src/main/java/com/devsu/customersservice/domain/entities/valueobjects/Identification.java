package com.devsu.customersservice.domain.entities.valueobjects;

import com.devsu.customersservice.domain.exceptions.InvalidDomainValueException;

public record Identification(String value) {
    public Identification { if (value == null || value.isBlank()) throw new InvalidDomainValueException("identification"); }
}
