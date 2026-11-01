package com.devsu.customersservice.infrastructure.messaging;

import java.time.Instant;
import java.util.UUID;

public record CustomerEventEnvelope(
        UUID eventId,
        String eventType,
        int eventVersion,
        Instant occurredAt,
        UUID customerId,
        Object data) {
}
