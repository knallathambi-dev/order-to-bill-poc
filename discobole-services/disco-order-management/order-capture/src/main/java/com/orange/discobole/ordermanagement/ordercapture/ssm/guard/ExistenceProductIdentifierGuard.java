// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.guard;

import com.orange.discobole.ordermanagement.ordercapture.constant.GuardNameConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.processflow.dto.generated.ChannelRef;
import com.orange.discobole.processflow.dto.generated.RelatedEntity;
import com.orange.discobole.processflow.dto.generated.RelatedParty;
import com.orange.discobole.processflow.ssm.guard.StateMachineGuard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component("isExistProductIdentifierGuard")
@Slf4j
public class ExistenceProductIdentifierGuard implements StateMachineGuard<String, String> {
    @Override
    public Mono<Boolean> apply(StateContext<String, String> context) {
        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            return StateMachineUtil.getGuardResult(context, GuardNameConstants.EXISTENCE_PRODUCT_IDENTIFIER_GUARD);
        } else {
            return evaluateProductIdentifier(context);
        }
    }

    private Mono<Boolean> evaluateProductIdentifier(StateContext<String, String> context) {
        log.info("Inside guard to check if the product identifier was provided");
        List<RelatedEntity> relatedEntities = getProcessRelatedEntity(context);
        if (!isProductExist(relatedEntities)) {
            StateMachineUtil.setGuardContext(context, false, GuardNameConstants.EXISTENCE_PRODUCT_IDENTIFIER_GUARD);
            return Mono.just(false);
        }
        String productId = findProductIdentifier(relatedEntities, OrderCaptureConstants.PRODUCT_TYPE);
        String productOfferingId = findProductIdentifier(relatedEntities, OrderCaptureConstants.PRODUCT_OFFERING_TYPE);

        RelatedParty relatedParty = getFirstRelatedParty(context);
        setContextVariables(context, productId, productOfferingId, relatedParty);

        StateMachineUtil.setGuardContext(context, true, GuardNameConstants.EXISTENCE_PRODUCT_IDENTIFIER_GUARD);
        return Mono.just(true);
    }

    private String findProductIdentifier(List<RelatedEntity> relatedEntities, String entityType) {
        return relatedEntities.stream()
                .filter(entity -> !isBlank(entity.getId()) && entity.getReferredType().equals(entityType))
                .findFirst()
                .map(RelatedEntity::getId)
                .orElse(null);
    }

    private List<RelatedEntity> getProcessRelatedEntity(StateContext<String, String> context) {
        return StateMachineUtil.getListValue(context.getExtendedState().getVariables(), OrderCaptureConstants.PROCESS_RELATED_ENTITY, RelatedEntity.class);
    }

    private boolean isProductExist(List<RelatedEntity> relatedEntities) {
        return relatedEntities.stream()
                .anyMatch(entity -> !isBlank(entity.getId()) && entity.getReferredType().equals(OrderCaptureConstants.PRODUCT_TYPE));
    }

    private RelatedParty getFirstRelatedParty(StateContext<String, String> context) {
        List<RelatedParty> relatedPartyList = getProcessRelatedParty(context);
        return CollectionUtils.isEmpty(relatedPartyList) ? null : relatedPartyList.get(0);
    }

    private List<RelatedParty> getProcessRelatedParty(StateContext<String, String> context) {
        return StateMachineUtil.getListValue(context.getExtendedState().getVariables(), OrderCaptureConstants.PROCESS_RELATED_PARTY, RelatedParty.class);
    }

    private void setContextVariables(StateContext<String, String> context, String productId, String productOfferingId, RelatedParty relatedParty) {
        context.getExtendedState().getVariables().put(OrderCaptureConstants.CONTRACT_PRODUCT_ID, productId);

        if (Objects.nonNull(productOfferingId)) {
            context.getExtendedState().getVariables().put(OrderCaptureConstants.OPTIONAL_PRODUCT_OFFERING_ID, productOfferingId);
        }

        if (Objects.nonNull(relatedParty) && !isBlank(relatedParty.getId()) && !isBlank(relatedParty.getRole())) {
            context.getExtendedState().getVariables().put(OrderCaptureConstants.RELATED_PARTY, relatedParty);
        }

        List<ChannelRef> channelRefList = StateMachineUtil.getListValue(context.getExtendedState().getVariables(), OrderCaptureConstants.PROCESS_CHANNEL, ChannelRef.class);
        String firstChannelId = getFirstChannelId(channelRefList);
        if (!isBlank(firstChannelId)) {
            context.getExtendedState().getVariables().put(OrderCaptureConstants.CHANNEL_ID, firstChannelId);
        }
    }

    private String getFirstChannelId(List<ChannelRef> channelRefList) {
        return CollectionUtils.isEmpty(channelRefList) ? null : channelRefList.get(0).getId();
    }
}