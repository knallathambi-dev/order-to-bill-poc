// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration_plan_executor.service.impl;

import com.orange.discobole.orderorchestration.exception.model.CoodNonRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.client.ProductManagementService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedOrchestrationPlanNodeRelationshipType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.service.DeliverSelectedNodesService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.repository.OrchestrationPlanCustomRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.service.impl.OrchestrationPlanExecutionServiceImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.product.kafka.handler.UpdateNodeAndProductStateHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationPlanModificationService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationPlanService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.impl.OrchestrationPlanModificationServiceImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.util.OrchestrationPlanNodeStateMapper;
import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.dto.v1.ProductSpecificationRef;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductRelationType.DELIVERS;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


class OrchestrationPlanExecutorServiceImplTest {
    @Mock
    OrchestrationPlanCustomRepository orchestrationPlanCustomRepository;
    @Mock
    OrchestrationPlanRepository orchestrationPlanRepository;
    @Mock
    OrchestrationPlanService orchestrationPlanService;
    @Mock
    ProductManagementService productManagementService;
    @Mock
    DeliverSelectedNodesService deliverSelectedNodesService;

    OrchestrationPlanExecutionServiceImpl orchestrationPlanExecutorServiceImpl;

    @Mock
    OrchestrationPlanModificationServiceImpl orchestrationPlanModificationServiceImpl;

    @Mock
    OrchestrationPlanNodeStateMapper orchestrationPlanNodeStateMapper;

    @Mock
    UpdateNodeAndProductStateHandler updateNodeAndProductStateHandler;

