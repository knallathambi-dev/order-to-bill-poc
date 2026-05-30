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
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState.*;

@JsonPropertyOrder({
        "id",
        "relatedProductOrder",
        "relatedParty",
        "state",
        "recievedDate",
        "requestedDeliveryDate",
        "orchestrationPlanNodes",
        "relatedContractName"
})
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_EMPTY)
@FieldNameConstants
public class OrchestrationPlan {
    @Id
    @JsonProperty("id")
    @NotNull
    private String id;
    @NotNull
    @JsonProperty("relatedProductOrder")
    private RelatedProductOrder relatedProductOrder;
    @JsonProperty("relatedParty")
    private List<RelatedParty> relatedParty;
    @JsonProperty("state")
    @NotNull
    private State state;
    @JsonProperty("receivedDate")
    private Instant receivedDate;
    @JsonProperty("requestedDeliveryDate")
    private Instant requestedDeliveryDate;
    @JsonProperty("orchestrationPlanNodes")
    private Set<OrchestrationPlanNode> orchestrationPlanNodes = new HashSet<>();
    @JsonProperty("orchestrationPlanSchedule")
    private OrchestrationPlanSchedule orchestrationPlanSchedule;
    @JsonProperty("archived")
    @Builder.Default
    private Boolean archived = false;

    @JsonProperty("lastModifiedDate")
    @LastModifiedDate
    private Instant lastModifiedDate;

    @JsonProperty("errorMessage")
    private List<OrchestrationPlanErrorMessage> errorMessage = new ArrayList<>();

    @JsonProperty("previousState")
    private State previousState;

    @JsonProperty("relatedContractName")
    private String relatedContractName;

    public void addErrorMessage(OrchestrationPlanErrorMessage errorMessage) {
        this.errorMessage.add(errorMessage);
    }

    @JsonProperty("lastModifiedDate")
    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    @JsonProperty("lastModifiedDate")
    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    /*
     * For orchestrationPlanSchedule a custom setter and getter must be used
     * because inside the orchestrationPlanSchedule we have a function getActualOrderCompletionDate
     * this function requires the parent plan reference, so we pass it in the getters and setters.
     *
     * This looks like a smell since we have some secret logic in setters and getters, but we're using it for now.
     *
     * In the future it might be better to put this in a service function and use mappers to remove this secret.
     */
    @JsonProperty("orchestrationPlanSchedule")
    public OrchestrationPlanSchedule getOrchestrationPlanSchedule() {
        if (Objects.isNull(this.orchestrationPlanSchedule)) {
            this.orchestrationPlanSchedule = OrchestrationPlanSchedule.builder().orchestrationPlan(this).build();
        }
        this.orchestrationPlanSchedule.setOrchestrationPlan(this);
        return this.orchestrationPlanSchedule;
    }

    @JsonProperty("orchestrationPlanSchedule")
    public void setOrchestrationPlanSchedule(OrchestrationPlanSchedule orchestrationPlanSchedule) {
        this.orchestrationPlanSchedule = orchestrationPlanSchedule;
        if (Objects.nonNull(this.orchestrationPlanSchedule)) {
            this.orchestrationPlanSchedule.setOrchestrationPlan(this);
        }
    }
    /*
     * For orchestrationPlanSchedule a custom setter and getter must be used
     * because inside the orchestrationPlanSchedule we have a function getActualOrderCompletionDate
     * this function requires the parent plan reference, so we pass it in the getters and setters.
     */


    @JsonProperty("archived")
    public Boolean getArchived() {
        return archived;
    }

    @JsonProperty("archived")
    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("relatedProductOrder")
    public RelatedProductOrder getRelatedProductOrder() {
        return relatedProductOrder;
    }

    @JsonProperty("relatedProductOrder")
    public void setRelatedProductOrder(RelatedProductOrder relatedProductOrder) {
        this.relatedProductOrder = relatedProductOrder;
    }

    @JsonProperty("relatedParty")
    public List<RelatedParty> getRelatedParty() {
        return relatedParty;
    }

    @JsonProperty("relatedParty")
    public void setRelatedParty(List<RelatedParty> relatedParty) {
        this.relatedParty = relatedParty;
    }

    @JsonProperty("state")
    public State getState() {
        return state;
    }

    @JsonProperty("state")
    public void setState(State state) {
        this.state = state;
    }

