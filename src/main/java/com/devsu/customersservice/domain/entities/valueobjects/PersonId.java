package com.devsu.customersservice.domain.entities.valueobjects;

public record PersonId(Integer value) {
    public static PersonId unassigned() {
        return new PersonId(null);
    }

}
