// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.impl;

import com.orange.discobole.ordermanagement.commons.dto.service.qualification.CheckServiceQualification;
import com.orange.discobole.ordermanagement.commons.dto.service.qualification.GeographicSite;
import com.orange.discobole.ordermanagement.commons.dto.service.qualification.Service;
import com.orange.discobole.ordermanagement.commons.dto.service.qualification.ServiceQualificationItem;
import com.orange.discobole.ordermanagement.ordercapture.service.util.DiscoServiceUrl;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceQualificationManagementServiceImplTest {

    private static final String VALID_ELIGIBILITY_MANAGEMENT_URL = "http://localhost:8082/eligibilityManagement/v5/eligibility";
    private static final String NOT_FOUND_ELIGIBILITY_ROLE_MANAGEMENT_URL = "notFoundURL";
    private static final String NUMBER = RandomStringUtils.randomAlphabetic(10);

    @Mock
    private WebClient webClient;

    @Mock
    private DiscoServiceUrl discoServiceUrl;
    @InjectMocks
    private ServiceQualificationManagementServiceImpl eligibilityManagementService;

    @Test
    @DisplayName("Given null service qualification, " +
            "when createServiceQualification is called, " +
            "then throw InvalidParameterException")
    void shouldThrowInvalidParameterExceptionForNullServiceQualification() {
        // Given & When & Then
        assertThrows(InvalidParameterException.class, () -> eligibilityManagementService.checkServiceQualification(null));
    }

    @Test
    @DisplayName("Given a non-existent eligibility  management URL, " +
            "when createServiceQualification is called, " +
            "then throw DiscoException")
    void shouldThrowDiscoExceptionForNonExistentEligibilityManagementUrl() {
        // Given
        CheckServiceQualification serviceQualification = createServiceQualification();
        when(discoServiceUrl.getServiceQualificationManagementUrl()).thenReturn(NOT_FOUND_ELIGIBILITY_ROLE_MANAGEMENT_URL);
        mockCreateServiceQualificationWebClientResponse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

        // When & Then
        assertThrows(DiscoException.class, () -> eligibilityManagementService.checkServiceQualification(serviceQualification));
    }

    @Test
    @DisplayName("Given a valid request, " +
            "when createServiceQualification is called, " +
            "then return a valid service qualification")
    void shouldReturnValidPartyRoleForValidRequest() {
        // Given
        CheckServiceQualification serviceQualification = createServiceQualification();
        when(discoServiceUrl.getServiceQualificationManagementUrl()).thenReturn(VALID_ELIGIBILITY_MANAGEMENT_URL);
        mockCreateServiceQualificationWebClientResponse(ResponseEntity.status(HttpStatus.CREATED).body(serviceQualification));

        // When
        CheckServiceQualification responseResult = eligibilityManagementService.checkServiceQualification(serviceQualification);

        // Then
        Assertions.assertEquals(serviceQualification, responseResult);
    }

    @Test
    @DisplayName("Given an empty response body, " +
            "when createServiceQualification is called, " +
            "then throw DiscoException")
    void shouldThrowDiscoExceptionForEmptyResponseBody() {
        // Given
        CheckServiceQualification serviceQualification = createServiceQualification();
        when(discoServiceUrl.getServiceQualificationManagementUrl()).thenReturn(VALID_ELIGIBILITY_MANAGEMENT_URL);
        mockCreateServiceQualificationWebClientResponse(ResponseEntity.status(HttpStatus.CREATED).build());

        // When & Then
        assertThrows(DiscoException.class, () -> eligibilityManagementService.checkServiceQualification(serviceQualification));
    }

    private void mockCreateServiceQualificationWebClientResponse(ResponseEntity<CheckServiceQualification> responseEntity) {
        WebClient.RequestBodyUriSpec requestBodyUriSpecMock = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpecMock = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec<?> requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);
        Mono<ResponseEntity<CheckServiceQualification>> responseEntityMonoMock = Mono.just(responseEntity);
        doReturn(requestBodyUriSpecMock).when(webClient).post();
        doReturn(requestBodySpecMock).when(requestBodyUriSpecMock).uri(anyString());
        doReturn(requestBodySpecMock).when(requestBodySpecMock).contentType(any());
        doReturn(requestHeadersSpecMock).when(requestBodySpecMock).bodyValue(any());
        doReturn(responseSpecMock).when(requestHeadersSpecMock).retrieve();
        doReturn(responseEntityMonoMock).when(responseSpecMock).toEntity(CheckServiceQualification.class);
    }

    private CheckServiceQualification createServiceQualification() {
        GeographicSite geographicSite = GeographicSite
                .builder()
                .id(NUMBER)
                .build();
        Service service = Service.builder()
                .place(List.of(geographicSite))
                .build();
        ServiceQualificationItem serviceQualificationItem = ServiceQualificationItem.builder()
                .service(service)
                .build();
        return CheckServiceQualification.builder()
                .serviceQualificationItem(List.of(serviceQualificationItem))
                .build();
    }
}