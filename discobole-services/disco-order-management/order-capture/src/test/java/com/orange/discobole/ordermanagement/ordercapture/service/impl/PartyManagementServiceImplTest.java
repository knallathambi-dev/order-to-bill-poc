// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.impl;


import com.orange.discobole.ordermanagement.commons.dto.party.management.Party;
import com.orange.discobole.ordermanagement.commons.dto.party.management.PartyCreditProfile;
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
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doReturn;


@ExtendWith(MockitoExtension.class)
class PartyManagementServiceImplTest {
    private static final String PARTY_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String VALID_PARTY_MANAGEMENT_URL = "http://localhost:8082/partyManagement/v5/individual";


    @InjectMocks
    private PartyManagementServiceImpl financialEligibilityService;

    @Mock
    private WebClient webClient;

    @Mock
    private DiscoServiceUrl discoServiceUrl;

    @Test
    @DisplayName("Given blank party id, " +
            "when checkFinancialEligibility is called, " +
            "then InvalidParameterException is thrown")
    void shouldThrowInvalidParameterExceptionForBlankPartyId() {
        // Given & When & Then
        assertThrows(InvalidParameterException.class, () -> financialEligibilityService.getPartyById(""));
    }

    @Test
    @DisplayName("Given null party id, " +
            "when checkFinancialEligibility is called, " +
            "then InvalidParameterException is thrown")
    void shouldThrowInvalidParameterExceptionForNullPartyId() {
        // Given & When & Then
        assertThrows(InvalidParameterException.class, () -> financialEligibilityService.getPartyById(null));
    }

    @Test
    @DisplayName("Given non-existent party, " +
            "when checkFinancialEligibility is called, " +
            "then DiscoException is thrown")
    void shouldThrowDiscoExceptionForNonExistingPartyId() {
        // Given
        when(discoServiceUrl.getPartyManagementByIdUrl(PARTY_ID)).thenReturn(VALID_PARTY_MANAGEMENT_URL);
        mockWebClientResponse(HttpStatus.NOT_FOUND, null);

        // When & Then
        assertThrows(DiscoException.class, () -> financialEligibilityService.getPartyById(PARTY_ID));
    }

    @Test
    @DisplayName("Given valid party id, " +
            "when checkFinancialEligibility is called, " +
            "then party is returned")
    void shouldReturnPartyForExistingPartyId() {
        // Given
        Party party = createParty();
        when(discoServiceUrl.getPartyManagementByIdUrl(PARTY_ID)).thenReturn(VALID_PARTY_MANAGEMENT_URL);
        mockWebClientResponse(HttpStatus.OK, party);

        // When
        Party result = financialEligibilityService.getPartyById(PARTY_ID);

        // Then
        Assertions.assertNotNull(result);
    }

    private Party createParty() {
        PartyCreditProfile partyCreditProfile = PartyCreditProfile.builder()
                .ratingScore(600)
                .build();
        return Party
                .builder()
                .id(PARTY_ID)
                .creditRating(List.of(partyCreditProfile))
                .build();
    }


    private void mockWebClientResponse(HttpStatus status, Party party, String... uriVariables) {
        WebClient.RequestHeadersUriSpec<?> uriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> headersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        if (status.is2xxSuccessful() && party != null) {
            ResponseEntity<Party> responseEntity = new ResponseEntity<>(party, status);
            Mono<ResponseEntity<Party>> responseMono = Mono.just(responseEntity);
            doReturn(responseMono).when(responseSpecMock).toEntity(Party.class);
        } else {
            WebClientResponseException exception = WebClientResponseException.create(
                    status.value(), status.getReasonPhrase(), null, null, null
            );
            doReturn(Mono.error(exception)).when(responseSpecMock).toEntity(Party.class);
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
