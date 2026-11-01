package com.devsu.customersservice;

import com.devsu.customersservice.application.events.CustomerEvent;
import com.devsu.customersservice.domain.entities.CustomerFactory;
import com.devsu.customersservice.domain.entities.valueobjects.Gender;
import com.devsu.customersservice.infrastructure.messaging.CustomerEventTransport;
import com.devsu.customersservice.infrastructure.messaging.CustomerEventsPublisher;
import com.devsu.customersservice.infrastructure.observability.CorrelationId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomerEventsPublisherTest {
    private static final String EXCHANGE = "customers.events";

    @AfterEach
    void clearTransactionSynchronization() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void publishesCreatedSnapshotAfterCommitWithConsumerPayloadAndCorrelationHeaders() throws Exception {
        RecordingTransport transport = new RecordingTransport();
        JsonMapper mapper = JsonMapper.builder().findAndAddModules().build();
        CustomerEventsPublisher publisher = publisher(transport, mapper);
        var customer = customer(true);

        TransactionSynchronizationManager.initSynchronization();
        try (var ignored = CorrelationId.bind("request-123")) {
            publisher.publish(CustomerEvent.snapshot(CustomerEvent.CUSTOMER_CREATED, customer));
            assertTrue(transport.sentMessages.isEmpty());
            runAfterCommit();
        }

        SentMessage sent = transport.sentMessages.getFirst();
        assertEquals(EXCHANGE, sent.exchange());
        assertEquals("customer.created", sent.routingKey());
        assertEquals("request-123", sent.message().getMessageProperties().getCorrelationId());
        assertEquals("request-123", sent.message().getMessageProperties()
                .getHeader(CorrelationId.MESSAGE_HEADER));

        var payload = mapper.readTree(sent.message().getBody());
        assertNotNull(UUID.fromString(payload.path("eventId").stringValue()));
        assertEquals("CustomerCreated", payload.path("eventType").stringValue());
        assertEquals(1, payload.path("eventVersion").intValue());
        assertTrue(payload.path("occurredAt").stringValue().endsWith("Z"));
        assertNotNull(Instant.parse(payload.path("occurredAt").stringValue()));
        assertEquals(customer.clientId().value().toString(), payload.path("customerId").stringValue());
        assertEquals("id-1", payload.path("data").path("identification").stringValue());
        assertEquals("Ada", payload.path("data").path("firstName").stringValue());
        assertEquals("Lovelace", payload.path("data").path("lastName").stringValue());
        assertEquals("ACTIVE", payload.path("data").path("status").stringValue());
    }

    @Test
    void publishesUpdatedSnapshotOnConsumerRoutingKey() throws Exception {
        RecordingTransport transport = new RecordingTransport();
        JsonMapper mapper = JsonMapper.builder().findAndAddModules().build();
        CustomerEventsPublisher publisher = publisher(transport, mapper);

        TransactionSynchronizationManager.initSynchronization();
        publisher.publish(CustomerEvent.snapshot(CustomerEvent.CUSTOMER_UPDATED, customer(true)));
        runAfterCommit();

        SentMessage sent = transport.sentMessages.getFirst();
        assertEquals("customer.updated", sent.routingKey());
        var payload = mapper.readTree(sent.message().getBody());
        assertEquals("CustomerUpdated", payload.path("eventType").stringValue());
        assertEquals(4, payload.path("data").size());
    }

    @Test
    void publishesOnlyStatusForStatusChangedEventOnConsumerRoutingKey() throws Exception {
        RecordingTransport transport = new RecordingTransport();
        JsonMapper mapper = JsonMapper.builder().findAndAddModules().build();
        CustomerEventsPublisher publisher = publisher(transport, mapper);

        TransactionSynchronizationManager.initSynchronization();
        publisher.publish(CustomerEvent.statusChanged(customer(false)));
        runAfterCommit();

        SentMessage sent = transport.sentMessages.getFirst();
        assertEquals("customer.status.changed", sent.routingKey());
        var payload = mapper.readTree(sent.message().getBody());
        assertEquals("CustomerStatusChanged", payload.path("eventType").stringValue());
        assertEquals("INACTIVE", payload.path("data").path("status").stringValue());
        assertEquals(1, payload.path("data").size());
    }

    @Test
    void doesNotPublishIfTransactionDoesNotCommit() {
        RecordingTransport transport = new RecordingTransport();
        CustomerEventsPublisher publisher = publisher(transport, JsonMapper.builder().build());

        TransactionSynchronizationManager.initSynchronization();
        publisher.publish(CustomerEvent.snapshot(CustomerEvent.CUSTOMER_UPDATED, customer(true)));
        TransactionSynchronizationManager.clearSynchronization();

        assertTrue(transport.sentMessages.isEmpty());
    }

    @Test
    void brokerFailureAfterCommitDoesNotEscapeOrUndoOperation() {
        RecordingTransport transport = new RecordingTransport();
        transport.failure = new IllegalStateException("broker unavailable");
        CustomerEventsPublisher publisher = publisher(transport, JsonMapper.builder().build());

        TransactionSynchronizationManager.initSynchronization();
        publisher.publish(CustomerEvent.snapshot(CustomerEvent.CUSTOMER_UPDATED, customer(true)));
        assertDoesNotThrow(this::runAfterCommit);
        assertEquals(1, transport.sendAttempts);
    }

    private CustomerEventsPublisher publisher(RecordingTransport transport, JsonMapper mapper) {
        return new CustomerEventsPublisher(transport, mapper, EXCHANGE, Duration.ofSeconds(5), "customers-service");
    }

    private void runAfterCommit() {
        List<TransactionSynchronization> synchronizations =
                TransactionSynchronizationManager.getSynchronizations();
        synchronizations.forEach(TransactionSynchronization::afterCommit);
    }

    private com.devsu.customersservice.domain.entities.Customer customer(boolean active) {
        return CustomerFactory.createCustomer(UUID.randomUUID(), "encoded", active, 1,
                "Ada", "Lovelace", Gender.FEMALE, 30, "id-1", "London", "555-0100");
    }

    private static final class RecordingTransport implements CustomerEventTransport {
        private final List<SentMessage> sentMessages = new ArrayList<>();
        private RuntimeException failure;
        private int sendAttempts;

        @Override
        public void send(String exchange, String routingKey, Message message, Duration confirmTimeout) {
            sendAttempts++;
            if (failure != null) {
                throw failure;
            }
            sentMessages.add(new SentMessage(exchange, routingKey, message));
        }
    }

    private record SentMessage(String exchange, String routingKey, Message message) { }
}
