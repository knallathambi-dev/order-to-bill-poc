// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.guard;


import com.orange.discobole.ordermanagement.commons.dto.party.management.Party;
import com.orange.discobole.ordermanagement.ordercapture.constant.GuardNameConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.service.*;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import com.orange.discobole.processflow.dto.generated.RelatedParty;
import com.orange.discobole.processflow.ssm.guard.StateMachineGuard;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.*;

import static com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants.*;
import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.LOGICAL_RESOURCE;

@Component("isFinanciallyEligibleGuard")
@Slf4j
public class FinanciallyEligibleGuard implements StateMachineGuard<String, String> {
    private static final Integer RATING_SCORE = 750;
    private final PartyManagementService partyManagementService;
    private final SettingsService settingsService;
    private final ProductOrderService productOrderService;
    private final ProductInventoryService productInventoryService;
    private final ResourceInventoryService resourceInventoryService;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public FinanciallyEligibleGuard(PartyManagementService partyManagementService, SettingsService settingsService, ProductOrderService productOrderService, ProductInventoryService productInventoryService, ResourceInventoryService resourceInventoryService) {
        this.partyManagementService = partyManagementService;
        this.settingsService = settingsService;
        this.productOrderService = productOrderService;
        this.productInventoryService = productInventoryService;
        this.resourceInventoryService = resourceInventoryService;
    }

    @Override
    public Mono<Boolean> apply(StateContext<String, String> context) {


        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            return StateMachineUtil.getGuardResult(context, GuardNameConstants.FINANCIALLY_ELIGIBLE_GUARD);
        } else {
            try {
                log.info("Inside guard to verify if party is eligible");
                if (settingsService.getSettings().isCheckFinancialEligibilityEnabled()) {
                    ProductOrder productOrder = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.CREATED_PRODUCT_ORDER, ProductOrder.class);
                    boolean hasInstallment = isHavingInstallment(productOrder);
                    if (!hasInstallment) {
                        StateMachineUtil.setGuardContext(context, true, GuardNameConstants.FINANCIALLY_ELIGIBLE_GUARD);
                        return Mono.just(true);
                    }
                    RelatedParty relatedParty = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.RELATED_PARTY, RelatedParty.class);
                    Party party = partyManagementService.getPartyById(relatedParty.getId());
                    boolean isEligible = checkIfEligible(party);
                    if (isEligible) {
                        StateMachineUtil.setGuardContext(context, true, GuardNameConstants.FINANCIALLY_ELIGIBLE_GUARD);
                        return Mono.just(true);
                    } else {
                        rejectProductsAndOrder(productOrder);
                        StateMachineUtil.setDescriptionContext(context, CANNOT_ACQUIRE_OFFER_WITH_INSTALLMENT);
                        StateMachineUtil.setGuardContext(context, false, GuardNameConstants.FINANCIALLY_ELIGIBLE_GUARD);
                        return Mono.just(false);
                    }
                }
                StateMachineUtil.setGuardContext(context, true, GuardNameConstants.FINANCIALLY_ELIGIBLE_GUARD);
                return Mono.just(true);
            } catch (Exception e) {
                log.error("Unable to verify if the party is eligible  [{}]:", e.getMessage(), e);
                StateMachineUtil.setDescriptionContext(context, INTERNAL_SERVER_ERROR);
                StateMachineUtil.setNextTaskToNull(context);
                StateMachineUtil.setGuardContext(context, false, GuardNameConstants.FINANCIALLY_ELIGIBLE_GUARD);
                return Mono.just(false);
            }
        }
    }

    private boolean checkIfEligible(Party party) {
        return Optional.ofNullable(party.getCreditRating())
                .orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull)
                .anyMatch(creditRating -> creditRating.getRatingScore() != null && creditRating.getRatingScore() >= RATING_SCORE);
    }


    private boolean isHavingInstallment(ProductOrder productOrder) {
        assert productOrder != null;
        return productOrder.getProductOrderItem().stream().anyMatch(productOrderItem ->
                !CollectionUtils.isEmpty(productOrderItem.getItemPrice()) &&
                        productOrderItem.getItemPrice()
                                .stream()
                                .anyMatch(orderPrice -> orderPrice.getProductOfferingPrice() instanceof InstallmentCharge));
    }

    private void rejectProductsAndOrder(ProductOrder productOrder) {
        assert productOrder != null;
        List<String> productIds = extractProductIds(productOrder);
        productInventoryService.abortProducts(productIds);
        List<String> reservedResourcesIdList = getProductOrderReservedResourcesIds(productOrder);
        if (!reservedResourcesIdList.isEmpty()) {
            resourceInventoryService.rollBackReservedResource(reservedResourcesIdList);
        }
        productOrderService.updateProductOrderInventoryState(productOrder, ProductOrderStateType.REJECTED);
    }

    private List<String> extractProductIds(ProductOrder productOrder) {
        return productOrder.getProductOrderItem().stream()
                .filter(Objects::nonNull)
                .filter(productOrderItem -> ItemActionType.ADD.equals(productOrderItem.getAction())
                        || isHavingMigrateFromRelationship(productOrderItem))
                .map(ProductOrderItem::getProduct)
                .filter(Objects::nonNull)
                .filter(Product.class::isInstance)
                .map(Product.class::cast)
                .map(Product::getId)
                .filter(Objects::nonNull)
                .toList();
    }

    private boolean isHavingMigrateFromRelationship(ProductOrderItem productOrderItem) {
        return Optional.ofNullable(productOrderItem.getProductOrderItemRelationship())
                .orElse(Collections.emptyList())
                .stream()
                .anyMatch(orderItemRelationship -> RelationshipType.MIGRATEFROM.equals(orderItemRelationship.getRelationshipType()));
    }

    private List<String> getProductOrderReservedResourcesIds(ProductOrder productOrder) {
        return productOrder.getProductOrderItem().stream()
                .map(ProductOrderItem::getProduct)
                .filter(Objects::nonNull)
                .filter(Product.class::isInstance)
                .map(Product.class::cast)
                .map(Product::getRealizingResource)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(resourceRef -> LOGICAL_RESOURCE.equals(resourceRef.getAtType()))
                .map(ResourceRef::getId)
                .toList();
    }

}