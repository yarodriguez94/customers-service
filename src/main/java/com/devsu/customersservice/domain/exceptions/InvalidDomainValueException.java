package com.devsu.customersservice.domain.exceptions;

public class InvalidDomainValueException extends DomainException {
    public InvalidDomainValueException(String fieldName) {
        super(fieldName + " must be valid");
    }
}
