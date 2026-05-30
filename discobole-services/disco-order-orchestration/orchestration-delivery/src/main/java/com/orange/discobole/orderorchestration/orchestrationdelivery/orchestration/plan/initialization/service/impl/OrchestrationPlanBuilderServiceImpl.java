// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.service.impl;

import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ItemActionType;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderItem;
import com.orange.discobole.orderorchestration.orchestrationdelivery.client.ProductSpecificationService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.kafka.consumer.session.service.DataPersistenceKafkaSessionService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.AggregatedSpecIdLeadTimeStatistics;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.service.OrchestrationPlanBuilderService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.service.OrchestrationPlanInitService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.service.OrchestrationPlanNodeRelationshipService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.leadtimestatistics.AggregatedSpecIdLeadTimeStatisticsRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationPlanService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@SuppressFBWarnings("EI_EXPOSE_REP2")
@RequiredArgsConstructor
public class OrchestrationPlanBuilderServiceImpl implements OrchestrationPlanBuilderService {
    private static final Long DAY_IN_SECONDS = 24 * 3600L;

    private final OrchestrationPlanService orchestrationPlanService;
    private final ProductSpecificationService productSpecificationService;
    private final OrchestrationPlanNodeRelationshipService orchestrationPlanNodeRelationshipService;
    private final OrchestrationPlanInitService orchestrationPlanInitService;
    private final DataPersistenceKafkaSessionService dataPersistenceKafkaSessionService;
    private final AggregatedSpecIdLeadTimeStatisticsRepository aggregatedSpecIdLeadTimeStatisticsRepository;

    @Value("${config.adaptiveOrchestrationPlanScheduling: false}")
    private boolean adaptiveOrchestrationPlanScheduling;

