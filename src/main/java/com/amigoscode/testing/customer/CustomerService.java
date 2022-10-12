package com.amigoscode.testing.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository repository;
    private final PhoneNumberValidator phoneNumberValidator;

    @Autowired
    public CustomerService(CustomerRepository repository, PhoneNumberValidator phoneNumberValidator) {
        this.repository = repository;
        this.phoneNumberValidator = phoneNumberValidator;
    }

    public void register(CustomerRegistrationDto dto) {
        String phoneNumber = dto.getCustomer().getPhoneNumber();

        if (!phoneNumberValidator.test(phoneNumber))
            throw new IllegalStateException(String.format("Phone number [%s] is not valid!", phoneNumber));

        Optional<Customer> customerOriginal = repository
                .getByPhoneNumber(phoneNumber);

        if (customerOriginal.isPresent()) {
            Customer customer = customerOriginal.get();
            if (customer.getName().equals(dto.getCustomer().getName()))
                return;
            throw new IllegalStateException(String.format("Phone number [%s] is taken!", phoneNumber));
        }

        if (dto.getCustomer().getId() == null)
            dto.getCustomer().setId(UUID.randomUUID());

        repository.save(dto.getCustomer());
    }

}
