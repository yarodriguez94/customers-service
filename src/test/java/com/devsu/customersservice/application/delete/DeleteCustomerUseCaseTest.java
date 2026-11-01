package com.devsu.customersservice.application.delete;

import com.devsu.customersservice.domain.entities.Customer;
import com.devsu.customersservice.domain.entities.CustomerFactory;
import com.devsu.customersservice.application.events.CustomerEvent;
import com.devsu.customersservice.application.ports.CustomerEventPublisher;
import com.devsu.customersservice.domain.entities.valueobjects.ClientId;
import com.devsu.customersservice.domain.entities.valueobjects.Gender;
import com.devsu.customersservice.domain.entities.valueobjects.Identification;
import com.devsu.customersservice.domain.exceptions.CustomerNotFoundException;
import com.devsu.customersservice.domain.repositories.CustomerPage;
import com.devsu.customersservice.domain.repositories.CustomerRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeleteCustomerUseCaseTest {
    @Test
    void deactivatesAndUpdatesTheExistingCustomer() {
        Customer activeCustomer = customer();
        RecordingCustomerRepository repository = new RecordingCustomerRepository(activeCustomer);
        RecordingCustomerEventPublisher eventPublisher = new RecordingCustomerEventPublisher();

        new DeleteCustomerUseCase(repository, eventPublisher).execute(activeCustomer.clientId());

        assertTrue(repository.updatedCustomer != null);
        assertFalse(repository.updatedCustomer.isActive());
        assertTrue(repository.savedCustomer == null);
        assertEquals(1, eventPublisher.events.size());
        assertEquals(CustomerEvent.CUSTOMER_STATUS_CHANGED, eventPublisher.events.getFirst().eventType());
        assertEquals(new CustomerEvent.StatusData("INACTIVE"), eventPublisher.events.getFirst().data());
    }

    @Test
    void failsWhenCustomerDoesNotExist() {
        RecordingCustomerRepository repository = new RecordingCustomerRepository(null);
        RecordingCustomerEventPublisher eventPublisher = new RecordingCustomerEventPublisher();
        assertThrows(CustomerNotFoundException.class,
                () -> new DeleteCustomerUseCase(repository, eventPublisher).execute(new ClientId(UUID.randomUUID())));
        assertTrue(repository.updatedCustomer == null);
        assertTrue(eventPublisher.events.isEmpty());
    }

    @Test
    void failsWhenCustomerCannotBeUpdated() {
        Customer activeCustomer = customer();
        RecordingCustomerRepository repository = new RecordingCustomerRepository(activeCustomer, false);
        RecordingCustomerEventPublisher eventPublisher = new RecordingCustomerEventPublisher();

        assertThrows(CustomerNotFoundException.class,
                () -> new DeleteCustomerUseCase(repository, eventPublisher).execute(activeCustomer.clientId()));
        assertFalse(repository.updatedCustomer.isActive());
        assertTrue(eventPublisher.events.isEmpty());
    }

    private Customer customer() {
        return CustomerFactory.createCustomer(UUID.randomUUID(), "encoded", true, 1,
                "Ada", "Lovelace", Gender.FEMALE, 30, "id-1", "address", "phone");
    }

    private static final class RecordingCustomerEventPublisher implements CustomerEventPublisher {
        private final List<CustomerEvent> events = new ArrayList<>();

        @Override
        public void publish(CustomerEvent event) {
            events.add(event);
        }
    }

    private static final class RecordingCustomerRepository implements CustomerRepository {
        private final Customer customer;
        private final boolean updateReturnsCustomer;
        private Customer updatedCustomer;
        private Customer savedCustomer;

        private RecordingCustomerRepository(Customer customer) {
            this(customer, true);
        }

        private RecordingCustomerRepository(Customer customer, boolean updateReturnsCustomer) {
            this.customer = customer;
            this.updateReturnsCustomer = updateReturnsCustomer;
        }

        @Override
        public Customer save(Customer customer) {
            savedCustomer = customer;
            return customer;
        }

        @Override
        public Optional<Customer> update(Customer customer) {
            updatedCustomer = customer;
            return updateReturnsCustomer ? Optional.of(customer) : Optional.empty();
        }

        @Override
        public CustomerPage findAll(int page, int size) {
            return new CustomerPage(List.of(), 0, 0, page, size);
        }

        @Override
        public Optional<Customer> findById(ClientId clientId) {
            return Optional.ofNullable(customer);
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
