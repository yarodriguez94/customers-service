package com.devsu.customersservice.domain.entities;

import com.devsu.customersservice.domain.entities.valueobjects.Address;
import com.devsu.customersservice.domain.entities.valueobjects.Age;
import com.devsu.customersservice.domain.entities.valueobjects.FirstName;
import com.devsu.customersservice.domain.entities.valueobjects.Gender;
import com.devsu.customersservice.domain.entities.valueobjects.Identification;
import com.devsu.customersservice.domain.entities.valueobjects.LastName;
import com.devsu.customersservice.domain.entities.valueobjects.PersonId;
import com.devsu.customersservice.domain.entities.valueobjects.Phone;

public abstract class Person {
    private final PersonId id;
    private final FirstName firstName;
    private final LastName lastName;
    private final Gender gender;
    private final Age age;
    private final Identification identification;
    private final Address address;
    private final Phone phone;

    Person(PersonId id, FirstName firstName, LastName lastName, Gender gender, Age age,
           Identification identification, Address address, Phone phone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.age = age;
        this.identification = identification;
        this.address = address;
        this.phone = phone;
    }

    public PersonId id() { return id; }
    public FirstName firstName() { return firstName; }
    public LastName lastName() { return lastName; }
    public Gender gender() { return gender; }
    public Age age() { return age; }
    public Identification identification() { return identification; }
    public Address address() { return address; }
    public Phone phone() { return phone; }
}
