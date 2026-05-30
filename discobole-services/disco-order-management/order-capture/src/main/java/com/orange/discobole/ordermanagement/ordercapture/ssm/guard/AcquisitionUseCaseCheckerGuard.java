// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.guard;

import com.orange.discobole.ordermanagement.commons.dto.product.configuration.ConfigurationAction;
import com.orange.discobole.ordermanagement.commons.dto.product.configuration.ProductConfiguration;
import com.orange.discobole.ordermanagement.commons.dto.product.configuration.QueryProductConfiguration;
import com.orange.discobole.ordermanagement.commons.dto.product.configuration.QueryProductConfigurationItem;
import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.GuardNameConstants;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductConfigurationService;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.processflow.ssm.guard.StateMachineGuard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

import static com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants.ERROR_RETRIEVING_PRODUCT_CONFIGURATION_ITEMS;
import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.*;

@Slf4j
@Component("isAcquisitionUseCaseGuard")
public class AcquisitionUseCaseCheckerGuard implements StateMachineGuard<String, String> {
    private final ProductConfigurationService productConfigurationService;

    public AcquisitionUseCaseCheckerGuard(ProductConfigurationService productConfigurationService) {
        this.productConfigurationService = productConfigurationService;
    }

    @Override
    public Mono<Boolean> apply(StateContext<String, String> context) {
        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            return StateMachineUtil.getGuardResult(context, GuardNameConstants.ACQUISITION_USE_CASE_CHECKER_GUARD);
        }
        return checkUseCase(context);
    }

    private Mono<Boolean> checkUseCase(StateContext<String, String> context) {
        log.info("Inside guard to check if the use case is an acquisition action");
        String configurationId = StateMachineUtil.getStringValue(context, CONFIGURATION_ID);
        try {
            QueryProductConfiguration queryProductConfiguration = productConfigurationService.getProductConfigurationById(configurationId);
            return evaluateAction(context, queryProductConfiguration);
        } catch (DiscoException e) {
            String reason = e.getReason();
            if (DescriptionConstants.PRODUCT_CONFIGURATOR_SERVICE_UNREACHABLE.equals(reason)) {
                StateMachineUtil.setNextTaskToNullInCaseOfUnreachableService(context, e.getReason(), DescriptionConstants.PRODUCT_CONFIGURATOR_SERVICE_UNREACHABLE);
                StateMachineUtil.setDescriptionContext(context, DescriptionConstants.PRODUCT_CONFIGURATOR_SERVICE_UNREACHABLE);
            } else if (ERROR_RETRIEVING_PRODUCT_CONFIGURATION_ITEMS.equals(reason)) {
                StateMachineUtil.setDescriptionContext(context, DescriptionConstants.VALID_CONFIGURATION_IDENTIFIER_REQUIRED);
            }
            markGuardAsFailed(context);
            return Mono.just(false);
        } catch (Exception e) {
            log.error("Unable to check if use case was acquisition [{}]:", e.getMessage(), e);
            StateMachineUtil.setDescriptionContext(context, DescriptionConstants.INTERNAL_SERVER_ERROR);
            markGuardAsFailed(context);
            return Mono.just(false);
        }
    }

    private Mono<Boolean> evaluateAction(StateContext<String, String> context, QueryProductConfiguration queryProductConfiguration) {
        ConfigurationAction configurationAction = findConfigurationAction(queryProductConfiguration);
        if (Objects.isNull(configurationAction) || Objects.isNull(configurationAction.getAction())) {
            throw new DiscoException(ERROR_RETRIEVING_PRODUCT_CONFIGURATION_ITEMS);
        }
        context.getExtendedState().getVariables().put(REQUESTED_CONFIGURATION_ACTION, configurationAction.getAction());
        if (configurationAction.getAction().equalsIgnoreCase(ADD)) {
            String productOfferingId = getProductOfferingId(queryProductConfiguration);
            if (Objects.isNull(productOfferingId)) {
                throw new DiscoException(ERROR_RETRIEVING_PRODUCT_CONFIGURATION_ITEMS);
            }
            StateMachineUtil.setGuardContext(context, true, GuardNameConstants.ACQUISITION_USE_CASE_CHECKER_GUARD);
            context.getExtendedState().getVariables().put(PRODUCT_OFFERING_ID, productOfferingId);
            return Mono.just(true);
        } else {
            StateMachineUtil.setGuardContext(context, false, GuardNameConstants.ACQUISITION_USE_CASE_CHECKER_GUARD);
            context.getExtendedState().getVariables().put(IS_MODIFICATION_OR_TERMINATION_OR_MIGRATION_USE_CASE, true);
            return Mono.just(false);
        }
    }

    private String getProductOfferingId(QueryProductConfiguration queryProductConfiguration) {
        ProductConfiguration productConfiguration = queryProductConfiguration.getRequestedProductConfigurationItems().get(0).getProductConfiguration();
        if (Objects.nonNull(productConfiguration) && Objects.nonNull(productConfiguration.getProductOffering()) && Objects.nonNull(productConfiguration.getProductOffering().getId())) {
            return productConfiguration.getProductOffering().getId();
        }
        return null;
    }

    private ConfigurationAction findConfigurationAction(QueryProductConfiguration queryProductConfiguration) {
        List<ConfigurationAction> configurationActionList = queryProductConfiguration
                .getRequestedProductConfigurationItems()
                .stream()
                .map(QueryProductConfigurationItem::getProductConfiguration)
                .filter(Objects::nonNull)
                .map(ProductConfiguration::getConfigurationActions)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .toList();
        return configurationActionList.stream()
                .filter(action -> Objects.nonNull(action.getIsSelected()) && action.getIsSelected())
                .findFirst()
                .orElse(null);
    }

    private void markGuardAsFailed(StateContext<String, String> context) {
        StateMachineUtil.setGuardContext(context, false, GuardNameConstants.ACQUISITION_USE_CASE_CHECKER_GUARD);
        context.getExtendedState().getVariables().put(IS_MODIFICATION_OR_TERMINATION_OR_MIGRATION_USE_CASE, false);
    }
}