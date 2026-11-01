package com.devsu.customersservice.infrastructure.messaging;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import java.time.Duration;

@Component
public class RabbitCustomerEventTransport implements CustomerEventTransport {
    private final RabbitTemplate rabbitTemplate;

    public RabbitCustomerEventTransport(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void send(String exchange, String routingKey, Message message, Duration confirmTimeout) {
        rabbitTemplate.invoke(operations -> {
            operations.send(exchange, routingKey, message);
            operations.waitForConfirmsOrDie(confirmTimeout.toMillis());
            return null;
        });
    }
}
