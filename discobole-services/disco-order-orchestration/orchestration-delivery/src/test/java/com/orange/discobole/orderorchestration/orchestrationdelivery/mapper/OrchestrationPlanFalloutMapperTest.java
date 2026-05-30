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
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.v1.RelatedParty;
 import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.enums.RelatedEntityRole;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


class OrchestrationPlanFalloutMapperTest {

    private OrchestrationPlanFalloutMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(OrchestrationPlanFalloutMapper.class);
    }

    @Test
    void givenOrchestrationPlan_whenFrom_thenMapToFalloutIncident() {
        // Given
        OrchestrationPlan orchestrationPlan = Instancio.create(OrchestrationPlan.class);

        // When
        FalloutIncident falloutIncident = mapper.from(orchestrationPlan);

        // Then
        assertNotNull(falloutIncident);
        assertEquals(orchestrationPlan.getRelatedParty().get(0).getId(), falloutIncident.getRelatedParty().getId());
        assertEquals(orchestrationPlan.getRelatedParty().get(0).getName(), falloutIncident.getRelatedParty().getName());
        assertEquals(orchestrationPlan.getRelatedParty().get(0).getRole(), falloutIncident.getRelatedParty().getRole());
        assertEquals(orchestrationPlan.getId(), falloutIncident.getRelatedEntity().get(0).getId());
        assertNull(falloutIncident.getErrorMessage());
    }

    @Test
    void givenOrchestrationPlanNodeAndPlan_whenFrom_thenMapToFalloutIncident() {
        // Given
        OrchestrationPlanNode orchestrationPlanNode = Instancio.create(OrchestrationPlanNode.class);
        OrchestrationPlan orchestrationPlan = Instancio.create(OrchestrationPlan.class);

        // When
        FalloutIncident falloutIncident = mapper.from(orchestrationPlanNode, orchestrationPlan);

        // Then
        assertThat(falloutIncident)
                .isNotNull()
                .satisfies(fallout -> {
                    assertThat(fallout.getRelatedEntity())
                            .hasSize(3)
                            .extracting(RelatedEntity::getId)
                            .containsExactly(
                                    orchestrationPlanNode.getId(),
                                    orchestrationPlan.getId(),
                                    orchestrationPlan.getRelatedProductOrder().getId()
                            );
                    assertThat(fallout.getErrorMessage()).isNull();
                });
    }

    @Test
    void givenOrchestrationPlan_whenMapRelatedEntities_thenReturnRelatedEntities() {
        // Given
        OrchestrationPlan orchestrationPlan = Instancio.create(OrchestrationPlan.class);

        // When
        List<RelatedEntity> relatedEntities = mapper.mapRelatedEntities(orchestrationPlan);

        // Then
        assertThat(relatedEntities)
                .isNotNull()
                .hasSize(3)
                .extracting(RelatedEntity::getId)
                .containsExactly(
                        orchestrationPlan.getId(),
                        orchestrationPlan.getId(),
                        orchestrationPlan.getRelatedProductOrder().getId()
                );
    }

    @Test
    void givenNullOrchestrationPlan_whenAsOrchestrationPlanEntity_thenNullReturned() {
        RelatedEntity relatedEntity = mapper.asOrchestrationPlanEntity(null, RelatedEntityRole.INITIATOR);
        assertThat(relatedEntity).isNull();
    }

    @Test
    void givenNullOrchestrationPlan_whenAsOrchestrationPlanNodeEntity_thenNullReturned() {
        RelatedEntity relatedEntity = mapper.asOrchestrationPlanNodeEntity(null, RelatedEntityRole.INITIATOR);
        assertThat(relatedEntity).isNull();
    }

    @Test
    void givenNullOrchestrationPlan_whenAsRelatedProductOrderEntity_thenNullReturned() {
        RelatedEntity relatedEntity = mapper.asRelatedProductOrderEntity(null);
        assertThat(relatedEntity).isNull();
    }

    @Test
    void givenOrchestrationPlanAndNode_whenMapRelatedEntitiesWithNode_thenReturnRelatedEntities() {
        // Given
        OrchestrationPlanNode orchestrationPlanNode = Instancio.create(OrchestrationPlanNode.class);
        OrchestrationPlan orchestrationPlan = Instancio.create(OrchestrationPlan.class);

        // When
        List<RelatedEntity> relatedEntities = mapper.mapRelatedEntitiesWithNode(orchestrationPlanNode, orchestrationPlan);

        // Then
        assertThat(relatedEntities)
                .isNotNull()
                .hasSize(3)
                .extracting(RelatedEntity::getId)
                .containsExactly(
                        orchestrationPlanNode.getId(),
                        orchestrationPlan.getId(),
                        orchestrationPlan.getRelatedProductOrder().getId()
                );
    }

    @Test
    void givenRelatedPartyList_whenFromRelatedParty_thenReturnRelatedParty() {
        // Given
        List<com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedParty> relatedPartyList = Instancio.ofList(com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedParty.class).size(1).create();
        com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedParty expectedRelatedParty = relatedPartyList.get(0);

        // When
        RelatedParty result = mapper.fromRelatedParty(relatedPartyList);

        // Then
        assertThat(result)
                .usingRecursiveComparison(RecursiveComparisonConfiguration.builder()
                        .withStrictTypeChecking(false)
                        .withIgnoredFields("atReferredType", "atType", "href")
                        .build())
                .isEqualTo(expectedRelatedParty);
    }

    @Test
    void givenEmptyRelatedPartyList_whenFromRelatedParty_thenReturnEmptyRelatedParty() {
        // Given
        List<com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedParty> emptyList = List.of();

        // When
        RelatedParty result = mapper.fromRelatedParty(emptyList);

        // Then
        assertThat(result)
                .isNotNull()
                .satisfies(rp -> {
                    assertThat(rp.getId()).isNull();
                    assertThat(rp.getName()).isNull();
                    assertThat(rp.getRole()).isNull();
                });
    }

    @Test
    void givenOrchestrationPlanNodeAndPlan_whenMapRelatedEntitiesWithNode_thenReturnsRelatedEntities() {
        // Act
        OrchestrationPlan orchestrationPlan = Instancio.create(OrchestrationPlan.class);
        OrchestrationPlanNode orchestrationPlanNode = Instancio.create(OrchestrationPlanNode.class);
        List<RelatedEntity> relatedEntities = mapper.mapRelatedEntitiesWithNode(orchestrationPlanNode, orchestrationPlan);

        // Assert
        assertEquals(3, relatedEntities.size());

        assertEquals(orchestrationPlanNode.getId(), relatedEntities.get(0).getId());
        assertEquals("initiator", relatedEntities.get(0).getRole());
        assertEquals(orchestrationPlanNode.getClass().getSimpleName(), relatedEntities.get(0).getAtReferredType());

        assertEquals(orchestrationPlan.getId(), relatedEntities.get(1).getId());
        assertEquals("relatedOrchestrationPlan", relatedEntities.get(1).getRole());
        assertEquals(orchestrationPlan.getClass().getSimpleName(), relatedEntities.get(1).getAtReferredType());

        assertEquals(orchestrationPlan.getRelatedProductOrder().getId(), relatedEntities.get(2).getId());
        assertEquals("relatedProductOrder", relatedEntities.get(2).getRole());
        assertEquals(ProductOrder.class.getSimpleName(), relatedEntities.get(2).getAtReferredType());
    }

    @Test
    void givenOrchestrationPlan_whenMapRelatedEntities_thenReturnsRelatedEntities() {
        // Act
        OrchestrationPlan orchestrationPlan = Instancio.create(OrchestrationPlan.class);
        List<RelatedEntity> relatedEntities = mapper.mapRelatedEntities(orchestrationPlan);

        // Assert
        assertEquals(3, relatedEntities.size());

        assertEquals(orchestrationPlan.getId(), relatedEntities.get(0).getId());
        assertEquals("initiator", relatedEntities.get(0).getRole());
        assertEquals(orchestrationPlan.getClass().getSimpleName(), relatedEntities.get(0).getAtReferredType());

        assertEquals(orchestrationPlan.getId(), relatedEntities.get(1).getId());
        assertEquals("relatedOrchestrationPlan", relatedEntities.get(1).getRole());
        assertEquals(orchestrationPlan.getClass().getSimpleName(), relatedEntities.get(1).getAtReferredType());

        assertEquals(orchestrationPlan.getRelatedProductOrder().getId(), relatedEntities.get(2).getId());
        assertEquals("relatedProductOrder", relatedEntities.get(2).getRole());
        assertEquals(ProductOrder.class.getSimpleName(), relatedEntities.get(2).getAtReferredType());
    }

    @Test
    void givenNullOrEmptyRelatedParty_whenFromRelatedParty_thenReturnsEmptyRelatedParty() {
        // Act
        RelatedParty relatedParty = mapper.fromRelatedParty(null);

        // Assert
        assertEquals(null, relatedParty.getId());
        assertEquals(null, relatedParty.getName());
        assertEquals(null, relatedParty.getRole());

        // Act
        relatedParty = mapper.fromRelatedParty(List.of());

        // Assert
        assertEquals(null, relatedParty.getId());
        assertEquals(null, relatedParty.getName());
        assertEquals(null, relatedParty.getRole());
    }

    @Test
    void givenRelatedPartyList_whenFromRelatedParty_thenReturnsMappedRelatedParty() {
        // Arrange
        com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedParty party =
                new com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedParty();
        party.setId("partyId");
        party.setName("partyName");
        party.setRole("partyRole");

        // Act
        RelatedParty relatedParty = mapper.fromRelatedParty(List.of(party));

        // Assert
        assertEquals("partyId", relatedParty.getId());
        assertEquals("partyName", relatedParty.getName());
        assertEquals("partyRole", relatedParty.getRole());
    }

}