package com.devsu.customersservice.domain.exceptions;

public class CustomerNotFoundException extends DomainException {
    public static final int CODE = 404;
    public static final String MESSAGE = "customer does not exist";

    public CustomerNotFoundException() {
        super(MESSAGE);
    }

    public int code() {
        return CODE;
    }
}
