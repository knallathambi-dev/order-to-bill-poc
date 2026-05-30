// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.unit;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.controller.filters.FalloutFilter;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.response.FalloutResponse;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model.FalloutNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.mapper.FalloutMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.service.FalloutManagementService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.service.MongoTemplateWrapperService;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {FalloutManagementService.class})
class FalloutManagementServiceTest {

    @MockBean
    MongoTemplateWrapperService mongoTemplateWrapperService;

    @MockBean
    FalloutMapper falloutMapper;

    @Autowired
    FalloutManagementService falloutManagementService;

    @Test
    void givenValidFilterAndSorts_whenGetFalloutList_thenReturnsFalloutResponseWithHttpStatusOK() {
        // Given
        FalloutFilter filter = FalloutFilter.builder()
                .offset(0)
                .limit(10)
                .build();
        List<String> sorts = Collections.singletonList("fieldName");

        // when
        when(mongoTemplateWrapperService.count(any(), any())).thenReturn(5L);
        when(mongoTemplateWrapperService.find(any(), any())).thenReturn(Collections.emptyList());

        FalloutResponse response = falloutManagementService.getFalloutList(filter, sorts);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        // Add more assertions based on the expected behavior
    }

    @Test
    void givenEmptySorts_whenGetFalloutList_thenReturnsFalloutResponseWithHttpStatusOK() {
        // Given
        FalloutFilter filter = FalloutFilter.builder()
                .offset(0)
                .limit(10)
                .build();
        List<String> sorts = Collections.emptyList();

        // When
        when(mongoTemplateWrapperService.count(any(), any())).thenReturn(5L);
        when(mongoTemplateWrapperService.find(any(), any())).thenReturn(Collections.emptyList());

        FalloutResponse response = falloutManagementService.getFalloutList(filter, sorts);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getHttpStatus());
    }

    @Test
    void givenNullFields_whenGetFalloutById_thenThrowsInvalidParameterException() {
        // Given
        String id = "123";
        String fields = null;

        // When & Then
        assertThrows(FalloutNotFoundException.class, () -> falloutManagementService.getFalloutById(id, fields));
        // Add more assertions based on the expected behavior
    }

    @Test
    void givenNonExistingId_whenGetFalloutById_thenThrowsFalloutNotFoundException() {
        // Given
        String id = "123";
        String fields = "field1,field2";

        // When
        when(mongoTemplateWrapperService.findOne(any(), any())).thenReturn(null);

        // Then
        FalloutNotFoundException exception = assertThrows(FalloutNotFoundException.class, () -> falloutManagementService.getFalloutById(id, fields));
        assertEquals("Fallout with id 123 does not exist", exception.getMessage());
    }


    @Test
    void givenValidIdAndFields_whenGetFalloutById_thenReturnsFalloutIncident() {
        // Given
        String id = "123";
        String fields = "field1,field2";

        // When
        when(mongoTemplateWrapperService.findOne(any(), any())).thenReturn(new FalloutIncident());

        // Then
        assertDoesNotThrow(() -> {
            FalloutIncident result = falloutManagementService.getFalloutById(id, fields);
            assertNotNull(result);
            // Add more assertions based on the expected behavior
        });
    }

    @Test
    void givenNullId_whenGetFalloutById_thenThrowsInvalidParameterException() {
        // Given
        String id = null;
        String fields = "field1,field2";

        // When & Then
        assertThrows(InvalidParameterException.class, () -> falloutManagementService.getFalloutById(id, fields));
        // Add more assertions based on the expected behavior
    }

    @Test
    void givenValidFilterAndSorts_whenGetFalloutList_thenReturnsFalloutResponseWithPaginationHeaders() {
        // Given
        FalloutFilter filter = FalloutFilter.builder()
                .offset(0)
                .limit(10)
                .build();

        List<String> sorts = Collections.singletonList("fieldName");

        // When
        when(mongoTemplateWrapperService.count(any(), any())).thenReturn(15L);
        when(mongoTemplateWrapperService.find(any(), any())).thenReturn(Collections.emptyList());

        FalloutResponse response = falloutManagementService.getFalloutList(filter, sorts);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertNotNull(response.getResponseHeaders());
        // Add more assertions based on the expected behavior
    }

    @Test
    void givenEmptyFilterAndSorts_whenGetFalloutList_thenReturnsFalloutResponseWithEmptyList() {
        // Given
        FalloutFilter filter = FalloutFilter.builder()
                .offset(0)
                .limit(10)
                .build();
        List<String> sorts = Collections.emptyList();

        // When
        when(mongoTemplateWrapperService.count(any(), any())).thenReturn(0L);

        FalloutResponse response = falloutManagementService.getFalloutList(filter, sorts);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertNotNull(response.getFalloutList());
        assertTrue(response.getFalloutList().isEmpty());
        // Add more assertions based on the expected behavior
    }

}

