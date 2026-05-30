// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.impl;

import com.orange.discobole.ordermanagement.commons.dto.role.EngagedParty;
import com.orange.discobole.ordermanagement.commons.dto.role.PartyRole;
import com.orange.discobole.ordermanagement.commons.dto.role.PartyRoleSpecification;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
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
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartyRoleManagementServiceImplTest {

    public static final String DEFAULT_PRODUCT_ORDER_ID = RandomStringUtils.randomAlphabetic(10);
    public static final String DEFAULT_ENGAGED_ID = RandomStringUtils.randomAlphabetic(10);
    public static final String DEFAULT_PARTY_ROLE_SPECIFICATION_ID = RandomStringUtils.randomAlphabetic(10);
    public static final String PARTY_ROLE_ID = RandomStringUtils.randomAlphabetic(10);
    public static final String PARTY_NAME = RandomStringUtils.randomAlphabetic(10);
    private static final String NOT_FOUND_PARTY_ROLE_MANAGEMENT_URL = "notFoundURL";
    private static final String VALID_PARTY_ROLE_MANAGEMENT_URL = "http://localhost:8082/api/partyRoleManagement/v5/partyRole";

    @Mock
    private DiscoServiceUrl discoServiceUrl;
    @Mock
    private WebClient webClient;
    @InjectMocks
    private PartyRoleManagementServiceImpl partyRoleManagementService;

    @Test
    @DisplayName("Given blank party role id, " +
            "when getPartyRoles is called, " +
            "then InvalidParameterException is thrown")
    void shouldThrowInvalidParameterExceptionForBlankPartyRoleId() {
        // Given & When & Then
        assertThrows(InvalidParameterException.class, () -> partyRoleManagementService.getPartyRoles("", ""));
    }

    @Test
    @DisplayName("Given null party role id, " +
            "when getPartyRoles is called, " +
            "then InvalidParameterException is thrown")
    void shouldThrowInvalidParameterExceptionForNullPartyRoleId() {
        // Given & When & Then
        assertThrows(InvalidParameterException.class, () -> partyRoleManagementService.getPartyRoles(null, null));
    }

    @Test
    @DisplayName("Given a non-existent party role, " +
            "when getPartyRoles is called, " +
            "then throw DiscoException")
    void shouldThrowDiscoExceptionForPartyRoleNotFound() {
        // Given
        when(discoServiceUrl.getPartyRoleManagementUrl()).thenReturn(VALID_PARTY_ROLE_MANAGEMENT_URL);
        mockWebClientResponse(HttpStatus.NOT_FOUND, null);

        // When & Then
        assertThrows(DiscoException.class, () -> partyRoleManagementService.getPartyRoles(PARTY_ROLE_ID, PARTY_NAME));
    }

    @Test
    @DisplayName("Given valid party role id, " +
            "when getPartyRoles is called, " +
            "then return a list of party roles")
    void shouldReturnListOfPartyRolesForSuccessfulRequest() {
        // Given
        PartyRole partyRole = createPartyRole();
        when(discoServiceUrl.getPartyRoleManagementUrl()).thenReturn(VALID_PARTY_ROLE_MANAGEMENT_URL);
        mockWebClientResponse(HttpStatus.OK, Collections.singletonList(partyRole));

        // When
        List<PartyRole> result = partyRoleManagementService.getPartyRoles(PARTY_ROLE_ID, PARTY_NAME);

        // Then
        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Given null party role, " +
            "when createPartyRole is called, " +
            "then throw InvalidParameterException")
    void shouldThrowInvalidParameterExceptionForNullPartyRole() {
        // Given & When & Then
        assertThrows(InvalidParameterException.class, () -> partyRoleManagementService.createPartyRole(null));
    }

    @Test
    @DisplayName("Given a non-existent party role management URL, " +
            "when createPartyRole is called, " +
            "then throw DiscoException")
    void shouldThrowDiscoExceptionForNonExistentPartyRoleManagementUrl() {
        // Given
        PartyRole partyRole = createPartyRole();
        when(discoServiceUrl.getPartyRoleManagementUrl()).thenReturn(NOT_FOUND_PARTY_ROLE_MANAGEMENT_URL);
        mockCreatePartyRoleWebClientResponse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

        // When & Then
        assertThrows(DiscoException.class, () -> partyRoleManagementService.createPartyRole(partyRole));
    }

    @Test
    @DisplayName("Given an empty response body, " +
            "when createPartyRole is called, " +
            "then throw DiscoException")
    void shouldThrowDiscoExceptionForEmptyResponseBody() {
        // Given
        PartyRole partyRole = createPartyRole();
        when(discoServiceUrl.getPartyRoleManagementUrl()).thenReturn(VALID_PARTY_ROLE_MANAGEMENT_URL);
        mockCreatePartyRoleWebClientResponse(ResponseEntity.status(HttpStatus.CREATED).build());

        // When & Then
        assertThrows(DiscoException.class, () -> partyRoleManagementService.createPartyRole(partyRole));
    }

    @Test
    @DisplayName("Given a valid request, " +
            "when createPartyRole is called, " +
            "then return a valid party role")
    void shouldReturnValidPartyRoleForValidRequest() {
        // Given
        PartyRole partyRole = createPartyRole();
        when(discoServiceUrl.getPartyRoleManagementUrl()).thenReturn(VALID_PARTY_ROLE_MANAGEMENT_URL);
        mockCreatePartyRoleWebClientResponse(ResponseEntity.status(HttpStatus.CREATED).body(partyRole));

        // When
        PartyRole responseResult = partyRoleManagementService.createPartyRole(partyRole);

        // Then
        Assertions.assertEquals(partyRole, responseResult);
    }

    private PartyRole createPartyRole() {
        return PartyRole.builder()
                .engagedParty(EngagedParty.builder()
                        .id(DEFAULT_ENGAGED_ID)
                        .build())
                .partyRoleSpecification(PartyRoleSpecification.builder()
                        .id(DEFAULT_PARTY_ROLE_SPECIFICATION_ID)
                        .name(OrderCaptureConstants.CUSTOMER)
                        .build())
                .build();
    }

    private void mockWebClientResponse(HttpStatus status, List<PartyRole> partyRoles) {
        WebClient.RequestHeadersUriSpec<?> uriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> headersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        if (status.is2xxSuccessful() && partyRoles != null) {
            ResponseEntity<List<PartyRole>> responseEntity = new ResponseEntity<>(partyRoles, status);
            Mono<ResponseEntity<List<PartyRole>>> responseMono = Mono.just(responseEntity);
            doReturn(responseMono).when(responseSpecMock).toEntityList(PartyRole.class);
        } else {
            WebClientResponseException exception = WebClientResponseException.create(
                    status.value(), status.getReasonPhrase(), null, null, null
            );
            doReturn(Mono.error(exception)).when(responseSpecMock).toEntityList(PartyRole.class);
        }

        doReturn(uriSpecMock).when(webClient).get();
        doReturn(headersSpecMock).when(uriSpecMock).uri(anyString(), ArgumentMatchers.<Function<UriBuilder, URI>>any());

        doReturn(responseSpecMock).when(headersSpecMock).retrieve();
    }

    private void mockCreatePartyRoleWebClientResponse(ResponseEntity<PartyRole> responseEntity) {
        WebClient.RequestBodyUriSpec requestBodyUriSpecMock = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpecMock = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec<?> requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);
        Mono<ResponseEntity<PartyRole>> responseEntityMonoMock = Mono.just(responseEntity);
        doReturn(requestBodyUriSpecMock).when(webClient).post();
        doReturn(requestBodySpecMock).when(requestBodyUriSpecMock).uri(anyString());
        doReturn(requestBodySpecMock).when(requestBodySpecMock).contentType(any());
        doReturn(requestHeadersSpecMock).when(requestBodySpecMock).bodyValue(any());
        doReturn(responseSpecMock).when(requestHeadersSpecMock).retrieve();
        doReturn(responseEntityMonoMock).when(responseSpecMock).toEntity(PartyRole.class);
    }
}