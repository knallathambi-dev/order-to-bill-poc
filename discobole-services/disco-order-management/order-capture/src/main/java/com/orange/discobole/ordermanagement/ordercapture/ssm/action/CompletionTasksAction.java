// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.action;

import com.orange.discobole.ordermanagement.commons.dto.product.offering.price.ProductOfferingPrice;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductOfferingPriceService;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import com.orange.discobole.processflow.ssm.action.StateMachineStateAction;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.NRC_PRICE_TYPE;
import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.RC_PRICE_TYPE;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Component("setCompletionTasksAction")
@Slf4j
public class CompletionTasksAction implements StateMachineStateAction<String, String> {
    private final ProductOfferingPriceService productOfferingPriceService;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public CompletionTasksAction(ProductOfferingPriceService productOfferingPriceService) {
        this.productOfferingPriceService = productOfferingPriceService;
    }

    @Override
    public Mono<Void> apply(StateContext<String, String> context) {
        if (TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            return Mono.empty();
        }
        log.info("Inside set completion tasks action");

        ProductOrder productOrder = StateMachineUtil.getObjectValue(
                context,
                OrderCaptureConstants.CREATED_PRODUCT_ORDER,
                ProductOrder.class
        );

        if (Objects.isNull(productOrder) || CollectionUtils.isEmpty(productOrder.getProductOrderItem())) {
            updateContextVariables(context, Map.of(OrderCaptureConstants.ARE_COMPLETION_TASKS_SET, false));
            return Mono.empty();
        }

        handleProductOrder(context, productOrder);
        return Mono.empty();
    }

