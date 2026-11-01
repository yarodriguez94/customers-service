package com.devsu.customersservice.infrastructure.controllers;

import com.devsu.customersservice.application.create.CreateCustomerUseCase;
import com.devsu.customersservice.application.list.ListCustomersUseCase;
import com.devsu.customersservice.application.update.UpdateCustomerUseCase;
import com.devsu.customersservice.application.delete.DeleteCustomerUseCase;
import com.devsu.customersservice.domain.entities.valueobjects.ClientId;
import com.devsu.customersservice.domain.exceptions.CustomerNotFoundException;
import com.devsu.customersservice.infrastructure.dtos.CreateCustomerRequest;
import com.devsu.customersservice.infrastructure.dtos.CreateCustomerRequestMapper;
import com.devsu.customersservice.infrastructure.dtos.CustomerResponse;
import com.devsu.customersservice.infrastructure.dtos.UpdateCustomerRequest;
import com.devsu.customersservice.infrastructure.dtos.UpdateCustomerRequestMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.UUID;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CreateCustomerUseCase createCustomerUseCase;
    private final ListCustomersUseCase listCustomersUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final DeleteCustomerUseCase deleteCustomerUseCase;

    public CustomerController(CreateCustomerUseCase createCustomerUseCase,
                              ListCustomersUseCase listCustomersUseCase,
                              UpdateCustomerUseCase updateCustomerUseCase,
                              DeleteCustomerUseCase deleteCustomerUseCase) {
        this.createCustomerUseCase = createCustomerUseCase;
        this.listCustomersUseCase = listCustomersUseCase;
        this.updateCustomerUseCase = updateCustomerUseCase;
        this.deleteCustomerUseCase = deleteCustomerUseCase;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CreateCustomerRequest request) {
        var command = CreateCustomerRequestMapper.toCommand(request);
        CustomerResponse response = CustomerResponse.fromDomain(createCustomerUseCase.execute(command));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        var result = listCustomersUseCase.execute(page, size);
        List<CustomerResponse> response = result.customers().stream().map(CustomerResponse::fromDomain).toList();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", Long.toString(result.totalElements()));
        headers.add("X-Total-Pages", Integer.toString(result.totalPages()));
        headers.add("X-Page", Integer.toString(result.page()));
        headers.add("X-Page-Size", Integer.toString(result.size()));
        return ResponseEntity.ok().headers(headers).body(response);
    }

    @PatchMapping("/{clientId}")
    public ResponseEntity<CustomerResponse> update(@PathVariable UUID clientId,
                                                   @Valid @RequestBody UpdateCustomerRequest request) {
        var command = UpdateCustomerRequestMapper.toCommand(clientId, request);
        CustomerResponse response = CustomerResponse.fromDomain(
                updateCustomerUseCase.execute(command).orElseThrow(CustomerNotFoundException::new));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> delete(@PathVariable UUID clientId) {
        deleteCustomerUseCase.execute(new ClientId(clientId));
        return ResponseEntity.noContent().build();
    }
}
