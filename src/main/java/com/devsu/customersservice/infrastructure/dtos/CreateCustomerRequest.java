package com.devsu.customersservice.infrastructure.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCustomerRequest(
        @NotBlank
        String password,
        @NotNull
        Boolean status,
        @NotBlank
        String firstName,
        @NotBlank
        String lastName,
        @NotBlank
        String gender,
        @NotNull @Min(0)
        Integer age,
        @NotBlank
        String identification,
        @NotBlank
        String address,
        @NotBlank
        String phone
) {}
