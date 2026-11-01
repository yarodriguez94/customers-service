package com.devsu.customersservice.application.events;

import com.devsu.customersservice.domain.entities.Customer;
import java.util.UUID;

public record CustomerEvent(String eventType, UUID customerId, Object data) {
    public static final String CUSTOMER_CREATED = "CustomerCreated";
    public static final String CUSTOMER_UPDATED = "CustomerUpdated";
    public static final String CUSTOMER_STATUS_CHANGED = "CustomerStatusChanged";

    public static CustomerEvent snapshot(String eventType, Customer customer) {
        return new CustomerEvent(eventType, customer.clientId().value(), new SnapshotData(
                customer.identification().value(), customer.firstName().value(), customer.lastName().value(),
                status(customer)));
    }

    public static CustomerEvent statusChanged(Customer customer) {
        return new CustomerEvent(CUSTOMER_STATUS_CHANGED, customer.clientId().value(),
                new StatusData(status(customer)));
    }

    private static String status(Customer customer) {
        return customer.isActive() ? "ACTIVE" : "INACTIVE";
    }

    public record SnapshotData(String identification, String firstName, String lastName, String status) { }

    public record StatusData(String status) { }
}
