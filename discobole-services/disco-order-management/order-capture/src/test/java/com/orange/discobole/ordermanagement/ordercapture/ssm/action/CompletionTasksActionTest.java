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
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.support.DefaultExtendedState;
import org.springframework.statemachine.support.DefaultStateContext;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;

import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.NRC_PRICE_TYPE;
import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.RC_PRICE_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompletionTasksActionTest {
    private static final String PRICE_ID_1 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRICE_ID_2 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRICE_ID_3 = RandomStringUtils.randomAlphabetic(10);
    private static final float VALID_PRICE = 10.0f;
    private static final float ZERO_PRICE = 0.0f;
    private static final float NEGATIVE_PRICE = -5.0f;
    private static final String CURRENCY_EUR = "EUR";
    private static final String ITEM_1 = "ITEM-1";
    private static final String ITEM_2 = "ITEM-2";
    private static final String ITEM_3 = "ITEM-3";
    private static final String ITEM_4 = "ITEM-4";

    @InjectMocks
    private CompletionTasksAction completionTasksAction;

    @Mock
    private ProductOfferingPriceService productOfferingPriceService;

    @Test
    @DisplayName("Given re-executed action, " +
            "when applying completion tasks, " +
            "then should skip all processing")
    void shouldSkipProcessingOnReExecution() {
        // Given
        ProductOrder productOrder = ProductOrder.builder().build();
        StateContext<String, String> context = createStateContext(productOrder, true);
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, true);

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        verifyNoInteractions(productOfferingPriceService);
    }

    @Test
    @DisplayName("Given null product order, " +
            "when applying completion tasks, " +
            "then should set completion flag to false")
    void shouldHandleNullProductOrder() {
        // Given
        StateContext<String, String> context = createStateContext(null, true);

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .containsEntry(OrderCaptureConstants.ARE_COMPLETION_TASKS_SET, false)
                .hasSize(2);
    }

    @Test
    @DisplayName("Given empty product order items list, " +
            "when applying completion tasks, " +
            "then should set completion flag to false")
    void shouldHandleEmptyProductOrderItems() {
        // Given
        ProductOrder productOrder = ProductOrder.builder()
                .productOrderItem(Collections.emptyList())
                .build();
        StateContext<String, String> context = createStateContext(productOrder, true);

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .containsEntry(OrderCaptureConstants.ARE_COMPLETION_TASKS_SET, false);
    }

    @Test
    @DisplayName("Given null product order items list, " +
            "when applying completion tasks, " +
            "then should set completion flag to false")
    void shouldHandleNullProductOrderItems() {
        // Given
        ProductOrder productOrder = ProductOrder.builder()
                .productOrderItem(null)
                .build();
        StateContext<String, String> context = createStateContext(productOrder, true);

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .containsEntry(OrderCaptureConstants.ARE_COMPLETION_TASKS_SET, false);
    }

    @Test
    @DisplayName("Given items with negative prices, " +
            "when applying completion tasks, " +
            "then should exclude those items")
    void shouldExcludeItemsWithNegativePrices() {
        // Given
        ProductOrder productOrder = createProductOrderWithPrice(ITEM_1, NRC_PRICE_TYPE, NEGATIVE_PRICE, PRICE_ID_1);
        StateContext<String, String> context = createStateContext(productOrder, false);

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .containsEntry(OrderCaptureConstants.ARE_COMPLETION_TASKS_SET, true)
                .containsEntry(OrderCaptureConstants.IS_COMPLETE_ORDER_REQUIRED, false)
                .doesNotContainKeys(
                        OrderCaptureConstants.UNPAID_ORDER_ITEM_IDS,
                        OrderCaptureConstants.ORDER_ITEM_IDS_REQUIRING_BA_REF,
                        OrderCaptureConstants.ORDER_ITEM_IDS_REQUIRING_APPOINTMENT_REF
                );
    }

    @Test
    @DisplayName("Given items with zero prices, " +
            "when applying completion tasks, " +
            "then should exclude those items")
    void shouldExcludeItemsWithZeroPrices() {
        // Given
        ProductOrder productOrder = createProductOrderWithPrice(ITEM_1, NRC_PRICE_TYPE, ZERO_PRICE, PRICE_ID_1);
        StateContext<String, String> context = createStateContext(productOrder, false);

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .containsEntry(OrderCaptureConstants.IS_COMPLETE_ORDER_REQUIRED, false)
                .doesNotContainKey(OrderCaptureConstants.UNPAID_ORDER_ITEM_IDS);
    }

    @Test
    @DisplayName("Given items with null price, " +
            "when applying completion tasks, " +
            "then should exclude those items")
    void shouldExcludeItemsWithNullPrice() {
        // Given
        ProductOrder productOrder = ProductOrder.builder()
                .productOrderItem(List.of(
                        ProductOrderItem.builder()
                                .id(ITEM_1)
                                .itemPrice(List.of(
                                        OrderPrice.builder()
                                                .priceType(NRC_PRICE_TYPE)
                                                .price(null)
                                                .productOfferingPrice(ProductOfferingPriceRef.builder().id(PRICE_ID_1).build())
                                                .build()
                                ))
                                .build()
                ))
                .build();
        StateContext<String, String> context = createStateContext(productOrder, false);

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .containsEntry(OrderCaptureConstants.IS_COMPLETE_ORDER_REQUIRED, false);
    }

    @Test
    @DisplayName("Given items with null tax included amount, " +
            "when applying completion tasks, " +
            "then should exclude those items")
    void shouldExcludeItemsWithNullTaxIncludedAmount() {
        // Given
        ProductOrder productOrder = ProductOrder.builder()
                .productOrderItem(List.of(
                        ProductOrderItem.builder()
                                .id(ITEM_1)
                                .itemPrice(List.of(
                                        OrderPrice.builder()
                                                .priceType(NRC_PRICE_TYPE)
                                                .price(Price.builder().taxIncludedAmount(null).build())
                                                .productOfferingPrice(ProductOfferingPriceRef.builder().id(PRICE_ID_1).build())
                                                .build()
                                ))
                                .build()
                ))
                .build();
        StateContext<String, String> context = createStateContext(productOrder, false);

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .containsEntry(OrderCaptureConstants.IS_COMPLETE_ORDER_REQUIRED, false);
    }

    @Test
    @DisplayName("Given items with null tax included amount value, " +
            "when applying completion tasks, " +
            "then should exclude those items")
    void shouldExcludeItemsWithNullTaxIncludedAmountValue() {
        // Given
        ProductOrder productOrder = ProductOrder.builder()
                .productOrderItem(List.of(
                        ProductOrderItem.builder()
                                .id(ITEM_1)
                                .itemPrice(List.of(
                                        OrderPrice.builder()
                                                .priceType(NRC_PRICE_TYPE)
                                                .price(Price.builder()
                                                        .taxIncludedAmount(Money.builder().value(null).build())
                                                        .build())
                                                .productOfferingPrice(ProductOfferingPriceRef.builder().id(PRICE_ID_1).build())
                                                .build()
                                ))
                                .build()
                ))
                .build();
        StateContext<String, String> context = createStateContext(productOrder, false);

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .containsEntry(OrderCaptureConstants.IS_COMPLETE_ORDER_REQUIRED, false);
    }

    @Test
    @DisplayName("Given recurring charge item, " +
            "when applying completion tasks, " +
            "then should require billing account")
    void shouldRequireBillingAccountForRecurringCharge() {
        // Given
        ProductOrder productOrder = createRecurringChargeOrder(ITEM_1, PRICE_ID_1);
        StateContext<String, String> context = createStateContext(productOrder, false);

        when(productOfferingPriceService.fetchProductOfferingPrices(anyList()))
                .thenReturn(List.of(createProductOfferingPrice(PRICE_ID_1, false)));

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .containsEntry(OrderCaptureConstants.ORDER_ITEM_IDS_REQUIRING_BA_REF, List.of(ITEM_1))
                .containsEntry(OrderCaptureConstants.IS_COMPLETE_ORDER_REQUIRED, true)
                .containsEntry(OrderCaptureConstants.ARE_COMPLETION_TASKS_SET, true);
    }

    @Test
    @DisplayName("Given non-recurring charge without immediate payment, " +
            "when applying completion tasks, " +
            "then should require billing account")
    void shouldRequireBillingAccountForNonRecurringWithoutImmediatePayment() {
        // Given
        ProductOrder productOrder = createProductOrderWithPrice(ITEM_1, NRC_PRICE_TYPE, VALID_PRICE, PRICE_ID_1);
        StateContext<String, String> context = createStateContext(productOrder, false);

        when(productOfferingPriceService.fetchProductOfferingPrices(anyList()))
                .thenReturn(List.of(createProductOfferingPrice(PRICE_ID_1, false)));

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .containsEntry(OrderCaptureConstants.ORDER_ITEM_IDS_REQUIRING_BA_REF, List.of(ITEM_1))
                .containsEntry(OrderCaptureConstants.IS_COMPLETE_ORDER_REQUIRED, true);
    }

    @Test
    @DisplayName("Given multiple recurring charge items, " +
            "when applying completion tasks, " +
            "then should require billing account for all")
    void shouldRequireBillingAccountForMultipleRecurringCharges() {
        // Given
        ProductOrder productOrder = ProductOrder.builder()
                .productOrderItem(List.of(
                        createRecurringOrderItem(ITEM_1, PRICE_ID_1),
                        createRecurringOrderItem(ITEM_2, PRICE_ID_2)
                ))
                .build();
        StateContext<String, String> context = createStateContext(productOrder, false);

        when(productOfferingPriceService.fetchProductOfferingPrices(anyList()))
                .thenReturn(List.of(
                        createProductOfferingPrice(PRICE_ID_1, false),
                        createProductOfferingPrice(PRICE_ID_2, false)
                ));

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .containsEntry(OrderCaptureConstants.ORDER_ITEM_IDS_REQUIRING_BA_REF, List.of(ITEM_1, ITEM_2))
                .containsEntry(OrderCaptureConstants.IS_COMPLETE_ORDER_REQUIRED, true);
    }

    @Test
    @DisplayName("Given item with any price requiring BA, " +
            "when applying completion tasks, " +
            "then should include that item")
    void shouldRequireBillingAccountWhenAnyPriceRequiresIt() {
        // Given
        ProductOrder productOrder = ProductOrder.builder()
                .productOrderItem(List.of(
                        ProductOrderItem.builder()
                                .id(ITEM_1)
                                .itemPrice(List.of(
                                        createOrderPrice(NRC_PRICE_TYPE, VALID_PRICE, PRICE_ID_1),
                                        createOrderPrice(RC_PRICE_TYPE, VALID_PRICE, PRICE_ID_2)
                                ))
                                .build()
                ))
                .build();
        StateContext<String, String> context = createStateContext(productOrder, false);

        when(productOfferingPriceService.fetchProductOfferingPrices(anyList()))
                .thenReturn(List.of(
                        createProductOfferingPrice(PRICE_ID_1, true),
                        createProductOfferingPrice(PRICE_ID_2, false)
                ));

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .containsEntry(OrderCaptureConstants.ORDER_ITEM_IDS_REQUIRING_BA_REF, List.of(ITEM_1));
    }

    @Test
    @DisplayName("Given recurring item without valid price, " +
            "when applying completion tasks, " +
            "then should not require billing account")
    void shouldNotRequireBillingAccountForRecurringWithoutValidPrice() {
        // Given
        ProductOrder productOrder = ProductOrder.builder()
                .productOrderItem(List.of(
                        ProductOrderItem.builder()
                                .id(ITEM_1)
                                .itemPrice(List.of(
                                        OrderPrice.builder()
                                                .priceType(RC_PRICE_TYPE)
                                                .price(null)
                                                .recurringChargePeriod(Quantity.builder().amount(1.0f).units("month").build())
                                                .productOfferingPrice(ProductOfferingPriceRef.builder().id(PRICE_ID_1).build())
                                                .build()
                                ))
                                .build()
                ))
                .build();
        StateContext<String, String> context = createStateContext(productOrder, false);

        when(productOfferingPriceService.fetchProductOfferingPrices(anyList()))
                .thenReturn(List.of(createProductOfferingPrice(PRICE_ID_1, false)));

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .doesNotContainKey(OrderCaptureConstants.ORDER_ITEM_IDS_REQUIRING_BA_REF)
                .containsEntry(OrderCaptureConstants.IS_COMPLETE_ORDER_REQUIRED, false);
    }

    @Test
    @DisplayName("Given non-recurring charge with immediate payment, " +
            "when applying completion tasks, " +
            "then should require immediate payment")
    void shouldRequireImmediatePaymentForNonRecurringWithImmediatePayment() {
        // Given
        ProductOrder productOrder = createProductOrderWithPrice(ITEM_1, NRC_PRICE_TYPE, VALID_PRICE, PRICE_ID_1);
        StateContext<String, String> context = createStateContext(productOrder, false);

        when(productOfferingPriceService.fetchProductOfferingPrices(anyList()))
                .thenReturn(List.of(createProductOfferingPrice(PRICE_ID_1, true)));

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .containsEntry(OrderCaptureConstants.UNPAID_ORDER_ITEM_IDS, List.of(ITEM_1))
                .containsEntry(OrderCaptureConstants.IS_COMPLETE_ORDER_REQUIRED, true)
                .containsEntry(OrderCaptureConstants.ARE_COMPLETION_TASKS_SET, true);
    }

    @Test
    @DisplayName("Given multiple items with immediate payment, " +
            "when applying completion tasks, " +
            "then should require payment for all")
    void shouldRequireImmediatePaymentForMultipleItems() {
        // Given
        ProductOrder productOrder = ProductOrder.builder()
                .productOrderItem(List.of(
                        createOrderItem(ITEM_1, NRC_PRICE_TYPE, VALID_PRICE, PRICE_ID_1),
                        createOrderItem(ITEM_2, NRC_PRICE_TYPE, VALID_PRICE, PRICE_ID_2)
                ))
                .build();
        StateContext<String, String> context = createStateContext(productOrder, false);

        when(productOfferingPriceService.fetchProductOfferingPrices(anyList()))
                .thenReturn(List.of(
                        createProductOfferingPrice(PRICE_ID_1, true),
                        createProductOfferingPrice(PRICE_ID_2, true)
                ));

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .containsEntry(OrderCaptureConstants.UNPAID_ORDER_ITEM_IDS, List.of(ITEM_1, ITEM_2))
                .containsEntry(OrderCaptureConstants.IS_COMPLETE_ORDER_REQUIRED, true);
    }

    @Test
    @DisplayName("Given item with all prices requiring immediate payment, " +
            "when applying completion tasks, " +
            "then should include that item")
    void shouldRequireImmediatePaymentWhenAllPricesRequireIt() {
        // Given
        ProductOrder productOrder = ProductOrder.builder()
                .productOrderItem(List.of(
                        ProductOrderItem.builder()
                                .id(ITEM_1)
                                .itemPrice(List.of(
                                        createOrderPrice(NRC_PRICE_TYPE, VALID_PRICE, PRICE_ID_1),
                                        createOrderPrice(NRC_PRICE_TYPE, VALID_PRICE, PRICE_ID_2)
                                ))
                                .build()
                ))
                .build();
        StateContext<String, String> context = createStateContext(productOrder, false);

        when(productOfferingPriceService.fetchProductOfferingPrices(anyList()))
                .thenReturn(List.of(
                        createProductOfferingPrice(PRICE_ID_1, true),
                        createProductOfferingPrice(PRICE_ID_2, true)
                ));

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .containsEntry(OrderCaptureConstants.UNPAID_ORDER_ITEM_IDS, List.of(ITEM_1));
    }

    @Test
    @DisplayName("Given item with mixed payment flags, " +
            "when applying completion tasks, " +
            "then should not include in unpaid items but require BA")
    void shouldNotRequireImmediatePaymentWhenNotAllPricesRequireIt() {
        // Given
        ProductOrder productOrder = ProductOrder.builder()
                .productOrderItem(List.of(
                        ProductOrderItem.builder()
                                .id(ITEM_1)
                                .itemPrice(List.of(
                                        createOrderPrice(NRC_PRICE_TYPE, VALID_PRICE, PRICE_ID_1),
                                        createOrderPrice(NRC_PRICE_TYPE, VALID_PRICE, PRICE_ID_2)
                                ))
                                .build()
                ))
                .build();
        StateContext<String, String> context = createStateContext(productOrder, false);

        when(productOfferingPriceService.fetchProductOfferingPrices(anyList()))
                .thenReturn(List.of(
                        createProductOfferingPrice(PRICE_ID_1, true),
                        createProductOfferingPrice(PRICE_ID_2, false)
                ));

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .doesNotContainKey(OrderCaptureConstants.UNPAID_ORDER_ITEM_IDS)
                .containsEntry(OrderCaptureConstants.ORDER_ITEM_IDS_REQUIRING_BA_REF, List.of(ITEM_1))
                .containsEntry(OrderCaptureConstants.IS_COMPLETE_ORDER_REQUIRED, true);
    }

    @Test
    @DisplayName("Given item without product offering price, " +
            "when applying completion tasks, " +
            "then should not require immediate payment")
    void shouldNotRequireImmediatePaymentWithoutProductOfferingPrice() {
        // Given
        ProductOrder productOrder = ProductOrder.builder()
                .productOrderItem(List.of(
                        ProductOrderItem.builder()
                                .id(ITEM_1)
                                .itemPrice(List.of(
                                        OrderPrice.builder()
                                                .priceType(NRC_PRICE_TYPE)
                                                .price(createPrice(VALID_PRICE))
                                                .productOfferingPrice(null)
                                                .build()
                                ))
                                .build()
                ))
                .build();
        StateContext<String, String> context = createStateContext(productOrder, false);

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .doesNotContainKey(OrderCaptureConstants.UNPAID_ORDER_ITEM_IDS)
                .containsEntry(OrderCaptureConstants.IS_COMPLETE_ORDER_REQUIRED, false);
    }

    @Test
    @DisplayName("Given item without price reference, " +
            "when applying completion tasks, " +
            "then should not require immediate payment")
    void shouldNotRequireImmediatePaymentWithoutPriceReference() {
        // Given
        ProductOrder productOrder = ProductOrder.builder()
                .productOrderItem(List.of(
                        ProductOrderItem.builder()
                                .id(ITEM_1)
                                .itemPrice(List.of(
                                        OrderPrice.builder()
                                                .priceType(NRC_PRICE_TYPE)
                                                .price(null)
                                                .productOfferingPrice(ProductOfferingPriceRef.builder().id(PRICE_ID_1).build())
                                                .build()
                                ))
                                .build()
                ))
                .build();
        StateContext<String, String> context = createStateContext(productOrder, false);

        when(productOfferingPriceService.fetchProductOfferingPrices(anyList()))
                .thenReturn(List.of(createProductOfferingPrice(PRICE_ID_1, true)));

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .doesNotContainKey(OrderCaptureConstants.UNPAID_ORDER_ITEM_IDS);
    }

    @Test
    @DisplayName("Given item with address characteristic and appointment required, " +
            "when applying completion tasks, " +
            "then should require appointment")
    void shouldRequireAppointmentWhenAddressCharacteristicPresentAndFlagIsTrue() {
        // Given
        ProductOrder productOrder = createProductOrderWithAddressCharacteristic(ITEM_1);
        StateContext<String, String> context = createStateContext(productOrder, true);

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .containsEntry(OrderCaptureConstants.ORDER_ITEM_IDS_REQUIRING_APPOINTMENT_REF, List.of(ITEM_1))
                .containsEntry(OrderCaptureConstants.IS_COMPLETE_ORDER_REQUIRED, true)
                .containsEntry(OrderCaptureConstants.ARE_COMPLETION_TASKS_SET, true);
    }

    @Test
    @DisplayName("Given item with address characteristic but appointment not required, " +
            "when applying completion tasks, " +
            "then should not require appointment")
    void shouldNotRequireAppointmentWhenFlagIsFalse() {
        // Given
        ProductOrder productOrder = createProductOrderWithAddressCharacteristic(ITEM_1);
        StateContext<String, String> context = createStateContext(productOrder, false);

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .doesNotContainKey(OrderCaptureConstants.ORDER_ITEM_IDS_REQUIRING_APPOINTMENT_REF)
                .containsEntry(OrderCaptureConstants.IS_COMPLETE_ORDER_REQUIRED, false)
                .containsEntry(OrderCaptureConstants.ARE_COMPLETION_TASKS_SET, true);
    }

    @Test
    @DisplayName("Given item without product, " +
            "when applying completion tasks, " +
            "then should not require appointment")
    void shouldNotRequireAppointmentWhenProductIsNull() {
        // Given
        ProductOrder productOrder = ProductOrder.builder()
                .productOrderItem(List.of(
                        ProductOrderItem.builder()
                                .id(ITEM_1)
                                .product(null)
                                .build()
                ))
                .build();
        StateContext<String, String> context = createStateContext(productOrder, true);

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .doesNotContainKey(OrderCaptureConstants.ORDER_ITEM_IDS_REQUIRING_APPOINTMENT_REF)
                .containsEntry(OrderCaptureConstants.IS_COMPLETE_ORDER_REQUIRED, false);
    }

    @Test
    @DisplayName("Given item with product but no characteristics, " +
            "when applying completion tasks, " +
            "then should not require appointment")
    void shouldNotRequireAppointmentWhenNoCharacteristics() {
        // Given
        ProductOrder productOrder = ProductOrder.builder()
                .productOrderItem(List.of(
                        ProductOrderItem.builder()
                                .id(ITEM_1)
                                .product(Product.builder()
                                        .productCharacteristic(null)
                                        .build())
                                .build()
                ))
                .build();
        StateContext<String, String> context = createStateContext(productOrder, true);

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .doesNotContainKey(OrderCaptureConstants.ORDER_ITEM_IDS_REQUIRING_APPOINTMENT_REF);
    }

    @Test
    @DisplayName("Given item with empty characteristics list, " +
            "when applying completion tasks, " +
            "then should not require appointment")
    void shouldNotRequireAppointmentWhenEmptyCharacteristics() {
        // Given
        ProductOrder productOrder = ProductOrder.builder()
                .productOrderItem(List.of(
                        ProductOrderItem.builder()
                                .id(ITEM_1)
                                .product(Product.builder()
                                        .productCharacteristic(Collections.emptyList())
                                        .build())
                                .build()
                ))
                .build();
        StateContext<String, String> context = createStateContext(productOrder, true);

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .doesNotContainKey(OrderCaptureConstants.ORDER_ITEM_IDS_REQUIRING_APPOINTMENT_REF);
    }

    @Test
    @DisplayName("Given item with ProductRefOrValue that is not Product instance, " +
            "when applying completion tasks, " +
            "then should not require appointment")
    void shouldNotRequireAppointmentWhenProductRefOrValueIsNotProductInstance() {
        // Given
        ProductRefOrValue productRef = mock(ProductRefOrValue.class);
        ProductOrder productOrder = ProductOrder.builder()
                .productOrderItem(List.of(
                        ProductOrderItem.builder()
                                .id(ITEM_1)
                                .product(productRef)
                                .build()
                ))
                .build();
        StateContext<String, String> context = createStateContext(productOrder, true);

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .doesNotContainKey(OrderCaptureConstants.ORDER_ITEM_IDS_REQUIRING_APPOINTMENT_REF);
    }

    @Test
    @DisplayName("Given multiple items with address characteristics, " +
            "when applying completion tasks, " +
            "then should require appointment for all")
    void shouldRequireAppointmentForMultipleItems() {
        // Given
        ProductOrder productOrder = ProductOrder.builder()
                .productOrderItem(List.of(
                        createOrderItemWithAddress(ITEM_1),
                        createOrderItemWithAddress(ITEM_2),
                        createOrderItemWithAddress(ITEM_3)
                ))
                .build();
        StateContext<String, String> context = createStateContext(productOrder, true);

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .containsEntry(OrderCaptureConstants.ORDER_ITEM_IDS_REQUIRING_APPOINTMENT_REF, List.of(ITEM_1, ITEM_2, ITEM_3));
    }

    @Test
    @DisplayName("Given null order item, " +
            "when applying completion tasks, " +
            "then should not require appointment")
    void shouldNotRequireAppointmentWhenItemIsNull() {
        // Given
        ProductOrder productOrder = ProductOrder.builder()
                .productOrderItem(Collections.emptyList())
                .build();
        StateContext<String, String> context = createStateContext(productOrder, true);

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .doesNotContainKey(OrderCaptureConstants.ORDER_ITEM_IDS_REQUIRING_APPOINTMENT_REF);
    }

    @Test
    @DisplayName("Given items with null item prices list, " +
            "when applying completion tasks, " +
            "then should handle gracefully")
    void shouldHandleNullItemPricesList() {
        // Given
        ProductOrder productOrder = ProductOrder.builder()
                .productOrderItem(List.of(
                        ProductOrderItem.builder()
                                .id(ITEM_1)
                                .itemPrice(null)
                                .build()
                ))
                .build();
        StateContext<String, String> context = createStateContext(productOrder, false);

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .containsEntry(OrderCaptureConstants.ARE_COMPLETION_TASKS_SET, true)
                .containsEntry(OrderCaptureConstants.IS_COMPLETE_ORDER_REQUIRED, false);
        verifyNoInteractions(productOfferingPriceService);
    }

    @Test
    @DisplayName("Given items with empty item prices list, " +
            "when applying completion tasks, " +
            "then should handle gracefully")
    void shouldHandleEmptyItemPricesList() {
        // Given
        ProductOrder productOrder = ProductOrder.builder()
                .productOrderItem(List.of(
                        ProductOrderItem.builder()
                                .id(ITEM_1)
                                .itemPrice(Collections.emptyList())
                                .build()
                ))
                .build();
        StateContext<String, String> context = createStateContext(productOrder, false);

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .containsEntry(OrderCaptureConstants.ARE_COMPLETION_TASKS_SET, true)
                .containsEntry(OrderCaptureConstants.IS_COMPLETE_ORDER_REQUIRED, false);
        verifyNoInteractions(productOfferingPriceService);
    }

    @Test
    @DisplayName("Given items without product offering price reference, " +
            "when applying completion tasks, " +
            "then should skip price fetching")
    void shouldSkipPriceFetchingWhenNoProductOfferingPriceReference() {
        // Given
        ProductOrder productOrder = ProductOrder.builder()
                .productOrderItem(List.of(
                        ProductOrderItem.builder()
                                .id(ITEM_1)
                                .itemPrice(List.of(
                                        OrderPrice.builder()
                                                .priceType(NRC_PRICE_TYPE)
                                                .price(createPrice(VALID_PRICE))
                                                .productOfferingPrice(null)
                                                .build()
                                ))
                                .build()
                ))
                .build();
        StateContext<String, String> context = createStateContext(productOrder, false);

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        verify(productOfferingPriceService, times(1)).fetchProductOfferingPrices(Collections.emptyList());
    }

    @Test
    @DisplayName("Given items with product offering prices, " +
            "when applying completion tasks, " +
            "then should fetch prices from service")
    void shouldFetchProductOfferingPricesFromService() {
        // Given
        ProductOrder productOrder = createProductOrderWithPrice(ITEM_1, NRC_PRICE_TYPE, VALID_PRICE, PRICE_ID_1);
        StateContext<String, String> context = createStateContext(productOrder, false);

        when(productOfferingPriceService.fetchProductOfferingPrices(List.of(PRICE_ID_1)))
                .thenReturn(List.of(createProductOfferingPrice(PRICE_ID_1, true)));

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        verify(productOfferingPriceService, times(1)).fetchProductOfferingPrices(List.of(PRICE_ID_1));
        assertThat(context.getExtendedState().getVariables())
                .containsEntry(OrderCaptureConstants.UNPAID_ORDER_ITEM_IDS, List.of(ITEM_1))
                .containsEntry(OrderCaptureConstants.ARE_COMPLETION_TASKS_SET, true);
    }

    @Test
    @DisplayName("Given items with duplicate price IDs, " +
            "when applying completion tasks, " +
            "then should fetch distinct prices only")
    void shouldFetchDistinctPricesOnly() {
        // Given
        ProductOrder productOrder = ProductOrder.builder()
                .productOrderItem(List.of(
                        createOrderItem(ITEM_1, NRC_PRICE_TYPE, VALID_PRICE, PRICE_ID_1),
                        createOrderItem(ITEM_2, NRC_PRICE_TYPE, VALID_PRICE, PRICE_ID_1)
                ))
                .build();
        StateContext<String, String> context = createStateContext(productOrder, false);

        when(productOfferingPriceService.fetchProductOfferingPrices(anyList()))
                .thenReturn(List.of(createProductOfferingPrice(PRICE_ID_1, true)));

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        verify(productOfferingPriceService, times(1)).fetchProductOfferingPrices(List.of(PRICE_ID_1));
    }

    @Test
    @DisplayName("Given items with multiple distinct price IDs, " +
            "when applying completion tasks, " +
            "then should fetch all distinct prices")
    void shouldFetchAllDistinctPrices() {
        // Given
        ProductOrder productOrder = ProductOrder.builder()
                .productOrderItem(List.of(
                        createOrderItem(ITEM_1, NRC_PRICE_TYPE, VALID_PRICE, PRICE_ID_1),
                        createOrderItem(ITEM_2, NRC_PRICE_TYPE, VALID_PRICE, PRICE_ID_2),
                        createOrderItem(ITEM_3, NRC_PRICE_TYPE, VALID_PRICE, PRICE_ID_3)
                ))
                .build();
        StateContext<String, String> context = createStateContext(productOrder, false);

        when(productOfferingPriceService.fetchProductOfferingPrices(anyList()))
                .thenReturn(List.of(
                        createProductOfferingPrice(PRICE_ID_1, true),
                        createProductOfferingPrice(PRICE_ID_2, false),
                        createProductOfferingPrice(PRICE_ID_3, true)
                ));

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        verify(productOfferingPriceService, times(1)).fetchProductOfferingPrices(anyList());
    }

    @Test
    @DisplayName("Given items with all three requirements, " +
            "when applying completion tasks, " +
            "then should set all requirement lists")
    void shouldSetAllRequirementsForComplexScenario() {
        // Given
        ProductOrder productOrder = ProductOrder.builder()
                .productOrderItem(List.of(
                        createOrderItem(ITEM_1, NRC_PRICE_TYPE, VALID_PRICE, PRICE_ID_1),
                        createRecurringOrderItem(ITEM_2, PRICE_ID_2),
                        createOrderItemWithAddress(ITEM_3)
                ))
                .build();

        when(productOfferingPriceService.fetchProductOfferingPrices(anyList()))
                .thenReturn(List.of(
                        createProductOfferingPrice(PRICE_ID_1, true),
                        createProductOfferingPrice(PRICE_ID_2, false)
                ));

        StateContext<String, String> context = createStateContext(productOrder, true);

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .containsEntry(OrderCaptureConstants.UNPAID_ORDER_ITEM_IDS, List.of(ITEM_1))
                .containsEntry(OrderCaptureConstants.ORDER_ITEM_IDS_REQUIRING_BA_REF, List.of(ITEM_2))
                .containsEntry(OrderCaptureConstants.ORDER_ITEM_IDS_REQUIRING_APPOINTMENT_REF, List.of(ITEM_3))
                .containsEntry(OrderCaptureConstants.IS_COMPLETE_ORDER_REQUIRED, true)
                .containsEntry(OrderCaptureConstants.ARE_COMPLETION_TASKS_SET, true);
    }

    @Test
    @DisplayName("Given items without any requirements, " +
            "when applying completion tasks, " +
            "then should not require order completion")
    void shouldNotRequireCompletionWhenNoRequirements() {
        // Given
        ProductOrder productOrder = ProductOrder.builder()
                .productOrderItem(List.of(
                        ProductOrderItem.builder()
                                .id(ITEM_1)
                                .product(Product.builder().build())
                                .build()
                ))
                .build();
        StateContext<String, String> context = createStateContext(productOrder, false);

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .containsEntry(OrderCaptureConstants.IS_COMPLETE_ORDER_REQUIRED, false)
                .containsEntry(OrderCaptureConstants.ARE_COMPLETION_TASKS_SET, true)
                .doesNotContainKeys(
                        OrderCaptureConstants.UNPAID_ORDER_ITEM_IDS,
                        OrderCaptureConstants.ORDER_ITEM_IDS_REQUIRING_BA_REF,
                        OrderCaptureConstants.ORDER_ITEM_IDS_REQUIRING_APPOINTMENT_REF
                );
    }

    @Test
    @DisplayName("Given single item with multiple requirements, " +
            "when applying completion tasks, " +
            "then should include item in all relevant lists")
    void shouldIncludeItemInAllRelevantLists() {
        // Given
        ProductOrder productOrder = ProductOrder.builder()
                .productOrderItem(List.of(
                        ProductOrderItem.builder()
                                .id(ITEM_1)
                                .itemPrice(List.of(
                                        createOrderPrice(NRC_PRICE_TYPE, VALID_PRICE, PRICE_ID_1),
                                        createOrderPrice(RC_PRICE_TYPE, VALID_PRICE, PRICE_ID_2)
                                ))
                                .product(Product.builder()
                                        .productCharacteristic(List.of(AddressCharacteristic.builder().build()))
                                        .build())
                                .build()
                ))
                .build();

        when(productOfferingPriceService.fetchProductOfferingPrices(anyList()))
                .thenReturn(List.of(
                        createProductOfferingPrice(PRICE_ID_1, true),
                        createProductOfferingPrice(PRICE_ID_2, false)
                ));

        StateContext<String, String> context = createStateContext(productOrder, true);

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .containsEntry(OrderCaptureConstants.ORDER_ITEM_IDS_REQUIRING_BA_REF, List.of(ITEM_1))
                .containsEntry(OrderCaptureConstants.ORDER_ITEM_IDS_REQUIRING_APPOINTMENT_REF, List.of(ITEM_1))
                .containsEntry(OrderCaptureConstants.IS_COMPLETE_ORDER_REQUIRED, true);
    }

    @Test
    @DisplayName("Given four items with different requirement combinations, " +
            "when applying completion tasks, " +
            "then should categorize correctly")
    void shouldCategorizeFourItemsCorrectly() {
        // Given
        ProductOrder productOrder = ProductOrder.builder()
                .productOrderItem(List.of(
                        createOrderItem(ITEM_1, NRC_PRICE_TYPE, VALID_PRICE, PRICE_ID_1),
                        createRecurringOrderItem(ITEM_2, PRICE_ID_2),
                        createOrderItemWithAddress(ITEM_3),
                        ProductOrderItem.builder()
                                .id(ITEM_4)
                                .product(Product.builder().build())
                                .build()
                ))
                .build();

        when(productOfferingPriceService.fetchProductOfferingPrices(anyList()))
                .thenReturn(List.of(
                        createProductOfferingPrice(PRICE_ID_1, true),
                        createProductOfferingPrice(PRICE_ID_2, false)
                ));

        StateContext<String, String> context = createStateContext(productOrder, true);

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .containsEntry(OrderCaptureConstants.UNPAID_ORDER_ITEM_IDS, List.of(ITEM_1))
                .containsEntry(OrderCaptureConstants.ORDER_ITEM_IDS_REQUIRING_BA_REF, List.of(ITEM_2))
                .containsEntry(OrderCaptureConstants.ORDER_ITEM_IDS_REQUIRING_APPOINTMENT_REF, List.of(ITEM_3))
                .containsEntry(OrderCaptureConstants.IS_COMPLETE_ORDER_REQUIRED, true);
    }

    @Test
    @DisplayName("Given exception during price fetching, " +
            "when applying completion tasks, " +
            "then should set completion flag to false")
    void shouldHandleExceptionDuringPriceFetching() {
        // Given
        ProductOrder productOrder = createProductOrderWithPrice(ITEM_1, NRC_PRICE_TYPE, VALID_PRICE, PRICE_ID_1);
        StateContext<String, String> context = createStateContext(productOrder, false);

        when(productOfferingPriceService.fetchProductOfferingPrices(anyList()))
                .thenThrow(new RuntimeException("Service unavailable"));

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .containsEntry(OrderCaptureConstants.ARE_COMPLETION_TASKS_SET, false);
    }

    @Test
    @DisplayName("Given NullPointerException during processing, " +
            "when applying completion tasks, " +
            "then should set completion flag to false")
    void shouldHandleNullPointerException() {
        // Given
        ProductOrder productOrder = createProductOrderWithPrice(ITEM_1, NRC_PRICE_TYPE, VALID_PRICE, PRICE_ID_1);
        StateContext<String, String> context = createStateContext(productOrder, false);

        when(productOfferingPriceService.fetchProductOfferingPrices(anyList()))
                .thenThrow(new NullPointerException("Null value encountered"));

        // When
        Mono<Void> result = completionTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertThat(context.getExtendedState().getVariables())
                .containsEntry(OrderCaptureConstants.ARE_COMPLETION_TASKS_SET, false);
    }

    private DefaultStateContext<String, String> createStateContext(ProductOrder productOrder, boolean isAppointmentRequired) {
        ExtendedState extendedState = new DefaultExtendedState();
        if (productOrder != null) {
            extendedState.getVariables().put(OrderCaptureConstants.CREATED_PRODUCT_ORDER, productOrder);
        }
        extendedState.getVariables().put(OrderCaptureConstants.IS_APPOINTMENT_REQUIRED, isAppointmentRequired);
        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, null, null, extendedState, null, null, null, null, null);
    }

    private ProductOrder createProductOrderWithPrice(String itemId, String priceType, float priceValue, String priceId) {
        return ProductOrder.builder()
                .productOrderItem(List.of(createOrderItem(itemId, priceType, priceValue, priceId)))
                .build();
    }

    private ProductOrderItem createOrderItem(String itemId, String priceType, float priceValue, String priceId) {
        return ProductOrderItem.builder()
                .id(itemId)
                .itemPrice(List.of(createOrderPrice(priceType, priceValue, priceId)))
                .build();
    }

    private OrderPrice createOrderPrice(String priceType, float priceValue, String priceId) {
        return OrderPrice.builder()
                .priceType(priceType)
                .price(createPrice(priceValue))
                .productOfferingPrice(ProductOfferingPriceCharge.builder().id(priceId)
                        .immediatePayment(false).build())
                .build();
    }

    private ProductOrder createRecurringChargeOrder(String itemId, String priceId) {
        return ProductOrder.builder()
                .productOrderItem(List.of(createRecurringOrderItem(itemId, priceId)))
                .build();
    }

    private ProductOrderItem createRecurringOrderItem(String itemId, String priceId) {
        return ProductOrderItem.builder()
                .id(itemId)
                .itemPrice(List.of(
                        OrderPrice.builder()
                                .priceType(RC_PRICE_TYPE)
                                .price(createPrice(VALID_PRICE))
                                .recurringChargePeriod(Quantity.builder().amount(1.0f).units("month").build())
                                .productOfferingPrice(ProductOfferingPriceCharge.builder().id(priceId).build())
                                .build(), OrderPrice.builder()
                                .priceType(RC_PRICE_TYPE)
                                .price(createPrice(VALID_PRICE))
                                .recurringChargePeriod(Quantity.builder().amount(1.0f).units("month").build())
                                .productOfferingPrice(InstallmentCharge.builder().id(priceId).build())
                                .build()))
                .build();
    }

    private ProductOrder createProductOrderWithAddressCharacteristic(String itemId) {
        return ProductOrder.builder()
                .productOrderItem(List.of(createOrderItemWithAddress(itemId)))
                .build();
    }

    private ProductOrderItem createOrderItemWithAddress(String itemId) {
        return ProductOrderItem.builder()
                .id(itemId)
                .product(Product.builder()
                        .productCharacteristic(List.of(AddressCharacteristic.builder().build()))
                        .build())
                .build();
    }

    private Price createPrice(float value) {
        return Price.builder()
                .taxIncludedAmount(Money.builder()
                        .value(value)
                        .unit(CURRENCY_EUR)
                        .build())
                .build();
    }

    private ProductOfferingPrice createProductOfferingPrice(String id, boolean immediatePayment) {
        return ProductOfferingPrice.builder()
                .id(id)
                .immediatePayment(immediatePayment)
                .build();
    }
}