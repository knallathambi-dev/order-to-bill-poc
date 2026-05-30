// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedOrchestrationPlanNodeRelationshipType;
import lombok.Builder;
import lombok.NoArgsConstructor;

@JsonPropertyOrder({
        "relatedNodeId",
        "relationshipType"
})
@NoArgsConstructor
@Builder
public class RelatedOrchestrationPlanNode {

    @JsonProperty("relatedNodeId")
    private String relatedNodeId;
    @JsonProperty("relationshipType")
    private RelatedOrchestrationPlanNodeRelationshipType relationshipType;

    public RelatedOrchestrationPlanNode(String relatedNodeId, RelatedOrchestrationPlanNodeRelationshipType relationshipType) {
        this.relatedNodeId = relatedNodeId;
        this.relationshipType = relationshipType;
    }

    @JsonProperty("relatedNodeId")
    public String getRelatedNodeId() {
        return relatedNodeId;
    }

    @JsonProperty("relatedNodeId")
    public void setRelatedNodeId(String relatedNodeId) {
        this.relatedNodeId = relatedNodeId;
    }

    @JsonProperty("relationshipType")
    public RelatedOrchestrationPlanNodeRelationshipType getRelationshipType() {
        return relationshipType;
    }

    @JsonProperty("relationshipType")
    public void setRelationshipType(RelatedOrchestrationPlanNodeRelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RelatedOrchestrationPlanNode)) {
            return false;
        }

        RelatedOrchestrationPlanNode that = (RelatedOrchestrationPlanNode) o;

        if (getRelatedNodeId() != null ? !getRelatedNodeId().equals(that.getRelatedNodeId()) : that.getRelatedNodeId() != null) {
            return false;
        }
        return getRelationshipType() == that.getRelationshipType();
    }

    @Override
    public int hashCode() {
        int result = getRelatedNodeId() != null ? getRelatedNodeId().hashCode() : 0;
        result = 31 * result + (getRelationshipType() != null ? getRelationshipType().hashCode() : 0);
        return result;
    }
}