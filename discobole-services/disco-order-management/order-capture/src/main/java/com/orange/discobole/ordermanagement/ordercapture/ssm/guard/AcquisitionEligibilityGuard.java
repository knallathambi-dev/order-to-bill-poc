// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.guard;

import com.orange.discobole.ordermanagement.commons.dto.product.offering.qualification.ProductOfferingQualification;
import com.orange.discobole.ordermanagement.commons.dto.product.offering.qualification.ProductOfferingQualificationItem;
import com.orange.discobole.ordermanagement.commons.dto.product.offering.qualification.ProductOfferingRef;
import com.orange.discobole.ordermanagement.commons.dto.product.offering.qualification.RelatedParty;
import com.orange.discobole.ordermanagement.ordercapture.constant.GuardNameConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductOfferingQualificationService;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductOfferingService;
import com.orange.discobole.ordermanagement.ordercapture.service.SettingsService;
import com.orange.discobole.ordermanagement.ordercapture.service.dto.ResponseResult;
import com.orange.discobole.ordermanagement.ordercapture.service.util.impl.AcquisitionProductOfferingValidator;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.processflow.ssm.guard.StateMachineGuard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants.*;

@Component("isEligibleAcquisitionGuard")
@Slf4j
public class AcquisitionEligibilityGuard implements StateMachineGuard<String, String> {
    private final ProductOfferingService productOfferingService;
    private final ProductOfferingQualificationService qualificationService;
    private final SettingsService settingsService;

    public AcquisitionEligibilityGuard(ProductOfferingService productOfferingService, ProductOfferingQualificationService qualificationService, SettingsService settingsService) {
        this.productOfferingService = productOfferingService;
        this.qualificationService = qualificationService;
        this.settingsService = settingsService;
    }

    @Override
    public Mono<Boolean> apply(StateContext<String, String> context) {
        boolean isProductOfferingExist;
        boolean isProductOfferingQualified;

        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            return StateMachineUtil.getGuardResult(context, GuardNameConstants.ACQUISITION_ELIGIBILITY_GUARD);
        } else {
            try {
                log.info("Inside guard to validate and check the commercial eligibility on acquisition action");
                String productOfferingId = StateMachineUtil.getStringValue(context, OrderCaptureConstants.PRODUCT_OFFERING_ID);
                ResponseResult productOfferingExist = productOfferingService.isProductOfferingExist(productOfferingId, new AcquisitionProductOfferingValidator());
                isProductOfferingExist = productOfferingExist.getResult();
                if (Boolean.FALSE.equals(isProductOfferingExist)) {
                    StateMachineUtil.setNextTaskToNullInCaseOfUnreachableService(context, productOfferingExist.getDescription(), CATALOG_SERVICE_UNREACHABLE);
                    StateMachineUtil.setDescriptionContext(context, productOfferingExist.getDescription());
                    StateMachineUtil.setGuardContext(context, false, GuardNameConstants.ACQUISITION_ELIGIBILITY_GUARD);
                    return Mono.just(false);
                }
                if (settingsService.getSettings().isCheckCommercialEligibilityEnabled() && CONTRACT_SELECTED_OFFER.equals(productOfferingExist.getDescription())) {
                    com.orange.discobole.processflow.dto.generated.RelatedParty relatedParty = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.RELATED_PARTY, com.orange.discobole.processflow.dto.generated.RelatedParty.class);
                    assert relatedParty != null;
                    ProductOfferingQualification productOfferingQualification = createProductOfferingQualification(productOfferingId, relatedParty);
                    ResponseResult productOfferingQualified = qualificationService.isProductOfferingQualified(productOfferingQualification);
                    isProductOfferingQualified = productOfferingQualified.getResult();
                    if (!isProductOfferingQualified) {
                        StateMachineUtil.setNextTaskToNullInCaseOfUnreachableService(context, productOfferingQualified.getDescription(), QUALIFICATION_SERVICE_UNREACHABLE);
                        StateMachineUtil.setDescriptionContext(context, productOfferingQualified.getDescription());
                        StateMachineUtil.setGuardContext(context, false, GuardNameConstants.ACQUISITION_ELIGIBILITY_GUARD);
                        return Mono.just(false);
                    }
                }

                StateMachineUtil.setGuardContext(context, true, GuardNameConstants.ACQUISITION_ELIGIBILITY_GUARD);
                return Mono.just(true);

            } catch (Exception e) {
                log.error("Unable to verify the validation and check of the commercial eligibility on acquisition [{}]:", e.getMessage(), e);
                StateMachineUtil.setDescriptionContext(context, INTERNAL_SERVER_ERROR);
                StateMachineUtil.setNextTaskToNull(context);
                StateMachineUtil.setGuardContext(context, false, GuardNameConstants.ACQUISITION_ELIGIBILITY_GUARD);
                return Mono.just(false);
            }
        }
    }

    private ProductOfferingQualification createProductOfferingQualification(String productOfferingId, com.orange.discobole.processflow.dto.generated.RelatedParty relatedParty) {
        RelatedParty party = RelatedParty.builder()
                .id(relatedParty.getId())
                .role(relatedParty.getRole())
                .type(OrderCaptureConstants.RELATED_PARTY_TYPE)
                .referredType(OrderCaptureConstants.RELATED_PARTY_REFERRED_TYPE)
                .build();

        ProductOfferingRef offeringRef = ProductOfferingRef
                .builder()
                .id(productOfferingId)
                .build();

        ProductOfferingQualificationItem qualificationItem = ProductOfferingQualificationItem.builder()
                .id(OrderCaptureConstants.DEFAULT_ID)
                .productOffering(offeringRef)
                .build();

        return ProductOfferingQualification.builder()
                .description(OrderCaptureConstants.DESCRIPTION_RECEIVED_PRODUCT_OFFERING_ELIGIBILITY_DESCRIPTION)
                .relatedParties(Collections.singletonList(party))
                .productOfferingQualificationItems(Collections.singletonList(qualificationItem))
                .build();
    }
}