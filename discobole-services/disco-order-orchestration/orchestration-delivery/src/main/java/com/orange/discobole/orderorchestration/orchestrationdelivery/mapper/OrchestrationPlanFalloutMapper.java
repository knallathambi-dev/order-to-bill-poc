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
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring")
public interface OrchestrationPlanFalloutMapper {
    String ORCHESTRATION_PLAN_HREF = "/orchestrationPlan/";

    @Mapping(target = "relatedParty", source = "relatedParty")
    @Mapping(target = "relatedEntity", source = ".", qualifiedByName = "mapRelatedEntities")
    @Mapping(target = "errorMessage", ignore = true)
    @Mapping(target = "href", source = "orchestrationPlan.id", qualifiedByName = "mapHref")
    @Mapping(target = "state", ignore = true)
    FalloutIncident from(OrchestrationPlan orchestrationPlan);

    @Mapping(target = "relatedParty", source = "orchestrationPlan.relatedParty")
    @Mapping(target = "relatedEntity", source = ".", qualifiedByName = "mapRelatedEntitiesWithNode")
    @Mapping(target = "errorMessage", ignore = true)
    @Mapping(target = "href", source = "orchestrationPlan.id", qualifiedByName = "mapHref")
    @Mapping(target = "state", ignore = true)
    FalloutIncident from(@Context OrchestrationPlanNode orchestrationPlanNode, OrchestrationPlan orchestrationPlan);

    @Named("mapRelatedEntitiesWithNode")
    default List<RelatedEntity> mapRelatedEntitiesWithNode(@Context OrchestrationPlanNode orchestrationPlanNode, OrchestrationPlan orchestrationPlan) {
        List<RelatedEntity> entities = new ArrayList<>();
        entities.add(asOrchestrationPlanNodeEntity(orchestrationPlanNode, RelatedEntityRole.INITIATOR));
        entities.add(asOrchestrationPlanEntity(orchestrationPlan, RelatedEntityRole.RELATED_ORCHESTRATION_PLAN));
        entities.add(asRelatedProductOrderEntity(orchestrationPlan));

        return entities;
    }

    @Named("mapRelatedEntities")
    default List<RelatedEntity> mapRelatedEntities(OrchestrationPlan orchestrationPlan) {
        List<RelatedEntity> entities = new ArrayList<>();
        entities.add(asOrchestrationPlanEntity(orchestrationPlan, RelatedEntityRole.INITIATOR));
        entities.add(asOrchestrationPlanEntity(orchestrationPlan, RelatedEntityRole.RELATED_ORCHESTRATION_PLAN));
        entities.add(asRelatedProductOrderEntity(orchestrationPlan));

        return entities;
    }

    default RelatedEntity asOrchestrationPlanEntity(OrchestrationPlan orchestrationPlan, RelatedEntityRole relatedEntityRole) {
        return Objects.nonNull(orchestrationPlan) ? RelatedEntity.builder()
                .id(orchestrationPlan.getId())
                .atReferredType(OrchestrationPlan.class.getSimpleName())
                .href(mapHref(orchestrationPlan.getId()))
                .role(relatedEntityRole.getValue())
                .build() : null;
    }

    default RelatedEntity asOrchestrationPlanNodeEntity(OrchestrationPlanNode orchestrationPlanNode, RelatedEntityRole relatedEntityRole) {
        return Objects.nonNull(orchestrationPlanNode) ? RelatedEntity.builder()
                .id(orchestrationPlanNode.getId())
                .atReferredType(OrchestrationPlanNode.class.getSimpleName())
                .role(relatedEntityRole.getValue())
                .build() : null;
    }

    default RelatedEntity asRelatedProductOrderEntity(OrchestrationPlan orchestrationPlan) {
        return Objects.nonNull(orchestrationPlan) && Objects.nonNull(orchestrationPlan.getRelatedProductOrder()) ? RelatedEntity.builder()
                .id(orchestrationPlan.getRelatedProductOrder().getId())
                .atReferredType(ProductOrder.class.getSimpleName())
                .role(RelatedEntityRole.RELATED_PRODUCT_ORDER.getValue())
                .build() : null;
    }

    default RelatedParty fromRelatedParty(List<com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedParty> relatedParty) {
        RelatedParty.RelatedPartyBuilder relatedPartyBuilder = RelatedParty.builder();
        if (Objects.isNull(relatedParty) || relatedParty.isEmpty()) {
            return relatedPartyBuilder.build();
        }
        relatedPartyBuilder.id(relatedParty.get(0).getId());
        relatedPartyBuilder.name(relatedParty.get(0).getName());
        relatedPartyBuilder.role(relatedParty.get(0).getRole());
        return relatedPartyBuilder.build();
    }

    @Named("mapHref")
    default String mapHref(String orchestrationPlanId) {
        return ORCHESTRATION_PLAN_HREF + orchestrationPlanId;
    }

}
