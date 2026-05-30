// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.impl;

import com.orange.discobole.ordermanagement.commons.dto.payment.Payment;
import com.orange.discobole.ordermanagement.ordercapture.service.dto.ResponseResult;
import com.orange.discobole.ordermanagement.ordercapture.service.util.DiscoServiceUrl;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentManagementServiceImplTest {
    public static final String PAYMENT_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String CORRELATOR_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String HREF = "https://host:port/paymentManagement/v4/payment/12345";
    private static final String NAME = "Example";
    private static final String DESCRIPTION = "a payment example";
    private static final String STATUS = "done";
    private final String paymentManagementUrl = "http://localhost:8082/paymentManagement/v4/payment";

    @Mock
    private WebClient webClient;
    @InjectMocks
    private PaymentManagementServiceImpl paymentManagementService;
    @Mock
    private DiscoServiceUrl discoServiceUrl;

    @Test
    @DisplayName("Given blank payment reference, " +
            "when checkPaymentRef is called, " +
            "then InvalidParameterException is thrown")
    void shouldThrowInvalidParameterExceptionForBlankPaymentReference() {
        // Given & When & Then
        assertThrows(InvalidParameterException.class, () -> paymentManagementService.checkPaymentRef(""));
    }

    @Test
    @DisplayName("Given null payment reference id, " +
            "when checkPaymentRef is called, " +
            "then InvalidParameterException is thrown")
    void shouldThrowInvalidParameterExceptionForNullPaymentReferenceId() {
        // Given & When & Then
        assertThrows(InvalidParameterException.class, () -> paymentManagementService.checkPaymentRef(null));
    }

    @Test
    @DisplayName("Given a non-valid payment id, " +
            "when checkPaymentRef is called, " +
            "then DiscoException is thrown with VALID_PAYMENT_REFERENCE_REQUIRED description")
    void shouldThrowDiscoExceptionForNonValidPaymentId() {
        // Given
        when(discoServiceUrl.getPaymentManagementByIdUrl(anyString())).thenReturn(paymentManagementUrl);
        mockWebClientResponse(HttpStatus.NOT_FOUND, null);

        // When & Then
        assertThrows(DiscoException.class, () -> paymentManagementService.checkPaymentRef("1233"));
    }

    @Test
    @DisplayName("Given a payment id that results in an internal server error, " +
            "when checkPaymentRef is called, " +
            "then DiscoException is thrown with PAYMENT_SERVICE_UNREACHABLE description")
    void shouldThrowDiscoExceptionForInternalServerError() {
        // Given
        when(discoServiceUrl.getPaymentManagementByIdUrl(anyString())).thenReturn(paymentManagementUrl);
        mockWebClientResponse(HttpStatus.INTERNAL_SERVER_ERROR, null);

        // When & Then
        assertThrows(DiscoException.class, () -> paymentManagementService.checkPaymentRef("1233"));
    }

    @Test
    @DisplayName("Given a valid payment id, " +
            "when checkPaymentRef is called, " +
            "then the result is true")
    void shouldReturnTrueForValidPaymentId() {
        // Given
        Payment payment = createPayment();
        when(discoServiceUrl.getPaymentManagementByIdUrl(PAYMENT_ID)).thenReturn(paymentManagementUrl);
        mockWebClientResponse(HttpStatus.OK, payment);

        // When
        ResponseResult responseResult = paymentManagementService.checkPaymentRef(PAYMENT_ID);

        // Then
        assertTrue(responseResult.getResult());
    }

    private Payment createPayment() {
        return Payment
                .builder()
                .id(PAYMENT_ID)
                .correlatorId(CORRELATOR_ID)
                .href(HREF)
                .paymentDate(new Date())
                .name(NAME)
                .description(DESCRIPTION)
                .status(STATUS)
                .statusDate(new Date())
                .build();
    }

    private void mockWebClientResponse(HttpStatus status, Payment payment) {
        WebClient.RequestHeadersUriSpec<?> uriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> headersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        if (status.is2xxSuccessful() && payment != null) {
            ResponseEntity<Payment> responseEntity = new ResponseEntity<>(payment, status);
            Mono<ResponseEntity<Payment>> responseMono = Mono.just(responseEntity);
            doReturn(responseMono).when(responseSpecMock).toEntity(Payment.class);
        } else {
            WebClientResponseException exception = WebClientResponseException.create(
                    status.value(), status.getReasonPhrase(), null, null, null
            );
            doReturn(Mono.error(exception)).when(responseSpecMock).toEntity(Payment.class);
        }

        doReturn(uriSpecMock).when(webClient).get();
        doReturn(headersSpecMock).when(uriSpecMock).uri(anyString());
        doReturn(responseSpecMock).when(headersSpecMock).retrieve();
    }
}