// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.impl;

import com.orange.discobole.ordermanagement.commons.dto.user.role.Entitlement;
import com.orange.discobole.ordermanagement.commons.dto.user.role.UserRole;
import com.orange.discobole.ordermanagement.ordercapture.config.SecurityConfigProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserRolesRequestService Tests")
class UserRolesRequestServiceTest {

    private static final String TEST_TOKEN = "test-jwt-token";
    private static final String TEST_ROLES_URL = "https://api.example.com/user/roles";
    private static final String BEARER_PREFIX = "Bearer ";

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

        // Setup common mock behavior using lenient() to avoid unnecessary stubbing issues
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
        Flux<UserRole> rolesFlux = Flux.fromIterable(expectedRoles);

        when(responseSpec.bodyToFlux(UserRole.class)).thenReturn(rolesFlux);

        // When
        List<UserRole> actualRoles = userRolesRequestService.fetchUserRoles(TEST_TOKEN);

        // Then - SonarQube compliant chained assertions
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
        verify(requestHeadersSpec).header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + TEST_TOKEN);
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToFlux(UserRole.class);
    }

    @Test
    @DisplayName("Given a valid token, when API returns empty response, then return empty list")
    void shouldReturnEmptyListWhenApiReturnsEmptyResponse() {
        // Given
        Flux<UserRole> emptyFlux = Flux.empty();
        when(responseSpec.bodyToFlux(UserRole.class)).thenReturn(emptyFlux);

        // When
        List<UserRole> actualRoles = userRolesRequestService.fetchUserRoles(TEST_TOKEN);

        // Then - SonarQube compliant chained assertions
        assertThat(actualRoles)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Given a valid token, when WebClient throws 404 exception, then return empty list")
    void shouldReturnEmptyListWhenApiReturns404() {
        // Given
        WebClientResponseException exception = WebClientResponseException.create(
                404, "Not Found", null, null, null);
        Flux<UserRole> errorFlux = Flux.error(exception);

        when(responseSpec.bodyToFlux(UserRole.class)).thenReturn(errorFlux);

        // When
        List<UserRole> actualRoles = userRolesRequestService.fetchUserRoles(TEST_TOKEN);

        // Then - SonarQube compliant chained assertions
        assertThat(actualRoles)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Given a valid token, when WebClient throws 500 exception, then return empty list")
    void shouldReturnEmptyListWhenApiReturns500() {
        // Given
        WebClientResponseException exception = WebClientResponseException.create(
                500, "Internal Server Error", null, null, null);
        Flux<UserRole> errorFlux = Flux.error(exception);

        when(responseSpec.bodyToFlux(UserRole.class)).thenReturn(errorFlux);

        // When
        List<UserRole> actualRoles = userRolesRequestService.fetchUserRoles(TEST_TOKEN);

        // Then - SonarQube compliant chained assertions
        assertThat(actualRoles)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Given a valid token, when network timeout occurs, then return empty list")
    void shouldReturnEmptyListWhenNetworkTimeoutOccurs() {
        // Given
        Flux<UserRole> timeoutFlux = Flux.error(new TimeoutException("Request timeout"));
        when(responseSpec.bodyToFlux(UserRole.class)).thenReturn(timeoutFlux);

        // When
        List<UserRole> actualRoles = userRolesRequestService.fetchUserRoles(TEST_TOKEN);

        // Then - SonarQube compliant chained assertions
        assertThat(actualRoles)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Given a valid token, when unexpected runtime exception occurs, then return empty list")
    void shouldReturnEmptyListWhenUnexpectedExceptionOccurs() {
        // Given
        RuntimeException exception = new RuntimeException("Unexpected error");
        Flux<UserRole> errorFlux = Flux.error(exception);

        when(responseSpec.bodyToFlux(UserRole.class)).thenReturn(errorFlux);

        // When
        List<UserRole> actualRoles = userRolesRequestService.fetchUserRoles(TEST_TOKEN);

        // Then - SonarQube compliant chained assertions
        assertThat(actualRoles)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Given different tokens, when fetching user roles, then use correct authorization header")
    void shouldUseCorrectAuthorizationHeaderForDifferentTokens() {
        // Given
        String customToken = "custom-jwt-token";
        List<UserRole> expectedRoles = createTestUserRoles();
        Flux<UserRole> rolesFlux = Flux.fromIterable(expectedRoles);

        when(responseSpec.bodyToFlux(UserRole.class)).thenReturn(rolesFlux);

        // When
        userRolesRequestService.fetchUserRoles(customToken);

        // Then
        verify(requestHeadersSpec).header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + customToken);
    }

    @Test
    @DisplayName("Given service configuration, when fetching user roles, then use correct URL")
    void shouldUseCorrectUrlFromConfiguration() {
        // Given
        String customUrl = "https://custom.api.com/roles";
        when(securityConfigProperties.getUserRoleRetrievalUrl()).thenReturn(customUrl);

        List<UserRole> expectedRoles = createTestUserRoles();
        Flux<UserRole> rolesFlux = Flux.fromIterable(expectedRoles);
        when(responseSpec.bodyToFlux(UserRole.class)).thenReturn(rolesFlux);

        // When
        userRolesRequestService.fetchUserRoles(TEST_TOKEN);

        // Then
        verify(requestHeadersUriSpec).uri(customUrl);
    }

    @Test
    @DisplayName("Given multiple calls, when fetching user roles, then each call should be independent")
    void shouldHandleMultipleCallsIndependently() {
        // Given
        List<UserRole> expectedRoles = createTestUserRoles();
        Flux<UserRole> rolesFlux = Flux.fromIterable(expectedRoles);

        when(responseSpec.bodyToFlux(UserRole.class)).thenReturn(rolesFlux);

        // When
        List<UserRole> firstCall = userRolesRequestService.fetchUserRoles(TEST_TOKEN);
        List<UserRole> secondCall = userRolesRequestService.fetchUserRoles(TEST_TOKEN);

        // Then - SonarQube compliant chained assertions
        assertThat(firstCall).hasSize(2);
        assertThat(secondCall).hasSize(2);

        // Verify that WebClient was called twice
        verify(webClient, times(2)).get();
    }

    @Test
    @DisplayName("Given null token, when fetching user roles, then handle gracefully")
    void shouldHandleNullTokenGracefully() {
        // Given
        Flux<UserRole> errorFlux = Flux.error(new IllegalArgumentException("Token cannot be null"));
        when(responseSpec.bodyToFlux(UserRole.class)).thenReturn(errorFlux);

        // When
        List<UserRole> actualRoles = userRolesRequestService.fetchUserRoles(null);

        // Then - SonarQube compliant chained assertions
        assertThat(actualRoles)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Given empty token, when fetching user roles, then handle gracefully")
    void shouldHandleEmptyTokenGracefully() {
        // Given
        String emptyToken = "";
        List<UserRole> expectedRoles = createTestUserRoles();
        Flux<UserRole> rolesFlux = Flux.fromIterable(expectedRoles);

        when(responseSpec.bodyToFlux(UserRole.class)).thenReturn(rolesFlux);

        // When
        List<UserRole> actualRoles = userRolesRequestService.fetchUserRoles(emptyToken);

        // Then
        verify(requestHeadersSpec).header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + emptyToken);
        assertThat(actualRoles).isNotNull();
    }

    @Test
    @DisplayName("Given WebClient block() throws exception, when fetching user roles, then return empty list")
    void shouldHandleBlockExceptionGracefully() {
        // Given
        // Simulate an exception during the blocking operation by creating a flux that times out
        Flux<UserRole> timeoutFlux = Flux.fromIterable(createTestUserRoles())
                .delayElements(Duration.ofSeconds(1))
                .timeout(Duration.ofMillis(100)); // This will cause a timeout

        when(responseSpec.bodyToFlux(UserRole.class)).thenReturn(timeoutFlux);

        // When
        List<UserRole> actualRoles = userRolesRequestService.fetchUserRoles(TEST_TOKEN);

        // Then - SonarQube compliant chained assertions
        assertThat(actualRoles)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Given service instance, when checking constructor injection, then verify dependencies are set")
    void shouldVerifyConstructorInjection() {
        // Given & When
        UserRolesRequestService service = new UserRolesRequestService(webClient, securityConfigProperties);

        // Then - Verify the service is properly constructed
        assertThat(service).isNotNull();
    }

    /**
     * Helper method to create test user roles with entitlements.
     * This method centralizes test data creation to improve maintainability.
     *
     * @return List of test UserRole objects
     */
    private List<UserRole> createTestUserRoles() {
        // Create entitlements
        Entitlement readEntitlement = Entitlement.builder()
                .id("READ_PRODUCT_ORDER")
                .build();

        Entitlement createEntitlement = Entitlement.builder()
                .id("CREATE_PRODUCT_ORDER")
                .build();

        Entitlement updateEntitlement = Entitlement.builder()
                .id("UPDATE_PRODUCT_ORDER")
                .build();

        // Create user roles
        UserRole adminRole = UserRole.builder()
                .id("role1")
                .entitlement(Arrays.asList(readEntitlement, createEntitlement, updateEntitlement))
                .build();

        UserRole userRole = UserRole.builder()
                .id("role2")
                .entitlement(Collections.singletonList(readEntitlement))
                .build();

        return Arrays.asList(adminRole, userRole);
    }
}
