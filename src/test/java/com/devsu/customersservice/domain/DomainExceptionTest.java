package com.devsu.customersservice.domain;

import com.devsu.customersservice.domain.exceptions.CustomerNotFoundException;
import com.devsu.customersservice.domain.exceptions.DomainException;
import com.devsu.customersservice.domain.exceptions.IdentificationAlreadyInUseException;
import com.devsu.customersservice.domain.exceptions.InvalidDomainValueException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class DomainExceptionTest {
    @Test
    void domainExceptionsShareTheDomainExceptionBaseType() {
        assertInstanceOf(DomainException.class, new CustomerNotFoundException());
        assertInstanceOf(DomainException.class, new InvalidDomainValueException("age"));
        assertInstanceOf(DomainException.class, new IdentificationAlreadyInUseException());
    }

    @Test
    void existingMessagesAndNotFoundCodeRemainUnchanged() {
        assertEquals("customer does not exist", new CustomerNotFoundException().getMessage());
        assertEquals(404, new CustomerNotFoundException().code());
        assertEquals("age must be valid", new InvalidDomainValueException("age").getMessage());
        assertEquals("Identification is already in use",
                new IdentificationAlreadyInUseException().getMessage());
    }
}
