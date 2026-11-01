package com.devsu.customersservice.infrastructure.dtos;

import com.devsu.customersservice.application.update.UpdateCustomerCommand;
import com.devsu.customersservice.domain.entities.valueobjects.ClientId;
import java.util.UUID;

public final class UpdateCustomerRequestMapper {
    private UpdateCustomerRequestMapper() { }

    public static UpdateCustomerCommand toCommand(UUID clientId, UpdateCustomerRequest request) {
        return new UpdateCustomerCommand(new ClientId(clientId), request.password(), request.status(),
                request.firstName(), request.lastName(), request.gender(), request.age(),
                request.identification(), request.address(), request.phone());
    }
}
