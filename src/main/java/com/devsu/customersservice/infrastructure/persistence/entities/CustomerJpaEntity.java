package com.devsu.customersservice.infrastructure.persistence.entities;

import com.devsu.customersservice.domain.entities.Customer;
import com.devsu.customersservice.domain.entities.CustomerFactory;
import com.devsu.customersservice.domain.entities.valueobjects.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;

@Entity
@Table(name = "customer", uniqueConstraints = @UniqueConstraint(name = "uk_customer_client_id", columnNames = "client_id"))
public class CustomerJpaEntity extends PersonJpaEntity {
    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "status", nullable = false)
    private boolean status;

    protected CustomerJpaEntity() { }

    private CustomerJpaEntity(Integer personId, String firstName, String lastName, String gender,
                              Integer age, String identification, String address, String phone,
                              UUID clientId, String password, boolean status) {
        super(personId, firstName, lastName, gender, age, identification, address, phone);
        this.clientId = clientId;
        this.password = password;
        this.status = status;
    }

    public static CustomerJpaEntity fromDomain(Customer customer) {
        return new CustomerJpaEntity(
                customer.id().value(),
                customer.firstName().value(),
                customer.lastName().value(),
                customer.gender().value(),
                customer.age().value(),
                customer.identification().value(),
                customer.address().value(),
                customer.phone().value(),
                customer.clientId().value(),
                customer.password().value(),
                customer.isActive());
    }

    public static CustomerJpaEntity forInsert(Customer customer) {
        return new CustomerJpaEntity(
                null,
                customer.firstName().value(),
                customer.lastName().value(),
                customer.gender().value(),
                customer.age().value(),
                customer.identification().value(),
                customer.address().value(),
                customer.phone().value(),
                customer.clientId().value(),
                customer.password().value(),
                customer.isActive());
    }

    public Customer toDomain() {
        return CustomerFactory.createCustomer(clientId, password, status,
                getPersonId(), getFirstName(), getLastName(),
                new Gender(getGender()), getAge(), getIdentification(), getAddress(), getPhone());
    }

    public void updateFromDomain(Customer customer) {
        updatePersonDetails(customer.firstName().value(), customer.lastName().value(),
                customer.gender().value(), customer.age().value(), customer.identification().value(),
                customer.address().value(), customer.phone().value());
        this.password = customer.password().value();
        this.status = customer.isActive();
    }

    public UUID getClientId() { return clientId; }
    public String getPassword() { return password; }
    public boolean isStatus() { return status; }
}
