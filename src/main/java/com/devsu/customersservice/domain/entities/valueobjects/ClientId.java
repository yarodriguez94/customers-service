package com.devsu.customersservice.domain.entities.valueobjects;

import com.devsu.customersservice.domain.exceptions.InvalidDomainValueException;
import java.util.UUID;

public record ClientId(UUID value) {
    public ClientId { if (value == null) throw new InvalidDomainValueException("clientId"); }

    public static ClientId create() {
        return new ClientId(UUID.randomUUID());
    }

}
