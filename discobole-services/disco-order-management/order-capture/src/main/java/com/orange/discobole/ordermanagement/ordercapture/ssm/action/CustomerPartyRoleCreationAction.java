// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.action;

import com.orange.discobole.ordermanagement.commons.dto.role.EngagedParty;
import com.orange.discobole.ordermanagement.commons.dto.role.PartyRole;
import com.orange.discobole.ordermanagement.commons.dto.role.PartyRoleSpecification;
import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.domain.SettingsEntity;
import com.orange.discobole.ordermanagement.ordercapture.service.PartyRoleManagementService;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductOrderService;
import com.orange.discobole.ordermanagement.ordercapture.service.SettingsService;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.PartyRef;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderStateType;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.RelatedPartyRefOrPartyRoleRef;
import com.orange.discobole.processflow.dto.generated.RelatedParty;
import com.orange.discobole.processflow.ssm.action.StateMachineStateAction;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Component("customerPartyRoleCreationAction")
@Slf4j
public class CustomerPartyRoleCreationAction implements StateMachineStateAction<String, String> {
    private final PartyRoleManagementService partyRoleManagementService;
    private final ProductOrderService productOrderService;
    private final SettingsService settingsService;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public CustomerPartyRoleCreationAction(PartyRoleManagementService partyRoleManagementService, ProductOrderService productOrderService, SettingsService settingsService) {
        this.partyRoleManagementService = partyRoleManagementService;
        this.productOrderService = productOrderService;
        this.settingsService = settingsService;
    }

    @Override
    public Mono<Void> apply(StateContext<String, String> context) {
        ProductOrder productOrder = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.CREATED_PRODUCT_ORDER, ProductOrder.class);
        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            return Mono.empty();
        }
        try {
            log.info("Inside customer party role creation action");
            assert productOrder != null;
            SettingsEntity settings = settingsService.getSettings();
            RelatedParty taskRelatedParty;

            PartyRole partyRole = createPartyRole(productOrder);
            RelatedPartyRefOrPartyRoleRef relatedParty = constructRelatedParty(partyRole, productOrder);

            if (settings.isCheckPartyManagementEnabled()) {
                PartyRole createdPartyRole = partyRoleManagementService.createPartyRole(partyRole);
                relatedParty = constructRelatedParty(createdPartyRole, productOrder);
            }
            taskRelatedParty = buildTaskRelatedParty(relatedParty);
            productOrder.setRelatedParty(Collections.singletonList(relatedParty));
            productOrderService.updateProductOrderRelatedParties(productOrder);

            setContextVariables(context, TRUE, productOrder, taskRelatedParty);
            return Mono.empty();
        } catch (Exception e) {
            log.error("Unable to create customer party role [{}]:", e.getMessage(), e);
            setContextVariables(context, FALSE, productOrder, null);
            return Mono.empty();
        }
    }

    private PartyRole createPartyRole(ProductOrder productOrder) {
        return PartyRole.builder()
                .engagedParty(EngagedParty.builder()
                        .id(((PartyRef) productOrder.getRelatedParty().get(0).getPartyOrPartyRole()).getId())
                        .build())
                .name(((PartyRef) productOrder.getRelatedParty().get(0).getPartyOrPartyRole()).getName())
                .partyRoleSpecification(PartyRoleSpecification.builder()
                        .name(OrderCaptureConstants.CUSTOMER)
                        .build())
                .build();
    }

    private RelatedPartyRefOrPartyRoleRef constructRelatedParty(PartyRole partyRole, ProductOrder order) {
        PartyRef engagedPartyRef = PartyRef.builder()
                .id(partyRole.getEngagedParty().getId())
                .name(partyRole.getName())
                .atReferredType(extractReferredTypeFromOrder(order))
                .atType("PartyRef")
                .build();

        return RelatedPartyRefOrPartyRoleRef.builder()
                .role(partyRole.getPartyRoleSpecification().getName())
                .partyOrPartyRole(engagedPartyRef)
                .atType("RelatedPartyRefOrPartyRoleRef")
                .build();
    }

    private String extractReferredTypeFromOrder(ProductOrder order) {
        return ((PartyRef) order.getRelatedParty()
                .get(0)
                .getPartyOrPartyRole())
                .getAtReferredType();
    }

    private RelatedParty buildTaskRelatedParty(RelatedPartyRefOrPartyRoleRef relatedParty) {
        return new RelatedParty()
                .id(((PartyRef) relatedParty.getPartyOrPartyRole()).getId())
                .name(((PartyRef) relatedParty.getPartyOrPartyRole()).getId())
                .role(relatedParty.getRole())
                .referredType(((PartyRef) relatedParty.getPartyOrPartyRole()).getAtReferredType());
    }

    private void setContextVariables(StateContext<String, String> context, boolean isCustomerPartyRoleCreated, ProductOrder productOrder, RelatedParty taskRelatedParty) {
        context.getExtendedState().getVariables().put(OrderCaptureConstants.IS_CUSTOMER_PARTY_ROLE_CREATED, isCustomerPartyRoleCreated);
        if (isCustomerPartyRoleCreated) {
            context.getExtendedState().getVariables().put(OrderCaptureConstants.CREATED_PRODUCT_ORDER, productOrder);
            context.getExtendedState().getVariables().put(OrderCaptureConstants.TASK_RELATED_PARTY, Collections.singletonList(taskRelatedParty));
        } else {
            StateMachineUtil.setDescriptionContext(context, DescriptionConstants.PARTY_MANAGEMENT_SERVICE_UNREACHABLE);
            productOrderService.updateProductOrderInventoryState(productOrder, ProductOrderStateType.HELD);
            context.getExtendedState().getVariables().put(OrderCaptureConstants.CREATED_PRODUCT_ORDER, productOrder);
        }
    }
}