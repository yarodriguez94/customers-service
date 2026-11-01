package com.devsu.customersservice.domain.repositories;

import com.devsu.customersservice.domain.entities.Customer;
import java.util.List;

public record CustomerPage(List<Customer> customers, long totalElements, int totalPages, int page, int size) {
    public CustomerPage {
        customers = List.copyOf(customers);
    }
}
