package com.devsu.customersservice.infrastructure.messaging;

public final class CustomerEventRoutingKeys {
    private CustomerEventRoutingKeys() { }

    public static String fromEventType(String eventType) {
        return switch (eventType) {
            case "CustomerCreated" -> "customer.created";
            case "CustomerUpdated" -> "customer.updated";
            case "CustomerStatusChanged" -> "customer.status.changed";
            default -> throw new IllegalArgumentException("Unsupported customer event type: " + eventType);
        };
    }
}
