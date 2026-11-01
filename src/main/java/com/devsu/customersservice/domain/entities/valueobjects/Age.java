package com.devsu.customersservice.domain.entities.valueobjects;

import com.devsu.customersservice.domain.exceptions.InvalidDomainValueException;

public record Age(int value) {
    public Age {
        if (value < 0) throw new InvalidDomainValueException("age");
    }
}
