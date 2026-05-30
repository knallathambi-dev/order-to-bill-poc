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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.exception.model.validations.OrchestrationPlanNodeValidationException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductRelationType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState.*;

@JsonPropertyOrder({
        "id",
        "state",
        "relatedServiceOrder",
        "relatedProductOrderItem",
        "relatedProduct",
        "relatedOrchestrationPlanNode",
        "orderItemStartDate",
        "expectedOrderItemCompletionDate",
        "actualOrderItemStartDate",
        "actualOrderItemCompletionDate"
})
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_EMPTY)
public class OrchestrationPlanNode {

    @JsonProperty("id")
    @NotNull
    private String id;
    @JsonProperty("state")
    @NotNull
    private OrchestrationPlanNodeState state;
    @JsonProperty("relatedServiceOrder")
    private RelatedServiceOrder relatedServiceOrder;
    @JsonProperty("relatedProductOrder")
    private RelatedProductOrder relatedProductOrder;
    @NotNull
    @JsonProperty("relatedProductOrderItem")
    private List<RelatedProductOrderItem> relatedProductOrderItem;

    @JsonProperty("relatedSupplyChainOrderItem")
    private RelatedSupplyChainOrder relatedSupplyChainOrder;
    @JsonProperty("relatedProduct")
    private List<RelatedProduct> relatedProduct = new ArrayList<>();
    @JsonProperty("relatedOrchestrationPlanNode")
    private List<RelatedOrchestrationPlanNode> relatedOrchestrationPlanNode = new ArrayList<>();
    @JsonProperty("orchestrationNodeSchedule")
    private OrchestrationNodeSchedule orchestrationNodeSchedule;
    @JsonProperty("errorMessage")
    private List<OrchestrationNodeErrorMessage> errorMessage = new ArrayList<>();

    @JsonProperty("previousState")
    private OrchestrationPlanNodeState previousState;

    @JsonIgnore
    private static final Map<OrchestrationPlanNodeState, Set<OrchestrationPlanNodeState>> ORCHESTRATION_PLAN_NODE_EXPECTED_STATE = Map.of(
            INITIALIZED, Set.of(REJECTED, ACKNOWLEDGED),
            ABORTED, Set.of(ABORTED),
            ACKNOWLEDGED, Set.of(IN_PROGRESS, ABORTED),
            IN_PROGRESS, Set.of(IN_DELIVERY, HELD, COMPLETED),
            IN_DELIVERY, Set.of(COMPLETED, FAILED, HELD),
            COMPLETED, Set.of(COMPLETED),
            FAILED, Set.of(FAILED),
            HELD, Set.of(IN_DELIVERY, COMPLETED, FAILED, IN_PROGRESS),
            REJECTED, Set.of()
    );

    @JsonProperty("isBatched")
    private boolean deliveryBatched = false;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("state")
    public OrchestrationPlanNodeState getState() {
        return state;
    }

    @JsonProperty("state")
    public void setState(OrchestrationPlanNodeState newState) {
        if (this.state == null) {
            this.state = newState;
        } else if (ORCHESTRATION_PLAN_NODE_EXPECTED_STATE.get(this.state).contains(newState)) {
            this.previousState = this.state;
            this.state = newState;
        } else if (!this.state.equals(newState)) {
            throw new OrchestrationPlanNodeValidationException(ExceptionCode.ORCHESTRATION_PLAN_NODE_STATE_NOT_VALID, this.state, newState);
        }
    }

    @JsonProperty("relatedServiceOrder")
    public RelatedServiceOrder getRelatedServiceOrder() {
        return relatedServiceOrder;
    }

    @JsonProperty("relatedServiceOrder")
    public void setRelatedServiceOrder(RelatedServiceOrder relatedServiceOrder) {
        this.relatedServiceOrder = relatedServiceOrder;
    }

    @JsonProperty("relatedProductOrderItem")
    public List<RelatedProductOrderItem> getRelatedProductOrderItem() {
        return relatedProductOrderItem;
    }

