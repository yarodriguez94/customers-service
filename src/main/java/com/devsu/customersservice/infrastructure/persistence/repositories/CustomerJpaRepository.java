package com.devsu.customersservice.infrastructure.persistence.repositories;

import com.devsu.customersservice.domain.entities.Customer;
import com.devsu.customersservice.domain.entities.valueobjects.Identification;
import com.devsu.customersservice.infrastructure.persistence.entities.CustomerJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerJpaRepository extends JpaRepository<CustomerJpaEntity, Integer> {
    Optional<CustomerJpaEntity> findByClientId(UUID clientId);
    Optional<CustomerJpaEntity> findByIdentification(String identification);
    boolean existsByIdentificationAndClientIdNot(String identification, UUID clientId);
}
