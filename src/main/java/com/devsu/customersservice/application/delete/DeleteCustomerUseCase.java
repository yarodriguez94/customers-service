package com.devsu.customersservice.application.delete;

import com.devsu.customersservice.domain.entities.Customer;
import com.devsu.customersservice.application.events.CustomerEvent;
import com.devsu.customersservice.application.ports.CustomerEventPublisher;
import com.devsu.customersservice.domain.entities.valueobjects.ClientId;
import com.devsu.customersservice.domain.exceptions.CustomerNotFoundException;
import com.devsu.customersservice.domain.repositories.CustomerRepository;

import org.springframework.transaction.annotation.Transactional;

public class DeleteCustomerUseCase {
    private final CustomerRepository customerRepository;
    private final CustomerEventPublisher eventPublisher;

    public DeleteCustomerUseCase(CustomerRepository customerRepository, CustomerEventPublisher eventPublisher) {
        this.customerRepository = customerRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void execute(ClientId clientId) {
        Customer customer = customerRepository.findById(clientId)
                .orElseThrow(CustomerNotFoundException::new);
        boolean wasActive = customer.isActive();
        customer = customer.deactivate();
        Customer updatedCustomer = customerRepository.update(customer).orElseThrow(CustomerNotFoundException::new);
        if (wasActive) {
            eventPublisher.publish(CustomerEvent.statusChanged(updatedCustomer));
        }
    }
}
