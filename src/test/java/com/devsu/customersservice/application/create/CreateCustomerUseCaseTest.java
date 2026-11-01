package com.devsu.customersservice.application.create;

import com.devsu.customersservice.application.ports.PasswordHasher;
import com.devsu.customersservice.application.ports.CustomerEventPublisher;
import com.devsu.customersservice.application.events.CustomerEvent;
import com.devsu.customersservice.domain.entities.Customer;
import com.devsu.customersservice.domain.entities.CustomerFactory;
import com.devsu.customersservice.domain.entities.valueobjects.ClientId;
import com.devsu.customersservice.domain.entities.valueobjects.Gender;
import com.devsu.customersservice.domain.entities.valueobjects.Identification;
import com.devsu.customersservice.domain.exceptions.IdentificationAlreadyInUseException;
import com.devsu.customersservice.domain.repositories.CustomerPage;
import com.devsu.customersservice.domain.repositories.CustomerRepository;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateCustomerUseCaseTest {
    @Test
    void hashesPasswordAndSavesNewCustomerWhenIdentificationIsAvailable() {
        RecordingCustomerRepository repository = new RecordingCustomerRepository(null);
        RecordingPasswordHasher passwordHasher = new RecordingPasswordHasher();
        RecordingCustomerEventPublisher eventPublisher = new RecordingCustomerEventPublisher();
        CreateCustomerCommand command = command();

        Customer result = new CreateCustomerUseCase(repository, passwordHasher, eventPublisher).execute(command);

        assertEquals(command.password(), passwordHasher.rawPassword);
        assertEquals("hashed-password", repository.savedCustomer.password().value());
        assertEquals(command.firstName(), repository.savedCustomer.firstName().value());
        assertEquals(command.lastName(), repository.savedCustomer.lastName().value());
        assertEquals(command.gender(), repository.savedCustomer.gender().value());
        assertEquals(command.age(), repository.savedCustomer.age().value());
        assertEquals(command.identification(), repository.savedCustomer.identification().value());
        assertEquals(command.address(), repository.savedCustomer.address().value());
        assertEquals(command.phone(), repository.savedCustomer.phone().value());
        assertEquals(command.status(), repository.savedCustomer.isActive());
        assertEquals(repository.savedCustomer, result);
        assertNotNull(repository.searchedIdentification);
        assertEquals(command.identification(), repository.searchedIdentification.value());
        assertEquals(1, eventPublisher.events.size());
        CustomerEvent event = eventPublisher.events.getFirst();
        assertEquals(CustomerEvent.CUSTOMER_CREATED, event.eventType());
        assertEquals(result.clientId().value(), event.customerId());
        assertEquals(new CustomerEvent.SnapshotData("id-1", "Ada", "Lovelace", "ACTIVE"), event.data());
    }

    @Test
    void rejectsCustomerWhenIdentificationIsAlreadyInUse() {
        Customer existingCustomer = CustomerFactory.createCustomer(
                java.util.UUID.randomUUID(), "encoded", true, 1,
                "Existing", "Customer", Gender.FEMALE, 30, "id-1", "address", "phone");
        RecordingCustomerRepository repository = new RecordingCustomerRepository(existingCustomer);
        RecordingPasswordHasher passwordHasher = new RecordingPasswordHasher();
        RecordingCustomerEventPublisher eventPublisher = new RecordingCustomerEventPublisher();

        assertThrows(IdentificationAlreadyInUseException.class,
                () -> new CreateCustomerUseCase(repository, passwordHasher, eventPublisher).execute(command()));

        assertNull(repository.savedCustomer);
        assertNull(passwordHasher.rawPassword);
        assertTrue(eventPublisher.events.isEmpty());
    }

    private CreateCustomerCommand command() {
        return new CreateCustomerCommand("plain-password", true, "Ada", "Lovelace", "Female",
                30, "id-1", "London", "555-0100");
    }

    private static final class RecordingPasswordHasher implements PasswordHasher {
        private String rawPassword;

        @Override
        public String hash(String rawPassword) {
            this.rawPassword = rawPassword;
            return "hashed-password";
        }
    }

    private static final class RecordingCustomerEventPublisher implements CustomerEventPublisher {
        private final List<CustomerEvent> events = new ArrayList<>();

        @Override
        public void publish(CustomerEvent event) {
            events.add(event);
        }
    }

    private static final class RecordingCustomerRepository implements CustomerRepository {
        private final Customer customerWithIdentification;
        private Customer savedCustomer;
        private Identification searchedIdentification;

        private RecordingCustomerRepository(Customer customerWithIdentification) {
            this.customerWithIdentification = customerWithIdentification;
        }

        @Override
        public Customer save(Customer customer) {
            savedCustomer = customer;
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
            return Optional.empty();
        }

        @Override
        public Optional<Customer> findByIdentification(Identification identification) {
            searchedIdentification = identification;
            return Optional.ofNullable(customerWithIdentification);
        }

        @Override
        public boolean existsByIdentificationAndClientIdNot(Identification identification, ClientId clientId) {
            return false;
        }
    }
}
