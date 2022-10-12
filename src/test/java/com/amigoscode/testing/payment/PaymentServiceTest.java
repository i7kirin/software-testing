package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

class PaymentServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CardPaymentCharger cardPaymentCharger;

    @Captor
    private ArgumentCaptor<Payment> paymentArgumentCaptor;

    private PaymentService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new PaymentService( paymentRepository, customerRepository, cardPaymentCharger);
    }

    @Test
    void itShouldChargeCard() {
        //Given
        UUID customerId = UUID.randomUUID();
        Payment payment = new Payment
                (1L,
                        UUID.randomUUID(),
                        new BigDecimal("10.00"),
                        Currency.USD,
                        "card-111",
                        "credit"
                );

        PaymentRequest paymentRequest = new PaymentRequest(payment);

        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));
        given(cardPaymentCharger.chargeCard(
                payment.getSource(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getDescription()
        )).willReturn(new CardPaymentCharge(true));

        //When
        underTest.chargeCard(customerId, paymentRequest);

        //Then
        then(paymentRepository).should().save(paymentArgumentCaptor.capture());
        Payment paymentArgumentCaptorValue = paymentArgumentCaptor.getValue();

        assertThat(paymentArgumentCaptorValue).isEqualToComparingFieldByField(payment);
    }

    @Test
    void itShouldThrowIfCustomerNotExists() {
        //Given
        UUID customerId = UUID.randomUUID();

        given(customerRepository.findById(customerId)).willReturn(Optional.empty());

        //When Customer not found in db
        //Then
        assertThatThrownBy(() -> underTest.chargeCard(customerId, new PaymentRequest(new Payment())))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Customer by id [%s] not found!", customerId));

        then(cardPaymentCharger).shouldHaveNoInteractions();
        then(paymentRepository).shouldHaveNoInteractions();
    }

    @Test
    void itShouldThrowExceptionWhenCurrencyIsNotSupported() {
        //Given
        UUID customerId = UUID.randomUUID();
        Payment payment = new Payment
                (1L,
                        UUID.randomUUID(),
                        new BigDecimal("10.00"),
                        null,
                        "card-111",
                        "credit"
                );

        PaymentRequest paymentRequest = new PaymentRequest(payment);

        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        //When
        //Then
        assertThatThrownBy(() -> underTest.chargeCard(customerId, paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Currency [%s] is not supported!", payment.getCurrency()));

        then(cardPaymentCharger).shouldHaveNoInteractions();
        then(paymentRepository).shouldHaveNoInteractions();
    }

    @Test
    void itShouldThrowExceptionWhenCardIsNotDebited() {
        //Given
        UUID customerId = UUID.randomUUID();
        Payment payment = new Payment
                (1L,
                        UUID.randomUUID(),
                        new BigDecimal("10.00"),
                        Currency.USD,
                        "card-111",
                        "credit"
                );

        PaymentRequest paymentRequest = new PaymentRequest(payment);

        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));
        given(cardPaymentCharger.chargeCard(
                payment.getSource(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getDescription()
        )).willReturn(new CardPaymentCharge(false));

        //When
        //Then
        assertThatThrownBy(() ->underTest.chargeCard(customerId, paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Card is not debited!");

        then(paymentRepository).should(never()).save(payment);
    }
}