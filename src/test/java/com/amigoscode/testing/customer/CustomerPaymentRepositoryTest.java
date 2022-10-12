package com.amigoscode.testing.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJpaTest(
        properties = {
                "spring.jpa.properties.javax.persistence.validation.mode=none"
        }
)
class CustomerPaymentRepositoryTest {

    @Autowired
    private CustomerRepository testRepository;

    @Test
    void itShouldSelectCustomerByPhoneNumber() {
        //Given
        UUID id = UUID.randomUUID();
        String phoneNumber = "333-11-95";
        Customer duman = new Customer(id, "Duman", phoneNumber);

        //When
        testRepository.save(duman);

        //Then
        assertThat(testRepository.getByPhoneNumber(phoneNumber))
                .isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c).isEqualToComparingFieldByField(duman);
                });
    }

    @Test
    void itShouldReturnEmptyWhenGetByPhoneNumber() {
        //Given
        String phoneNumber = "333-11-95";

        //When
        Optional<Customer> byPhoneNumber = testRepository.getByPhoneNumber(phoneNumber);

        //Then
        assertThat(byPhoneNumber).isNotPresent();
    }

    @Test
    void itShouldSaveCustomer() {
        //Given
        UUID id = UUID.randomUUID();
        Customer duman = new Customer(id, "Duman", "333-11-95");

        //When
        testRepository.save(duman);

        //Then
        Optional<Customer> optionalCustomer = testRepository.findById(id);
        assertThat(optionalCustomer)
                .isPresent()
                .hasValueSatisfying(c -> {
//                    assertThat(c.getId()).isEqualTo(id);
//                    assertThat(c.getName()).isEqualTo(duman.getName());
//                    assertThat(c.getPhoneNumber()).isEqualTo("333-11-95");
                    assertThat(c).isEqualToComparingFieldByField(duman);
                });
    }

    @Test
    void itShouldNotSaveWhenNameIsNull() {
        //Given
        UUID id = UUID.randomUUID();
        Customer duman = new Customer(id, null, "333-11-95");

        //When
        //Then
        assertThatThrownBy(() -> testRepository.save(duman))
                .isInstanceOf(DataIntegrityViolationException.class)
                        .hasMessageContaining("not-null property references a null or transient value : com.amigoscode.testing.customer.Customer.name");
    }


    @Test
    void itShouldNotSaveWhenPhoneNumberIsNull() {
        //Given
        UUID id = UUID.randomUUID();
        Customer duman = new Customer(id, "Duman", null);

        //When
        //Then
        assertThatThrownBy(() -> testRepository.save(duman))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("not-null property references a null or transient value : com.amigoscode.testing.customer.Customer.phoneNumber");
    }
}