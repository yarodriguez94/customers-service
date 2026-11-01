package com.devsu.customersservice.infrastructure.messaging;

import org.springframework.amqp.core.Message;
import java.time.Duration;

public interface CustomerEventTransport {
    void send(String exchange, String routingKey, Message message, Duration confirmTimeout);
}
