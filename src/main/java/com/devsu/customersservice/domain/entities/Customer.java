package com.devsu.customersservice.domain.entities;

import com.devsu.customersservice.domain.entities.valueobjects.Address;
import com.devsu.customersservice.domain.entities.valueobjects.Age;
import com.devsu.customersservice.domain.entities.valueobjects.ClientId;
import com.devsu.customersservice.domain.entities.valueobjects.CustomerStatus;
import com.devsu.customersservice.domain.entities.valueobjects.EncodedPassword;
import com.devsu.customersservice.domain.entities.valueobjects.FirstName;
import com.devsu.customersservice.domain.entities.valueobjects.Gender;
import com.devsu.customersservice.domain.entities.valueobjects.Identification;
import com.devsu.customersservice.domain.entities.valueobjects.LastName;
import com.devsu.customersservice.domain.entities.valueobjects.PersonId;
import com.devsu.customersservice.domain.entities.valueobjects.Phone;

public final class Customer extends Person {
    private final ClientId clientId;
    private final EncodedPassword password;
    private final CustomerStatus status;

    private Customer(ClientId clientId, EncodedPassword password, CustomerStatus status,
                     PersonId id, FirstName firstName, LastName lastName, Gender gender,
                     Age age, Identification identification, Address address, Phone phone) {
        super(id, firstName, lastName, gender, age, identification, address, phone);
        this.clientId = clientId;
        this.password = password;
        this.status = status;
    }

    static Customer create(ClientId clientId, EncodedPassword password, CustomerStatus status,
                           PersonId id, FirstName firstName, LastName lastName, Gender gender,
                           Age age, Identification identification, Address address, Phone phone) {
        return new Customer(clientId, password, status, id, firstName, lastName, gender,
                age, identification, address, phone);
    }

    public ClientId clientId() { return clientId; }
    public EncodedPassword password() { return password; }
    public CustomerStatus status() { return status; }
    public boolean isActive() { return status.value(); }

    public Customer activate() {
        return withStatus(new CustomerStatus(true));
    }

    public Customer deactivate() {
        return withStatus(new CustomerStatus(false));
    }

    public Customer withUpdates(FirstName firstName, LastName lastName, Gender gender, Age age,
                                Identification identification, Address address, Phone phone,
                                EncodedPassword password, CustomerStatus status) {
        return create(clientId,
                password == null ? this.password : password,
                status == null ? this.status : status,
                id(),
                firstName == null ? firstName() : firstName,
                lastName == null ? lastName() : lastName,
                gender == null ? gender() : gender,
                age == null ? age() : age,
                identification == null ? identification() : identification,
                address == null ? address() : address,
                phone == null ? phone() : phone);
    }

    private Customer withStatus(CustomerStatus newStatus) {
        return create(clientId, password, newStatus, id(), firstName(), lastName(), gender(),
                age(), identification(), address(), phone());
    }
}