    @Override
    public void buildOrchestrationPlan(OrchestrationPlan orchestrationPlan, ProductOrder productOrder) {

        List<ProductOrderItem> filteredProductOrderItemsWithSpec = orchestrationPlanInitService.filterOrderItems(productOrder);
        List<OrchestrationPlanNode> tangibleNodes = orchestrationPlanInitService.buildTangibleOrchestrationNodes(filteredProductOrderItemsWithSpec, productOrder.getId());
        List<ProductOrderItem> cfsProductOrderItems = new ArrayList<>(productOrder.getProductOrderItem());
        cfsProductOrderItems.removeAll(orchestrationPlanInitService.fetchShippingAndTangibleOrderItems(cfsProductOrderItems));
        List<OrchestrationPlanNode> cfsNodesWithMigrateAction = orchestrationPlanInitService.buildMigrationOrchestrationNodes(cfsProductOrderItems, productOrder.getId());
        List<ProductOrderItem> orderItemsWithMigrateAction = fetchOrderItemsWithMigrateAction(cfsProductOrderItems);
        cfsProductOrderItems.removeAll(orderItemsWithMigrateAction);
        List<OrchestrationPlanNode> cfsNodesWithOtherActions = orchestrationPlanInitService.buildDefaultCFSOrchestrationNodes(cfsProductOrderItems, productOrder.getId());

        List<OrchestrationPlanNode> orchestrationPlanNodes = Stream.of(tangibleNodes, cfsNodesWithMigrateAction, cfsNodesWithOtherActions)
                .flatMap(List::stream)
                .toList();
        orchestrationPlan.setOrchestrationPlanNodes(new HashSet<>(orchestrationPlanNodes));

        Map<String, com.orange.discobole.orderorchestration.orchestrationdelivery.dto.common.ProductSpecification> productSpecificationFromProductCatalog = productSpecificationService
                .retrieveProductSpecificationsFromProductOrderItem(productOrder.getProductOrderItem(), orchestrationPlan.getId());

        orchestrationPlanNodeRelationshipService.maintainRelationshipsBetweenNodes(productOrder.getProductOrderItem(), orchestrationPlan, productSpecificationFromProductCatalog);

        orchestrationPlanInitService.initRelatedProductWithDeliversType(productOrder.getId(), orchestrationPlan.getOrchestrationPlanNodes(), productOrder.getProductOrderItem(), productSpecificationFromProductCatalog);
        dataPersistenceKafkaSessionService.setPlanToBePersisted(orchestrationPlan);

        //move node state from initialized to ack
        //set node initial estimated lead time for delivery
        orchestrationPlan.getOrchestrationPlanNodes().forEach(node -> {
            orchestrationPlanService.setNextOrchestrationPlanNodeState(node);
            node.getActualRelatedProductOptional().ifPresent(relatedProduct -> {
                        AggregatedSpecIdLeadTimeStatistics statisticsByProductSpecId = aggregatedSpecIdLeadTimeStatisticsRepository.findByProductSpecId(relatedProduct.getProductSpecification().getId());
                        if (statisticsByProductSpecId != null) {
                            node.getOrchestrationNodeSchedule().setEstimatedOrderItemDeliveryLeadTime(statisticsByProductSpecId.getAverageLeadTime().longValue());
                        } else {
                            node.getOrchestrationNodeSchedule().setEstimatedOrderItemDeliveryLeadTime(0L);
                        }
                    }
            );
        });

        // set plan status based on requested delivery date, if it is in future move to PLANNED else move to ACKNOWLEDGED
        // note after this condition what persists the plan is the @KafkaSessionScope
        // this is a bad practice that we're going to remove in the future
        if (
                Objects.nonNull(orchestrationPlan.getRequestedDeliveryDate())
                        && orchestrationPlan.getRequestedDeliveryDate().isAfter(Instant.now())
        ) {

            Instant requestedDeliveryDate = orchestrationPlan.getRequestedDeliveryDate() != null ? orchestrationPlan.getRequestedDeliveryDate() : Instant.now();
            if (!adaptiveOrchestrationPlanScheduling || orchestrationPlan.getOrchestrationPlanSchedule().getEstimatedOrderDeliveryLeadTime() <= DAY_IN_SECONDS) {
                orchestrationPlan.getOrchestrationPlanSchedule().setOrderStartDate(requestedDeliveryDate);
                orchestrationPlan.getLeafs().forEach(node ->
                        node.getOrchestrationNodeSchedule().setOrderItemStartDate(requestedDeliveryDate)
                );
            } else {
                Instant requiredOrderStartDate = requestedDeliveryDate.minus(Duration.ofSeconds(orchestrationPlan.getOrchestrationPlanSchedule().getEstimatedOrderDeliveryLeadTime()));

                orchestrationPlan.getOrchestrationPlanSchedule().setOrderStartDate(
                        requiredOrderStartDate.isBefore(Instant.now()) ? Instant.now() : requiredOrderStartDate
                );

                orchestrationPlan.getLeafs().forEach(node -> {
                    Long longestEstimatedTime = orchestrationPlan.getOrchestrationPlanSchedule().getNodesLongestEstimatedLeadTime().get(node.getId());
                    if (longestEstimatedTime <= DAY_IN_SECONDS) {
                        node.getOrchestrationNodeSchedule().setOrderItemStartDate(requestedDeliveryDate);
                    } else {
                        Instant requiredOrderItemStartDate = requestedDeliveryDate.minus(Duration.ofSeconds(longestEstimatedTime));
                        node.getOrchestrationNodeSchedule().setOrderItemStartDate(
                                requiredOrderItemStartDate.isBefore(Instant.now()) ? Instant.now() : requiredOrderItemStartDate
                        );
                    }
                });
            }

            orchestrationPlan.setState(State.PLANNED);
        } else {
            orchestrationPlan.setState(State.ACKNOWLEDGED);

            orchestrationPlan.getOrchestrationPlanSchedule().setOrderStartDate(Instant.now());
            orchestrationPlan.getLeafs().forEach(node ->
                    node.getOrchestrationNodeSchedule().setOrderItemStartDate(Instant.now())
            );
        }
    }

    private List<ProductOrderItem> fetchOrderItemsWithMigrateAction(List<ProductOrderItem> productOrderItems) {
        return productOrderItems.stream().filter(productOrderItem -> productOrderItem.getAction().equals(ItemActionType.MIGRATE)).collect(Collectors.toList());
    }
}