    @Mock
    OrchestrationPlanModificationService orchestrationPlanModificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orchestrationPlanExecutorServiceImpl = new OrchestrationPlanExecutionServiceImpl(orchestrationPlanRepository, orchestrationPlanService, deliverSelectedNodesService, updateNodeAndProductStateHandler, orchestrationPlanModificationService);
    }

    @Test
    void testExecuteLeaf() {
        OrchestrationPlan orchestrationPlan = new OrchestrationPlan("id", new RelatedProductOrder("id"),
                List.of(new RelatedParty("id")),
                State.ACKNOWLEDGED,
                LocalDateTime.of(2023, Month.JUNE, 21, 8, 16, 2).toInstant(ZoneOffset.UTC),
                LocalDateTime.of(2023, Month.JUNE, 21, 8, 16, 2).toInstant(ZoneOffset.UTC),
                Set.of(
                        new OrchestrationPlanNode("id1", OrchestrationPlanNodeState.ACKNOWLEDGED, new RelatedServiceOrder(), null, List.of(new RelatedProductOrderItem("id1", "add", 1)), new RelatedSupplyChainOrder("id1", "id1"), List.of(RelatedProduct.builder().id("id1").relationshipType(DELIVERS).productSpecification(
                                ProductSpecification.builder().serviceSpecification(List.of(new com.orange.discobole.orderorchestration.orchestrationdelivery.model.ServiceSpecification("baseType", "referredType", "schemaLocation", "type", "href", "id", "name", "version"))).build()).build()), List.of(new RelatedOrchestrationPlanNode("id2", RelatedOrchestrationPlanNodeRelationshipType.DELIVER_AFTER)), null, null, null, false),
                        new OrchestrationPlanNode("id2", OrchestrationPlanNodeState.ACKNOWLEDGED, new RelatedServiceOrder(), null, List.of(new RelatedProductOrderItem("id2", "add", 1)), new RelatedSupplyChainOrder("id2", "id2"), List.of(RelatedProduct.builder().id("id2").relationshipType(DELIVERS).productSpecification(
                                ProductSpecification.builder().serviceSpecification(List.of(new com.orange.discobole.orderorchestration.orchestrationdelivery.model.ServiceSpecification("baseType", "referredType", "schemaLocation", "type", "href", "id2", "name", "version"))).build()).build()), List.of(), null, null, null, false),
                        new OrchestrationPlanNode("id3", OrchestrationPlanNodeState.ACKNOWLEDGED, new RelatedServiceOrder(), null, List.of(new RelatedProductOrderItem("id3", "add", 1)), new RelatedSupplyChainOrder("id3", "id3"), List.of(RelatedProduct.builder().id("id3").relationshipType(DELIVERS).productSpecification(
                                ProductSpecification.builder().serviceSpecification(List.of(new com.orange.discobole.orderorchestration.orchestrationdelivery.model.ServiceSpecification("baseType", "referredType", "schemaLocation", "type", "href", "id3", "name", "version"))).build()).build()), null, null, null, null, false)
                ),
                null,
                false, Instant.now(), List.of(), null, null
        );
        when(orchestrationPlanRepository.findOrchestrationPlanById(anyString())).thenReturn(Optional.of(orchestrationPlan));
        orchestrationPlan.setState(State.IN_PROGRESS);
        Product resultCPIB1 = Product.builder().id("id")
                .productSpecification(ProductSpecificationRef.builder().id("id").build())
                .productOrderItem(List.of(com.orange.discobole.productinventory.dto.v1.RelatedProductOrderItem.builder().orderItemId("id2").productOrderId("id").build()))
                .build();
        Product resultCPIB2 = Product.builder().id("id")
                .productSpecification(ProductSpecificationRef.builder().id("id").build())
                .productOrderItem(List.of(com.orange.discobole.productinventory.dto.v1.RelatedProductOrderItem.builder().orderItemId("id3").productOrderId("id").build()))
                .build();
        when(productManagementService.getProductsByOrderIdAndItemIds(any(), any())).thenReturn(List.of(resultCPIB1, resultCPIB2));
        orchestrationPlanExecutorServiceImpl.executeLeaf(orchestrationPlan);
        ArgumentCaptor<OrchestrationPlanNode> orchestrationPlanNodeCaptor = ArgumentCaptor.forClass(OrchestrationPlanNode.class);
        Mockito.verify(orchestrationPlanService, Mockito.times(2)).updateOrchestrationPlanNodeState(orchestrationPlanNodeCaptor.capture(), eq(OrchestrationPlanNodeState.IN_PROGRESS));
        Assertions.assertEquals(State.IN_PROGRESS, orchestrationPlan.getState());
        List<String> capturedIds = orchestrationPlanNodeCaptor.getAllValues().stream()
                .map(OrchestrationPlanNode::getId)
                .toList();
        Assertions.assertTrue(capturedIds.contains("id2"));
        Assertions.assertTrue(capturedIds.contains("id3"));
    }

    @Test
    void givenOrchestrationPlanNodeWithoutAttachedPlan_whenExecuteOrchestrationPlanNode_ThenAnExceptionWillBeThrown() {
        OrchestrationPlanNode orchestrationPlanNode = initOrchestrationPlanNode();
        Assertions.assertThrows(CoodNonRecoverableAndNonRetryableException.class, () -> orchestrationPlanExecutorServiceImpl.executeOrchestrationPlanNode(orchestrationPlanNode));
    }

    private static @NotNull OrchestrationPlanNode initOrchestrationPlanNode() {
        return new OrchestrationPlanNode("1", OrchestrationPlanNodeState.COMPLETED, new RelatedServiceOrder("id", "orderItemId", "SOMRef"), new RelatedProductOrder("id"), List.of(new RelatedProductOrderItem("2", "add", 1)), new RelatedSupplyChainOrder("2", "2"), List.of(RelatedProduct.builder().id("id1").relationshipType(DELIVERS).productSpecification(
                ProductSpecification.builder().serviceSpecification(List.of(new ServiceSpecification("baseType", "referredType", "schemaLocation", "type", "href", "id", "name", "version"))).build()).build()), List.of(new RelatedOrchestrationPlanNode("relatedNodeId", RelatedOrchestrationPlanNodeRelationshipType.DELIVER_AFTER)), null, null, null, false);
    }

    @Test
    void testExecuteRelatedNodesAborted() {
        OrchestrationPlanNode node1 = new OrchestrationPlanNode("1", OrchestrationPlanNodeState.ABORTED, new RelatedServiceOrder("id", "orderItemId", "SOMRef"), new RelatedProductOrder("id"), List.of(new RelatedProductOrderItem("id", "add", 1)), new RelatedSupplyChainOrder("2", "id"), List.of(RelatedProduct.builder().id("id").relationshipType(DELIVERS).productSpecification(
                ProductSpecification.builder().serviceSpecification(List.of(new com.orange.discobole.orderorchestration.orchestrationdelivery.model.ServiceSpecification("baseType", "referredType", "schemaLocation", "type", "href", "id", "name", "version"))).build()).build()), List.of(new RelatedOrchestrationPlanNode("relatedNodeId", RelatedOrchestrationPlanNodeRelationshipType.DELIVER_AFTER)), null, null, null, false);
        OrchestrationPlanNode node2 = new OrchestrationPlanNode("2", OrchestrationPlanNodeState.ACKNOWLEDGED, new RelatedServiceOrder("id", "orderItemId", "SOMRef"), new RelatedProductOrder("id"), List.of(new RelatedProductOrderItem("id", "add", 1)), new RelatedSupplyChainOrder("2", "id"), List.of(RelatedProduct.builder().id("id").relationshipType(DELIVERS).productSpecification(
                ProductSpecification.builder().serviceSpecification(List.of(new com.orange.discobole.orderorchestration.orchestrationdelivery.model.ServiceSpecification("baseType", "referredType", "schemaLocation", "type", "href", "id", "name", "version"))).build()).build()), List.of(new RelatedOrchestrationPlanNode("1", RelatedOrchestrationPlanNodeRelationshipType.DELIVER_AFTER)), null, null, null, false);
        OrchestrationPlanNode node3 = new OrchestrationPlanNode("3", OrchestrationPlanNodeState.ACKNOWLEDGED, new RelatedServiceOrder("id", "orderItemId", "SOMRef"), new RelatedProductOrder("id"), List.of(new RelatedProductOrderItem("id", "add", 1)), new RelatedSupplyChainOrder("2", "id"), List.of(RelatedProduct.builder().id("id").relationshipType(DELIVERS).productSpecification(
                ProductSpecification.builder().serviceSpecification(List.of(new com.orange.discobole.orderorchestration.orchestrationdelivery.model.ServiceSpecification("baseType", "referredType", "schemaLocation", "type", "href", "id", "name", "version"))).build()).build()), List.of(new RelatedOrchestrationPlanNode("1", RelatedOrchestrationPlanNodeRelationshipType.DELIVER_AFTER)), null, null, null, false);
        OrchestrationPlanNode node4 = new OrchestrationPlanNode("4", OrchestrationPlanNodeState.IN_PROGRESS, new RelatedServiceOrder("id", "orderItemId", "SOMRef"), new RelatedProductOrder("id"), List.of(new RelatedProductOrderItem("id", "add", 1)), new RelatedSupplyChainOrder("2", "id"), List.of(RelatedProduct.builder().id("id").relationshipType(DELIVERS).productSpecification(
                ProductSpecification.builder().serviceSpecification(List.of(new com.orange.discobole.orderorchestration.orchestrationdelivery.model.ServiceSpecification("baseType", "referredType", "schemaLocation", "type", "href", "id", "name", "version"))).build()).build()), List.of(new RelatedOrchestrationPlanNode("relatedNodeId", RelatedOrchestrationPlanNodeRelationshipType.DELIVER_AFTER)), null, null, null, false);

        OrchestrationPlan orchestrationPlan = new OrchestrationPlan("id", new RelatedProductOrder("id"), List.of(new RelatedParty("id")), State.INITIALIZED, LocalDateTime.of(2023, Month.JULY, 21, 12, 53, 23).toInstant(ZoneOffset.UTC), LocalDateTime.of(2023, Month.JULY, 21, 12, 53, 23).toInstant(ZoneOffset.UTC), Set.of(
                node1, node2, node3, node4
        ), null, false, Instant.now(), List.of(), null, null);
        when(orchestrationPlanRepository.findOrchestrationPlanByOrchestrationPlanNodes_id(anyString())).thenReturn(Optional.of(orchestrationPlan));
        Mockito.doAnswer(invocationOnMock -> {
            node2.setState(OrchestrationPlanNodeState.ABORTED);
            return null;
        }).when(orchestrationPlanService).updateOrchestrationPlanNodeState(node2, OrchestrationPlanNodeState.ABORTED);
        Mockito.doAnswer(invocationOnMock -> {
            node3.setState(OrchestrationPlanNodeState.ABORTED);
            return null;
        }).when(orchestrationPlanService).updateOrchestrationPlanNodeState(node3, OrchestrationPlanNodeState.ABORTED);
        orchestrationPlanExecutorServiceImpl.executeOrchestrationPlanNode(node1);
        Assertions.assertEquals(OrchestrationPlanNodeState.ABORTED, node2.getState());
        Assertions.assertEquals(OrchestrationPlanNodeState.ABORTED, node3.getState());
        Assertions.assertEquals(OrchestrationPlanNodeState.IN_PROGRESS, node4.getState());
    }

    @Test
    void testExecuteRelatedNodesHeld() {
        OrchestrationPlanNode node1 = new OrchestrationPlanNode("1", OrchestrationPlanNodeState.HELD, new RelatedServiceOrder("id", "orderItemId", "SOMRef"), new RelatedProductOrder("id"), List.of(new RelatedProductOrderItem("id", "add", 1)), new RelatedSupplyChainOrder("2", "id"), List.of(RelatedProduct.builder().id("id").relationshipType(DELIVERS).productSpecification(
                ProductSpecification.builder().serviceSpecification(List.of(new com.orange.discobole.orderorchestration.orchestrationdelivery.model.ServiceSpecification("baseType", "referredType", "schemaLocation", "type", "href", "id", "name", "version"))).build()).build()), List.of(new RelatedOrchestrationPlanNode("relatedNodeId", RelatedOrchestrationPlanNodeRelationshipType.DELIVER_AFTER)), null, null, null, false);
        OrchestrationPlanNode node2 = new OrchestrationPlanNode("2", OrchestrationPlanNodeState.IN_PROGRESS, new RelatedServiceOrder("id", "orderItemId", "SOMRef"), new RelatedProductOrder("id"), List.of(new RelatedProductOrderItem("id", "add", 1)), new RelatedSupplyChainOrder("2", "id"), List.of(RelatedProduct.builder().id("id").relationshipType(DELIVERS).productSpecification(
                ProductSpecification.builder().serviceSpecification(List.of(new com.orange.discobole.orderorchestration.orchestrationdelivery.model.ServiceSpecification("baseType", "referredType", "schemaLocation", "type", "href", "id", "name", "version"))).build()).build()), List.of(new RelatedOrchestrationPlanNode("1", RelatedOrchestrationPlanNodeRelationshipType.DELIVER_AFTER)), null, null, null, false);
        OrchestrationPlanNode node3 = new OrchestrationPlanNode("3", OrchestrationPlanNodeState.IN_PROGRESS, new RelatedServiceOrder("id", "orderItemId", "SOMRef"), new RelatedProductOrder("id"), List.of(new RelatedProductOrderItem("id", "add", 1)), new RelatedSupplyChainOrder("2", "id"), List.of(RelatedProduct.builder().id("id").relationshipType(DELIVERS).productSpecification(
                ProductSpecification.builder().serviceSpecification(List.of(new com.orange.discobole.orderorchestration.orchestrationdelivery.model.ServiceSpecification("baseType", "referredType", "schemaLocation", "type", "href", "id", "name", "version"))).build()).build()), List.of(new RelatedOrchestrationPlanNode("1", RelatedOrchestrationPlanNodeRelationshipType.DELIVER_AFTER)), null, null, null, false);
        OrchestrationPlanNode node4 = new OrchestrationPlanNode("4", OrchestrationPlanNodeState.IN_PROGRESS, new RelatedServiceOrder("id", "orderItemId", "SOMRef"), new RelatedProductOrder("id"), List.of(new RelatedProductOrderItem("id", "add", 1)), new RelatedSupplyChainOrder("2", "id"), List.of(RelatedProduct.builder().id("id").relationshipType(DELIVERS).productSpecification(
                ProductSpecification.builder().serviceSpecification(List.of(new com.orange.discobole.orderorchestration.orchestrationdelivery.model.ServiceSpecification("baseType", "referredType", "schemaLocation", "type", "href", "id", "name", "version"))).build()).build()), List.of(new RelatedOrchestrationPlanNode("relatedNodeId", RelatedOrchestrationPlanNodeRelationshipType.DELIVER_AFTER)), null, null, null, false);

        OrchestrationPlan orchestrationPlan = new OrchestrationPlan("id", new RelatedProductOrder("id"), List.of(new RelatedParty("id")), State.INITIALIZED, LocalDateTime.of(2023, Month.JULY, 21, 12, 53, 23).toInstant(ZoneOffset.UTC), LocalDateTime.of(2023, Month.JULY, 21, 12, 53, 23).toInstant(ZoneOffset.UTC), Set.of(
                node1, node2, node3, node4
        ), null, false, Instant.now(), List.of(), null, null);
        when(orchestrationPlanRepository.findOrchestrationPlanByOrchestrationPlanNodes_id(anyString())).thenReturn(Optional.of(orchestrationPlan));
        Mockito.doAnswer(invocationOnMock -> {
            node2.setState(OrchestrationPlanNodeState.HELD);
            return null;
        }).when(orchestrationPlanService).updateOrchestrationPlanNodeState(node2, OrchestrationPlanNodeState.HELD);
        Mockito.doAnswer(invocationOnMock -> {
            node3.setState(OrchestrationPlanNodeState.HELD);
            return null;
        }).when(orchestrationPlanService).updateOrchestrationPlanNodeState(node3, OrchestrationPlanNodeState.HELD);
        orchestrationPlanExecutorServiceImpl.executeOrchestrationPlanNode(node1);
        Assertions.assertEquals(OrchestrationPlanNodeState.IN_PROGRESS, node2.getState());
        Assertions.assertEquals(OrchestrationPlanNodeState.IN_PROGRESS, node3.getState());
        Assertions.assertEquals(OrchestrationPlanNodeState.IN_PROGRESS, node4.getState());
    }
}

