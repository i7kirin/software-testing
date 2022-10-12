package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRegistrationDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentIntegrationTest {

    //Todo refactor
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void itShouldCreatePaymentSuccessfully() throws Exception {
        //Given
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId, "Duman", "+77003331195");

        CustomerRegistrationDto customerRegistrationDto = new CustomerRegistrationDto(customer);

        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/customer-registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(objectToJson(customerRegistrationDto))));

        //When

        long paymentId = 1L;
        Payment payment = new Payment(paymentId,
                customerId,
                new BigDecimal("100.00"),
                Currency.GBP,
                "0x0x0x0",
                "Donate");
        PaymentRequest paymentRequest = new PaymentRequest(payment);

        ResultActions paymentResultAction = mockMvc.perform(post("/api/v1/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(paymentRequest))));

        //Then
        paymentResultAction.andExpect(status().isOk());
        resultActions.andExpect(status().isOk());

        assertThat(paymentRepository.findById(paymentId))
                .isPresent()
                .hasValueSatisfying(p -> assertThat(p).isEqualToComparingFieldByField(payment));

        //TODO: SMS Twilio delivering
    }

    private String objectToJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            fail("Something is wrong when converting to json");
            return null;
        }
    }
}
