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
import com.orange.discobole.ordermanagement.ordercapture.service.ProductInventoryService;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.processflow.ssm.guard.StateMachineGuard;
import com.orange.discobole.productinventory.dto.v1.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;

import static com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants.INTERNAL_SERVER_ERROR;
import static com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants.SELECTED_PRODUCT_NOT_VALID;
import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.MIGRATE_FROM;
import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.CONTRACT_PRODUCT_OFFERING_TYPE;

@Component("isValidProductIdentifierGuard")
@Slf4j
public class ValidationProductIdentifierGuard implements StateMachineGuard<String, String> {

    private final ProductInventoryService productInventoryService;

    public ValidationProductIdentifierGuard(ProductInventoryService productInventoryService) {
        this.productInventoryService = productInventoryService;
    }

    @Override
    public Mono<Boolean> apply(StateContext<String, String> stateContext) {
        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(stateContext))) {
            return StateMachineUtil.getGuardResult(stateContext, GuardNameConstants.VALIDATION_PRODUCT_IDENTIFIER_GUARD);
        }

        return executeProductValidation(stateContext);
    }

    private Mono<Boolean> executeProductValidation(StateContext<String, String> stateContext) {
        try {
            log.info("Inside guard to check if product is validated");
            String productIdentifier = StateMachineUtil.getStringValue(stateContext, OrderCaptureConstants.CONTRACT_PRODUCT_ID);
            Product product = productInventoryService.getProductById(productIdentifier);

            if (isProductValid(product)) {
                processValidProduct(stateContext, product);
                return Mono.just(true);
            } else {
                processInvalidProduct(stateContext);
                return Mono.just(false);
            }
        } catch (Exception exception) {
            return handleValidationException(stateContext, exception);
        }
    }

    private boolean isProductValid(Product product) {
        if (!isActiveContractProduct(product)) {
            return false;
        }

        return getMigrateFromProductId(product)
                .map(this::isMigratedProductTerminated)
                .orElse(true);
    }

    private boolean isActiveContractProduct(Product product) {
        if (!ProductOperationalStatusType.ACTIVE.equals(product.getOperationalStatus())) {
            return false;
        }

        return Optional.ofNullable(product.getProductOffering())
                .map(ProductOfferingRef::getAtType)
                .filter(CONTRACT_PRODUCT_OFFERING_TYPE::equalsIgnoreCase)
                .isPresent();
    }

    private boolean isMigratedProductTerminated(String migrateFromProductId) {
        List<Product> products = productInventoryService.getProductByIds(Collections.singletonList(migrateFromProductId));

        return products.isEmpty() ||
                products.get(0).getOperationalStatus() == ProductOperationalStatusType.TERMINATED;
    }

    private void processValidProduct(StateContext<String, String> stateContext, Product product) {
        StateMachineUtil.setGuardContext(stateContext, true, GuardNameConstants.VALIDATION_PRODUCT_IDENTIFIER_GUARD);
        updateContextWithProductOffering(stateContext, product);
    }

    private void processInvalidProduct(StateContext<String, String> stateContext) {
        StateMachineUtil.setDescriptionContext(stateContext, SELECTED_PRODUCT_NOT_VALID);
        StateMachineUtil.setGuardContext(stateContext, false, GuardNameConstants.VALIDATION_PRODUCT_IDENTIFIER_GUARD);
    }

    private Mono<Boolean> handleValidationException(StateContext<String, String> stateContext, Exception exception) {
        log.error("Unable to check if product is validated [{}]:", exception.getMessage(), exception);
        if (exception instanceof DiscoException discoException) {
            StateMachineUtil.setDescriptionContext(stateContext, discoException.getReason());
        } else {
            StateMachineUtil.setDescriptionContext(stateContext, INTERNAL_SERVER_ERROR);
        }
        StateMachineUtil.setNextTaskToNull(stateContext);
        StateMachineUtil.setGuardContext(stateContext, false, GuardNameConstants.VALIDATION_PRODUCT_IDENTIFIER_GUARD);
        return Mono.just(false);
    }

    private void updateContextWithProductOffering(StateContext<String, String> stateContext, Product product) {
        String productOfferingIdentifier = product.getProductOffering().getId();
        stateContext.getExtendedState().getVariables().put(OrderCaptureConstants.PRODUCT_OFFERING_ID, productOfferingIdentifier);
    }

    private Optional<String> getMigrateFromProductId(Product product) {
        return Optional.ofNullable(product.getProductRelationship())
                .stream()
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .filter(this::isMigrateFromRelationship)
                .map(ProductRelationship::getProduct)
                .filter(ProductRef.class::isInstance)
                .map(ProductRef.class::cast)
                .map(ProductRef::getId)
                .findFirst();
    }

    private boolean isMigrateFromRelationship(ProductRelationship relationship) {
        return MIGRATE_FROM.equals(relationship.getRelationshipType());
    }
}