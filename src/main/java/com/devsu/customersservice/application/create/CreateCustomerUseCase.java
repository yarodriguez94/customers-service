package com.devsu.customersservice.application.create;

import com.devsu.customersservice.application.ports.PasswordHasher;
import com.devsu.customersservice.application.ports.CustomerEventPublisher;
import com.devsu.customersservice.application.events.CustomerEvent;
import com.devsu.customersservice.domain.exceptions.IdentificationAlreadyInUseException;
import com.devsu.customersservice.domain.entities.Customer;
import com.devsu.customersservice.domain.entities.CustomerFactory;
import com.devsu.customersservice.domain.entities.valueobjects.Gender;
import com.devsu.customersservice.domain.entities.valueobjects.Identification;
import com.devsu.customersservice.domain.repositories.CustomerRepository;

import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

public class CreateCustomerUseCase {
    private final CustomerRepository customerRepository;
    private final PasswordHasher passwordHasher;
    private final CustomerEventPublisher eventPublisher;

    public CreateCustomerUseCase(CustomerRepository customerRepository, PasswordHasher passwordHasher,
                                 CustomerEventPublisher eventPublisher) {
        this.customerRepository = customerRepository;
        this.passwordHasher = passwordHasher;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Customer execute(CreateCustomerCommand command) {

        Optional<Customer> existWithIdentification = customerRepository.findByIdentification(new Identification(command.identification()));
        if (existWithIdentification.isPresent()) {
            throw new IdentificationAlreadyInUseException();
        }
        String hashedPassword = passwordHasher.hash(command.password());
        Customer customer = CustomerFactory.createNewCustomer(
                hashedPassword,
                command.status(),
                command.firstName(),
                command.lastName(),
                new Gender(command.gender()),
                command.age(),
                command.identification(),
                command.address(),
                command.phone());
        Customer savedCustomer = customerRepository.save(customer);
        eventPublisher.publish(CustomerEvent.snapshot(CustomerEvent.CUSTOMER_CREATED, savedCustomer));
        return savedCustomer;
    }
}