    public Instant getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Instant receivedDate) {
        this.receivedDate = receivedDate;
    }

    public Instant getRequestedDeliveryDate() {
        return requestedDeliveryDate;
    }

    public void setRequestedDeliveryDate(Instant requestedDeliveryDate) {
        this.requestedDeliveryDate = requestedDeliveryDate;
    }

    @JsonProperty("orchestrationPlanNodes")
    public Set<OrchestrationPlanNode> getOrchestrationPlanNodes() {
        return orchestrationPlanNodes;
    }

    @JsonProperty("orchestrationPlanNodes")
    public void setOrchestrationPlanNodes(Set<OrchestrationPlanNode> orchestrationPlanNodes) {
        this.orchestrationPlanNodes = orchestrationPlanNodes;
    }

    @Override
    public String toString() {
        return "OrchestrationPlan{" +
                "id='" + id + '\'' +
                ", relatedProductOrder=" + relatedProductOrder +
                ", relatedParty=" + relatedParty +
                ", state=" + state +
                ", recievedDate=" + receivedDate +
                ", requestedDeliveryDate=" + requestedDeliveryDate +
                ", orchestrationPlanNodes=" + orchestrationPlanNodes +
                '}';
    }

    //IPCEISCOOD-60: separation-embedded will have Set<Nodes> no need to have the plan
    public Optional<OrchestrationPlanNode> getOrchestrationPlanNodeByOrderItemId(String productOrderItemId) {
        return orchestrationPlanNodes.stream()
                .filter(node -> node.getRelatedProductOrderItem().stream()
                        .anyMatch(orderItem -> orderItem.getId().equals(productOrderItemId)))
                .findFirst();
    }

    //IPCEISCOOD-60: separation-embedded will have Set<Nodes> no need to have the plan
    public Optional<OrchestrationPlanNode> getOrchestrationPlanNodeById(String id) {
        return this.getOrchestrationPlanNodes().stream().filter(orchestrationPlanNode -> orchestrationPlanNode.getId().equals(id)).findFirst();
    }

    public Optional<String> getOrchestrationPlanNodeIDBy(String productOrderItemId) {
        return this.getOrchestrationPlanNodeByOrderItemId(productOrderItemId).map(OrchestrationPlanNode::getId);
    }

    //IPCEISCOOD-60: separation - to be changed to fetch the leafs from the db
    @JsonIgnore
    public Set<OrchestrationPlanNode> getLeafs() {
        return this.getOrchestrationPlanNodes().stream().filter(OrchestrationPlanNode::isLeaf).collect(Collectors.toSet());
    }

    @JsonIgnore
    public static Set<String> fieldsName() {
        return (Set<String>) Arrays.stream(OrchestrationPlan.class.getDeclaredFields()).filter(field -> field.getType().isMemberClass()).map(Field::getName).collect(Collectors.toSet());
    }

    /**
     * The name of the contract level offer that is delivered by this plan
     *
     * @return relatedContractName
     */

    @Schema(name = "relatedContractName", description = "The name of the contract level offer that is delivered by this plan", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("relatedContractName")
    public String getRelatedContractName() {
        return relatedContractName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrchestrationPlan)) {
            return false;
        }

        OrchestrationPlan that = (OrchestrationPlan) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) {
            return false;
        }
        if (getRelatedProductOrder() != null ? !getRelatedProductOrder().equals(that.getRelatedProductOrder()) : that.getRelatedProductOrder() != null) {
            return false;
        }
        if (getRelatedParty() != null ? !getRelatedParty().equals(that.getRelatedParty()) : that.getRelatedParty() != null) {
            return false;
        }
        if (getState() != that.getState()) {
            return false;
        }
        if (getReceivedDate() != null ? !getReceivedDate().equals(that.getReceivedDate()) : that.getReceivedDate() != null) {
            return false;
        }
        for (OrchestrationPlanNode node : getOrchestrationPlanNodes()) {
            if (!that.getOrchestrationPlanNodes().contains(node)) {
                return false;
            }
        }
        return getRequestedDeliveryDate() != null ? getRequestedDeliveryDate().equals(that.getRequestedDeliveryDate()) : that.getRequestedDeliveryDate() == null;
    }

    @JsonIgnore
    public Boolean hasPredecessorInTerminalStates(OrchestrationPlanNode node) {
        return hasPredecessorInStates(node, List.of(HELD, FAILED, ABORTED));
    }

    @JsonIgnore
    public Boolean hasPredecessorInStates(OrchestrationPlanNode node, List<OrchestrationPlanNodeState> states) {
        Map<String, OrchestrationPlanNode> nodeIndex = this.getOrchestrationPlanNodes().stream()
                .collect(Collectors.toMap(
                        OrchestrationPlanNode::getId,
                        Function.identity()
                ));

        return hasPredecessorInStatesRecursively(node, states, nodeIndex);
    }

    @JsonIgnore
    private Boolean hasPredecessorInStatesRecursively(OrchestrationPlanNode node, List<OrchestrationPlanNodeState> states, Map<String, OrchestrationPlanNode> nodeIndex) {
        for (RelatedOrchestrationPlanNode related : node.getRelatedOrchestrationPlanNode()) {
            OrchestrationPlanNode parent = nodeIndex.get(related.getRelatedNodeId());
            if (parent == null) {
                continue;
            }

            if (states.contains(parent.getState())) {
                return true;
            }

            if (hasPredecessorInStatesRecursively(parent, states, nodeIndex)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getRelatedProductOrder() != null ? getRelatedProductOrder().hashCode() : 0);
        result = 31 * result + (getRelatedParty() != null ? getRelatedParty().hashCode() : 0);
        result = 31 * result + (getState() != null ? getState().hashCode() : 0);
        result = 31 * result + (getReceivedDate() != null ? getReceivedDate().hashCode() : 0);
        result = 31 * result + (getRequestedDeliveryDate() != null ? getRequestedDeliveryDate().hashCode() : 0);
        result = 31 * result + (getOrchestrationPlanNodes() != null ? getOrchestrationPlanNodes().hashCode() : 0);
        return result;
    }
}
