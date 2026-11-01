package com.devsu.customersservice.domain;

import com.devsu.customersservice.domain.entities.valueobjects.Age;
import com.devsu.customersservice.domain.entities.valueobjects.FirstName;
import com.devsu.customersservice.domain.entities.valueobjects.Identification;
import com.devsu.customersservice.domain.entities.valueobjects.Phone;
import com.devsu.customersservice.domain.exceptions.InvalidDomainValueException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomerValueObjectsTest {
    @Test
    void ageMustNotBeNegative() {
        assertThrows(InvalidDomainValueException.class, () -> new Age(-1));
        assertDoesNotThrow(() -> new Age(0));
    }

    @Test
    void requiredTextValuesMustNotBeBlank() {
        assertThrows(InvalidDomainValueException.class, () -> new FirstName("  "));
        assertThrows(InvalidDomainValueException.class, () -> new Identification(""));
        assertThrows(InvalidDomainValueException.class, () -> new Phone(" "));
    }
}
