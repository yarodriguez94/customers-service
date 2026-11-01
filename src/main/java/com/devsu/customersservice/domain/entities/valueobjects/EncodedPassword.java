package com.devsu.customersservice.domain.entities.valueobjects;

import com.devsu.customersservice.domain.exceptions.InvalidDomainValueException;

public record EncodedPassword(String value) {
    public EncodedPassword { if (value == null || value.isBlank()) throw new InvalidDomainValueException("password"); }
}
