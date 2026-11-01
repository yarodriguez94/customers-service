package com.devsu.customersservice.infrastructure.messaging;

import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class CustomerEventsMessagingConfiguration {
    @Bean
    public TopicExchange customerEventsExchange(@Value("${customer-events.exchange}") String exchange) {
        return ExchangeBuilder.topicExchange(exchange).durable(true).build();
    }
}
