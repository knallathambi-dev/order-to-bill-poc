// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.mapper;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.v1.FalloutIncident;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.v1.RelatedEntity;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristic;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FalloutObjectCharacteristicMapperTest {

    private FalloutObjectCharacteristicMapper mapper = new FalloutObjectCharacteristicMapperImpl();

    @Mock
    private OrchestrationPlanFalloutMapper orchestrationPlanFalloutMapper;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenOrchestrationPlan_whenMapping_thenReturnObjectCharacteristic() {
        // Given
        MockedStatic<Mappers> mappersMockedStatic = Mockito.mockStatic(Mappers.class);
        when(Mappers.getMapper(OrchestrationPlanFalloutMapper.class)).thenReturn(orchestrationPlanFalloutMapper);

        OrchestrationPlan orchestrationPlan = Instancio.create(OrchestrationPlan.class);
        FalloutIncident expectedFalloutIncident = Instancio.create(FalloutIncident.class);

        when(orchestrationPlanFalloutMapper.from(any(OrchestrationPlan.class))).thenReturn(expectedFalloutIncident);

        // When
        ObjectCharacteristic characteristic = mapper.from(orchestrationPlan);

        // Then
        assertObjectCharacteristic(characteristic, "orchestrationPlan", ObjectCharacteristic.class.getSimpleName(), "Fallout", expectedFalloutIncident);
        mappersMockedStatic.close();
    }

    @Test
    void givenOrchestrationPlanAndNode_whenMapping_thenReturnObjectCharacteristic() {
        // Given
        MockedStatic<Mappers> mappersMockedStatic = Mockito.mockStatic(Mappers.class);
        when(Mappers.getMapper(OrchestrationPlanFalloutMapper.class)).thenReturn(orchestrationPlanFalloutMapper);
        FalloutIncident falloutIncident = Instancio.create(FalloutIncident.class);
        OrchestrationPlanNode orchestrationPlanNode = Instancio.create(OrchestrationPlanNode.class);
        OrchestrationPlan orchestrationPlan = Instancio.create(OrchestrationPlan.class);
        when(orchestrationPlanFalloutMapper.from(any(), any())).thenReturn(falloutIncident);

        // When
        ObjectCharacteristic characteristic = mapper.from(orchestrationPlan, orchestrationPlanNode);

        // Then
        assertObjectCharacteristic(characteristic, "orchestrationPlanNode", ObjectCharacteristic.class.getSimpleName(), "Fallout", falloutIncident);
        mappersMockedStatic.close();
    }

    private void assertObjectCharacteristic(ObjectCharacteristic characteristic, String expectedName, String expectedType, String expectedValueType, FalloutIncident expectedValue) {
        assertEquals(expectedName, characteristic.getName());
        assertEquals(expectedType, characteristic.getType());
        assertEquals(expectedValueType, characteristic.getValueType());
        assertFalloutIncident(expectedValue, (FalloutIncident) characteristic.getValue());
    }

    private void assertFalloutIncident(FalloutIncident expected, FalloutIncident actual) {
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getState(), actual.getState());
        assertEquals(expected.getCreationDate(), actual.getCreationDate());
        assertEquals(expected.getModificationDate(), actual.getModificationDate());
        assertEquals(expected.getReason(), actual.getReason());
        assertEquals(expected.getResolution().getStatus(), actual.getResolution().getStatus());
        assertRelatedEntityList(expected.getRelatedEntity(), actual.getRelatedEntity());
    }

    private void assertRelatedEntityList(List<RelatedEntity> expectedList, List<RelatedEntity> actualList) {
        assertNotNull(actualList);
        assertEquals(expectedList.size(), actualList.size());
        for (int i = 0; i < expectedList.size(); i++) {
            assertRelatedEntity(expectedList.get(i), actualList.get(i));
        }
    }

    private void assertRelatedEntity(RelatedEntity expected, RelatedEntity actual) {
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getAtReferredType(), actual.getAtReferredType());
        assertEquals(expected.getRole(), actual.getRole());
        assertEquals(expected.getHref(), actual.getHref());
    }
}

