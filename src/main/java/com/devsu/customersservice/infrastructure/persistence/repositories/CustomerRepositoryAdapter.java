package com.devsu.customersservice.infrastructure.persistence.repositories;

import com.devsu.customersservice.domain.entities.Customer;
import com.devsu.customersservice.domain.entities.valueobjects.ClientId;
import com.devsu.customersservice.domain.entities.valueobjects.Identification;
import com.devsu.customersservice.domain.exceptions.IdentificationAlreadyInUseException;
import com.devsu.customersservice.domain.repositories.CustomerRepository;
import com.devsu.customersservice.domain.repositories.CustomerPage;
import com.devsu.customersservice.infrastructure.persistence.entities.CustomerJpaEntity;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.dao.DataIntegrityViolationException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Repository
public class CustomerRepositoryAdapter implements CustomerRepository {
    private final CustomerJpaRepository repository;

    public CustomerRepositoryAdapter(CustomerJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Customer save(Customer customer) {
        try {
            CustomerJpaEntity entity = CustomerJpaEntity.forInsert(customer);
            return repository.saveAndFlush(entity).toDomain();
        } catch (DataIntegrityViolationException exception) {
            if (isIdentificationConstraintViolation(exception)) {
                throw new IdentificationAlreadyInUseException();
            }
            throw exception;
        }
    }

    @Override
    public Optional<Customer> update(Customer customer) {
        try {
            return repository.findByClientId(customer.clientId().value())
                    .map(existing -> {
                        existing.updateFromDomain(customer);
                        return repository.saveAndFlush(existing).toDomain();
                    });
        } catch (DataIntegrityViolationException exception) {
            if (isIdentificationConstraintViolation(exception)) {
                throw new IdentificationAlreadyInUseException();
            }
            throw exception;
        }
    }

    @Override
    public CustomerPage findAll(int page, int size) {
        Page<CustomerJpaEntity> result = repository.findAll(
                PageRequest.of(page, size, Sort.by("personId").ascending()));
        return new CustomerPage(result.getContent().stream().map(CustomerJpaEntity::toDomain).toList(),
                result.getTotalElements(), result.getTotalPages(), result.getNumber(), result.getSize());
    }

    @Override
    public Optional<Customer> findById(ClientId clientId) {
        return repository.findByClientId(clientId.value()).map(CustomerJpaEntity::toDomain);
    }

    @Override
    public Optional<Customer> findByIdentification(Identification identification) {
        return repository.findByIdentification(identification.value())
                .map(CustomerJpaEntity::toDomain);
    }

    @Override
    public boolean existsByIdentificationAndClientIdNot(Identification identification, ClientId clientId) {
        return repository.existsByIdentificationAndClientIdNot(identification.value(), clientId.value());
    }

    private boolean isIdentificationConstraintViolation(Throwable exception) {
        for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
            if (cause instanceof ConstraintViolationException violation
                    && "uk_person_identification".equals(violation.getConstraintName())) {
                return true;
            }
        }
        return false;
    }
}
