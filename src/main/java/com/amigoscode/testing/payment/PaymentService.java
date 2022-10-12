package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final CardPaymentCharger cardPaymentCharger;

    @Autowired
    public PaymentService(
            PaymentRepository paymentRepository,
            CustomerRepository customerRepository,
            CardPaymentCharger cardPaymentCharger
    ) {
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
        this.cardPaymentCharger = cardPaymentCharger;
    }

    void chargeCard(UUID customerId, PaymentRequest paymentRequest) {
        Payment payment = paymentRequest.getPayment();

        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isEmpty())
            throw new IllegalStateException(String.format("Customer by id [%s] not found!", customerId));

        if (!Currency.contains(payment.getCurrency()))
            throw new IllegalStateException(String.format("Currency [%s] is not supported!", payment.getCurrency()));

        CardPaymentCharge cardPaymentCharge = cardPaymentCharger.chargeCard(
                payment.getSource(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getDescription()
        );

        if (!cardPaymentCharge.isCardDebited())
            throw new IllegalStateException("Card is not debited!");

        payment.setCustomerId(customerId);

        paymentRepository.save(payment);

        // Todo: send sms
    }

}