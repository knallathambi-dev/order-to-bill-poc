// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.action;

import com.orange.discobole.ordermanagement.commons.dto.product.configuration.*;
import com.orange.discobole.ordermanagement.commons.dto.product.offering.ProductOffering;
import com.orange.discobole.ordermanagement.commons.dto.product.offering.price.Money;
import com.orange.discobole.ordermanagement.commons.dto.product.offering.price.ProductOfferingPrice;
import com.orange.discobole.ordermanagement.commons.dto.product.specification.ProductSpecification;
import com.orange.discobole.ordermanagement.commons.dto.product.specification.ProductSpecificationRelationship;
import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants;
import com.orange.discobole.ordermanagement.ordercapture.domain.SettingsEntity;
import com.orange.discobole.ordermanagement.ordercapture.enums.ConfigurationState;
import com.orange.discobole.ordermanagement.ordercapture.service.*;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderItem;
import com.orange.discobole.processflow.dto.generated.ChannelRef;
import com.orange.discobole.processflow.dto.generated.RelatedEntity;
import com.orange.discobole.processflow.dto.generated.RelatedParty;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.productinventory.dto.v1.BillingAccountRef;
import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.dto.v1.ProductRef;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.ObjectStateMachine;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.state.ObjectState;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.DefaultExtendedState;
import org.springframework.statemachine.support.DefaultStateContext;
import org.springframework.statemachine.transition.DefaultExternalTransition;
import org.springframework.statemachine.transition.Transition;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Stream;

