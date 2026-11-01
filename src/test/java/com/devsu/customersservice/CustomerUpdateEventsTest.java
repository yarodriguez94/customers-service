package com.devsu.customersservice;

import com.devsu.customersservice.application.events.CustomerEvent;
import com.devsu.customersservice.application.ports.CustomerEventPublisher;
import com.devsu.customersservice.application.update.UpdateCustomerCommand;
import com.devsu.customersservice.application.update.UpdateCustomerUseCase;
import com.devsu.customersservice.domain.entities.Customer;
import com.devsu.customersservice.domain.entities.CustomerFactory;
import com.devsu.customersservice.domain.entities.valueobjects.ClientId;
import com.devsu.customersservice.domain.entities.valueobjects.Gender;
import com.devsu.customersservice.domain.entities.valueobjects.Identification;
import com.devsu.customersservice.domain.repositories.CustomerPage;
import com.devsu.customersservice.domain.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomerUpdateEventsTest {

    @Test
    void publishesSnapshotAndStatusEventWhenPatchChangesStatus() {
        Customer active = customer(true);
        RecordingRepository repository = new RecordingRepository(active);
        RecordingPublisher publisher = new RecordingPublisher();

        new UpdateCustomerUseCase(repository, raw -> raw, publisher)
                .execute(new UpdateCustomerCommand(active.clientId(), null, false, null, null, null,
                        null, null, null, null));

        assertEquals(2, publisher.events.size());
        assertEquals(CustomerEvent.CUSTOMER_UPDATED, publisher.events.get(0).eventType());
        assertEquals(new CustomerEvent.SnapshotData("id-1", "Ada", "Lovelace", "INACTIVE"),
                publisher.events.get(0).data());
        assertEquals(CustomerEvent.CUSTOMER_STATUS_CHANGED, publisher.events.get(1).eventType());
        assertEquals(new CustomerEvent.StatusData("INACTIVE"), publisher.events.get(1).data());
    }

    @Test
    void publishesSnapshotWithoutStatusEventWhenStatusDoesNotChange() {
        Customer active = customer(true);
        RecordingRepository repository = new RecordingRepository(active);
        RecordingPublisher publisher = new RecordingPublisher();

        new UpdateCustomerUseCase(repository, raw -> raw, publisher)
                .execute(new UpdateCustomerCommand(active.clientId(), null, true, "Augusta", null, null,
                        null, null, null, null));

        assertEquals(1, publisher.events.size());
        assertEquals(CustomerEvent.CUSTOMER_UPDATED, publisher.events.getFirst().eventType());
        assertTrue(((CustomerEvent.SnapshotData) publisher.events.getFirst().data()).firstName().equals("Augusta"));
    }

    private Customer customer(boolean active) {
        return CustomerFactory.createCustomer(UUID.randomUUID(), "encoded", active, 1,
                "Ada", "Lovelace", Gender.FEMALE, 30, "id-1", "address", "phone");
    }

    private static final class RecordingPublisher implements CustomerEventPublisher {
        private final List<CustomerEvent> events = new ArrayList<>();

        @Override
        public void publish(CustomerEvent event) {
            events.add(event);
        }
    }

    private static final class RecordingRepository implements CustomerRepository {
        private final Customer existing;

        private RecordingRepository(Customer existing) {
            this.existing = existing;
        }

        @Override
        public Customer save(Customer customer) {
            return customer;
        }

        @Override
        public Optional<Customer> update(Customer customer) {
            return Optional.of(customer);
        }

        @Override
        public CustomerPage findAll(int page, int size) {
            return new CustomerPage(List.of(), 0, 0, page, size);
        }

        @Override
        public Optional<Customer> findById(ClientId clientId) {
            return Optional.of(existing);
        }

        @Override
        public Optional<Customer> findByIdentification(Identification identification) {
            return Optional.empty();
        }

        @Override
        public boolean existsByIdentificationAndClientIdNot(Identification identification, ClientId clientId) {
            return false;
        }
    }
}
