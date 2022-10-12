package com.amigoscode.testing.customer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomerRegistrationDto {

    private final Customer customer;

    public CustomerRegistrationDto(@JsonProperty("customer") Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }
}
