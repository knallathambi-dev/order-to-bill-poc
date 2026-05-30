// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.impl;

import com.orange.discobole.ordermanagement.commons.dto.account.BillingAccount;
import com.orange.discobole.ordermanagement.commons.dto.account.Money;
import com.orange.discobole.ordermanagement.ordercapture.service.dto.ResponseResult;
import com.orange.discobole.ordermanagement.ordercapture.service.util.DiscoServiceUrl;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Function;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountManagementServiceImplTest {

    private static final String BILLING_ACCOUNT_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String VALID_ACCOUNT_MANAGEMENT_URL = "http://localhost:8082/accountManagement/v5/billingAccount";
    private static final String RELATED_PARTY_ID = RandomStringUtils.randomAlphabetic(10);

    @InjectMocks
    private AccountManagementServiceImpl accountManagementService;

    @Mock
    private WebClient webClient;

    @Mock
    private DiscoServiceUrl discoServiceUrl;

    @Test
    @DisplayName("Given blank billing account id, " +
            "when checkBillingAccount is called, " +
            "then InvalidParameterException is thrown")
    void shouldThrowInvalidParameterExceptionForBlankBillingAccountId() {
        // Given & When & Then
        Assertions.assertThrows(InvalidParameterException.class, () -> accountManagementService.checkBillingAccount(""));
    }

    @Test
    @DisplayName("Given null billing account id, " +
            "when checkBillingAccount is called, " +
            "then InvalidParameterException is thrown")
    void shouldThrowInvalidParameterExceptionForNullBillingAccountId() {
        // Given & When & Then
        Assertions.assertThrows(InvalidParameterException.class, () -> accountManagementService.checkBillingAccount(null));
    }

    @Test
    @DisplayName("Given non-existent billing account, " +
            "when checkBillingAccount is called, " +
            "then DiscoException is thrown")
    void shouldThrowDiscoExceptionForNonExistingBillingAccount() {
        // Given
        when(discoServiceUrl.getAccountManagementByIdUrl(BILLING_ACCOUNT_ID)).thenReturn(VALID_ACCOUNT_MANAGEMENT_URL);
        mockWebClientResponse(HttpStatus.NOT_FOUND, null);

        // When & Then
        Assertions.assertThrows(DiscoException.class, () -> accountManagementService.checkBillingAccount(BILLING_ACCOUNT_ID));
    }

    @Test
    @DisplayName("Given valid billing account id, " +
            "when checkBillingAccount is called, " +
            "then billing account is returned")
    void shouldReturnBillingAccountForExistingBillingAccountId() {
        // Given
        BillingAccount billingAccount = createBillingAccount();
        when(discoServiceUrl.getAccountManagementByIdUrl(BILLING_ACCOUNT_ID)).thenReturn(VALID_ACCOUNT_MANAGEMENT_URL);
        mockWebClientResponse(HttpStatus.OK, billingAccount);

        // When
        ResponseResult result = accountManagementService.checkBillingAccount(BILLING_ACCOUNT_ID);

        // Then
        Assertions.assertTrue(result.getResult());
        Assertions.assertNull(result.getDescription());
    }

    @Test
    @DisplayName("Given blank related party id, " +
            "when fetchBillingAccountByRelatedPartyId is called, " +
            "then InvalidParameterException is thrown")
    void shouldThrowInvalidParameterExceptionForBlankRelatedPartyId() {
        // Given & When & Then
        Assertions.assertThrows(InvalidParameterException.class, () -> accountManagementService.fetchBillingAccountByRelatedPartyId(""));
    }

    @Test
    @DisplayName("Given null related party id, " +
            "when fetchBillingAccountByRelatedPartyId is called, " +
            "then InvalidParameterException is thrown")
    void shouldThrowInvalidParameterExceptionForNullRelatedPartyId() {
        // Given & When & Then
        Assertions.assertThrows(InvalidParameterException.class, () -> accountManagementService.fetchBillingAccountByRelatedPartyId(null));
    }

    @Test
    @DisplayName("Given valid related party id, " +
            "when fetchBillingAccountByRelatedPartyId is called, " +
            "then billing account is returned")
    void shouldReturnBillingAccountForExistingRelatedPartyId() {
        // Given
        BillingAccount billingAccount = createBillingAccount();
        when(discoServiceUrl.getAccountManagement()).thenReturn(VALID_ACCOUNT_MANAGEMENT_URL);
        mockWebClientResponse(HttpStatus.OK, billingAccount, RELATED_PARTY_ID);
        // When
        BillingAccount result = accountManagementService.fetchBillingAccountByRelatedPartyId(RELATED_PARTY_ID);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(BILLING_ACCOUNT_ID, result.getId());
    }

    @Test
    @DisplayName("Given non-existent related party id, " +
            "when fetchBillingAccountByRelatedPartyId is called, " +
            "then DiscoException is thrown")
    void shouldThrowDiscoExceptionForNonExistingRelatedPartyId() {
        // Given & When
        when(discoServiceUrl.getAccountManagement()).thenReturn(VALID_ACCOUNT_MANAGEMENT_URL);
        mockWebClientResponse(HttpStatus.NOT_FOUND, null, RELATED_PARTY_ID);

        // Then
        Assertions.assertThrows(DiscoException.class, () -> accountManagementService.fetchBillingAccountByRelatedPartyId(RELATED_PARTY_ID));
    }

    private BillingAccount createBillingAccount() {
        return BillingAccount.builder()
                .id(BILLING_ACCOUNT_ID)
                .creditLimit(Money.builder()
                        .unit("EUR")
                        .value(10f)
                        .build())
                .state("Active")
                .type("Business")
                .build();
    }

    private void mockWebClientResponse(HttpStatus status, BillingAccount billingAccount, String... uriVariables) {
        WebClient.RequestHeadersUriSpec<?> uriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> headersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        if (status.is2xxSuccessful() && billingAccount != null) {
            ResponseEntity<BillingAccount> responseEntity = new ResponseEntity<>(billingAccount, status);
            Mono<ResponseEntity<BillingAccount>> responseMono = Mono.just(responseEntity);
            doReturn(responseMono).when(responseSpecMock).toEntity(BillingAccount.class);
        } else {
            WebClientResponseException exception = WebClientResponseException.create(
                    status.value(), status.getReasonPhrase(), null, null, null
            );
            doReturn(Mono.error(exception)).when(responseSpecMock).toEntity(BillingAccount.class);
        }

        doReturn(uriSpecMock).when(webClient).get();

        if (uriVariables.length > 0) {
            doReturn(headersSpecMock).when(uriSpecMock).uri(anyString(), ArgumentMatchers.<Function<UriBuilder, URI>>any());
        } else {
            doReturn(headersSpecMock).when(uriSpecMock).uri(anyString());
        }

        doReturn(responseSpecMock).when(headersSpecMock).retrieve();
    }
}