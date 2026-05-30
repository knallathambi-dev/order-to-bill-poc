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

@Component("isExistProductOfferingGuard")
@Slf4j
public class ProductOfferingGuard implements StateMachineGuard<String, String> {
    @Override
    public Mono<Boolean> apply(StateContext<String, String> context) {
        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            return StateMachineUtil.getGuardResult(context, GuardNameConstants.EXISTENCE_PRODUCT_OFFERING_GUARD);
        } else {
            return evaluateProductOffering(context);
        }
    }

    private Mono<Boolean> evaluateProductOffering(StateContext<String, String> context) {
        log.info("Inside guard to check if product offering was provided");
        RelatedEntity relatedEntity = getProcessRelatedEntity(context);
        if (!isRelatedEntityProductOffering(relatedEntity)) {
            StateMachineUtil.setGuardContext(context, false, GuardNameConstants.EXISTENCE_PRODUCT_OFFERING_GUARD);
            return Mono.just(false);
        }

        String productOfferingId = relatedEntity.getId();
        RelatedParty relatedParty = getProcessRelatedParty(context);

        setContextVariables(context, productOfferingId, relatedParty);
        StateMachineUtil.setGuardContext(context, true, GuardNameConstants.EXISTENCE_PRODUCT_OFFERING_GUARD);
        return Mono.just(true);
    }

    private RelatedParty getProcessRelatedParty(StateContext<String, String> context) {
        List<RelatedParty> relatedPartyList = StateMachineUtil.getListValue(context.getExtendedState().getVariables(), OrderCaptureConstants.PROCESS_RELATED_PARTY, RelatedParty.class);
        return CollectionUtils.isEmpty(relatedPartyList) ? null : relatedPartyList.get(0);
    }

    private boolean isRelatedEntityProductOffering(RelatedEntity relatedEntity) {
        return Objects.nonNull(relatedEntity)
                && !isBlank(relatedEntity.getId())
                && !isBlank(relatedEntity.getReferredType())
                && relatedEntity.getReferredType().equals(OrderCaptureConstants.PRODUCT_OFFERING_TYPE);
    }

    private RelatedEntity getProcessRelatedEntity(StateContext<String, String> context) {
        List<RelatedEntity> relatedEntityList = StateMachineUtil.getListValue(context.getExtendedState().getVariables(), OrderCaptureConstants.PROCESS_RELATED_ENTITY, RelatedEntity.class);
        return CollectionUtils.isEmpty(relatedEntityList) ? null : relatedEntityList.get(0);
    }

    private void setContextVariables(StateContext<String, String> context, String productOfferingId, RelatedParty relatedParty) {
        context.getExtendedState().getVariables().put(OrderCaptureConstants.PRODUCT_OFFERING_ID, productOfferingId);

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