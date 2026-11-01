package com.devsu.customersservice.infrastructure.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateCustomerRequest(
        @Size(min = 1)
        String password,
        Boolean status,
        @Size(min = 1)
        String firstName,
        @Size(min = 1)
        String lastName,
        @Pattern(regexp = "Male|Female|Other")
        String gender,
        @Min(0)
        Integer age,
        @Size(min = 1)
        String identification,
        @Size(min = 1)
        String address,
        @Size(min = 1)
        String phone) { }
