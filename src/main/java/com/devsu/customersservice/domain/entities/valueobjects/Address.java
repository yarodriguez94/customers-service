package com.devsu.customersservice.domain.entities.valueobjects;

import com.devsu.customersservice.domain.exceptions.InvalidDomainValueException;

public record Address(String value) {
    public Address { if (value == null || value.isBlank()) throw new InvalidDomainValueException("address"); }
}