import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.BUNDLES;
import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.RELIES_ON;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderInstantiationActionTest {
    private static final float AMOUNT = 10.0f;
    private static final String ATOMIC_PRODUCT_OFFERING_ID_1 = RandomStringUtils.randomAlphabetic(10);
    private static final String ATOMIC_PRODUCT_OFFERING_ID_2 = RandomStringUtils.randomAlphabetic(10);
    private static final String ATOMIC_PRODUCT_OFFERING_ID_3 = RandomStringUtils.randomAlphabetic(10);
    private static final String BILLING_ID = RandomStringUtils.randomAlphabetic(5);
    private static final String BUNDLE_PRODUCT_OFFERING_ID_1 = RandomStringUtils.randomAlphabetic(10);
    private static final String BUNDLE_PRODUCT_OFFERING_ID_2 = RandomStringUtils.randomAlphabetic(10);
    private static final String CHANNEL_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String CHANNEL_NAME = "Web";
    private static final String CONTRACT_PRODUCT_OFFERING_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String DEFAULT_PRODUCT_NAME = RandomStringUtils.randomAlphabetic(5);
    private static final String DEFAULT_PRODUCT_OFFERING_NAME = RandomStringUtils.randomAlphabetic(5);
    private static final float DUTY_FREE_AMOUNT_VALUE = 10f;
    private static final float PRODUCT_OFFERING_PRICE_AMOUNT_VALUE = 13f;
    private static final String HYBRID = "hybrid";
    private static final String NON_RECURRING_DISCOUNT = "nonRecurringDiscount";
    private static final String NRC_PRICE_TYPE = "nonRecurringCharge";
    private static final OffsetDateTime OFFSET_DATE_TIME = OffsetDateTime.parse("2100-01-01T23:20:50.520Z");
    private static final float PERCENTAGE_FOR_ALTERATION = 30.0f;
    private static final String POST_PAID = "postpaid";
    private static final String PRICE_UNIT = "Euro";
    private static final float PRICE_VALUE = 3f;
    private static final String PRODUCT_ID = RandomStringUtils.randomAlphabetic(5);
    private static final String PRODUCT_ID_1 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ID_2 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ID_3 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ID_4 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ID_5 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ID_6 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ID_7 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_OFFERING_ID = RandomStringUtils.randomAlphabetic(5);
    private static final String PRODUCT_OFFERING_PRICE_ID_1 = RandomStringUtils.randomAlphabetic(5);
    private static final String PRODUCT_OFFERING_PRICE_ID_2 = RandomStringUtils.randomAlphabetic(5);
    private static final String PRODUCT_OFFERING_TYPE = "productOffering";
    private static final String PRODUCT_ORDER_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_TYPE = "Product";
    private static final String RC_PRICE_TYPE = "recurringCharge";
    private static final String RECURRING_DISCOUNT = "recurringDiscount";
    private static final String RELATED_PARTY_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String RELATED_PARTY_ROLE = "customer";
    private static final float TAX_INCLUDED_AMOUNT_VALUE = 10f;
    private static final String UNIT = "EUR";
    private static final String UNITS = "month";

    @InjectMocks
    private OrderInstantiationAction orderInstantiationAction;

    @Mock
    private ProductConfigurationService configurationService;
    @Mock
    private ProductOrderService productOrderService;
    @Mock
    private ProductSpecificationService productSpecificationService;
    @Mock
    private SettingsService settingsService;
    @Mock
    private ProductOfferingService productOfferingService;
    @Mock
    private ProductInventoryService productInventoryService;
    @Mock
    private ProductOfferingPriceService productOfferingPriceService;
    @Mock
    private BillingCycleService billingCycleService;

    @Test
    @DisplayName("Given re-executed order instantiation action, " +
            "when apply is called, " +
            "then product order is not instantiated")
    void shouldNotInstantiateOrderWhenReExecuted() {
        // Given
        StateContext<String, String> context = createStateContext(ConfigurationState.VALIDATED, false, OrderCaptureConstants.ADD);
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, true);

        // When
        Mono<Void> result = orderInstantiationAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        Assertions.assertNull(context.getExtendedState().getVariables().get(OrderCaptureConstants.IS_PRODUCT_ORDER_INSTANTIATED));
    }

    @Test
    @DisplayName("Given an internal server error in configuration service with DiscoException, " +
            "when apply is called, " +
            "then order instantiation fails with internal error handling")
    void shouldNotInstantiateOrderWhenInternalServerErrorOccursDiscoException() {
        // Given
        DiscoException exception = new DiscoException(DescriptionConstants.INTERNAL_SERVER_ERROR);
        when(configurationService.getProductConfigurationById(any())).thenThrow(exception);
        StateContext<String, String> context = createStateContext(ConfigurationState.VALIDATED, false, OrderCaptureConstants.ADD);

        // When
        Mono<Void> result = orderInstantiationAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        Assertions.assertEquals(Boolean.FALSE, context.getExtendedState().getVariables().get(OrderCaptureConstants.IS_PRODUCT_ORDER_INSTANTIATED));
        Assertions.assertEquals(DescriptionConstants.INTERNAL_SERVER_ERROR, context.getExtendedState().getVariables().get(StateMachineUtil.DESCRIPTION));
    }

    @Test
    @DisplayName("Given unreachable configuration service, " +
            "when apply is called, " +
            "then should handle unreachable service error")
    void shouldNotInstantiateOrderWhenConfigurationServiceIsUnreachable() {
        // Given
        DiscoException exception = new DiscoException(DescriptionConstants.PRODUCT_CONFIGURATOR_SERVICE_UNREACHABLE);
        when(configurationService.getProductConfigurationById(any())).thenThrow(exception);
        StateContext<String, String> context = createStateContext(ConfigurationState.VALIDATED, false, OrderCaptureConstants.MODIFICATION);

        // When
        Mono<Void> result = orderInstantiationAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        Assertions.assertEquals(Boolean.FALSE, context.getExtendedState().getVariables().get(OrderCaptureConstants.IS_PRODUCT_ORDER_INSTANTIATED));
        Assertions.assertEquals(DescriptionConstants.PRODUCT_CONFIGURATOR_SERVICE_UNREACHABLE, context.getExtendedState().getVariables().get(StateMachineUtil.DESCRIPTION));
    }

    @Test
    @DisplayName("Given unreachable configuration service ERROR_RETRIEVING_PRODUCT_CONFIGURATION_ITEMS, " +
            "when apply is called, " +
            "then should handle unreachable service error")
    void shouldNotInstantiateOrderWhenConfigServiceUnreachable() {
        // Given
        DiscoException exception = new DiscoException(DescriptionConstants.ERROR_RETRIEVING_PRODUCT_CONFIGURATION_ITEMS);
        when(configurationService.getProductConfigurationById(any())).thenThrow(exception);
        StateContext<String, String> context = createStateContext(ConfigurationState.VALIDATED, false, OrderCaptureConstants.MODIFICATION);

        // When
        Mono<Void> result = orderInstantiationAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        Assertions.assertEquals(Boolean.FALSE, context.getExtendedState().getVariables().get(OrderCaptureConstants.IS_PRODUCT_ORDER_INSTANTIATED));
        Assertions.assertEquals(DescriptionConstants.VALID_CONFIGURATION_IDENTIFIER_REQUIRED, context.getExtendedState().getVariables().get(StateMachineUtil.DESCRIPTION));
    }

    @Test
    @DisplayName("Given an internal server error in configuration service, " +
            "when apply is called, " +
            "then order instantiation fails with internal error handling")
    void shouldNotInstantiateOrderWhenInternalServerErrorOccurs() {
        // Given
        RuntimeException exception = new RuntimeException(DescriptionConstants.INTERNAL_SERVER_ERROR);
        when(configurationService.getProductConfigurationById(any())).thenThrow(exception);
        StateContext<String, String> context = createStateContext(ConfigurationState.VALIDATED, false, OrderCaptureConstants.ADD);

        // When
        Mono<Void> result = orderInstantiationAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        Assertions.assertEquals(Boolean.FALSE, context.getExtendedState().getVariables().get(OrderCaptureConstants.IS_PRODUCT_ORDER_INSTANTIATED));
        Assertions.assertEquals(DescriptionConstants.INTERNAL_SERVER_ERROR, context.getExtendedState().getVariables().get(StateMachineUtil.DESCRIPTION));
    }

    @ParameterizedTest
    @MethodSource("provideTestDataForAcquisitionExclusionCases")
    @DisplayName("Given valid configuration for an acquisition use case, " +
            "when apply is called, " +
            "then product order is created successfully")
    void shouldCreateProductOrderAndReliesOnLinks(String specId, String reliesOnSpecId) {
        // Given
        QueryProductConfiguration productConfiguration = createQueryProductConfiguration(OrderCaptureConstants.ADD, OrderCaptureConstants.ADD);
        when(configurationService.getProductConfigurationById(any())).thenReturn(productConfiguration);
        when(productOfferingService.getProductOfferingById(any())).thenReturn(createProductOffering(POST_PAID));
        when(billingCycleService.getNextBillingDate(any())).thenReturn(Instant.now());
        ProductOrder productOrder = createProductOrder();
        when(productOrderService.createProductOrder(any())).thenReturn(productOrder);
        when(productSpecificationService.fetchProductSpecifications(any())).thenReturn(createProductSpecifications(specId, reliesOnSpecId));
        when(settingsService.getSettings()).thenReturn(createSettings(false));
        StateContext<String, String> context = createStateContext(ConfigurationState.VALIDATED, false, OrderCaptureConstants.ADD);

        // When
        Mono<Void> result = orderInstantiationAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        ProductOrder updatedProductOrder = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.CREATED_PRODUCT_ORDER, ProductOrder.class);
        Assertions.assertNotNull(updatedProductOrder);
        Assertions.assertEquals(Boolean.TRUE, context.getExtendedState().getVariables().get(OrderCaptureConstants.IS_PRODUCT_ORDER_INSTANTIATED));
    }

    @ParameterizedTest
    @MethodSource("provideTestDataForExclusionCases")
    @DisplayName("Given parent-child mapping from product inventory, " +
            "when apply is called, " +
            "then product order is created without reliesOn relationship")
    void shouldCreateOrderWithParentChildAndReliesOnLinks(String specId, String reliesOnSpecId, String parentId, String childSpecId) {
        // Given
        QueryProductConfiguration config = createQueryProductConfiguration(OrderCaptureConstants.MODIFICATION, OrderCaptureConstants.ADD);
        when(configurationService.getProductConfigurationById(any())).thenReturn(config);
        when(productOfferingService.getProductOfferingById(any())).thenReturn(createProductOffering(POST_PAID));
        when(billingCycleService.getNextBillingDate(any())).thenReturn(Instant.now());
        ProductOrder expectedOrder = createProductOrder();
        when(productOrderService.createProductOrder(any())).thenReturn(expectedOrder);

        when(productSpecificationService.fetchProductSpecifications(any())).thenReturn(createProductSpecifications(specId, reliesOnSpecId));
        List<Product> products = getProduct(parentId, childSpecId);
        when(productInventoryService.getProductsByRelationship(anyString())).thenReturn(products);
        when(settingsService.getSettings()).thenReturn(createSettings(false));

        StateContext<String, String> context = createStateContext(ConfigurationState.VALIDATED, false, OrderCaptureConstants.MODIFICATION);

        // When
        Mono<Void> result = orderInstantiationAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        ProductOrder updatedOrder = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.CREATED_PRODUCT_ORDER, ProductOrder.class);
        Assertions.assertNotNull(updatedOrder);
        Assertions.assertTrue((Boolean) context.getExtendedState().getVariables().get(OrderCaptureConstants.IS_PRODUCT_ORDER_INSTANTIATED));
    }

    @Test
    @DisplayName("Given no parent-child mapping from product inventory, " +
            "when apply is called, " +
            "then product order is created without reliesOn relationship")
    void shouldCreateProductOrderWithoutParentChildRelationship() {
        // Given
        QueryProductConfiguration config = createQueryProductConfiguration(OrderCaptureConstants.MODIFICATION, OrderCaptureConstants.ADD);
        when(configurationService.getProductConfigurationById(any())).thenReturn(config);
        when(productOfferingService.getProductOfferingById(any())).thenReturn(createProductOffering(POST_PAID));
        when(billingCycleService.getNextBillingDate(any())).thenReturn(Instant.now());
        ProductOrder expectedOrder = createProductOrder();
        when(productOrderService.createProductOrder(any())).thenReturn(expectedOrder);
        when(productSpecificationService.fetchProductSpecifications(any())).thenReturn(Collections.emptyList());
        when(settingsService.getSettings()).thenReturn(createSettings(false));

        StateContext<String, String> context = createStateContext(ConfigurationState.VALIDATED, false, OrderCaptureConstants.MODIFICATION);

        // When
        Mono<Void> result = orderInstantiationAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        ProductOrder updatedOrder = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.CREATED_PRODUCT_ORDER, ProductOrder.class);
        Assertions.assertNotNull(updatedOrder);
        Assertions.assertTrue((Boolean) context.getExtendedState().getVariables().get(OrderCaptureConstants.IS_PRODUCT_ORDER_INSTANTIATED));
    }

    @Test
    @DisplayName("Given modified configuration, " +
            "when apply is called, " +
            "then existing product order is updated")
    void shouldUpdateExistingOrderWhenConfigurationIsModified() {
        // Given
        QueryProductConfiguration productConfiguration = createQueryProductConfiguration(OrderCaptureConstants.ADD, OrderCaptureConstants.ADD);
        ProductOrder productOrder = createProductOrder();
        when(configurationService.getProductConfigurationById(any())).thenReturn(productConfiguration);
        when(productOfferingService.getProductOfferingById(any())).thenReturn(createProductOffering(POST_PAID));
        when(billingCycleService.getNextBillingDate(any())).thenReturn(Instant.now());
        when(settingsService.getSettings()).thenReturn(createSettings(false));
        doNothing().when(productOrderService).updateOrderItemsAndOrderTotalPrice(any(), any(), any());
        StateContext<String, String> context = createStateContext(ConfigurationState.MODIFIED, false, OrderCaptureConstants.ADD);
        context.getExtendedState().getVariables().put(OrderCaptureConstants.CREATED_PRODUCT_ORDER, productOrder);
        context.getExtendedState().getVariables().put(OrderCaptureConstants.IS_PRODUCT_ORDER_INSTANTIATED, true);

        // When
        Mono<Void> result = orderInstantiationAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        verify(productOrderService, times(1)).updateOrderItemsAndOrderTotalPrice(any(), any(), any());
        Assertions.assertEquals(Boolean.TRUE, context.getExtendedState().getVariables().get(OrderCaptureConstants.IS_PRODUCT_ORDER_INSTANTIATED));
    }

    @Test
    @DisplayName("Given termination use case, " +
            "when apply is called, " +
            "then requested completion date is set")
    void shouldSetRequestedCompletionDateWhenTerminationUseCase() {
        // Given
        StateContext<String, String> context = createStateContext(ConfigurationState.VALIDATED, true, OrderCaptureConstants.TERMINATION);
        QueryProductConfiguration productConfiguration = createQueryProductConfiguration(OrderCaptureConstants.TERMINATION, OrderCaptureConstants.TERMINATION);
        when(configurationService.getProductConfigurationById(any())).thenReturn(productConfiguration);
        when(settingsService.getSettings()).thenReturn(createSettings(true));
        when(productOfferingService.getProductOfferingById(any())).thenReturn(createProductOffering(POST_PAID));
        when(billingCycleService.getNextBillingDate(any())).thenReturn(Instant.now());
        ProductOrder productOrder = createProductOrder();
        when(productOrderService.createProductOrder(any())).thenReturn(productOrder);

        // When
        Mono<Void> result = orderInstantiationAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        ProductOrder updatedProductOrder = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.CREATED_PRODUCT_ORDER, ProductOrder.class);
        Assertions.assertNotNull(updatedProductOrder);
        Assertions.assertNotNull(updatedProductOrder.getRequestedCompletionDate());
    }

    @Test
    @DisplayName("Given a modification use case with non-immediate payment, " +
            "when apply is called, " +
            "then requested completion date is properly adjusted")
    void shouldSetRequestedCompletionDateWhenModificationUseCaseWithNonImmediatePayment() {
        // Given
        StateContext<String, String> context = createStateContext(ConfigurationState.VALIDATED, true, OrderCaptureConstants.MODIFICATION);
        QueryProductConfiguration productConfiguration = createQueryProductConfiguration(OrderCaptureConstants.MODIFICATION, OrderCaptureConstants.MODIFICATION);
        when(configurationService.getProductConfigurationById(any())).thenReturn(productConfiguration);
        when(settingsService.getSettings()).thenReturn(createSettings(true));
        when(productOfferingService.getProductOfferingById(any())).thenReturn(createProductOffering(HYBRID));
        when(billingCycleService.getNextBillingDate(any())).thenReturn(Instant.now());
        when(productOfferingPriceService.fetchProductOfferingPrices(any())).thenReturn(createProductOfferingPrice());
        ProductOrder productOrder = createProductOrder();
        when(productOrderService.createProductOrder(any())).thenReturn(productOrder);

        // When
        Mono<Void> result = orderInstantiationAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        ProductOrder updatedProductOrder = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.CREATED_PRODUCT_ORDER, ProductOrder.class);
        Assertions.assertNotNull(updatedProductOrder);
        Assertions.assertNotNull(updatedProductOrder.getRequestedCompletionDate());
    }

    @Test
    @DisplayName("Given a modification use case with at least one item to be terminated, " +
            "when apply is called, " +
            "then requested completion date is properly adjusted")
    void shouldSetRequestedCompletionDateWhenModificationUseCaseWithAtLeastItemToBeTerminated() {
        // Given
        StateContext<String, String> context = createStateContext(ConfigurationState.VALIDATED, true, OrderCaptureConstants.MODIFICATION);
        QueryProductConfiguration productConfiguration = createQueryProductConfiguration(OrderCaptureConstants.TERMINATION, OrderCaptureConstants.TERMINATION);
        when(configurationService.getProductConfigurationById(any())).thenReturn(productConfiguration);
        when(settingsService.getSettings()).thenReturn(createSettings(true));
        when(productOfferingService.getProductOfferingById(any())).thenReturn(createProductOffering(HYBRID));
        lenient().when(productInventoryService.getProductByIds(anyList())).thenReturn(List.of(createProduct()));
        lenient().when(billingCycleService.getNextBillingDate(any())).thenReturn(Instant.now());
        ProductOrder productOrder = createProductOrder();
        when(productOrderService.createProductOrder(any())).thenReturn(productOrder);

        // When
        Mono<Void> result = orderInstantiationAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        ProductOrder updatedProductOrder = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.CREATED_PRODUCT_ORDER, ProductOrder.class);
        Assertions.assertNotNull(updatedProductOrder);
        Assertions.assertNotNull(updatedProductOrder.getRequestedCompletionDate());
    }

    @Test
    @DisplayName("Given a migration use case with non-immediate payment, " +
            "when apply is called, " +
            "then requested completion date is properly adjusted")
    void shouldSetRequestedCompletionDateWhenMigrationUseCaseWithNonImmediatePayment() {
        // Given
        StateContext<String, String> context = createStateContext(ConfigurationState.VALIDATED, true, OrderCaptureConstants.MIGRATE);
        QueryProductConfiguration productConfiguration = createQueryProductConfiguration(OrderCaptureConstants.MIGRATE, OrderCaptureConstants.MIGRATE);
        when(configurationService.getProductConfigurationById(any())).thenReturn(productConfiguration);
        when(settingsService.getSettings()).thenReturn(createSettings(true));
        when(productOfferingService.getProductOfferingById(any())).thenReturn(createProductOffering(POST_PAID));
        when(billingCycleService.getNextBillingDate(any())).thenReturn(Instant.now());
        when(productOfferingPriceService.fetchProductOfferingPrices(any())).thenReturn(createProductOfferingPrice());
        ProductOrder productOrder = createProductOrder();
        when(productOrderService.createProductOrder(any())).thenReturn(productOrder);

        // When
        Mono<Void> result = orderInstantiationAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        ProductOrder updatedProductOrder = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.CREATED_PRODUCT_ORDER, ProductOrder.class);
        Assertions.assertNotNull(updatedProductOrder);
        Assertions.assertNotNull(updatedProductOrder.getRequestedCompletionDate());
    }

    @Test
    @DisplayName("Given a migration use case with immediate payment only, " +
            "when apply is called, " +
            "then requested completion date remains unset")
    void shouldNotSetRequestedCompletionDateWhenMigrationUseCaseWithoutNonImmediatePayment() {
        // Given
        StateContext<String, String> context = createStateContext(ConfigurationState.VALIDATED, true, OrderCaptureConstants.MIGRATE);
        QueryProductConfiguration productConfiguration = createQueryProductConfiguration(OrderCaptureConstants.MIGRATE, OrderCaptureConstants.MIGRATE);
        when(configurationService.getProductConfigurationById(any())).thenReturn(productConfiguration);
        when(settingsService.getSettings()).thenReturn(createSettings(true));
        when(productOfferingService.getProductOfferingById(any())).thenReturn(createProductOffering(POST_PAID));
        when(productOfferingPriceService.fetchProductOfferingPrices(any())).thenReturn(createImmediatePaymentProductOfferingPrice());
        lenient().when(productInventoryService.getProductByIds(anyList())).thenReturn(Collections.emptyList());
        when(productOrderService.createProductOrder(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Mono<Void> result = orderInstantiationAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        ProductOrder updatedProductOrder = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.CREATED_PRODUCT_ORDER, ProductOrder.class);
        Assertions.assertNotNull(updatedProductOrder);
        Assertions.assertNull(updatedProductOrder.getRequestedCompletionDate());
        verify(billingCycleService, never()).getNextBillingDate(any());
    }

    @Test
    @DisplayName("Given a migration use case with prepaid billing type, " +
            "when apply is called, " +
            "then requested completion date remains unset")
    void shouldNotSetRequestedCompletionDateWhenMigrationUseCaseWithPrepaidBillingType() {
        // Given
        StateContext<String, String> context = createStateContext(ConfigurationState.VALIDATED, true, OrderCaptureConstants.MIGRATE);
        QueryProductConfiguration productConfiguration = createQueryProductConfiguration(OrderCaptureConstants.MIGRATE, OrderCaptureConstants.MIGRATE);
        when(configurationService.getProductConfigurationById(any())).thenReturn(productConfiguration);
        when(settingsService.getSettings()).thenReturn(createSettings(true));
        when(productOfferingService.getProductOfferingById(any())).thenReturn(createProductOffering("prepaid"));
        when(productOrderService.createProductOrder(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Mono<Void> result = orderInstantiationAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        ProductOrder updatedProductOrder = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.CREATED_PRODUCT_ORDER, ProductOrder.class);
        Assertions.assertNotNull(updatedProductOrder);
        Assertions.assertNull(updatedProductOrder.getRequestedCompletionDate());
        verify(billingCycleService, never()).getNextBillingDate(any());
    }

    @Test
    @DisplayName("Given a migration use case with at least one item associated to a billing account, " +
            "when apply is called, " +
            "then requested completion date is properly adjusted")
    void shouldSetRequestedCompletionDateWhenMigrationUseCaseWithBillingAccount() {
        // Given
        StateContext<String, String> context = createStateContext(ConfigurationState.VALIDATED, true, OrderCaptureConstants.MIGRATE);
        QueryProductConfiguration productConfiguration = createQueryProductConfiguration(OrderCaptureConstants.MIGRATE, OrderCaptureConstants.MIGRATE);
        when(configurationService.getProductConfigurationById(any())).thenReturn(productConfiguration);
        when(settingsService.getSettings()).thenReturn(createSettings(true));
        when(productOfferingService.getProductOfferingById(any())).thenReturn(createProductOffering(HYBRID));
        lenient().when(productInventoryService.getProductByIds(anyList())).thenReturn(List.of(createProduct()));
        lenient().when(billingCycleService.getNextBillingDate(any())).thenReturn(Instant.now());
        ProductOrder productOrder = createProductOrder();
        when(productOrderService.createProductOrder(any())).thenReturn(productOrder);

        // When
        Mono<Void> result = orderInstantiationAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        ProductOrder updatedProductOrder = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.CREATED_PRODUCT_ORDER, ProductOrder.class);
        Assertions.assertNotNull(updatedProductOrder);
        Assertions.assertNotNull(updatedProductOrder.getRequestedCompletionDate());
    }

    @Test
    @DisplayName("Given valid configuration for an migration use case, " +
            "when apply is called, " +
            "then product order is created successfully")
    void shouldCreateProductOrderWhenConfigurationIsValidForMigration() {
        // Given
        QueryProductConfiguration productConfiguration = createQueryProductConfiguration(OrderCaptureConstants.MIGRATE, OrderCaptureConstants.MIGRATE);
        when(configurationService.getProductConfigurationById(any())).thenReturn(productConfiguration);
        ProductOrder productOrder = createProductOrderForMigration();
        when(productOrderService.createProductOrder(any())).thenReturn(productOrder);
        when(settingsService.getSettings()).thenReturn(createSettings(false));
        when(productInventoryService.getProductByIds(anyList())).thenReturn(List.of(getMigrateToProduct()));
        StateContext<String, String> context = createStateContext(ConfigurationState.VALIDATED, false, OrderCaptureConstants.MIGRATE);

        // When
        Mono<Void> result = orderInstantiationAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        Assertions.assertEquals(Boolean.TRUE, context.getExtendedState().getVariables().get(OrderCaptureConstants.IS_PRODUCT_ORDER_INSTANTIATED));
        ProductOrder updatedProductOrder = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.CREATED_PRODUCT_ORDER, ProductOrder.class);
        Assertions.assertNotNull(updatedProductOrder);
    }

    private Product getMigrateToProduct() {
        return Product
                .builder()
                .id(PRODUCT_ID_4)
                .realizingResource(
                        List.of(com.orange.discobole.productinventory.dto.v1.ResourceRef
                                .builder()
                                .id("111")
                                .href("AAA")
                                .atType("LogicalResource")
                                .build())
                )
                .realizingService(List.of(com.orange.discobole.productinventory.dto.v1.ServiceRef.builder()
                        .id("123")
                        .href("AAAA")
                        .build()))
                .atType("Product")
                .build();

    }

    @Test
    @DisplayName("Given valid configuration for a migration use case, " +
            "when apply is called, " +
            "then product order is created successfully")
    void shouldCreateProductOrderAndReliesOnLinksForMigration() {
        // Given
        QueryProductConfiguration productConfiguration = createQueryProductConfiguration(OrderCaptureConstants.MIGRATE, OrderCaptureConstants.MIGRATE);
        when(configurationService.getProductConfigurationById(any())).thenReturn(productConfiguration);
        ProductOrder productOrder = createProductOrder();
        when(productOrderService.createProductOrder(any())).thenReturn(productOrder);
        when(productSpecificationService.fetchProductSpecifications(any())).thenReturn(createProductSpecifications("10", "11"));
        when(settingsService.getSettings()).thenReturn(createSettings(false));
        StateContext<String, String> context = createStateContext(ConfigurationState.VALIDATED, false, OrderCaptureConstants.MIGRATE);

        // When
        Mono<Void> result = orderInstantiationAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        ProductOrder updatedProductOrder = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.CREATED_PRODUCT_ORDER, ProductOrder.class);
        Assertions.assertNotNull(updatedProductOrder);
        Assertions.assertEquals(Boolean.TRUE, context.getExtendedState().getVariables().get(OrderCaptureConstants.IS_PRODUCT_ORDER_INSTANTIATED));
    }

    @Test
    @DisplayName("Given valid configuration for a migration use case with terminate item, " +
            "when apply is called, " +
            "then product order is created successfully")
    void shouldCreateProductOrderAndReliesOnLinksForMigrationForDeleted() {
        // Given
        QueryProductConfiguration productConfiguration = createQueryProductConfiguration(OrderCaptureConstants.MIGRATE, OrderCaptureConstants.MIGRATE);
        when(configurationService.getProductConfigurationById(any())).thenReturn(productConfiguration);
        ProductOrder productOrder = createProductOrder();
        when(productOrderService.createProductOrder(any())).thenReturn(productOrder);
        when(productSpecificationService.fetchProductSpecifications(any())).thenReturn(createProductSpecifications("10", "12"));
        when(settingsService.getSettings()).thenReturn(createSettings(false));
        StateContext<String, String> context = createStateContext(ConfigurationState.VALIDATED, false, OrderCaptureConstants.MIGRATE);

        // When
        Mono<Void> result = orderInstantiationAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        ProductOrder updatedProductOrder = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.CREATED_PRODUCT_ORDER, ProductOrder.class);
        Assertions.assertNotNull(updatedProductOrder);
        Assertions.assertEquals(Boolean.TRUE, context.getExtendedState().getVariables().get(OrderCaptureConstants.IS_PRODUCT_ORDER_INSTANTIATED));
    }

    private List<ProductSpecification> createProductSpecifications(String specId, String reliesOnSpecId) {
        return List.of(ProductSpecification.builder()
                .id(specId)
                .productSpecificationRelationship(List.of(ProductSpecificationRelationship.builder()
                        .id(reliesOnSpecId)
                        .relationshipType(RELIES_ON)
                        .build()))
                .build());
    }

    private Product createProduct() {
        return Product.builder()
                .id(PRODUCT_ID)
                .billingAccount(BillingAccountRef.builder()
                        .id(BILLING_ID)
                        .atReferredType("BillingAccountRef")
                        .build())
                .build();
    }

    private ProductOrder createProductOrder() {
        return ProductOrder.builder()
                .id(PRODUCT_ORDER_ID)
                .requestedCompletionDate(Instant.now())
                .orderTotalPrice(List.of())
                .productOrderItem(List.of())
                .build();
    }

    private ProductOrder createProductOrderForMigration() {
        return ProductOrder.builder()
                .id(PRODUCT_ORDER_ID)
                .requestedCompletionDate(Instant.now())
                .orderTotalPrice(List.of())
                .productOrderItem(List.of(
                        ProductOrderItem.builder()
                                .id("05")
                                .build()

                ))
                .build();
    }

    private SettingsEntity createSettings(boolean checkAndSetBillCycleDateEnabled) {
        SettingsEntity settings = new SettingsEntity();
        settings.setCheckAndSetBillCycleDateEnabled(checkAndSetBillCycleDateEnabled);
        return settings;
    }

    private ProductOffering createProductOffering(String billingType) {
        return ProductOffering.builder()
                .billingType(billingType)
                .build();
    }


    private List<ProductOfferingPrice> createProductOfferingPrice() {
        return Collections.singletonList(
                ProductOfferingPrice.builder()
                        .id(PRODUCT_OFFERING_PRICE_ID_1)
                        .price(Money.builder()
                                .unit(PRICE_UNIT)
                                .value(PRICE_VALUE)
                                .build())
                        .immediatePayment(Boolean.FALSE)
                        .build()
        );
    }

    private List<ProductOfferingPrice> createImmediatePaymentProductOfferingPrice() {
        return Collections.singletonList(
                ProductOfferingPrice.builder()
                        .id(PRODUCT_OFFERING_PRICE_ID_1)
                        .price(Money.builder()
                                .unit(PRICE_UNIT)
                                .value(PRICE_VALUE)
                                .build())
                        .immediatePayment(Boolean.TRUE)
                        .build()
        );
    }

    private RelatedParty createProcessRelatedParty() {
        return new RelatedParty()
                .id(RELATED_PARTY_ID)
                .role(RELATED_PARTY_ROLE);
    }

    private List<RelatedEntity> createRelatedEntity() {
        RelatedEntity productOfferingRelatedEntity = new RelatedEntity()
                .id(PRODUCT_OFFERING_ID)
                .name(DEFAULT_PRODUCT_OFFERING_NAME)
                .referredType(PRODUCT_OFFERING_TYPE);
        RelatedEntity productRelatedEntity = new RelatedEntity()
                .id(PRODUCT_ID)
                .name(DEFAULT_PRODUCT_NAME)
                .referredType(PRODUCT_TYPE);
        return List.of(productOfferingRelatedEntity, productRelatedEntity);
    }

    private List<ChannelRef> createRelatedChannels() {
        return List.of(new ChannelRef()
                .id(CHANNEL_ID)
                .name(CHANNEL_NAME));
    }

    private QueryProductConfiguration createQueryProductConfiguration(String requestedConfigAction, String configurationItemAction) {
        ConfigurationAction requestedConfigurationAction = ConfigurationAction.builder()
                .action(requestedConfigAction)
                .isSelected(true)
                .type("ConfigurationAction")
                .build();

        ConfigurationAction configurationComputedItemAction = ConfigurationAction.builder()
                .action(configurationItemAction)
                .isSelected(true)
                .type("ConfigurationAction")
                .build();

        List<ConfigurationAction> configurationItemActions = Collections.singletonList(configurationComputedItemAction);
        List<ConfigurationAction> configurationRequestedActions = Collections.singletonList(requestedConfigurationAction);
        ConfigurationAction modifyConfigurationComputedItemAction = ConfigurationAction.builder()
                .action("modify")
                .isSelected(true)
                .type("ConfigurationAction")
                .build();
        List<ConfigurationAction> modifyConfigurationItemActions = Collections.singletonList(modifyConfigurationComputedItemAction);


        ConfigurationAction terminateConfigurationComputedItemAction = ConfigurationAction.builder()
                .action("terminate")
                .isSelected(true)
                .type("ConfigurationAction")
                .build();

        List<ConfigurationAction> terminateConfigurationItemActions = Collections.singletonList(terminateConfigurationComputedItemAction);


        ProductOfferingRef mobilePackageMaxPlus =
                createProductOfferingQualificationRef(CONTRACT_PRODUCT_OFFERING_ID, "Mobile Package Max plus", "Contract");
        ProductOfferingRef bundledMobilePackageMax =
                createProductOfferingQualificationRef(BUNDLE_PRODUCT_OFFERING_ID_1, "Mobile Package Max", ServiceConstants.BUNDLE_PRODUCT_OFFERING_TYPE);

        ProductOfferingRef timeBundleOffering =
                createProductOfferingQualificationRef(ATOMIC_PRODUCT_OFFERING_ID_2, "Time Bundle", "AtomicProductOffering");
        ProductSpecificationRef timeBundleSpec =
                createQualificationProductSpecificationRef("04", "Time Bundle");
        ProductOfferingRef mobileLineOffering =
                createProductOfferingQualificationRef(ATOMIC_PRODUCT_OFFERING_ID_1, "Mobile Line", "AtomicProductOffering");
        ProductSpecificationRef mobileLineSpec =
                createQualificationProductSpecificationRef("01", "Mobile Line");
        ProductSpecificationRef dataBundleSpec =
                createQualificationProductSpecificationRef("10", "Data bundle");
        ProductSpecificationRef mobileLineSpec2 =
                createQualificationProductSpecificationRef("11", "mobile line");
        ProductSpecificationRef dataBundleSpec2 =
                createQualificationProductSpecificationRef("12", "mobile line");

        ProductOfferingRef dataBundleOffering =
                createProductOfferingQualificationRef(ATOMIC_PRODUCT_OFFERING_ID_1, "data Bundle", "AtomicProductOffering");

        ProductOfferingRef bundledRingPackage =
                createProductOfferingQualificationRef(BUNDLE_PRODUCT_OFFERING_ID_2, "Mobile Ring", ServiceConstants.BUNDLE_PRODUCT_OFFERING_TYPE);

        ProductOfferingRef ringBundleOffering =
                createProductOfferingQualificationRef(ATOMIC_PRODUCT_OFFERING_ID_3, "Ring Bundle", "AtomicProductOffering");
        ProductSpecificationRef ringBundleSpec =
                createQualificationProductSpecificationRef("09", "Ring Bundle");

        ValidityCharacteristic validityCharacteristic = ValidityCharacteristic.builder()
                .id("1")
                .name("Validity")
                .validTo(OFFSET_DATE_TIME)
                .build();

        StringCharacteristic stringCharacteristic = StringCharacteristic.builder()
                .id("1")
                .name("Volume")
                .value("One hour")
                .build();
        ObjectCharacteristic objectCharacteristic = ObjectCharacteristic.builder()
                .id("1")
                .name("Quantity")
                .value("50")
                .unitOfMeasure("GB")
                .build();

        ConfigurationCharacteristicValue configurationCharacteristicValue1 = ConfigurationCharacteristicValue.builder()
                .isSelected(Boolean.TRUE)
                .isSelectable(Boolean.TRUE)
                .characteristic(validityCharacteristic)
                .build();

        ConfigurationCharacteristicValue configurationCharacteristicValue2 = ConfigurationCharacteristicValue.builder()
                .isSelected(Boolean.TRUE)
                .isSelectable(Boolean.TRUE)
                .characteristic(stringCharacteristic)
                .build();
        ConfigurationCharacteristicValue configurationCharacteristicValue3 = ConfigurationCharacteristicValue.builder()
                .isSelected(Boolean.TRUE)
                .isSelectable(Boolean.TRUE)
                .characteristic(objectCharacteristic)
                .build();

        ConfigurationCharacteristic configurationCharacteristics1 = ConfigurationCharacteristic.builder()
                .configurationCharacteristicValues(List.of(configurationCharacteristicValue1))
                .build();

        ConfigurationCharacteristic configurationCharacteristics2 = ConfigurationCharacteristic.builder()
                .configurationCharacteristicValues(List.of(configurationCharacteristicValue2))
                .build();
        ConfigurationCharacteristic configurationCharacteristics3 = ConfigurationCharacteristic.builder()
                .configurationCharacteristicValues(List.of(configurationCharacteristicValue3))
                .build();

        List<ConfigurationPrice> configurationPrices1 = createConfigurationPrice(
                RC_PRICE_TYPE, PRODUCT_OFFERING_PRICE_ID_1, PERCENTAGE_FOR_ALTERATION);
        List<ConfigurationPrice> configurationPrices2 = createConfigurationPrice(
                NRC_PRICE_TYPE, PRODUCT_OFFERING_PRICE_ID_2, null);

        BundledProductOffering bundledProductOffering1 = BundledProductOffering
                .builder()
                .productOfferingRef(timeBundleOffering)
                .build();

        BundledProductOffering bundledProductOffering2 = BundledProductOffering
                .builder()
                .productOfferingRef(mobileLineOffering)
                .build();

        BundledGroupProductOffering bundledGroupProductOffering = BundledGroupProductOffering.builder()
                .bundledProductOfferings(List.of(bundledProductOffering1, bundledProductOffering2))
                .build();

        BundledProductOffering bundledProductOffering3 = BundledProductOffering
                .builder()
                .productOfferingRef(ringBundleOffering)
                .build();

        BundledGroupProductOffering bundledGroupProductOffering2 = BundledGroupProductOffering.builder()
                .bundledProductOfferings(List.of(bundledProductOffering3))
                .build();

        com.orange.discobole.ordermanagement.commons.dto.product.configuration.Product product1 = com.orange.discobole.ordermanagement.commons.dto.product.configuration.Product.builder()
                .id(PRODUCT_ID_1)
                .type(PRODUCT_TYPE)
                .build();

        com.orange.discobole.ordermanagement.commons.dto.product.configuration.Product product2 = com.orange.discobole.ordermanagement.commons.dto.product.configuration.Product.builder()
                .id(PRODUCT_ID_2)
                .type(PRODUCT_TYPE)
                .build();

        com.orange.discobole.ordermanagement.commons.dto.product.configuration.Product product3 = com.orange.discobole.ordermanagement.commons.dto.product.configuration.Product.builder()
                .id(PRODUCT_ID_3)
                .type(PRODUCT_TYPE)
                .build();

        com.orange.discobole.ordermanagement.commons.dto.product.configuration.Product product4 = com.orange.discobole.ordermanagement.commons.dto.product.configuration.Product.builder()
                .id(PRODUCT_ID_4)
                .type(PRODUCT_TYPE)
                .build();

        com.orange.discobole.ordermanagement.commons.dto.product.configuration.Product product5 = com.orange.discobole.ordermanagement.commons.dto.product.configuration.Product.builder()
                .id(PRODUCT_ID_5)
                .type(PRODUCT_TYPE)
                .build();

        com.orange.discobole.ordermanagement.commons.dto.product.configuration.Product product6 = com.orange.discobole.ordermanagement.commons.dto.product.configuration.Product.builder()
                .id(PRODUCT_ID_6)
                .type(PRODUCT_TYPE)
                .build();

        ProductConfiguration config1 = createProductConfiguration("02", mobilePackageMaxPlus, product1, configurationRequestedActions, null, null, null, null);
        ProductConfiguration config2 = createProductConfiguration("03", bundledMobilePackageMax, product2, configurationRequestedActions, null, null, bundledGroupProductOffering, null);
        ProductConfiguration config3 = createProductConfiguration("04", timeBundleOffering, product3, configurationItemActions, timeBundleSpec, configurationPrices1, null, List.of(configurationCharacteristics1));
        ProductConfiguration config4 = createProductConfiguration("05", mobileLineOffering, product4, configurationItemActions, mobileLineSpec, configurationPrices2, null, List.of(configurationCharacteristics2));
        ConfigurationPrice installmentConfigurationPrice = ConfigurationPrice.builder()
                .productOfferingPrice(ProductOfferingPriceRef.builder()
                        .type("InstallmentCharge")
                        .downPayment(700F)
                        .build())
                .build();
        ProductConfiguration config5 = createProductConfiguration("06", bundledRingPackage, product5, configurationRequestedActions, null, null, bundledGroupProductOffering2, null);
        ProductConfiguration config6 = createProductConfiguration("07", ringBundleOffering, product6, configurationItemActions, ringBundleSpec, null, null, null);
        ProductConfiguration config7 = createProductConfiguration("05", dataBundleOffering, product4, modifyConfigurationItemActions, dataBundleSpec, null, null, List.of(configurationCharacteristics3));
        ProductConfiguration config8 = createProductConfiguration("05", mobileLineOffering, product4, configurationItemActions, mobileLineSpec2, configurationPrices2, null, List.of(configurationCharacteristics2));
        ProductConfiguration config9 = createProductConfiguration("08", dataBundleOffering, product4, terminateConfigurationItemActions, dataBundleSpec, null, null, List.of(configurationCharacteristics3));
        ProductConfiguration config10 = createProductConfiguration("08", dataBundleOffering, product4, terminateConfigurationItemActions, dataBundleSpec2, null, null, List.of(configurationCharacteristics3));
        ProductConfiguration config11 = createProductConfiguration("08", dataBundleOffering, product4, terminateConfigurationItemActions, dataBundleSpec2, List.of(installmentConfigurationPrice), null, List.of(configurationCharacteristics3));


        ProductConfigurationItemRelationship relationship1 = createProductConfigurationItemRelationship("01", "requestItem");
        ProductConfigurationItemRelationship relationship2 = createProductConfigurationItemRelationship("03", "bundles");
        ProductConfigurationItemRelationship relationship3 = createProductConfigurationItemRelationship("06", "bundles");
        List<ProductConfigurationItemRelationship> relationships1 = Arrays.asList(relationship1, relationship2, relationship3);

        ProductConfigurationItemRelationship relationship4 = createProductConfigurationItemRelationship("04", "bundles");
        ProductConfigurationItemRelationship relationship5 = createProductConfigurationItemRelationship("05", "bundles");
        List<ProductConfigurationItemRelationship> relationships2 = List.of(relationship4, relationship5);

        ProductConfigurationItemRelationship relationship6 = createProductConfigurationItemRelationship("07", "bundles");
        List<ProductConfigurationItemRelationship> relationships3 = List.of(relationship6);
        ProductConfigurationItemRelationship relationship7;
        ProductConfigurationItemRelationship relationship8;
        QueryProductConfigurationItem item8;
        QueryProductConfigurationItem item9;
        QueryProductConfigurationItem item6;

        if (OrderCaptureConstants.MIGRATE.equals(requestedConfigAction)) {
            relationship7 = createProductConfigurationItemRelationship("05", "migrateFrom");
            relationship8 = createProductConfigurationItemRelationship("04", "migrateTo");


            item8 = createQueryProductConfigurationItem("04", config3, List.of(relationship7));
            item8.getProductConfiguration().setConfigurationActions(List.of(ConfigurationAction.builder()
                    .isSelected(Boolean.TRUE)
                    .action("migrate")
                    .build()));
            item9 = createQueryProductConfigurationItem("05", config8, List.of(relationship8));
            item6 = createQueryProductConfigurationItem("07", config6, List.of(relationship7));
        } else {
            item8 = createQueryProductConfigurationItem("04", config3, null);
            item9 = createQueryProductConfigurationItem("05", config4, null);
            item6 = createQueryProductConfigurationItem("07", config6, null);

        }

        QueryProductConfigurationItem item1 = createQueryProductConfigurationItem("02", config1, relationships1);
        QueryProductConfigurationItem item2 = createQueryProductConfigurationItem("03", config2, relationships2);
        QueryProductConfigurationItem item3 = createQueryProductConfigurationItem("04", config3, null);
        QueryProductConfigurationItem item4 = createQueryProductConfigurationItem("05", config4, null);

        QueryProductConfigurationItem item5 = createQueryProductConfigurationItem("06", config5, relationships3);
        QueryProductConfigurationItem item11 = createQueryProductConfigurationItem("07", config7, relationships3);
        QueryProductConfigurationItem item10 = createQueryProductConfigurationItem("08", config9, relationships3);
        QueryProductConfigurationItem item12 = createQueryProductConfigurationItem("09", config10, relationships3);
        QueryProductConfigurationItem item13 = createQueryProductConfigurationItem("09", config11, relationships3);


        List<QueryProductConfigurationItem> items = Arrays.asList(item1, item2, item3, item4, item5, item6, item8, item9, item10, item11, item12, item13);
        List<QueryProductConfigurationItem> requestedConfiguration = List.of(createQueryProductConfigurationItem("1", config1, null));

        return QueryProductConfiguration.builder()
                .requestedProductConfigurationItems(requestedConfiguration)
                .computedProductConfigurationItems(items)
                .type("QueryProductConfiguration")
                .build();
    }

    private ProductOfferingRef createProductOfferingQualificationRef(
            String id, String name, String referredType) {
        return ProductOfferingRef.builder()
                .id(id)
                .name(name)
                .type("ProductOfferingRef")
                .referredType(referredType)
                .build();
    }

    private List<ConfigurationPrice> createConfigurationPrice(String priceType, String productOfferingPriceId,
                                                              Float percentageForAlteration) {
        Price price = Price.builder()
                .dutyFreeAmount(com.orange.discobole.ordermanagement.commons.dto.product.configuration.Money.builder()
                        .value(DUTY_FREE_AMOUNT_VALUE)
                        .unit(UNIT)
                        .build())
                .taxIncludedAmount(com.orange.discobole.ordermanagement.commons.dto.product.configuration.Money.builder()
                        .value(TAX_INCLUDED_AMOUNT_VALUE)
                        .unit(UNIT)
                        .build())
                .build();

        PriceAlteration priceAlteration = PriceAlteration.builder()
                .priceType(priceType.equals(RC_PRICE_TYPE) ? RECURRING_DISCOUNT : NON_RECURRING_DISCOUNT)
                .recurringChargePeriod(priceType.equals(RC_PRICE_TYPE) ? Quantity
                        .builder()
                        .units(UNITS)
                        .amount(AMOUNT)
                        .build() : null)
                .price(percentageForAlteration != null
                        ? Price.builder()
                        .percentage(percentageForAlteration)
                        .build()
                        : Price.builder()
                        .dutyFreeAmount(com.orange.discobole.ordermanagement.commons.dto.product.configuration.Money.builder()
                                .value(3.0f)
                                .unit(UNIT)
                                .build())
                        .taxIncludedAmount(com.orange.discobole.ordermanagement.commons.dto.product.configuration.Money.builder()
                                .value(3.0f)
                                .unit(UNIT)
                                .build())
                        .build())
                .build();

        return List.of(ConfigurationPrice.builder()
                .priceType(priceType)
                .recurringChargePeriod(priceType.equals(RC_PRICE_TYPE) ? Quantity
                        .builder()
                        .units(UNITS)
                        .amount(AMOUNT)
                        .build() : null)
                .price(price)
                .priceAlterations(List.of(priceAlteration))
                .productOfferingPrice(ProductOfferingPriceRef.builder()
                        .id(productOfferingPriceId)
                        .price(com.orange.discobole.ordermanagement.commons.dto.product.configuration.Money.builder()
                                .value(PRODUCT_OFFERING_PRICE_AMOUNT_VALUE)
                                .unit(UNIT)
                                .build())
                        .type("ProductOfferingPriceCharge")
                        .build())
                .build());
    }

    private ProductConfiguration createProductConfiguration(String id,
                                                            ProductOfferingRef productOffering,
                                                            ProductRefOrValue product,
                                                            List<ConfigurationAction> configurationActions,
                                                            ProductSpecificationRef spec,
                                                            List<ConfigurationPrice> configurationPrices,
                                                            BundledGroupProductOffering bundledGroupProductOffering,
                                                            List<ConfigurationCharacteristic> configurationCharacteristics) {
        ProductConfiguration.ProductConfigurationBuilder builder = ProductConfiguration.builder()
                .id(id)
                .isSelectable(true)
                .isSelected(true)
                .bundledGroupProductOffering(bundledGroupProductOffering)
                .configurationActions(configurationActions)
                .productOffering(productOffering)
                .configurationCharacteristics(configurationCharacteristics)
                .configurationPrices(configurationPrices);
        if (spec != null) {
            builder.productSpecification(spec);
        }
        if (configurationActions.get(0).getAction().equals(OrderCaptureConstants.MODIFICATION) || configurationActions.get(0).getAction().equals(OrderCaptureConstants.MIGRATE)) {
            builder.product(product);
        }
        return builder.build();
    }

    private ProductSpecificationRef createQualificationProductSpecificationRef(String id, String name) {
        return ProductSpecificationRef.builder()
                .id(id)
                .name(name)
                .type("ProductSpecificationRef")
                .build();
    }

    private ProductConfigurationItemRelationship createProductConfigurationItemRelationship(String id, String relationshipType) {
        return ProductConfigurationItemRelationship.builder()
                .id(id)
                .relationshipType(relationshipType)
                .type("ProductConfigurationItemRelationship")
                .build();
    }

    private QueryProductConfigurationItem createQueryProductConfigurationItem(String id, ProductConfiguration config,
                                                                              List<ProductConfigurationItemRelationship> relationships) {
        QueryProductConfigurationItem.QueryProductConfigurationItemBuilder builder = QueryProductConfigurationItem.builder()
                .id(id)
                .productConfiguration(config)
                .state("approved");
        if (relationships != null) {
            builder.productConfigurationItemRelationships(relationships);
        }
        return builder.build();
    }

    private DefaultStateContext<String, String> createStateContext(ConfigurationState configurationState,
                                                                   boolean isModificationOrTerminationUseCase,
                                                                   String configurationAction) {
        ExtendedState extendedState = new DefaultExtendedState();
        extendedState.getVariables().put(OrderCaptureConstants.CONFIGURATION_STATE, configurationState);
        extendedState.getVariables().put(OrderCaptureConstants.IS_MODIFICATION_OR_TERMINATION_OR_MIGRATION_USE_CASE, isModificationOrTerminationUseCase);
        extendedState.getVariables().put(OrderCaptureConstants.RELATED_PARTY, createProcessRelatedParty());
        extendedState.getVariables().put(OrderCaptureConstants.RELATED_ENTITY, createRelatedEntity());
        extendedState.getVariables().put(OrderCaptureConstants.REQUESTED_CONFIGURATION_ACTION, configurationAction);
        extendedState.getVariables().put(OrderCaptureConstants.TASK_CHANNEL, createRelatedChannels());
        extendedState.getVariables().put(OrderCaptureConstants.CONTRACT_PRODUCT_ID, PRODUCT_ID);

        ObjectStateMachine<String, String> stateMachine = createStateMachine();

        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, null, null, extendedState, null, stateMachine, null,
                null, null);
    }

    private List<Product> getProduct(String parentId, String childSpecId) {
        List<com.orange.discobole.productinventory.dto.v1.ProductRelationship> productRelationships = Collections.singletonList(com.orange.discobole.productinventory.dto.v1.ProductRelationship.builder()
                .relationshipType(BUNDLES)
                .product(ProductRef.builder()
                        .id(PRODUCT_ID_5)
                        .build())
                .build());
        Product parentProduct = Product.builder()
                .id(parentId)
                .productRelationship(productRelationships)
                .build();
        Product childProduct = Product.builder()
                .id(PRODUCT_ID_5)
                .productSpecification(com.orange.discobole.productinventory.dto.v1.ProductSpecificationRef.builder()
                        .id(childSpecId)
                        .build())
                .productRelationship(Collections.emptyList())
                .build();
        List<Product> products = new ArrayList<>();
        products.add(parentProduct);
        products.add(childProduct);
        return products;
    }

    private ObjectStateMachine<String, String> createStateMachine() {
        ObjectState<String, String> source = new ObjectState<>("CANCEL");
        ObjectState<String, String> target = new ObjectState<>("END");
        Collection<State<String, String>> states = new ArrayList<>();
        states.add(source);
        states.add(target);

        DefaultExternalTransition<String, String> transition = new DefaultExternalTransition<>(source, target, null, "cancelProcess", null, null, null);
        List<Transition<String, String>> transitions = new ArrayList<>();
        transitions.add(transition);

        return new ObjectStateMachine<>(states, transitions, source);
    }

    static Stream<Arguments> provideTestDataForExclusionCases() {
        return Stream.of(
                Arguments.of("04", "06", PRODUCT_ID_2, "99"), //No reliesOn relationship
                Arguments.of("04", "05", PRODUCT_ID_2, "06"), //item and its reliesOn relationship are belong the same parent
                Arguments.of("05", "07", PRODUCT_ID_2, "07"), //use case of order item and its relationship reliesOn already created
                Arguments.of("05", "07", PRODUCT_ID_7, "07"), //use case of order item and its relationship reliesOn already created but not belong the same parent
                Arguments.of("05", "09", PRODUCT_ID_2, "07") //use case of order item and its relationship reliesOn not belong the same parent
        );
    }

    static Stream<Arguments> provideTestDataForAcquisitionExclusionCases() {
        return Stream.of(
                Arguments.of("04", "07"), //No reliesOn relationship
                Arguments.of("04", "05"), //item and its reliesOn relationship are belong the same parent
                Arguments.of("04", "09") //use case of order item and its relationship reliesOn noy belong the same parent
        );
    }
}