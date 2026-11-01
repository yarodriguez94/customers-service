package com.devsu.customersservice.application.update;

import com.devsu.customersservice.application.ports.PasswordHasher;
import com.devsu.customersservice.domain.exceptions.IdentificationAlreadyInUseException;
import com.devsu.customersservice.domain.entities.Customer;
import com.devsu.customersservice.domain.entities.valueobjects.Address;
import com.devsu.customersservice.domain.entities.valueobjects.Age;
import com.devsu.customersservice.domain.entities.valueobjects.CustomerStatus;
import com.devsu.customersservice.domain.entities.valueobjects.EncodedPassword;
import com.devsu.customersservice.domain.entities.valueobjects.FirstName;
import com.devsu.customersservice.domain.entities.valueobjects.Gender;
import com.devsu.customersservice.domain.entities.valueobjects.Identification;
import com.devsu.customersservice.domain.entities.valueobjects.LastName;
import com.devsu.customersservice.domain.entities.valueobjects.Phone;
import com.devsu.customersservice.domain.repositories.CustomerRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public class UpdateCustomerUseCase {
    private final CustomerRepository customerRepository;
    private final PasswordHasher passwordHasher;

    public UpdateCustomerUseCase(CustomerRepository customerRepository, PasswordHasher passwordHasher) {
        this.customerRepository = customerRepository;
        this.passwordHasher = passwordHasher;
    }
    @Transactional
    public Optional<Customer> execute(UpdateCustomerCommand command) {

        if (command.identification() != null) {
            var updatedCustomer = customerRepository.findById(command.clientId());
            if (updatedCustomer.isPresent()
                    && !updatedCustomer.get().identification().value().equals(command.identification())
                    && customerRepository.existsByIdentificationAndClientIdNot(
                    new Identification(command.identification()), command.clientId())) {
                throw new IdentificationAlreadyInUseException();
            }
        }

        return customerRepository.findById(command.clientId())
                .map(customer -> customer.withUpdates(
                        command.firstName() == null ? null : new FirstName(command.firstName()),
                        command.lastName() == null ? null : new LastName(command.lastName()),
                        command.gender() == null ? null : new Gender(command.gender()),
                        command.age() == null ? null : new Age(command.age()),
                        command.identification() == null ? null : new Identification(command.identification()),
                        command.address() == null ? null : new Address(command.address()),
                        command.phone() == null ? null : new Phone(command.phone()),
                        command.password() == null ? null : new EncodedPassword(passwordHasher.hash(command.password())),
                        command.status() == null ? null : new CustomerStatus(command.status())))
                .flatMap(customerRepository::update);
    }
}
