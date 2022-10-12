package com.amigoscode.testing.payment.stripe;

import com.amigoscode.testing.payment.CardPaymentCharge;
import com.amigoscode.testing.payment.Currency;
import com.stripe.exception.ApiConnectionException;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

class StripeServiceTest {

    private StripeService underTest;

    @Mock
    private StripeApi stripeApi;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new StripeService(stripeApi);
    }

    @Test
    void itShouldChargeCard() throws StripeException {
        //Given
        String source = "0x0x0x";
        BigDecimal amount = new BigDecimal("10.00");
        Currency currency = Currency.USD;
        String description = "Thanks";

        Charge charge = new Charge();
        charge.setPaid(true);
        given(stripeApi.create(anyMap(), any())).willReturn(charge);

        //When
        CardPaymentCharge cardPaymentCharge = underTest.chargeCard(source, amount, currency, description);

        //Then
        ArgumentCaptor<Map<String, Object>> mapArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<RequestOptions> requestOptionsArgumentCaptor = ArgumentCaptor.forClass(RequestOptions.class);

        then(stripeApi).should().create(mapArgumentCaptor.capture(), requestOptionsArgumentCaptor.capture());

        Map<String, Object> requestMap = mapArgumentCaptor.getValue();

        assertThat(requestMap.keySet()).hasSize(4);

        assertThat(requestMap.get("amount")).isEqualTo(amount);
        assertThat(requestMap.get("currency")).isEqualTo(currency);
        assertThat(requestMap.get("source")).isEqualTo(source);
        assertThat(requestMap.get("description")).isEqualTo(description);

        RequestOptions options = requestOptionsArgumentCaptor.getValue();
        assertThat(options).isNotNull();

        assertThat(cardPaymentCharge).isNotNull();
        assertThat(cardPaymentCharge.isCardDebited()).isTrue();
    }

    @Test
    void itShouldThrowExceptionWhenCardIsNotCharged() throws StripeException {
        //Given
        String source = "0x0x0x";
        BigDecimal amount = new BigDecimal("10.00");
        Currency currency = Currency.USD;
        String description = "Thanks";

        given(stripeApi.create(anyMap(), any())).willThrow(new ApiConnectionException(""));

        //When
        //Then
        assertThatThrownBy(() -> underTest.chargeCard(source, amount, currency, description))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot make stripe charge");
    }
}