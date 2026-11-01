package com.devsu.customersservice.domain.repositories;

import com.devsu.customersservice.domain.entities.Customer;
import com.devsu.customersservice.domain.entities.valueobjects.ClientId;
import com.devsu.customersservice.domain.entities.valueobjects.Identification;

import java.util.Optional;

public interface CustomerRepository {
    Customer save(Customer customer);

    Optional<Customer> update(Customer customer);

    CustomerPage findAll(int page, int size);

    Optional<Customer> findById(ClientId clientId);

    Optional<Customer> findByIdentification(Identification identification);

    boolean existsByIdentificationAndClientIdNot(Identification identification, ClientId clientId);
}