    @JsonProperty("relatedProductOrderItem")
    public void addRelatedProductOrderItem(RelatedProductOrderItem relatedProductOrderItem) {
        if (Objects.isNull(this.relatedProductOrderItem) || this.relatedProductOrderItem.isEmpty()) {
            this.relatedProductOrderItem = new ArrayList<>(Arrays.asList(relatedProductOrderItem));
        } else {
            this.relatedProductOrderItem.add(relatedProductOrderItem);
        }
    }

    @JsonProperty("relatedProduct")
    public List<RelatedProduct> getRelatedProduct() {
        return relatedProduct;
    }

    @JsonProperty("relatedProduct")
    public void setRelatedProduct(List<RelatedProduct> relatedProduct) {
        this.relatedProduct = relatedProduct;
    }

    @JsonProperty("errorMessage")
    public List<OrchestrationNodeErrorMessage> getErrorMessage() {
        return errorMessage;
    }

    public Optional<RelatedProduct> getRelatedProductByProductSpecificationId(String id) {
        return relatedProduct.stream().filter(relatedProduct -> relatedProduct.getProductSpecification().getId().equals(id)).findFirst();
    }

    @JsonProperty("orchestrationNodeSchedule")
    public OrchestrationNodeSchedule getOrchestrationNodeSchedule() {
        if (Objects.isNull(this.orchestrationNodeSchedule)) {
            this.orchestrationNodeSchedule = OrchestrationNodeSchedule.builder().build();
        }
        return this.orchestrationNodeSchedule;
    }

    public void addRelatedProduct(RelatedProduct relatedProduct) {
        if (Objects.isNull(this.relatedProduct) || this.relatedProduct.isEmpty()) {
            this.relatedProduct = new ArrayList<>(Arrays.asList(relatedProduct));
        } else {
            this.relatedProduct.add(relatedProduct);
        }
    }

    public void addRelatedOrchestrationPlanNode(RelatedOrchestrationPlanNode relatedOrchestrationPlanNode) {
        if (Objects.isNull(this.relatedOrchestrationPlanNode) || this.relatedOrchestrationPlanNode.isEmpty()) {
            this.relatedOrchestrationPlanNode = new ArrayList<>(Arrays.asList(relatedOrchestrationPlanNode));
        } else {
            this.relatedOrchestrationPlanNode.add(relatedOrchestrationPlanNode);
        }
    }

    public void addErrorMessage(OrchestrationNodeErrorMessage errorMessage) {
        if (Objects.isNull(this.errorMessage) || this.errorMessage.isEmpty()) {
            this.errorMessage = new ArrayList<>(Arrays.asList(errorMessage));
        } else {
            this.errorMessage.add(errorMessage);
            if (this.errorMessage.size() > 20) {
                this.errorMessage.remove(0);
            }
        }
    }

    @JsonProperty("relatedOrchestrationPlanNode")
    public List<RelatedOrchestrationPlanNode> getRelatedOrchestrationPlanNode() {
        return relatedOrchestrationPlanNode;
    }

    @JsonProperty("relatedOrchestrationPlanNode")
    public void setRelatedOrchestrationPlanNode(List<RelatedOrchestrationPlanNode> relatedOrchestrationPlanNode) {
        if (Objects.nonNull(relatedOrchestrationPlanNode)) {
            this.relatedOrchestrationPlanNode = relatedOrchestrationPlanNode;
        }
    }

    @JsonProperty("relatedSupplyChainOrderItem")
    public RelatedSupplyChainOrder getRelatedSupplyChainOrder() {
        return relatedSupplyChainOrder;
    }

    @JsonProperty("relatedSupplyChainOrderItem")
    public void setRelatedSupplyChainOrder(RelatedSupplyChainOrder relatedSupplyChainOrder) {
        this.relatedSupplyChainOrder = relatedSupplyChainOrder;
    }

