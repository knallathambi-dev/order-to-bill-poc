// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.mapper;

import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.ServiceOrderRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedParty;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static com.orange.discobole.ordermanagement.orderinventory.dto.v1.RelationshipType.MIGRATEFROM;

@Mapper(uses = {EnumMapper.class})
public interface OrchestrationPlanMapper {
    Logger LOGGER = LoggerFactory.getLogger(OrchestrationPlanMapper.class);

    @Mapping(source = "requestedCompletionDate", target = "requestedDeliveryDate")
    @Mapping(target = "receivedDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "state", expression = "java(com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State.INITIALIZED)")
    @Mapping(source = "id", target = "relatedProductOrder.id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "relatedContractName", source = "productOrderItem", qualifiedByName = "mapContractName")
    OrchestrationPlan toOrchestrationPlan(ProductOrder productOrder);

    List<RelatedParty> toRelatedPartyList(List<RelatedPartyRefOrPartyRoleRef> relatedPartyRefOrPartyRoleRefs);

    default RelatedParty toRelatedParty(RelatedPartyRefOrPartyRoleRef relatedPartyRefOrPartyRoleRef) {
        if (relatedPartyRefOrPartyRoleRef.getPartyOrPartyRole() instanceof PartyRef partyRef) {
            return new RelatedParty(partyRef.getId(), partyRef.getHref(), relatedPartyRefOrPartyRoleRef.getRole(), partyRef.getName());
        } else if (relatedPartyRefOrPartyRoleRef.getPartyOrPartyRole() instanceof PartyRoleRef partyRoleRef) {
            return new RelatedParty(partyRoleRef.getId(), partyRoleRef.getHref(), relatedPartyRefOrPartyRoleRef.getRole(), partyRoleRef.getName());
        }
        return null;
    }

    @Mapping(target = "state", expression = "java(com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState.INITIALIZED)")
    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "relatedProductOrderItem", ignore = true)
    OrchestrationPlanNode toInitializedOrchestrationPlanNode(ProductOrderItem productOrderItem);

    com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan toDto(OrchestrationPlan orchestrationPlan);

    @Mapping(source = "relatedSupplyChainOrder", target = "relatedSupplyChainOrderItem")
    com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlanNode toDto(OrchestrationPlanNode orchestrationPlan);

    @Mapping(target = "serviceOrderManagementRef", source = "somRef")
    ServiceOrderRef toDto(com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedServiceOrder relatedServiceOrder);

    @Mapping(source = "type", target = "atType")
    com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.ProductRef toDto(RelatedProduct relatedProduct);

    List<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan> toDtoList(List<OrchestrationPlan> orchestrationPlan);

    default OffsetDateTime map(Instant value) {
        return value != null ? OffsetDateTime.ofInstant(value, ZoneOffset.UTC) : null;
    }

    @Named("mapContractName")
    default String mapContractName(List<ProductOrderItem> productOrderItems) {
        // For migration use cases, there should be two "Contract" product offerings:
        // one with order item relationship MIGRATE-FROM (the new contract) and one with order item relationship MIGRATE-TO (the old contract).
        // In this scenario, we need to pick the name of the contract marked with MIGRATE-FROM.
        // For non-migration cases, simply return the first "Contract" product offering name.
        Predicate<ProductOrderItem> isContract = productOrderItem
                -> Objects.nonNull(productOrderItem.getProductOffering()) && "Contract".equals(productOrderItem.getProductOffering().getAtType());

        Predicate<ProductOrderItem> isContractNotMigration = productOrderItem
                -> !ItemActionType.MIGRATE.equals(productOrderItem.getAction());

        Predicate<ProductOrderItem> isContractMigrationFrom = productOrderItem
                -> ItemActionType.MIGRATE.equals(productOrderItem.getAction()) &&
                    productOrderItem.getProductOrderItemRelationship() != null &&
                    productOrderItem.getProductOrderItemRelationship().stream().anyMatch(orderItemRelationship -> MIGRATEFROM.equals(orderItemRelationship.getRelationshipType()));

        return productOrderItems.stream()
                .filter(isContract.and(isContractNotMigration.or(isContractMigrationFrom)))
                .map(productOrderItem -> productOrderItem.getProductOffering().getName())
                .findFirst()
                .orElse(null);
    }
}
