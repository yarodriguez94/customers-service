package com.devsu.customersservice.infrastructure.dtos;

import com.devsu.customersservice.application.create.CreateCustomerCommand;

public final class CreateCustomerRequestMapper {
    private CreateCustomerRequestMapper() { }

    public static CreateCustomerCommand toCommand(CreateCustomerRequest request) {
        return new CreateCustomerCommand(
                request.password(),
                request.status(),
                request.firstName(),
                request.lastName(),
                request.gender(),
                request.age(),
                request.identification(),
                request.address(),
                request.phone());
    }
}
