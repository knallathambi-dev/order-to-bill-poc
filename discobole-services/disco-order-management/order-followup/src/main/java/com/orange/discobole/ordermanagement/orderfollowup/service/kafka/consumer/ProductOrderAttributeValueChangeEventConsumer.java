// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.service.kafka.consumer;

import com.orange.discobole.ordermanagement.event.om.ProductOrderAttributePayloadEvent;
import com.orange.discobole.ordermanagement.event.om.ProductOrderAttributeValueChangeEvent;
import com.orange.discobole.ordermanagement.orderfollowup.constant.ExceptionMessage;
import com.orange.discobole.ordermanagement.orderfollowup.service.ProductOrderEventService;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderItem;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderItemStateType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@Slf4j
public class ProductOrderAttributeValueChangeEventConsumer implements Consumer<Message<ProductOrderAttributeValueChangeEvent>> {

    private final ProductOrderEventService productOrderEventService;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ProductOrderAttributeValueChangeEventConsumer(ProductOrderEventService productOrderEventService) {
        this.productOrderEventService = productOrderEventService;
    }

    @Override
    public void accept(Message<ProductOrderAttributeValueChangeEvent> message) {
        try {
            ProductOrderAttributeValueChangeEvent event = message.getPayload();
            String eventId = event.getEventId();
            String productOrderId = event.getEvent() != null && event.getEvent().getProductOrder() != null
                    ? event.getEvent().getProductOrder().getId() : "unknown";

            log.info("Received ProductOrderAttributeValueChangeEvent - eventId: {}, productOrderId: {}, eventType: {}",
                    eventId, productOrderId, event.getEventType());
            log.debug("Full message headers: {}", message.getHeaders());

            validateEvent(event);
            log.debug("Event validation passed - eventId: {}", eventId);

            ProductOrderItem productOrderItem = getSingleProductOrderItem(event);
            if (isProductSpecificationPresent(productOrderItem)) {
                log.info("Product specification present and eligible for processing - eventId: {}, productOrderId: {}, itemState: {}, isInstallable: true",
                        eventId, productOrderId, productOrderItem.getState());
                productOrderEventService.addProductOrderItem(event);
                log.info("Successfully processed attribute value change event - eventId: {}, productOrderId: {}", eventId, productOrderId);
            } else {
                log.debug("Product order item not eligible for processing - eventId: {}, productOrderId: {}, reason: {}",
                        eventId, productOrderId, getSkipReason(productOrderItem));
            }
        } catch (IllegalArgumentException e) {
            log.error("Event validation failed - {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error processing ProductOrderAttributeValueChangeEvent - {}", e.getMessage(), e);
        }
    }

    private void validateEvent(ProductOrderAttributeValueChangeEvent event) {
        Assert.notNull(event, ExceptionMessage.INVALID_MESSAGE);
        Assert.notNull(event.getEventId(), ExceptionMessage.INVALID_EVENT_ID);
        Assert.notNull(event.getEventTime(), ExceptionMessage.INVALID_EVENT_TIME);
        Assert.notNull(event.getEventType(), ExceptionMessage.INVALID_EVENT_TYPE);
        ProductOrderAttributePayloadEvent payloadEvent = event.getEvent();
        Assert.notNull(payloadEvent, ExceptionMessage.INVALID_MESSAGE);
        Assert.notNull(payloadEvent.getProductOrder(), ExceptionMessage.PRODUCT_ORDER_MAY_NOT_BE_NULL);
        Assert.notNull(payloadEvent.getProductOrder().getId(), ExceptionMessage.PRODUCT_ORDER_ID_MAY_NOT_BE_NULL);
        Assert.notNull(payloadEvent.getProductOrder().getState(), ExceptionMessage.INVALID_STATE_TYPE);
        Assert.notEmpty(payloadEvent.getProductOrder().getProductOrderItem(), ExceptionMessage.PRODUCT_ORDER_ITEM_MAY_NOT_BE_NULL);
    }

    private ProductOrderItem getSingleProductOrderItem(ProductOrderAttributeValueChangeEvent event) {
        List<ProductOrderItem> productOrderItems = event.getEvent().getProductOrder().getProductOrderItem();
        return productOrderItems.get(0);
    }

    private boolean isProductSpecificationPresent(ProductOrderItem productOrderItem) {
        return Objects.nonNull(productOrderItem.getProduct())
                && productOrderItem.getProduct() instanceof Product product
                && Objects.nonNull(product.getProductSpecification())
                && Objects.nonNull(productOrderItem.getState())
                && !productOrderItem.getState().equals(ProductOrderItemStateType.INPROGRESS)
                && Boolean.TRUE.equals(productOrderItem.getIsInstallable());
    }

    private String getSkipReason(ProductOrderItem productOrderItem) {
        if (Objects.isNull(productOrderItem.getProduct())) {
            return "product is null";
        }
        if (!(productOrderItem.getProduct() instanceof Product product)) {
            return "product is not of expected type";
        }
        if (Objects.isNull(product.getProductSpecification())) {
            return "productSpecification is null";
        }
        if (Objects.isNull(productOrderItem.getState())) {
            return "item state is null";
        }
        if (productOrderItem.getState().equals(ProductOrderItemStateType.INPROGRESS)) {
            return "item state is INPROGRESS";
        }
        if (!Boolean.TRUE.equals(productOrderItem.getIsInstallable())) {
            return "item is not installable";
        }
        return "unknown";
    }
}