package com.devsu.customersservice.domain.entities;

import com.devsu.customersservice.domain.exceptions.InvalidDomainValueException;
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
import java.util.UUID;

import static com.devsu.customersservice.domain.entities.valueobjects.ClientId.create;

public final class CustomerFactory {
    private CustomerFactory() { }

    public static Customer createCustomer(UUID clientId, String encodedPassword, boolean status,
                                          int id, String firstName, String lastName, Gender gender,
                                          int age, String identification, String address, String phone) {
        if (gender == null) throw new InvalidDomainValueException("gender");

        return Customer.create(new ClientId(clientId), new EncodedPassword(encodedPassword),
                new CustomerStatus(status), new PersonId(id), new FirstName(firstName),
                new LastName(lastName), gender, new Age(age), new Identification(identification),
                new Address(address), new Phone(phone));
    }

    public static Customer createNewCustomer(String encodedPassword, boolean status,
                                             String firstName, String lastName, Gender gender,
                                             int age, String identification, String address, String phone) {
        if (gender == null) throw new InvalidDomainValueException("gender");

        return Customer.create(create(), new EncodedPassword(encodedPassword),
                new CustomerStatus(status), PersonId.unassigned(), new FirstName(firstName),
                new LastName(lastName), gender, new Age(age), new Identification(identification),
                new Address(address), new Phone(phone));
    }
}
