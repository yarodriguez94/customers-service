package com.devsu.customersservice.infrastructure.messaging;

import com.devsu.customersservice.application.events.CustomerEvent;
import com.devsu.customersservice.application.ports.CustomerEventPublisher;
import com.devsu.customersservice.infrastructure.observability.CorrelationId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class CustomerEventsPublisher implements CustomerEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(CustomerEventsPublisher.class);

    private final CustomerEventTransport transport;
    private final JsonMapper jsonMapper;
    private final String exchange;
    private final Duration confirmTimeout;
    private final String applicationName;

    public CustomerEventsPublisher(CustomerEventTransport transport, JsonMapper jsonMapper,
                                   @Value("${customer-events.exchange}") String exchange,
                                   @Value("${customer-events.confirm-timeout:5s}") Duration confirmTimeout,
                                   @Value("${spring.application.name}") String applicationName) {
        this.transport = transport;
        this.jsonMapper = jsonMapper;
        this.exchange = exchange;
        this.confirmTimeout = confirmTimeout;
        this.applicationName = applicationName;
    }

    @Override
    public void publish(CustomerEvent event) {
        UUID eventId = UUID.randomUUID();
        Instant occurredAt = Instant.now();
        String correlationId = CorrelationId.current().orElse(null);
        Runnable publication = () -> publishNow(event, eventId, occurredAt, correlationId);

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publication.run();
                }
            });
        } else {
            publication.run();
        }
    }

    private void publishNow(CustomerEvent event, UUID eventId, Instant occurredAt, String correlationId) {
        CustomerEventEnvelope envelope = new CustomerEventEnvelope(eventId, event.eventType(), 1,
                occurredAt, event.customerId(), event.data());
        byte[] body;
        try {
            body = jsonMapper.writeValueAsBytes(envelope);
        } catch (RuntimeException exception) {
            log.error("Could not serialize customer event {} for customer {}; event may be lost without an outbox",
                    event.eventType(), event.customerId(), exception);
            return;
        }

        try {
            Message message = new Message(body, messageProperties(event, eventId, occurredAt, correlationId));
            transport.send(exchange, CustomerEventRoutingKeys.fromEventType(event.eventType()), message,
                    confirmTimeout);
        } catch (RuntimeException exception) {
            log.error("Could not publish customer event {} for customer {}; event may be lost without an outbox",
                    event.eventType(), event.customerId(), exception);
        }
    }

    private MessageProperties messageProperties(CustomerEvent event, UUID eventId, Instant occurredAt,
                                                 String correlationId) {
        MessageProperties properties = new MessageProperties();
        properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        properties.setContentEncoding(StandardCharsets.UTF_8.name());
        properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        properties.setMessageId(eventId.toString());
        properties.setType(event.eventType());
        properties.setAppId(applicationName);
        properties.setTimestamp(Date.from(occurredAt));
        if (correlationId != null) {
            properties.setCorrelationId(correlationId);
            properties.setHeader(CorrelationId.MESSAGE_HEADER, correlationId);
        }
        return properties;
    }
}
