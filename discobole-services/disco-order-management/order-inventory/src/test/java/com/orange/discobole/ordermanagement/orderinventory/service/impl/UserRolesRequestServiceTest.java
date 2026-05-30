// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.service.impl;

import com.orange.discobole.ordermanagement.commons.dto.user.role.Entitlement;
import com.orange.discobole.ordermanagement.commons.dto.user.role.UserRole;
import com.orange.discobole.ordermanagement.orderinventory.config.security.SecurityConfigProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserRolesRequestService Tests")
class UserRolesRequestServiceTest {

    private static final String TEST_TOKEN = "test-jwt-token";
    private static final String TEST_ROLES_URL = "https://api.example.com/user/roles";

    @Mock
    private WebClient webClient;

    @Mock
    private SecurityConfigProperties securityConfigProperties;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private UserRolesRequestService userRolesRequestService;

    @BeforeEach
    void setUp() {
        userRolesRequestService = new UserRolesRequestService(webClient, securityConfigProperties);

        // Setup common mock behavior
        lenient().when(securityConfigProperties.getUserRoleRetrievalUrl()).thenReturn(TEST_ROLES_URL);
        lenient().when(webClient.get()).thenReturn(requestHeadersUriSpec);
        lenient().when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        lenient().when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        lenient().when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    @DisplayName("Given a valid token, when fetching user roles successfully, then return the user roles")
    void shouldFetchUserRolesSuccessfully() {
        // Given
        List<UserRole> expectedRoles = createTestUserRoles();
        ResponseEntity<List<UserRole>> responseEntity = new ResponseEntity<>(expectedRoles, HttpStatus.OK);

        when(responseSpec.toEntityList(UserRole.class))
                .thenReturn(Mono.just(responseEntity));

        // When
        List<UserRole> actualRoles = userRolesRequestService.fetchUserRoles(TEST_TOKEN);

        // Then
        assertThat(actualRoles)
                .isNotNull()
                .hasSize(2)
                .satisfies(roles -> {
                    assertThat(roles.get(0).getId()).isEqualTo("role1");
                    assertThat(roles.get(1).getId()).isEqualTo("role2");
                });

        // Verify interactions
        verify(securityConfigProperties).getUserRoleRetrievalUrl();
        verify(webClient).get();
        verify(requestHeadersUriSpec).uri(TEST_ROLES_URL);
        verify(requestHeadersSpec).header("Authorization", "Bearer " + TEST_TOKEN);
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).toEntityList(UserRole.class);
    }

