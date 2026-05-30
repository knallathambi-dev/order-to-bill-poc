// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.service.kafka.consumer;

import com.orange.discobole.ordermanagement.event.om.ProductOrderCommand;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.ordermanagement.orderinventory.service.ProductOrderService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

import static com.orange.discobole.ordermanagement.event.om.Command.EventType.*;

public class ProductOrderCommandConsumer implements Consumer<Message<ProductOrderCommand>> {

    private final Logger log = LoggerFactory.getLogger(ProductOrderCommandConsumer.class);

    private final ProductOrderService productOrderService;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ProductOrderCommandConsumer(ProductOrderService productOrderService) {
        this.productOrderService = productOrderService;
    }

    @Override
    public void accept(Message<ProductOrderCommand> message) {
        try {
            log.debug("Consuming product order command message : {}", message);
            ProductOrderCommand payload = message.getPayload();
            if (isNotValidProductOrderCommand(payload)) {
                return;
            }
            ProductOrder productOrder = payload.getEvent().getProductOrder();
            if (payload.getEventType().equals(PRODUCT_ORDER_STATE_CHANGE_COMMAND)) {
                productOrderService.updateState(
                        productOrder
                );
            } else if (payload.getEventType().equals(ORDER_ITEMS_AND_ORDER_TOTAL_PRICE_VALUE_CHANGE_COMMAND)) {
                productOrderService.updateOrderItemsAndOrderTotalPrice(productOrder);
            } else if (payload.getEventType().equals(REALIZING_RESOURCE_VALUE_CHANGE_COMMAND)) {
                productOrderService.updateProductOrderRealizingResource(productOrder);
            } else if (payload.getEventType().equals(PAYMENT_VALUE_CHANGE_COMMAND)) {
                productOrderService.updateProductOrderPayment(productOrder);
            } else if (payload.getEventType().equals(BILLING_ACCOUNT_VALUE_CHANGE_COMMAND)) {
                productOrderService.updateProductOrderBillingAccount(productOrder);
            } else if (payload.getEventType().equals(APPOINTMENT_VALUE_CHANGE_COMMAND)) {
                productOrderService.updateProductOrderAppointment(productOrder);
            } else if (payload.getEventType().equals(PRODUCT_VALUE_CHANGE_COMMAND)) {
                productOrderService.updateProductOrderProduct(productOrder);
            } else if (payload.getEventType().equals(RELATED_PARTIES_VALUE_CHANGE_COMMAND)) {
                productOrderService.updateProductOrderRelatedParties(productOrder);
            } else if (payload.getEventType().equals(REQUESTED_COMPLETION_DATE_VALUE_CHANGE_COMMAND)) {
                productOrderService.updateRequestedCompletionDate(productOrder);
            }
        } catch (Exception e) {
            log.error("Invalid received message", e);
        }
    }

    private boolean isNotValidProductOrderCommand(ProductOrderCommand productOrderCommand) {
        return productOrderCommand == null ||
                productOrderCommand.getEventType() == null ||
                productOrderCommand.getEvent() == null ||
                productOrderCommand.getEvent().getProductOrder() == null ||
                productOrderCommand.getEvent().getProductOrder().getId() == null;
    }
}