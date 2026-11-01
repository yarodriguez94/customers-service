package com.devsu.customersservice.infrastructure.config;

import com.devsu.customersservice.application.create.CreateCustomerUseCase;
import com.devsu.customersservice.application.list.ListCustomersUseCase;
import com.devsu.customersservice.application.delete.DeleteCustomerUseCase;
import com.devsu.customersservice.application.update.UpdateCustomerUseCase;
import com.devsu.customersservice.application.ports.PasswordHasher;
import com.devsu.customersservice.domain.repositories.CustomerRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationUseCaseConfiguration {
    @Bean
    public CreateCustomerUseCase createCustomerUseCase(CustomerRepository customerRepository,
                                                       PasswordHasher passwordHasher) {
        return new CreateCustomerUseCase(customerRepository, passwordHasher);
    }

    @Bean
    public ListCustomersUseCase listCustomersUseCase(CustomerRepository customerRepository) {
        return new ListCustomersUseCase(customerRepository);
    }

    @Bean
    public UpdateCustomerUseCase updateCustomerUseCase(CustomerRepository customerRepository,
                                                       PasswordHasher passwordHasher) {
        return new UpdateCustomerUseCase(customerRepository, passwordHasher);
    }

    @Bean
    public DeleteCustomerUseCase deleteCustomerUseCase(CustomerRepository customerRepository) {
        return new DeleteCustomerUseCase(customerRepository);
    }
}
