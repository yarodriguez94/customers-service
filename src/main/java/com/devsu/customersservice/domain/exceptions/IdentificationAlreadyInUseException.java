package com.devsu.customersservice.domain.exceptions;

public class IdentificationAlreadyInUseException extends DomainException {
    public IdentificationAlreadyInUseException() {
        super("Identification is already in use");
    }
}
