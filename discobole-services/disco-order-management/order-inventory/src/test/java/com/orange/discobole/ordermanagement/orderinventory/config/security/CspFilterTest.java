// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CspFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private CspFilter cspFilter;

    @ParameterizedTest(name = "Given a request to {2}, when filtering, then CSP header is set with expected policy")
    @MethodSource("cspPolicyTestCases")
    @DisplayName("CSP policy is set correctly based on request URI")
    void shouldSetCorrectCspPolicyBasedOnRequestUri(String requestUri, String expectedCspPolicy, String description) throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn(requestUri);
        when(response.containsHeader("Content-Security-Policy")).thenReturn(false);

        // When
        cspFilter.doFilter(request, response, filterChain);

        // Then
        verify(response).setHeader("Content-Security-Policy", expectedCspPolicy);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Given a response already contains CSP header, " +
            "when filtering, " +
            "then CSP header is not overridden")
    void shouldNotOverrideCspHeaderWhenAlreadyPresent() throws ServletException, IOException {
        // Given
        when(response.containsHeader("Content-Security-Policy")).thenReturn(true);

        // When
        cspFilter.doFilter(request, response, filterChain);

        // Then
        verify(response, never()).setHeader(anyString(), anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Given a request with null URI, " +
            "when filtering, " +
            "then filter chain continues without exception")
    void shouldHandleNullRequestUri() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn(null);
        when(response.containsHeader("Content-Security-Policy")).thenReturn(false);

        // When
        cspFilter.doFilter(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
    }

    private static Stream<Arguments> cspPolicyTestCases() {
        return Stream.of(
                Arguments.of("/swagger-ui/index.html", "default-src 'self' 'unsafe-inline' 'unsafe-eval' data:", "swagger-ui"),
                Arguments.of("/v3/api-docs", "default-src 'self' 'unsafe-inline' 'unsafe-eval' data:", "v3/api-docs"),
                Arguments.of("/v3/api-docs/swagger-config", "default-src 'self' 'unsafe-inline' 'unsafe-eval' data:", "v3/api-docs with path"),
                Arguments.of("/swagger-ui", "default-src 'self' 'unsafe-inline' 'unsafe-eval' data:", "swagger-ui root"),
                Arguments.of("/api/productOrder", "default-src 'self'", "regular endpoint"),
                Arguments.of("/", "default-src 'self'", "root path"),
                Arguments.of("/api/swagger-ui/data", "default-src 'self'", "path containing swagger-ui but not starting with it"),
                Arguments.of("/api/v3/api-docs/data", "default-src 'self'", "path containing v3/api-docs but not starting with it"),
                Arguments.of("", "default-src 'self'", "empty URI"),
                Arguments.of("/actuator/health", "default-src 'self'", "actuator endpoint")
        );
    }
}
