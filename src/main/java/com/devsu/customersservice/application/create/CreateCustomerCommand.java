package com.devsu.customersservice.application.create;

public record CreateCustomerCommand(
        String password,
        boolean status,
        String firstName,
        String lastName,
        String gender,
        int age,
        String identification,
        String address,
        String phone) { }
