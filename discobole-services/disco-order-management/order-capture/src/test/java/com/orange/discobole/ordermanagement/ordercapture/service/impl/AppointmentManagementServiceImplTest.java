// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.impl;

import com.orange.discobole.ordermanagement.commons.dto.appointment.Appointment;
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
class AppointmentManagementServiceImplTest {

    private static final String APPOINTMENT_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String VALID_APPOINTMENT_URL = "http://localhost:8082/appointmentManagement/v4/appointment";

    @InjectMocks
    private AppointmentManagementServiceImpl appointmentManagementService;

    @Mock
    private WebClient webClient;

    @Mock
    private DiscoServiceUrl discoServiceUrl;

    @Test
    @DisplayName("Given blank appointment id, " +
            "when fetchAppointmentById is called, " +
            "then InvalidParameterException is thrown")
    void shouldThrowInvalidParameterExceptionForBlankAppointmentId() {
        // Given & When & Then
        Assertions.assertThrows(InvalidParameterException.class, () -> appointmentManagementService.fetchAppointmentById(""));
    }

    @Test
    @DisplayName("Given null appointment id, " +
            "when fetchAppointmentById is called, " +
            "then InvalidParameterException is thrown")
    void shouldThrowInvalidParameterExceptionForNullAppointmentId() {
        // Given & When & Then
        Assertions.assertThrows(InvalidParameterException.class, () -> appointmentManagementService.fetchAppointmentById(null));
    }

    @Test
    @DisplayName("Given non-existent appointment, " +
            "when fetchAppointmentById is called, " +
            "then DiscoException is thrown")
    void shouldThrowDiscoExceptionForNonExistingAppointmentId() {
        // Given
        when(discoServiceUrl.getAppointmentManagementByIdUrl(APPOINTMENT_ID)).thenReturn(VALID_APPOINTMENT_URL);
        mockWebClientResponse(HttpStatus.NOT_FOUND, null);

        // When & Then
        Assertions.assertThrows(DiscoException.class, () -> appointmentManagementService.fetchAppointmentById(APPOINTMENT_ID));
    }

    @Test
    @DisplayName("Given valid appointment id, " +
            "when fetchAppointmentById is called, " +
            "then appointment is returned")
    void shouldReturnBillingAccountForExistingBillingAccountId() {
        // Given
        Appointment appointment = createAppointment();
        when(discoServiceUrl.getAppointmentManagementByIdUrl(APPOINTMENT_ID)).thenReturn(VALID_APPOINTMENT_URL);
        mockWebClientResponse(HttpStatus.OK, appointment);

        // When
        ResponseResult result = appointmentManagementService.fetchAppointmentById(APPOINTMENT_ID);

        // Then
        Assertions.assertTrue(result.getResult());
        Assertions.assertNull(result.getDescription());
    }

    private Appointment createAppointment() {
        return Appointment.builder()
                .id(APPOINTMENT_ID)
                .category("intervention")
                .status("accepted")
                .build();
    }

    private void mockWebClientResponse(HttpStatus status, Appointment appointment, String... uriVariables) {
        WebClient.RequestHeadersUriSpec<?> uriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> headersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        if (status.is2xxSuccessful() && appointment != null) {
            ResponseEntity<Appointment> responseEntity = new ResponseEntity<>(appointment, status);
            Mono<ResponseEntity<Appointment>> responseMono = Mono.just(responseEntity);
            doReturn(responseMono).when(responseSpecMock).toEntity(Appointment.class);
        } else {
            WebClientResponseException exception = WebClientResponseException.create(
                    status.value(), status.getReasonPhrase(), null, null, null
            );
            doReturn(Mono.error(exception)).when(responseSpecMock).toEntity(Appointment.class);
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