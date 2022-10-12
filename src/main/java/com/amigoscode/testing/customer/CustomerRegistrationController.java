package com.amigoscode.testing.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/customer-registration")
public class CustomerRegistrationController {

    private final CustomerService customerService;

    @Autowired
    public CustomerRegistrationController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PutMapping
    public void register(@Valid @RequestBody CustomerRegistrationDto dto) {
        customerService.register(dto);
    }
}
