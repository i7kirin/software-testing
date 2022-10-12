package com.amigoscode.testing.utils;

import com.amigoscode.testing.customer.PhoneNumberValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class PhoneNumberValidatorTest {
    private PhoneNumberValidator underTest;

    @BeforeEach
    void setUp() {
        underTest = new PhoneNumberValidator();
    }

    @ParameterizedTest
    @CsvSource(value = {
            "+77003331195, TRUE",
            "770033311955, FALSE",
            "+770033311957, FALSE"
    })
    void itShouldValidatePhoneNumber(String phoneNumber, String expected) {
        //Given
//        String phoneNumber = "+77003331195";

        //When
        boolean isValid = underTest.test(phoneNumber);

        //Then
        assertThat(isValid).isEqualTo(Boolean.valueOf(expected));
    }

    @Test
    @DisplayName(" Should fall when length bigger than 12")
    void itShouldValidatePhoneNumberWhenIncorrectAndHasLengthBiggerThan12() {
        //Given
        String phoneNumber = "+770033311951";

        //When
        boolean isValid = underTest.test(phoneNumber);

        //Then
        assertThat(isValid).isFalse();
    }


    @Test
    @DisplayName(" Should fall when starts without +")
    void itShouldValidatePhoneNumberWhenStartsWithoutPlus() {
        //Given
        String phoneNumber = "7700333119511";

        //When
        boolean isValid = underTest.test(phoneNumber);

        //Then
        assertThat(isValid).isFalse();
    }
}
