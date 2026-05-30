// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Transient;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationNodeSchedule.UNDEFINED_DATE_VALUE;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationNodeSchedule.UNDEFINED_LONG_VALUE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrchestrationPlanSchedule {
    @JsonProperty("orderStartDate")
    private Instant orderStartDate;
    @JsonProperty("estimatedOrderDeliveryLeadTime")
    private Long estimatedOrderDeliveryLeadTime;
    @JsonProperty("actualOrderStartDate")
    private Instant actualOrderStartDate;

    @Transient
    @JsonIgnore
    private OrchestrationPlan orchestrationPlan;

    public Instant getExpectedOrderCompletionDate() {
        Instant start = getOrderStartDate();
        Long leadSeconds = getEstimatedOrderDeliveryLeadTime();
        if (start == null || leadSeconds == null) {
            return null;
        }

        if (leadSeconds == UNDEFINED_LONG_VALUE) {
            return UNDEFINED_DATE_VALUE;
        }

        return start.plusSeconds(leadSeconds);
    }

    public Long getActualOrderDeliveryLeadTime() {
        if (Objects.isNull(this.getActualOrderStartDate()) || Objects.isNull(this.getActualOrderCompletionDate())) {
            return null;
        }
        return Duration.between(getActualOrderStartDate(), getActualOrderCompletionDate()).getSeconds();
    }

    public Instant getActualOrderCompletionDate() {
        if (orchestrationPlan == null || orchestrationPlan.getState() != State.EXECUTED) {
            return null;
        }

        var nodes = orchestrationPlan.getOrchestrationPlanNodes();
        // if all nodes are completed and have actual order item completion date
        boolean isAllNodesCompleted = nodes.stream()
                .allMatch(node ->
                        node.getState() == OrchestrationPlanNodeState.COMPLETED &&
                                node.getOrchestrationNodeSchedule().getActualOrderItemCompletionDate() != null);

        if (isAllNodesCompleted) {
            // return maximum order item completion time as the order completion time, else if no nodes return null
            return nodes.stream()
                    .map(node -> node.getOrchestrationNodeSchedule().getActualOrderItemCompletionDate())
                    .max(Comparator.naturalOrder())
                    .orElse(null);
        }
        return null;
    }

    public Long getEstimatedOrderDeliveryLeadTime() {
        if (Objects.nonNull(this.estimatedOrderDeliveryLeadTime)) {
            return this.estimatedOrderDeliveryLeadTime;
        }

        // if the plan is not initialized then we have no data
        if (orchestrationPlan == null || orchestrationPlan.getOrchestrationPlanNodes() == null) {
            return null;
        }

        // Memoization for already computed results
        Map<String, Long> nodesLongestEstimatedLeadTimePerNodeId = getNodesLongestEstimatedLeadTime();

        long max = 0;

        for (OrchestrationPlanNode node : orchestrationPlan.getLeafs()) {
            Long childNodeLongestEstimatedLeadTime = nodesLongestEstimatedLeadTimePerNodeId.get(node.getId());
            if (Objects.isNull(childNodeLongestEstimatedLeadTime)) {
                return null;
            }
            max = Math.max(max, childNodeLongestEstimatedLeadTime);
        }

        estimatedOrderDeliveryLeadTime = max;

        return max;
    }

    @Transient
    public Map<String, Long> getNodesLongestEstimatedLeadTime() {
        // if the plan is not initialized then we have no data
        if (orchestrationPlan == null || orchestrationPlan.getOrchestrationPlanNodes() == null) {
            return new HashMap<>();
        }

        Map<String, Long> estimatedTimes = getEstimatedTimesPerNodeId();

        Map<String, List<String>> linkedNodes = getLinkedNodesPerNodeId();

        Map<String, Long> memo = new HashMap<>();

        for (OrchestrationPlanNode node : orchestrationPlan.getLeafs()) {
            calculateLongestEstimatedTime(node.getId(), linkedNodes, estimatedTimes, memo);
        }

        return memo;
    }

    @NotNull
    private Map<String, Long> getEstimatedTimesPerNodeId() {
        // if the plan is not initialized then we have no data
        if (orchestrationPlan == null || orchestrationPlan.getOrchestrationPlanNodes() == null) {
            return new HashMap<>();
        }

        Map<String, Long> estimatedTimes = new HashMap<>();
        orchestrationPlan.getOrchestrationPlanNodes().forEach(node -> {
            estimatedTimes.put(
                    node.getId(),
                    node.getOrchestrationNodeSchedule().getEstimatedOrderItemDeliveryLeadTime()
            );
        });
        return estimatedTimes;
    }

    /**
     * Builds a map of each node to the list of nodes that depend on it.
     * <p>
     * The original data stores "delivers after" relationships, but to calculate the longest path,
     * we need to invert these relationships. This allows us to start from root nodes (with no dependencies)
     * and traverse through dependent nodes to compute the maximum path based on estimated times.
     *
     * @return a map where each key is a node ID, and the value is a list of node IDs that depend on it
     */
    @NotNull
    private Map<String, List<String>> getLinkedNodesPerNodeId() {
        // if the plan is not initialized then we have no data
        if (orchestrationPlan == null || orchestrationPlan.getOrchestrationPlanNodes() == null) {
            return new HashMap<>();
        }

        Map<String, List<String>> linkedNodes = new HashMap<>();
        // Step 1: Initialize the map with all node IDs and empty lists
        for (OrchestrationPlanNode node : orchestrationPlan.getOrchestrationPlanNodes()) {
            linkedNodes.put(node.getId(), new ArrayList<>());
        }
        // Step 2: Populate the map with linked node relationships
        for (OrchestrationPlanNode node : orchestrationPlan.getOrchestrationPlanNodes()) {
            List<RelatedOrchestrationPlanNode> relatedNodes = node.getRelatedOrchestrationPlanNode();
            if (relatedNodes == null || relatedNodes.isEmpty()) {
                continue;
            }
            for (RelatedOrchestrationPlanNode relatedNode : relatedNodes) {
                linkedNodes.get(relatedNode.getRelatedNodeId()).add(node.getId());
            }
        }
        return linkedNodes;
    }

    /**
     * Calculates the longest estimated lead time for a given node ID.
     * <p>
     * This method recursively determines the maximum path sum of estimated lead times
     * starting from the specified node, traversing through all its dependent child nodes.
     * If any node in the path (including the root node) does not have an estimated lead time,
     * the method returns {@code null} to indicate that the longest estimated lead time cannot be calculated.
     * <p>
     * Results are memoized to optimize repeated calculations for the same node.
     *
     * @param nodeId         the ID of the node to start the calculation from
     * @param linkedNodes    a map where each key is a node ID and the value is a list of child node IDs that depend on it
     * @param estimatedTimes a map containing the estimated lead time for each node ID
     * @param memo           a map used for memoization to cache previously calculated results
     * @return the longest estimated lead time as a {@code Long}, or {@code null} if any node in the path lacks an estimated lead time
     */
    private Long calculateLongestEstimatedTime(
            String nodeId,
            Map<String, List<String>> linkedNodes,
            Map<String, Long> estimatedTimes,
            Map<String, Long> memo) {
        if (memo.containsKey(nodeId)) {
            return memo.get(nodeId);
        }

        long maxChild = 0L;
        for (String childrenNode : linkedNodes.get(nodeId)) {
            Long childNodeLongestEstimatedLeadTime = calculateLongestEstimatedTime(childrenNode, linkedNodes, estimatedTimes, memo);
            if (Objects.isNull(childNodeLongestEstimatedLeadTime)) {
                return null;
            }
            maxChild = Math.max(maxChild, childNodeLongestEstimatedLeadTime);
        }

        Long current = estimatedTimes.get(nodeId);
        if (Objects.isNull(current)) {
            return null;
        }
        Long result = current + maxChild;
        memo.put(nodeId, result);
        return result;
    }

}