    @Test
    @DisplayName("Given a valid token, when API returns empty response body, then return empty list")
    void shouldReturnEmptyListWhenResponseBodyIsEmpty() {
        // Given
        ResponseEntity<List<UserRole>> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

        when(responseSpec.toEntityList(UserRole.class))
                .thenReturn(Mono.just(responseEntity));

        // When
        List<UserRole> actualRoles = userRolesRequestService.fetchUserRoles(TEST_TOKEN);

        // Then
        assertThat(actualRoles)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Given a valid token, when API returns empty list, then return empty list")
    void shouldReturnEmptyListWhenResponseBodyIsEmptyList() {
        // Given
        ResponseEntity<List<UserRole>> responseEntity = new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);

        when(responseSpec.toEntityList(UserRole.class))
                .thenReturn(Mono.just(responseEntity));

        // When
        List<UserRole> actualRoles = userRolesRequestService.fetchUserRoles(TEST_TOKEN);

        // Then
        assertThat(actualRoles)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Given a valid token, when API returns non-OK status, then return empty list")
    void shouldReturnEmptyListWhenApiReturnsNonOkStatus() {
        // Given
        ResponseEntity<List<UserRole>> responseEntity = new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);

        when(responseSpec.toEntityList(UserRole.class))
                .thenReturn(Mono.just(responseEntity));

        // When
        List<UserRole> actualRoles = userRolesRequestService.fetchUserRoles(TEST_TOKEN);

        // Then
        assertThat(actualRoles)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Given a valid token, when API returns 404 status, then return empty list")
    void shouldReturnEmptyListWhenApiReturns404() {
        // Given
        ResponseEntity<List<UserRole>> responseEntity = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        when(responseSpec.toEntityList(UserRole.class))
                .thenReturn(Mono.just(responseEntity));

        // When
        List<UserRole> actualRoles = userRolesRequestService.fetchUserRoles(TEST_TOKEN);

        // Then
        assertThat(actualRoles)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Given a valid token, when WebClient throws exception, then return empty list")
    void shouldReturnEmptyListWhenWebClientThrowsException() {
        // Given
        when(responseSpec.toEntityList(UserRole.class))
                .thenReturn(Mono.error(new WebClientResponseException(500, "Internal Server Error", null, null, null)));

        // When
        List<UserRole> actualRoles = userRolesRequestService.fetchUserRoles(TEST_TOKEN);

        // Then
        assertThat(actualRoles)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Given a valid token, when network timeout occurs, then return empty list")
    void shouldReturnEmptyListWhenNetworkTimeoutOccurs() {
        // Given
        when(responseSpec.toEntityList(UserRole.class))
                .thenReturn(Mono.error(new RuntimeException("Connection timeout")));

        // When
        List<UserRole> actualRoles = userRolesRequestService.fetchUserRoles(TEST_TOKEN);

        // Then
        assertThat(actualRoles)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Given different tokens, when fetching user roles, then use correct authorization header")
    void shouldUseCorrectAuthorizationHeader() {
        // Given
        String customToken = "custom-jwt-token";
        List<UserRole> expectedRoles = createTestUserRoles();
        ResponseEntity<List<UserRole>> responseEntity = new ResponseEntity<>(expectedRoles, HttpStatus.OK);

        when(requestHeadersSpec.header("Authorization", "Bearer " + customToken)).thenReturn(requestHeadersSpec);
        when(responseSpec.toEntityList(UserRole.class))
                .thenReturn(Mono.just(responseEntity));

        // When
        userRolesRequestService.fetchUserRoles(customToken);

        // Then
        verify(requestHeadersSpec).header("Authorization", "Bearer " + customToken);
    }

    @Test
    @DisplayName("Given service configuration, when fetching user roles, then use correct URL")
    void shouldUseCorrectUrlFromConfiguration() {
        // Given
        String customUrl = "https://custom.api.com/roles";
        when(securityConfigProperties.getUserRoleRetrievalUrl()).thenReturn(customUrl);
        when(requestHeadersUriSpec.uri(customUrl)).thenReturn(requestHeadersSpec);

        List<UserRole> expectedRoles = createTestUserRoles();
        ResponseEntity<List<UserRole>> responseEntity = new ResponseEntity<>(expectedRoles, HttpStatus.OK);
        when(responseSpec.toEntityList(UserRole.class))
                .thenReturn(Mono.just(responseEntity));

        // When
        userRolesRequestService.fetchUserRoles(TEST_TOKEN);

        // Then
        verify(requestHeadersUriSpec).uri(customUrl);
    }

    @Test
    @DisplayName("Given multiple calls, when fetching user roles, then each call should be independent")
    void shouldHandleMultipleCalls() {
        // Given
        List<UserRole> expectedRoles = createTestUserRoles();
        ResponseEntity<List<UserRole>> responseEntity = new ResponseEntity<>(expectedRoles, HttpStatus.OK);

        when(responseSpec.toEntityList(UserRole.class))
                .thenReturn(Mono.just(responseEntity));

        // When
        List<UserRole> firstCall = userRolesRequestService.fetchUserRoles(TEST_TOKEN);
        List<UserRole> secondCall = userRolesRequestService.fetchUserRoles(TEST_TOKEN);

        // Then
        assertThat(firstCall).hasSize(2);
        assertThat(secondCall).hasSize(2);

        // Verify that WebClient was called twice
        verify(webClient, times(2)).get();
    }

    @Test
    @DisplayName("Given API returns various HTTP status codes, when fetching user roles, then handle appropriately")
    void shouldHandleVariousHttpStatusCodes() {
        // Test different status codes
        testStatusCode(HttpStatus.BAD_REQUEST);
        testStatusCode(HttpStatus.UNAUTHORIZED);
        testStatusCode(HttpStatus.FORBIDDEN);
        testStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        testStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
    }

    private void testStatusCode(HttpStatus status) {
        // Reset mocks for each test
        reset(responseSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        // Given
        ResponseEntity<List<UserRole>> responseEntity = new ResponseEntity<>(null, status);
        when(responseSpec.toEntityList(UserRole.class))
                .thenReturn(Mono.just(responseEntity));

        // When
        List<UserRole> actualRoles = userRolesRequestService.fetchUserRoles(TEST_TOKEN);

        // Then
        assertThat(actualRoles)
                .isNotNull()
                .isEmpty();
    }

    private List<UserRole> createTestUserRoles() {
        // Create entitlements
        Entitlement entitlement1 = Entitlement.builder()
                .id("READ_PRODUCT_ORDER")
                .build();

        Entitlement entitlement2 = Entitlement.builder()
                .id("CREATE_PRODUCT_ORDER")
                .build();

        Entitlement entitlement3 = Entitlement.builder()
                .id("UPDATE_PRODUCT_ORDER")
                .build();

        // Create user roles
        UserRole adminRole = UserRole.builder()
                .id("role1")
                .entitlement(Arrays.asList(entitlement1, entitlement2, entitlement3))
                .build();

        UserRole userRole = UserRole.builder()
                .id("role2")
                .entitlement(Arrays.asList(entitlement1))
                .build();

        return Arrays.asList(adminRole, userRole);
    }
}
