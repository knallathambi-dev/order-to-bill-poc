// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.kafka.consumer.session.service;

import com.orange.discobole.orderorchestration.exception.model.CoodNoSessionFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.MongoTemplateWrapperService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationPlanModificationService;
import com.orange.discobole.orderorchestration.outbox.EventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ContextConfiguration(classes = {DataPersistenceKafkaSessionService.class})
@ExtendWith(SpringExtension.class)
@SuppressWarnings("PMD.UnusedPrivateField")
class DataPersistenceKafkaSessionServiceDiffblueTest {

    @Autowired
    private DataPersistenceKafkaSessionService dataPersistenceKafkaSessionService;

    @MockBean
    private EventPublisher eventPublisher;

    @MockBean
    private MongoTemplateWrapperService mongoTemplateWrapperService;

    @MockBean
    private OrchestrationPlanModificationService orchestrationPlanModificationService;

    @MockBean
    @SuppressWarnings("PMD.UnusedPrivateField")
    private OrchestrationPlanRepository orchestrationPlanRepository;

    @MockBean
    private DataPersistenceKafkaService dataPersistenceKafkaService;

    /**
     * Method under test: {@link DataPersistenceKafkaSessionService#getSession()}
     */
    @Test
    void givenNoSession_whenGetSession_thenThrowCoodNoSessionFoundException() {
        // Arrange, Act and Assert
        assertThrows(CoodNoSessionFoundException.class, dataPersistenceKafkaSessionService::getSession);
    }

    /**
     * Method under test:
     * {@link DataPersistenceKafkaSessionService#addNodeStateChange(OrchestrationPlanNode)}
     */
    @Test
    void givenNoSession_whenAddNodeStateChange_thenThrowCoodNoSessionFoundException() {
        // Arrange, Act and Assert
        assertThrows(CoodNoSessionFoundException.class, this::addNodeStateChange);
    }

    private void addNodeStateChange() {
        dataPersistenceKafkaSessionService.addNodeStateChange(new OrchestrationPlanNode());
    }

    /**
     * Method under test:
     * {@link DataPersistenceKafkaSessionService#addNodeRelatedProductChange(OrchestrationPlanNode)}
     */
    @Test
    void givenNoSession_whenAddNodeRelatedProductChange_thenThrowCoodNoSessionFoundException() {
        // Arrange, Act and Assert
        assertThrows(CoodNoSessionFoundException.class, this::getAddNodeRelatedProductChange);
    }

    private void getAddNodeRelatedProductChange() {
        dataPersistenceKafkaSessionService.addNodeRelatedProductChange(new OrchestrationPlanNode());
    }

    /**
     * Method under test:
     * {@link DataPersistenceKafkaSessionService#addNodeRelatedProductChange(OrchestrationPlanNode)}
     */
    @Test
    void givenInitializedOrchestrationPlanNode_whenAddNodeRelatedProductChange_thenThrowCoodNoSessionFoundException() {
        // Arrange
        OrchestrationPlanNode.OrchestrationPlanNodeBuilder builderResult = OrchestrationPlanNode.builder();
        OrchestrationPlanNode.OrchestrationPlanNodeBuilder idResult = builderResult.errorMessage(new ArrayList<>())
                .id("42");
        OrchestrationPlanNode.OrchestrationPlanNodeBuilder relatedOrchestrationPlanNodeResult = idResult
                .relatedOrchestrationPlanNode(new ArrayList<>());
        OrchestrationPlanNode.OrchestrationPlanNodeBuilder relatedProductResult = relatedOrchestrationPlanNodeResult
                .relatedProduct(new ArrayList<>());
        RelatedProductOrder relatedProductOrder = RelatedProductOrder.builder()
                .id("42")
                .build();

        OrchestrationPlanNode.OrchestrationPlanNodeBuilder relatedProductOrderResult = relatedProductResult
                .relatedProductOrder(relatedProductOrder);
        OrchestrationPlanNode.OrchestrationPlanNodeBuilder relatedProductOrderItemResult = relatedProductOrderResult
                .relatedProductOrderItem(new ArrayList<>());
        RelatedServiceOrder relatedServiceOrder = RelatedServiceOrder.builder()
                .id("42")
                .orderItemId("42")
                .somRef("Som Ref")
                .build();
        OrchestrationPlanNode.OrchestrationPlanNodeBuilder relatedServiceOrderResult = relatedProductOrderItemResult
                .relatedServiceOrder(relatedServiceOrder);
        RelatedSupplyChainOrder relatedSupplyChainOrder = RelatedSupplyChainOrder.builder()
                .id("42")
                .orderItemId("42")
                .build();
        OrchestrationPlanNode node = relatedServiceOrderResult.relatedSupplyChainOrder(relatedSupplyChainOrder)
                .state(OrchestrationPlanNodeState.INITIALIZED)
                .build();

        // Act and Assert
        assertThrows(CoodNoSessionFoundException.class,
                () -> dataPersistenceKafkaSessionService.addNodeRelatedProductChange(node));
    }

    /**
     * Method under test: {@link DataPersistenceKafkaSessionService#persistAll()}
     */
    @Test
    void givenNoSession_whenPersistAll_thenThrowCoodNoSessionFoundException() {
        // Arrange, Act and Assert
        assertThrows(CoodNoSessionFoundException.class, () -> dataPersistenceKafkaSessionService.persistAll());
    }

    /**
     * Method under test:
     * {@link DataPersistenceKafkaSessionService#setPlanToBePersisted(OrchestrationPlan)}
     */
    @Test
    void givenNoSession_whenSetPlanToBePersisted_thenThrowCoodNoSessionFoundException() {
        // Arrange, Act and Assert
        assertThrows(CoodNoSessionFoundException.class, this::setPlanToBePersisted);
    }

    private void setPlanToBePersisted() {
        dataPersistenceKafkaSessionService.setPlanToBePersisted(new OrchestrationPlan());
    }
}
