// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.service.kafka.consumer;

import com.orange.discobole.ordermanagement.event.om.ProductOrderPayloadEvent;
import com.orange.discobole.ordermanagement.event.om.ProductOrderStateChangeEvent;
import com.orange.discobole.ordermanagement.orderfollowup.constant.ExceptionMessage;
import com.orange.discobole.ordermanagement.orderfollowup.service.ProductOrderEventService;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderStateType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.util.Assert;

import java.util.function.Consumer;

import static com.orange.discobole.ordermanagement.commons.enumeration.EventType.PRODUCT_ORDER_STATE_CHANGE_EVENT;

@Slf4j
public class ProductOrderEventConsumer implements Consumer<Message<ProductOrderStateChangeEvent>> {
    private final ProductOrderEventService productOrderEventService;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ProductOrderEventConsumer(ProductOrderEventService productOrderEventService) {
        this.productOrderEventService = productOrderEventService;
    }

    @Override
    public void accept(Message<ProductOrderStateChangeEvent> message) {
        try {
            ProductOrderStateChangeEvent productOrderStateChangeEvent = message.getPayload();
            String eventId = productOrderStateChangeEvent.getEventId();
            String productOrderId = productOrderStateChangeEvent.getEvent() != null
                    && productOrderStateChangeEvent.getEvent().getProductOrder() != null
                    ? productOrderStateChangeEvent.getEvent().getProductOrder().getId() : "unknown";
            ProductOrderStateType state = productOrderStateChangeEvent.getEvent() != null
                    && productOrderStateChangeEvent.getEvent().getProductOrder() != null
                    ? productOrderStateChangeEvent.getEvent().getProductOrder().getState() : null;

            log.info("Received ProductOrderStateChangeEvent - eventId: {}, productOrderId: {}, eventType: {}, orderState: {}",
                    eventId, productOrderId, productOrderStateChangeEvent.getEventType(), state);
            log.debug("Full message headers: {}", message.getHeaders());

            checkProductOrderEventParameter(productOrderStateChangeEvent);
            log.debug("Event validation passed - eventId: {}", eventId);

            if (isProductOrderAccepted(productOrderStateChangeEvent)) {
                log.info("Product order ACCEPTED - creating follow-up event - productOrderId: {}", productOrderId);
                productOrderEventService.createProductOrderItemStateChangedEvent(
                        productOrderStateChangeEvent.getEvent().getProductOrder(),
                        productOrderStateChangeEvent.getEventTime());
                log.info("Successfully created follow-up event for ACCEPTED order - productOrderId: {}", productOrderId);
            } else if (isProductOrderCancelled(productOrderStateChangeEvent)) {
                log.info("Product order CANCELLED - deleting existing events - productOrderId: {}", productOrderId);
                productOrderEventService.deleteByProductOrderId(
                        productOrderStateChangeEvent.getEvent().getProductOrder().getId());
                log.info("Successfully deleted events for CANCELLED order - productOrderId: {}", productOrderId);
            } else {
                log.info("Product order state not actionable - eventId: {}, productOrderId: {}, state: {} - no action taken",
                        eventId, productOrderId, state);
            }
        } catch (IllegalArgumentException e) {
            log.error("Event validation failed - {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error processing ProductOrderStateChangeEvent - {}", e.getMessage(), e);
        }
    }

    private void checkProductOrderEventParameter(ProductOrderStateChangeEvent productOrderStateChangeEvent) {
        Assert.notNull(productOrderStateChangeEvent, ExceptionMessage.INVALID_MESSAGE);
        Assert.notNull(productOrderStateChangeEvent.getEventId(), ExceptionMessage.INVALID_EVENT_ID);
        Assert.notNull(productOrderStateChangeEvent.getEventTime(), ExceptionMessage.INVALID_EVENT_TIME);
        Assert.notNull(productOrderStateChangeEvent.getEventType(), ExceptionMessage.INVALID_EVENT_TYPE);
        ProductOrderPayloadEvent event = productOrderStateChangeEvent.getEvent();
        Assert.notNull(event, ExceptionMessage.INVALID_MESSAGE);
        Assert.notNull(event.getProductOrder(), ExceptionMessage.PRODUCT_ORDER_MAY_NOT_BE_NULL);
        Assert.notNull(event.getProductOrder().getId(), ExceptionMessage.PRODUCT_ORDER_ID_MAY_NOT_BE_NULL);
        Assert.notNull(event.getProductOrder().getState(), ExceptionMessage.INVALID_STATE_TYPE);
        Assert.notEmpty(event.getProductOrder().getProductOrderItem(), ExceptionMessage.PRODUCT_ORDER_ITEM_MAY_NOT_BE_NULL);
    }

    private boolean isProductOrderAccepted(ProductOrderStateChangeEvent productOrderStateChangeEvent) {
        return productOrderStateChangeEvent.getEventType().equals(PRODUCT_ORDER_STATE_CHANGE_EVENT.getValue())
                && productOrderStateChangeEvent.getEvent().getProductOrder().getState().equals(ProductOrderStateType.ACCEPTED);
    }

    private boolean isProductOrderCancelled(ProductOrderStateChangeEvent productOrderStateChangeEvent) {
        return productOrderStateChangeEvent.getEventType().equals(PRODUCT_ORDER_STATE_CHANGE_EVENT.getValue())
                && productOrderStateChangeEvent.getEvent().getProductOrder().getState().equals(ProductOrderStateType.CANCELLED);
    }
}