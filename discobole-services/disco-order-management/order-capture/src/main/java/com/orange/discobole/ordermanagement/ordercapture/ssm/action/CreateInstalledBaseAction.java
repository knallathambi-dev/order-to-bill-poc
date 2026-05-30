// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.action;

import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductOrderService;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import com.orange.discobole.processflow.ssm.action.StateMachineStateAction;
import com.orange.discobole.productinventory.dto.v1.Product;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component("createInstalledBaseAction")
@Slf4j
public class CreateInstalledBaseAction implements StateMachineStateAction<String, String> {
    private final ProductOrderService productOrderService;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public CreateInstalledBaseAction(ProductOrderService productOrderService) {
        this.productOrderService = productOrderService;
    }

    @Override
    public Mono<Void> apply(StateContext<String, String> context) {
        ProductOrder productOrder = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.CREATED_PRODUCT_ORDER, ProductOrder.class);

        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            return Mono.empty();
        }

        try {
            log.info("Inside create installed base action");
            String contractProductId = null;
            Map<String, String> physicalProductOrderItemSerialNumberMap = StateMachineUtil.getMapValue(context.getExtendedState().getVariables(), OrderCaptureConstants.PHYSICAL_PRODUCT_ORDER_ITEM_SERIAL_NUMBER_MAP);
            String configurationAction = StateMachineUtil.getStringValue(context, OrderCaptureConstants.REQUESTED_CONFIGURATION_ACTION);
            if (OrderCaptureConstants.MODIFICATION.equals(configurationAction) || OrderCaptureConstants.TERMINATION.equals(configurationAction) || OrderCaptureConstants.MIGRATE.equals(configurationAction)) {
                contractProductId = StateMachineUtil.getStringValue(context, OrderCaptureConstants.CONTRACT_PRODUCT_ID);
            }

            List<Product> products = productOrderService.createProducts(productOrder, physicalProductOrderItemSerialNumberMap, configurationAction, contractProductId);

            processProductOrderItems(productOrder, products);
            updateContextVariables(context, true, productOrder);

            return Mono.empty();
        } catch (Exception e) {
            log.error("Unable to create installed base [{}]:", e.getMessage(), e);
            updateContextVariables(context, false, productOrder);
            return Mono.empty();
        }
    }

    private void updateContextVariables(StateContext<String, String> context, boolean productsCreated, ProductOrder productOrder) {
        context.getExtendedState().getVariables().put(OrderCaptureConstants.ARE_PRODUCTS_CREATED, productsCreated);
        context.getExtendedState().getVariables().put(OrderCaptureConstants.CREATED_PRODUCT_ORDER, productOrder);

        if (!productsCreated) {
            StateMachineUtil.setDescriptionContext(context, DescriptionConstants.PRODUCT_INSTANTIATION_ERROR);
            productOrderService.updateProductOrderInventoryState(productOrder, ProductOrderStateType.HELD);
        }
    }

    private void processProductOrderItems(ProductOrder productOrder, List<Product> products) {
        Map<String, Product> orderItemToProductMap = mapOrderItemsToProducts(productOrder, products);
        updateOrderItemDetails(productOrder, orderItemToProductMap);
        productOrderService.updateProduct(productOrder);
    }

    private Map<String, Product> mapOrderItemsToProducts(ProductOrder productOrder, List<Product> products) {
        return products.stream()
                .flatMap(product -> product.getProductOrderItem().stream()
                        .filter(orderItem -> productOrder.getId().equals(orderItem.getProductOrderId()))
                        .map(orderItem -> Map.entry(orderItem.getOrderItemId(), product)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (existingValue, newValue) -> existingValue));
    }

    private void updateOrderItemDetails(ProductOrder productOrder, Map<String, Product> orderItemToProductMap) {
        productOrder.getProductOrderItem().stream()
                .filter(orderItem -> ItemActionType.ADD.equals(orderItem.getAction()) || isMigratedItem(orderItem))
                .forEach(orderItem -> updateProductDetails(orderItem, orderItemToProductMap.get(orderItem.getId())));
    }

    private boolean isMigratedItem(ProductOrderItem orderItem) {
        return ItemActionType.MIGRATE.equals(orderItem.getAction()) &&
                Objects.nonNull(orderItem.getProductOrderItemRelationship()) &&
                orderItem.getProductOrderItemRelationship()
                        .stream()
                        .anyMatch(orderItemRelationship ->
                                RelationshipType.MIGRATEFROM.equals(orderItemRelationship.getRelationshipType())
                        );
    }

    private void updateProductDetails(ProductOrderItem orderItem, Product product) {
        if (Objects.isNull(product)) {
            return;
        }

        if (orderItem.getProduct() == null) {
            ProductRef newProductRef = new ProductRef();
            newProductRef.setId(product.getId());
            newProductRef.setHref(product.getHref());
            orderItem.setProduct(newProductRef);
        } else {
            if (orderItem.getProduct() instanceof ProductRef productRef) {
                productRef.setId(product.getId());
                productRef.setHref(product.getHref());
            } else {
                com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product orderProduct = (com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product) orderItem.getProduct();
                orderProduct.setId(product.getId());
                orderProduct.setHref(product.getHref());
            }
        }
    }
}