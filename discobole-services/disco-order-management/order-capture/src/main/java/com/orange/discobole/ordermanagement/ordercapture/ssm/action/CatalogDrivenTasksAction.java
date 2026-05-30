// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.action;

import com.orange.discobole.ordermanagement.commons.dto.product.specification.ProductSpecification;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.domain.SettingsEntity;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductOrderService;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductSpecificationService;
import com.orange.discobole.ordermanagement.ordercapture.service.SettingsService;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ItemActionType;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderItem;
import com.orange.discobole.processflow.ssm.action.StateMachineStateAction;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.*;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Component("setCatalogDrivenTasksAction")
@Slf4j
public class CatalogDrivenTasksAction implements StateMachineStateAction<String, String> {
    private final ProductOrderService productOrderService;
    private final ProductSpecificationService productSpecificationService;
    private final SettingsService settingsService;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public CatalogDrivenTasksAction(ProductOrderService productOrderService, ProductSpecificationService productSpecificationService, SettingsService settingsService) {
        this.productOrderService = productOrderService;
        this.productSpecificationService = productSpecificationService;
        this.settingsService = settingsService;
    }

    @Override
    public Mono<Void> apply(StateContext<String, String> context) {
        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            return Mono.empty();
        }
        try {
            log.info("Inside set catalog driven tasks action");
            ProductOrder productOrder = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.CREATED_PRODUCT_ORDER, ProductOrder.class);
            assert productOrder != null;
            Map<String, String> productOrderItemProductSpecIdMap = getProductOrderItemProductSpecIdMap(productOrder);
            Map<String, List<String>> productOrderItemLogicalResourcesMap = new HashMap<>();
            List<String> physicalProductOrderItems = new ArrayList<>();

            if (!CollectionUtils.isEmpty(productOrderItemProductSpecIdMap)) {
                SettingsEntity settings = settingsService.getSettings();
                List<String> productSpecIds = productOrderItemProductSpecIdMap.values().stream()
                        .distinct()
                        .toList();
                List<ProductSpecification> productSpecifications = productSpecificationService.fetchProductSpecifications(productSpecIds);

                if (settings.isReserveLogicalResourceEnabled()) {
                    productOrderItemLogicalResourcesMap = productOrderService.getProductOrderItemLogicalResourcesIds(productOrderItemProductSpecIdMap, productSpecifications);
                }
                physicalProductOrderItems = productOrderService.getPhysicalProductOrderItems(productOrderItemProductSpecIdMap, productSpecifications);
            }

            setContextVariables(context, productOrderItemLogicalResourcesMap, physicalProductOrderItems, TRUE);
            return Mono.empty();
        } catch (Exception e) {
            log.error("Unable to set catalog driven tasks [{}]:", e.getMessage(), e);
            setContextVariables(context, Collections.emptyMap(), Collections.emptyList(), FALSE);
            return Mono.empty();
        }
    }

    public Map<String, String> getProductOrderItemProductSpecIdMap(ProductOrder productOrder) {
        Map<String, String> productOrderItemProdSpecMap = new HashMap<>();

        for (ProductOrderItem productOrderItem : productOrder.getProductOrderItem()) {
            if (ItemActionType.ADD.equals(productOrderItem.getAction()) &&
                    productOrderItem.getProduct() instanceof Product product &&
                    product.getProductSpecification() != null) {

                productOrderItemProdSpecMap.put(productOrderItem.getId(), product.getProductSpecification().getId());
            }
        }

        return productOrderItemProdSpecMap;
    }

    private void setContextVariables(StateContext<String, String> context, Map<String, List<String>> productOrderItemLogicalResourcesMap, List<String> physicalProductOrderItems, Boolean areCatalogDrivenTasksSet) {
        context.getExtendedState().getVariables().put(OrderCaptureConstants.ARE_CATALOG_DRIVEN_TASKS_SET, areCatalogDrivenTasksSet);
        context.getExtendedState().getVariables().put(OrderCaptureConstants.PRODUCT_ORDER_ITEM_LOGICAL_RESOURCES, productOrderItemLogicalResourcesMap);
        context.getExtendedState().getVariables().put(OrderCaptureConstants.PHYSICAL_PRODUCT_ITEM_ID_LIST, physicalProductOrderItems);
    }
}