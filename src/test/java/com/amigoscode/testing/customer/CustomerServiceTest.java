package com.amigoscode.testing.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;

class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PhoneNumberValidator phoneNumberValidator;
    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;
    private CustomerService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new CustomerService(customerRepository, phoneNumberValidator);
    }

    @Test
    void itShouldSaveNewCustomer() {
        //Given
        String phoneNumber = "+77003331195";
        Customer customer = new Customer(UUID.randomUUID(), "Maryam", phoneNumber);

        CustomerRegistrationDto registrationDto = new CustomerRegistrationDto(customer);

        given(customerRepository.getByPhoneNumber(phoneNumber)).willReturn(Optional.empty());
        //Mock valid phone number
        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);

        //When
        underTest.register(registrationDto);

        //Then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();

        assertThat(customerArgumentCaptorValue).isEqualTo(customer);
    }

    @Test
    void itShouldNotSaveNewCustomerWhenPhoneNumberIsInvalid() {
        //Given
        String phoneNumber = "+77003331195";
        Customer customer = new Customer(UUID.randomUUID(), "Maryam", phoneNumber);

        CustomerRegistrationDto registrationDto = new CustomerRegistrationDto(customer);

        //Mock valid phone number
        given(phoneNumberValidator.test(phoneNumber)).willReturn(false);

        //When
        assertThatThrownBy(() -> underTest.register(registrationDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Phone number [%s] is not valid!", phoneNumber));

        //Then
        then(customerRepository).shouldHaveNoInteractions();
    }

    @Test
    void itShouldSaveNewCustomerWhenIdNull() {
        //Given
        String phoneNumber = "00099";
        Customer customer = new Customer(null, "Maryam", phoneNumber);

        CustomerRegistrationDto registrationDto = new CustomerRegistrationDto(customer);

        given(customerRepository.getByPhoneNumber(phoneNumber)).willReturn(Optional.empty());
        //Mock valid phone number
        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);

        //When
        underTest.register(registrationDto);

        //Then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();

        assertThat(customerArgumentCaptorValue).isEqualToIgnoringGivenFields(customer, "id");
        assertThat(customerArgumentCaptorValue.getId()).isNotNull();
    }

    @Test
    void itShouldNotSaveCustomerWhenCustomerIsExists() {
        //Given
        String phoneNumber = "00099";
        Customer customer = new Customer(UUID.randomUUID(), "Maryam", phoneNumber);

        CustomerRegistrationDto registrationDto = new CustomerRegistrationDto(customer);

        given(customerRepository.getByPhoneNumber(phoneNumber)).willReturn(Optional.of(customer));
        //Mock valid phone number
        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);

        //When
        underTest.register(registrationDto);

        //Then
        then(customerRepository).should(never()).save(any());

//        then(mockRepository).should().getByPhoneNumber(phoneNumber);
//        then(mockRepository).shouldHaveNoMoreInteractions(); // Check which methods called in testing method and get sure that other repository methods wasnot called
    }

    @Test
    void itShouldThrowExceptionWhenPhoneIsNumberTaken() {
        //Given
        String phoneNumber = "00099";
        Customer customer = new Customer(UUID.randomUUID(), "Maryam", phoneNumber);
        Customer anotherCustomer = new Customer(UUID.randomUUID(), "Duman", phoneNumber);

        CustomerRegistrationDto registrationDto = new CustomerRegistrationDto(customer);

        given(customerRepository.getByPhoneNumber(phoneNumber)).willReturn(Optional.of(anotherCustomer));
        //Mock valid phone number
        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);

        //When
        assertThatThrownBy(() -> underTest.register(registrationDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Phone number [%s] is taken!", phoneNumber));

        //Then
        then(customerRepository).should(never()).save(any());
    }
}