    private void handleProductOrder(StateContext<String, String> context, ProductOrder productOrder) {
        try {

            Map<ProductOrderItem, List<String>> itemPricesMap = extractItemPricesMap(productOrder);
            if (!CollectionUtils.isEmpty(itemPricesMap)) {
                List<String> priceIds = extractPriceIds(itemPricesMap);
                List<ProductOfferingPrice> productOfferingPrices = productOfferingPriceService.fetchProductOfferingPrices(priceIds);
                updateOrderPricesWithOfferingPrices(productOfferingPrices, itemPricesMap);
            }

            List<String> unpaidOrderItemIds = extractOrderItemIdsBasedOnPriceCondition(
                    productOrder,
                    this::isImmediatePaymentRequired,
                    stream -> stream.allMatch(Boolean::booleanValue)
            );

            List<String> orderItemsRequiringBA = extractOrderItemIdsBasedOnPriceCondition(
                    productOrder,
                    this::requiresBillingAccountRef,
                    stream -> stream.anyMatch(Boolean::booleanValue)
            );

            boolean isAppointmentRequired = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_APPOINTMENT_REQUIRED, FALSE);
            List<String> orderItemsRequiringAppointment = extractOrderItemIdsBasedOnAddressCharacteristic(productOrder, isAppointmentRequired);
            Map<String, Object> contextVars = new HashMap<>();
            if (!orderItemsRequiringBA.isEmpty()) {
                contextVars.put(OrderCaptureConstants.ORDER_ITEM_IDS_REQUIRING_BA_REF, orderItemsRequiringBA);
            }
            if (!unpaidOrderItemIds.isEmpty()) {
                contextVars.put(OrderCaptureConstants.UNPAID_ORDER_ITEM_IDS, unpaidOrderItemIds);
            }
            if (!orderItemsRequiringAppointment.isEmpty()) {
                contextVars.put(OrderCaptureConstants.ORDER_ITEM_IDS_REQUIRING_APPOINTMENT_REF, orderItemsRequiringAppointment);
            }

            boolean isCompleteOrderRequired =
                    !(orderItemsRequiringBA.isEmpty()
                            && unpaidOrderItemIds.isEmpty()
                            && orderItemsRequiringAppointment.isEmpty());

            contextVars.put(OrderCaptureConstants.IS_COMPLETE_ORDER_REQUIRED, isCompleteOrderRequired);

            contextVars.put(OrderCaptureConstants.ARE_COMPLETION_TASKS_SET, true);
            updateContextVariables(context, contextVars);

        } catch (Exception e) {
            log.error("Unable to set completion tasks [{}]:", e.getMessage(), e);
            updateContextVariables(context, Map.of(OrderCaptureConstants.ARE_COMPLETION_TASKS_SET, false));
        }
    }

    private Map<ProductOrderItem, List<String>> extractItemPricesMap(ProductOrder productOrder) {
        return productOrder.getProductOrderItem().stream()
                .filter(Objects::nonNull)
                .filter(item -> !CollectionUtils.isEmpty(item.getItemPrice()))
                .collect(Collectors.toMap(
                        item -> item,
                        item -> item.getItemPrice().stream()
                                .filter(orderPrice -> orderPrice.getProductOfferingPrice() != null)
                                .map(orderPrice -> extractProductOfferingPriceId(orderPrice.getProductOfferingPrice()))
                                .filter(Objects::nonNull)
                                .toList()
                ));
    }

    private String extractProductOfferingPriceId(ProductOfferingPriceRefOrValue productOfferingPrice) {
        if (productOfferingPrice instanceof ProductOfferingPriceCharge priceCharge) {
            return priceCharge.getId();
        }
        if (productOfferingPrice instanceof InstallmentCharge installmentCharge) {
            return installmentCharge.getId();
        }
        return null;
    }

    private List<String> extractPriceIds(Map<ProductOrderItem, List<String>> itemPricesMap) {
        return itemPricesMap.values().stream()
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    private List<String> extractOrderItemIdsBasedOnPriceCondition(
            ProductOrder productOrder,
            Predicate<OrderPrice> priceCondition,
            Predicate<Stream<Boolean>> matchStrategy) {

        return productOrder.getProductOrderItem().stream()
                .filter(item -> !CollectionUtils.isEmpty(item.getItemPrice()))
                .filter(item -> item.getItemPrice().stream()
                        .allMatch(this::hasValidPositivePrice))
                .filter(item -> {
                    Stream<Boolean> matches = item.getItemPrice().stream()
                            .map(priceCondition::test);
                    return matchStrategy.test(matches);
                })
                .map(ProductOrderItem::getId)
                .toList();
    }

    private List<String> extractOrderItemIdsBasedOnAddressCharacteristic(ProductOrder productOrder, boolean isAppointmentRequired) {
        if (!isAppointmentRequired) {
            return Collections.emptyList();
        }

        return productOrder.getProductOrderItem().stream()
                .filter(this::hasAddressCharacteristic)
                .map(ProductOrderItem::getId)
                .toList();
    }

    private boolean hasAddressCharacteristic(ProductOrderItem item) {

        if (item == null || item.getProduct() == null) {
            return false;
        }

        ProductRefOrValue prv = item.getProduct();
        if (!(prv instanceof Product product)) {
            return false;
        }

        return Optional.ofNullable(product.getProductCharacteristic())
                .orElse(Collections.emptyList())
                .stream()
                .anyMatch(AddressCharacteristic.class::isInstance);
    }

    private boolean hasValidPositivePrice(OrderPrice orderPrice) {
        return Objects.nonNull(orderPrice.getPrice()) &&
                Objects.nonNull(orderPrice.getPrice().getTaxIncludedAmount()) &&
                Objects.nonNull(orderPrice.getPrice().getTaxIncludedAmount().getValue()) &&
                orderPrice.getPrice().getTaxIncludedAmount().getValue() > 0;
    }

    private void updateOrderPricesWithOfferingPrices(List<ProductOfferingPrice> productOfferingPrices,
                                                     Map<ProductOrderItem, List<String>> itemPricesMap) {
        itemPricesMap.forEach((productOrderItem, ids) ->
                productOrderItem.getItemPrice().forEach(orderPrice ->
                        productOfferingPrices.stream()
                                .filter(pop -> Objects.nonNull(pop.getImmediatePayment()))
                                .filter(pop -> ids.contains(pop.getId()) && pop.getId().equals(extractProductOfferingPriceId(orderPrice.getProductOfferingPrice())))
                                .findFirst()
                                .ifPresent(pop -> updateImmediatePayment(orderPrice.getProductOfferingPrice(), pop.getImmediatePayment()))
                )
        );
    }

    private void updateImmediatePayment(ProductOfferingPriceRefOrValue productOfferingPrice, boolean immediatePayment) {
        if (productOfferingPrice instanceof ProductOfferingPriceCharge priceCharge) {
            priceCharge.setImmediatePayment(immediatePayment);
        }
    }

    private boolean requiresBillingAccountRef(OrderPrice itemPrice) {
        boolean hasValidPrice = Objects.nonNull(itemPrice.getPrice());
        boolean isRecurringCharge = RC_PRICE_TYPE.equals(itemPrice.getPriceType());
        boolean isNonRecurringChargeWithImmediatePayment = NRC_PRICE_TYPE.equals(itemPrice.getPriceType()) &&
                Objects.nonNull(itemPrice.getProductOfferingPrice()) &&
                !getImmediatePayment(itemPrice.getProductOfferingPrice());

        return (hasValidPrice && (isRecurringCharge || isNonRecurringChargeWithImmediatePayment))
                || (itemPrice.getProductOfferingPrice() instanceof InstallmentCharge);
    }

    private boolean isImmediatePaymentRequired(OrderPrice itemPrice) {
        return Objects.nonNull(itemPrice.getPrice()) &&
                Objects.nonNull(itemPrice.getProductOfferingPrice()) &&
                getImmediatePayment(itemPrice.getProductOfferingPrice())
                || (itemPrice.getProductOfferingPrice() instanceof InstallmentCharge installmentCharge
                && Objects.nonNull(installmentCharge.getDownPayment()));
    }

    private boolean getImmediatePayment(ProductOfferingPriceRefOrValue productOfferingPrice) {
        if (productOfferingPrice instanceof ProductOfferingPriceCharge priceCharge) {
            return priceCharge.getImmediatePayment();
        }
        return false;
    }

    private void updateContextVariables(StateContext<String, String> context, Map<String, Object> vars) {
        context.getExtendedState().getVariables().putAll(vars);
    }
}
