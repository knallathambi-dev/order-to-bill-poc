// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.mapper;


import com.orange.discobole.orderorchestration.exception.model.constants.CoodError;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceOrderItemErrorMessage;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationNodeErrorMessage;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

class ErrorMessageMapperTest {

    private ErrorMessageMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(ErrorMessageMapper.class);
    }

    @Test
    void givenProductCharacteristic_whenMap_thenCorrectlyMapsToOrchestrationNodeErrorMessage() {
        // Given
        Characteristic productCharacteristic = Instancio.create(Characteristic.class);

        // When
        OrchestrationNodeErrorMessage result = mapper.map(productCharacteristic);

        // Then
        assertNotNull(result, "Resulting object should not be null");
        assertEquals(
                "Service specification characteristic {" + productCharacteristic.getName() + "} was not found in the service catalog",
                result.getMessage(),
                "Message should be formatted correctly"
        );
        assertEquals(
                "Service specification characteristic {" + productCharacteristic.getName() + "}'s id is needed for the service order request creation",
                result.getReason(),
                "Reason should be formatted correctly"
        );
        assertEquals("SERVICE_SPEC_CHARACTERISTICS_NOT_FOUND", result.getCode(), "Code should be correct");
        assertNotNull(result.getTimeStamp(), "Timestamp should be set");
    }

    @Test
    void givenServiceOrderItemErrorMessageWithValidTimestamp_whenMap_thenCorrectlyMapsToOrchestrationNodeErrorMessage() {
        // Given
        ServiceOrderItemErrorMessage serviceOrderItemErrorMessage = Instancio.create(ServiceOrderItemErrorMessage.class);
        serviceOrderItemErrorMessage.setTimestamp("2024-08-12T10:15:30Z");

        // When
        CoodError result = mapper.map(serviceOrderItemErrorMessage);

        // Then
        assertNotNull(result, "Resulting object should not be null");
        assertEquals(Instant.parse("2024-08-12T10:15:30Z"), result.timestamp(), "Timestamp should be parsed correctly");
    }

    @Test
    void givenServiceOrderItemErrorMessageWithInvalidTimestamp_whenMap_thenTimestampIsNull() {
        // Given
        ServiceOrderItemErrorMessage serviceOrderItemErrorMessage = Instancio.create(ServiceOrderItemErrorMessage.class);
        serviceOrderItemErrorMessage.setTimestamp("InvalidTimestamp");

        // When
        assertThrows(DateTimeParseException.class, () -> mapper.map(serviceOrderItemErrorMessage));

    }

    @Test
    void givenServiceOrderItemErrorMessageWithBlankTimestamp_whenMap_thenTimestampIsNull() {
        // Given
        ServiceOrderItemErrorMessage serviceOrderItemErrorMessage = Instancio.create(ServiceOrderItemErrorMessage.class);
        serviceOrderItemErrorMessage.setTimestamp("");

        // When
        CoodError result = mapper.map(serviceOrderItemErrorMessage);

        // Then
        assertNotNull(result, "Resulting object should not be null");
        assertNull(result.timestamp(), "Timestamp should be null for blank input");
    }
}
