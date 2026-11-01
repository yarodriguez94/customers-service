package com.devsu.customersservice.infrastructure.dtos;

import com.devsu.customersservice.domain.entities.Customer;
import java.util.UUID;

public record CustomerResponse(
        UUID clientId,
        Integer personId,
        String firstName,
        String lastName,
        String gender,
        int age,
        String identification,
        String address,
        String phone,
        boolean status) {

    public static CustomerResponse fromDomain(Customer customer) {
        return new CustomerResponse(
                customer.clientId().value(),
                customer.id().value(),
                customer.firstName().value(),
                customer.lastName().value(),
                customer.gender().value(),
                customer.age().value(),
                customer.identification().value(),
                customer.address().value(),
                customer.phone().value(),
                customer.isActive());
    }
}
