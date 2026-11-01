package com.devsu.customersservice.application.update;

import com.devsu.customersservice.domain.entities.valueobjects.ClientId;

public record UpdateCustomerCommand(
        ClientId clientId,
        String password,
        Boolean status,
        String firstName,
        String lastName,
        String gender,
        Integer age,
        String identification,
        String address,
        String phone) { }