    //TODO: refactor getServiceSpecification, isCFSOrchestrationPlanNode, getStockItemType
    // getServiceSpecification was returning only ServiceSpecification of the Delivers Related product.
    // need to decide what should is returns
    @JsonIgnore
    public List<ServiceSpecification> getServiceSpecification() {
        return this.relatedProduct.stream()
                .filter(related -> Objects.nonNull(related.getProductSpecification()) && Objects.nonNull(related.getProductSpecification().getServiceSpecification()))
                .map(related -> related.getProductSpecification().getServiceSpecification())
                .flatMap(List::stream)
                .toList();
    }

    @JsonIgnore
    public boolean isCFSOrchestrationPlanNode() {
        return this.relatedProduct.stream().anyMatch(relatedProduct1 -> relatedProduct1.getRelationshipType().equals(RelatedProductRelationType.DELIVERS)
                && relatedProduct1.getType().equals(RelatedProductType.CFS));
    }

    @JsonIgnore
    public Optional<RelatedProduct> findProduct() {
        return this.relatedProduct.stream().filter(relatedProduct1 -> relatedProduct1.getRelationshipType().equals(RelatedProductRelationType.DELIVERS)).findFirst();
    }

    @JsonIgnore
    public boolean isTangibleOrchestrationPlanNode() {
        return this.relatedProduct.stream().anyMatch(relatedProduct1 -> relatedProduct1.getRelationshipType().equals(RelatedProductRelationType.DELIVERS) &&
                relatedProduct1.getType().equals(RelatedProductType.PHYSICAL_PRODUCT));
    }

    @JsonIgnore
    public String getActualOrderItemId() {
        return getActualRelatedProductOptional()
                .map(RelatedProduct::getProductOrderItemId)
                .orElseThrow(() -> new IllegalStateException("Related product is not present"));
    }

    @JsonIgnore
    public RelatedProductOrderItem getActualRelatedOrderItem() {
        String actualOrderItemId = getActualOrderItemId();
        return relatedProductOrderItem.stream()
                .filter(item -> item.getId().equals(actualOrderItemId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("RelatedProductOrderItem with ID " + actualOrderItemId + " not found"));
    }

    @JsonIgnore
    public Optional<RelatedProduct> getActualRelatedProductOptional() {
        return relatedProduct.stream()
                .filter(this::isValidRelatedProduct)
                .findFirst();
    }

    @JsonIgnore
    public boolean isDeliveryStarted() {
        if (this.isTangibleOrchestrationPlanNode()) {
            return Objects.nonNull(this.getRelatedSupplyChainOrder()) && Objects.nonNull(this.getRelatedSupplyChainOrder().getOrderItemId()) && Objects.nonNull(this.getRelatedSupplyChainOrder().getId());
        } else if (this.isCFSOrchestrationPlanNode()) {
            return Objects.nonNull(this.getRelatedServiceOrder()) && Objects.nonNull(this.getRelatedServiceOrder().getOrderItemId()) && Objects.nonNull(this.getRelatedServiceOrder().getId());
        }

        return false;
    }

    private boolean isValidRelatedProduct(RelatedProduct relatedProduct) {
        return relatedProduct.getRelationshipType() == RelatedProductRelationType.DELIVERS &&
                (relatedProduct.getType().equals(RelatedProductType.PHYSICAL_PRODUCT) || relatedProduct.getType().equals(RelatedProductType.CFS));
    }

    @JsonIgnore
    public boolean isLeaf() {
        return Objects.isNull(this.getRelatedOrchestrationPlanNode()) || this.getRelatedOrchestrationPlanNode().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrchestrationPlanNode that = (OrchestrationPlanNode) o;
        return Objects.equals(id, that.id) && state == that.state && Objects.equals(relatedServiceOrder, that.relatedServiceOrder) && Objects.equals(relatedProductOrderItem, that.relatedProductOrderItem) && Objects.equals(relatedProduct, that.relatedProduct) && Objects.equals(relatedOrchestrationPlanNode, that.relatedOrchestrationPlanNode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, state, relatedServiceOrder, relatedProductOrderItem, relatedProduct, relatedOrchestrationPlanNode);
    }

    @JsonIgnore
    public Boolean isRelatedProductEmptyOrNull() {
        return Objects.isNull(relatedProduct) || relatedProduct.isEmpty();
    }

}
