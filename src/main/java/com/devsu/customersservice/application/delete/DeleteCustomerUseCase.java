package com.devsu.customersservice.application.delete;

import com.devsu.customersservice.domain.entities.Customer;
import com.devsu.customersservice.domain.entities.valueobjects.ClientId;
import com.devsu.customersservice.domain.exceptions.CustomerNotFoundException;
import com.devsu.customersservice.domain.repositories.CustomerRepository;

import org.springframework.transaction.annotation.Transactional;

public class DeleteCustomerUseCase {
    private final CustomerRepository customerRepository;

    public DeleteCustomerUseCase(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public void execute(ClientId clientId) {
        Customer customer = customerRepository.findById(clientId)
                .orElseThrow(CustomerNotFoundException::new);
        customer = customer.deactivate();
        customerRepository.update(customer).orElseThrow(CustomerNotFoundException::new);
    }
}
