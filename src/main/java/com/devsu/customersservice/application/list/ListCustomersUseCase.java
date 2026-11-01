package com.devsu.customersservice.application.list;

import com.devsu.customersservice.domain.repositories.CustomerRepository;
import com.devsu.customersservice.domain.repositories.CustomerPage;
import org.springframework.transaction.annotation.Transactional;

public class ListCustomersUseCase {
    private final CustomerRepository customerRepository;

    public ListCustomersUseCase(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public CustomerPage execute(int page, int size) {
        return customerRepository.findAll(page, size);
    }
}
