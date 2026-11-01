package com.devsu.customersservice.application.delete;

import com.devsu.customersservice.domain.entities.Customer;
import com.devsu.customersservice.domain.entities.CustomerFactory;
import com.devsu.customersservice.domain.entities.valueobjects.ClientId;
import com.devsu.customersservice.domain.entities.valueobjects.Gender;
import com.devsu.customersservice.domain.entities.valueobjects.Identification;
import com.devsu.customersservice.domain.exceptions.CustomerNotFoundException;
import com.devsu.customersservice.domain.repositories.CustomerPage;
import com.devsu.customersservice.domain.repositories.CustomerRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeleteCustomerUseCaseTest {
    @Test
    void deactivatesAndUpdatesTheExistingCustomer() {
        Customer activeCustomer = customer();
        RecordingCustomerRepository repository = new RecordingCustomerRepository(activeCustomer);

        new DeleteCustomerUseCase(repository).execute(activeCustomer.clientId());

        assertTrue(repository.updatedCustomer != null);
        assertFalse(repository.updatedCustomer.isActive());
        assertTrue(repository.savedCustomer == null);
    }

    @Test
    void failsWhenCustomerDoesNotExist() {
        RecordingCustomerRepository repository = new RecordingCustomerRepository(null);
        assertThrows(CustomerNotFoundException.class,
                () -> new DeleteCustomerUseCase(repository).execute(new ClientId(UUID.randomUUID())));
    }

    private Customer customer() {
        return CustomerFactory.createCustomer(UUID.randomUUID(), "encoded", true, 1,
                "Ada", "Lovelace", Gender.FEMALE, 30, "id-1", "address", "phone");
    }

    private static final class RecordingCustomerRepository implements CustomerRepository {
        private final Customer customer;
        private Customer updatedCustomer;
        private Customer savedCustomer;

        private RecordingCustomerRepository(Customer customer) {
            this.customer = customer;
        }

        @Override
        public Customer save(Customer customer) {
            savedCustomer = customer;
            return customer;
        }

        @Override
        public Optional<Customer> update(Customer customer) {
            updatedCustomer = customer;
            return Optional.of(customer);
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
