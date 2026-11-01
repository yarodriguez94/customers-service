package com.devsu.customersservice.application.ports;

public interface PasswordHasher {
    String hash(String rawPassword);
}
