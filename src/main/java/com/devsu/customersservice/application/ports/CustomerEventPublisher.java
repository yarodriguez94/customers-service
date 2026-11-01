package com.devsu.customersservice.application.ports;

import com.devsu.customersservice.application.events.CustomerEvent;

public interface CustomerEventPublisher {
    void publish(CustomerEvent event);
}
