// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.mapper;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.v1.FalloutIncident;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(InstancioExtension.class)
class FalloutMapperTest {

    private FalloutMapper falloutMapper;
    private com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident sourceIncident;
    private List<com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident> sourceIncidentList;

    @BeforeEach
    void setUp() {
        falloutMapper = Mappers.getMapper(FalloutMapper.class);

        // Create a single FalloutIncident object using Instancio
        sourceIncident = Instancio.create(com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident.class);

        // Create a list of FalloutIncident objects using Instancio
        sourceIncidentList = Instancio.ofList(com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident.class)
                .size(3)
                .create();
    }

    @Test
    void givenSingleFalloutIncident_whenMappedToDto_thenAllFieldsAreMappedCorrectly() {
        // Given
        // (Setup is already done in setUp())

        // When
        FalloutIncident dtoIncident = falloutMapper.toDto(sourceIncident);

        // Then
        assertNotNull(dtoIncident);
        assertEquals(sourceIncident.getResolution().getStatus().name().toLowerCase(), dtoIncident.getResolution().getStatus().name().toLowerCase());
        assertEquals(sourceIncident.getId(), dtoIncident.getId());

        if (sourceIncident.getState() != null) {
            assertEquals(sourceIncident.getState().getValue(), dtoIncident.getState());
        } else {
            assertNull(dtoIncident.getState());
        }

        assertEquals(sourceIncident.getCreationDate(), dtoIncident.getCreationDate());
        assertEquals(sourceIncident.getModificationDate(), dtoIncident.getModificationDate());

        if (sourceIncident.getRelatedEntity() != null) {
            assertEquals(sourceIncident.getRelatedEntity().size(), dtoIncident.getRelatedEntity().size());
            for (int i = 0; i < sourceIncident.getRelatedEntity().size(); i++) {
                assertRelatedEntity(i, dtoIncident);
            }
        } else {
            assertNull(dtoIncident.getRelatedEntity());
        }

        if (sourceIncident.getRelatedParty() != null) {
            assertEquals(sourceIncident.getRelatedParty().getName(), dtoIncident.getRelatedParty().getName());
        } else {
            assertNull(dtoIncident.getRelatedParty());
        }

        assertFalloutIncident(sourceIncident, dtoIncident);
    }

    private void assertRelatedEntity(int i, FalloutIncident dtoIncident) {
        assertEquals(sourceIncident.getRelatedEntity().get(i).getId(), dtoIncident.getRelatedEntity().get(i).getId());
        assertEquals(sourceIncident.getRelatedEntity().get(i).getRole().getValue(), dtoIncident.getRelatedEntity().get(i).getRole());
        assertEquals(sourceIncident.getRelatedEntity().get(i).getHref(), dtoIncident.getRelatedEntity().get(i).getHref());
        assertEquals(sourceIncident.getRelatedEntity().get(i).getAtReferredType(), dtoIncident.getRelatedEntity().get(i).getAtReferredType());
    }

    @Test
    void givenListOfFalloutIncidents_whenMappedToDtoList_thenAllFieldsAreMappedCorrectly() {
        // Given
        // (Setup is already done in setUp())

        // When
        List<FalloutIncident> dtoIncidentList = falloutMapper.toDtoList(sourceIncidentList);

        // Then
        assertNotNull(dtoIncidentList);
        assertEquals(sourceIncidentList.size(), dtoIncidentList.size());

        for (int i = 0; i < sourceIncidentList.size(); i++) {
            com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident source = sourceIncidentList.get(i);
            FalloutIncident dto = dtoIncidentList.get(i);

            assertEquals(source.getResolution().getStatus().name().toLowerCase(), dto.getResolution().getStatus().name().toLowerCase());
            assertEquals(source.getId(), dto.getId());

            if (source.getState() != null) {
                assertEquals(source.getState().getValue(), dto.getState());
            } else {
                assertNull(dto.getState());
            }

            assertEquals(source.getCreationDate(), dto.getCreationDate());
            assertEquals(source.getModificationDate(), dto.getModificationDate());

            if (source.getRelatedEntity() != null) {
                assertEquals(source.getRelatedEntity().size(), dto.getRelatedEntity().size());
                for (int j = 0; j < source.getRelatedEntity().size(); j++) {
                    assertEquals(source.getRelatedEntity().get(j).getId(), dto.getRelatedEntity().get(j).getId());
                }
            } else {
                assertNull(dto.getRelatedEntity());
            }

            if (source.getRelatedParty() != null) {
                assertEquals(source.getRelatedParty().getName(), dto.getRelatedParty().getName());
            } else {
                assertNull(dto.getRelatedParty());
            }

            assertFalloutIncident(source, dto);
        }
    }

    private static void assertFalloutIncident(com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident source, FalloutIncident dto) {
        assertEquals(source.getErrorMessage().code(), dto.getErrorMessage().getCode());
        assertEquals(source.getErrorMessage().reason(), dto.getErrorMessage().getReason());
        assertEquals(source.getErrorMessage().message(), dto.getErrorMessage().getMessage());
        assertEquals(source.getHref(), dto.getHref());

    }

    @Test
    void givenEmptyListOfFalloutIncidents_whenMappedToDtoList_thenReturnsEmptyList() {
        // Given
        List<com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident> emptyList = List.of();

        // When
        List<FalloutIncident> result = falloutMapper.toDtoList(emptyList);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void givenNullFalloutIncident_whenMappedToDto_thenReturnsNull() {
        // Given
        com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident nullIncident = null;

        // When
        FalloutIncident dtoIncident = falloutMapper.toDto(nullIncident);

        // Then
        assertNull(dtoIncident);
    }

    @Test
    void givenNullListOfFalloutIncidents_whenMappedToDtoList_thenReturnsNull() {
        // Given
        List<com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident> nullList = null;

        // When
        List<FalloutIncident> result = falloutMapper.toDtoList(nullList);

        // Then
        assertNull(result);
    }
}
