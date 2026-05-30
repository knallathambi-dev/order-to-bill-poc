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
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.ObjectStateMachine;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.support.DefaultExtendedState;
import org.springframework.statemachine.support.DefaultStateContext;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderStateType.DRAFT;
import static java.lang.Boolean.FALSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogDrivenTasksActionTest {

    public static final String DEFAULT_HREF = RandomStringUtils.randomAlphabetic(10);
    public static final String DEFAULT_PRODUCT_ORDER_ID = RandomStringUtils.randomAlphabetic(10);
    public static final Instant DEFAULT_CREATION_DATE = Instant.now();
    public static final String DEFAULT_RELATED_RESOURCE_ID_1 = RandomStringUtils.randomAlphabetic(3);
    public static final String DEFAULT_RELATED_RESOURCE_ID_2 = RandomStringUtils.randomAlphabetic(3);
    public static final String PRODUCT_ORDER_ID_1 = RandomStringUtils.randomAlphabetic(3);
    public static final String PRODUCT_ORDER_ID_2 = RandomStringUtils.randomAlphabetic(3);
    public static final String PRODUCT_SPEC_ID = RandomStringUtils.randomAlphabetic(3);
    public static final String DEFAULT_ORDER_ITEM_ID = RandomStringUtils.randomAlphabetic(5);

    @Mock
    private ProductOrderService productOrderService;
    @Mock
    private ProductSpecificationService productSpecificationService;
    @Mock
    private SettingsService settingsService;

    @InjectMocks
    private CatalogDrivenTasksAction catalogDrivenTasksAction;

    @Test
    @DisplayName("Given re-executed action, " +
            "when applying catalog driven tasks, " +
            "then SSM context variables should not be set")
    void shouldNotSetSSMContextVariablesWhenReExecuted() {
        // Given
        StateContext<String, String> context = mockStateContext(ItemActionType.ADD);
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, true);

        // When
        Mono<Void> result = catalogDrivenTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        boolean areCatalogDrivenTasksSet = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_CATALOG_DRIVEN_TASKS_SET, FALSE);
        assertThat(areCatalogDrivenTasksSet).isFalse();
    }

    @Test
    @DisplayName("Given an exception in get product order related resources ids service, " +
            "when applying catalog driven tasks, " +
            "then SSM context variables should not be set")
    void shouldNotSetSSMContextVariablesWhenExceptionInGetProductOrderRelatedResourcesIdsService() {
        // Given
        SettingsEntity settingsEntity = createSettings();
        when(settingsService.getSettings()).thenReturn(settingsEntity);
        List<ProductSpecification> productSpecifications = createProductSpecifications();
        when(productSpecificationService.fetchProductSpecifications(any())).thenReturn(productSpecifications);
        when(productOrderService.getProductOrderItemLogicalResourcesIds(any(), any())).thenThrow(RuntimeException.class);
        StateContext<String, String> context = mockStateContext(ItemActionType.ADD);

        // When
        Mono<Void> result = catalogDrivenTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        boolean areCatalogDrivenTasksSet = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_CATALOG_DRIVEN_TASKS_SET, FALSE);
        assertThat(areCatalogDrivenTasksSet).isFalse();
    }

    @Test
    @DisplayName("Given an exception in get physical product order service, " +
            "when applying catalog driven tasks, " +
            "then SSM context variables should not be set")
    void shouldNotSetSSMContextVariablesWhenExceptionInGetPhysicalProductOrderService() {
        // Given
        SettingsEntity settingsEntity = createSettings();
        when(settingsService.getSettings()).thenReturn(settingsEntity);
        List<ProductSpecification> productSpecifications = createProductSpecifications();
        when(productSpecificationService.fetchProductSpecifications(any())).thenReturn(productSpecifications);
        Map<String, List<String>> expectedProductOrderItemSpecIdMap = createRelatedResourceIdList();
        when(productOrderService.getProductOrderItemLogicalResourcesIds(any(), any())).thenReturn(expectedProductOrderItemSpecIdMap);
        when(productOrderService.getPhysicalProductOrderItems(any(), any())).thenThrow(RuntimeException.class);
        StateContext<String, String> context = mockStateContext(ItemActionType.ADD);

        // When
        Mono<Void> result = catalogDrivenTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        boolean areCatalogDrivenTasksSet = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_CATALOG_DRIVEN_TASKS_SET, FALSE);
        assertThat(areCatalogDrivenTasksSet).isFalse();
    }

    @Test
    @DisplayName("Given valid related resources id list and reserveLogicalResource is disabled, " +
            "when applying catalog driven tasks, " +
            "then SSM context variables should be set")
    void shouldSetSSMContextVariablesWhenValidRelatedResourcesIdListAndReserveLogicalResourceIsDisabled() {
        // Given
        SettingsEntity settingsEntity = createSettings();
        settingsEntity.setReserveLogicalResourceEnabled(false);
        when(settingsService.getSettings()).thenReturn(settingsEntity);
        List<ProductSpecification> productSpecifications = createProductSpecifications();
        when(productSpecificationService.fetchProductSpecifications(any())).thenReturn(productSpecifications);
        List<String> expectedPhysicalProductOrderItems = Collections.singletonList(DEFAULT_ORDER_ITEM_ID);
        when(productOrderService.getPhysicalProductOrderItems(any(), any())).thenReturn(expectedPhysicalProductOrderItems);
        StateContext<String, String> context = mockStateContext(ItemActionType.ADD);

        // When
        Mono<Void> result = catalogDrivenTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        boolean areCatalogDrivenTasksSet = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_CATALOG_DRIVEN_TASKS_SET, FALSE);
        Map<String, List<String>> actualProductOrderItemLogicalResourcesMap = StateMachineUtil.getMapValue(context.getExtendedState().getVariables(), OrderCaptureConstants.PRODUCT_ORDER_ITEM_LOGICAL_RESOURCES);
        List<String> physicalProductOrderItems = StateMachineUtil.getListValue(context.getExtendedState().getVariables(), OrderCaptureConstants.PHYSICAL_PRODUCT_ITEM_ID_LIST, String.class);
        assertThat(areCatalogDrivenTasksSet).isTrue();
        assertThat(actualProductOrderItemLogicalResourcesMap).isEmpty();
        assertThat(physicalProductOrderItems).isEqualTo(expectedPhysicalProductOrderItems);
    }

    @Test
    @DisplayName("Given valid related resources id list and modify order item action, " +
            "when applying catalog driven tasks, " +
            "then SSM context variables should be set")
    void shouldSetSSMContextVariablesWhenValidRelatedResourcesIdListAndModifyOrderItemAction() {
        // Given
        StateContext<String, String> context = mockStateContext(ItemActionType.MODIFY);

        // When
        Mono<Void> result = catalogDrivenTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        boolean areCatalogDrivenTasksSet = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_CATALOG_DRIVEN_TASKS_SET, FALSE);
        Map<String, List<String>> actualProductOrderItemLogicalResourcesMap = StateMachineUtil.getMapValue(context.getExtendedState().getVariables(), OrderCaptureConstants.PRODUCT_ORDER_ITEM_LOGICAL_RESOURCES);
        List<String> physicalProductOrderItems = StateMachineUtil.getListValue(context.getExtendedState().getVariables(), OrderCaptureConstants.PHYSICAL_PRODUCT_ITEM_ID_LIST, String.class);
        assertThat(areCatalogDrivenTasksSet).isTrue();
        assertThat(actualProductOrderItemLogicalResourcesMap).isEmpty();
        assertThat(physicalProductOrderItems).isEmpty();
    }

    @Test
    @DisplayName("Given valid related resources id list and order item without specification, " +
            "when applying catalog driven tasks, " +
            "then SSM context variables should be set")
    void shouldSetSSMContextVariablesWhenValidRelatedResourcesIdListWithoutSpec() {
        // Given
        StateContext<String, String> context = mockStateContext(ItemActionType.ADD);
        context.getExtendedState().getVariables().put(OrderCaptureConstants.CREATED_PRODUCT_ORDER, createProductOrderWithNoSpec());

        // When
        Mono<Void> result = catalogDrivenTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        boolean areCatalogDrivenTasksSet = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_CATALOG_DRIVEN_TASKS_SET, FALSE);
        Map<String, List<String>> actualProductOrderItemLogicalResourcesMap = StateMachineUtil.getMapValue(context.getExtendedState().getVariables(), OrderCaptureConstants.PRODUCT_ORDER_ITEM_LOGICAL_RESOURCES);
        List<String> physicalProductOrderItems = StateMachineUtil.getListValue(context.getExtendedState().getVariables(), OrderCaptureConstants.PHYSICAL_PRODUCT_ITEM_ID_LIST, String.class);
        assertThat(areCatalogDrivenTasksSet).isTrue();
        assertThat(actualProductOrderItemLogicalResourcesMap).isEmpty();
        assertThat(physicalProductOrderItems).isEmpty();
    }

    @Test
    @DisplayName("Given valid related resources id list, " +
            "when applying catalog driven tasks, " +
            "then SSM context variables should be set")
    void shouldSetSSMContextVariablesWhenValidRelatedResourcesIdList() {
        // Given
        SettingsEntity settingsEntity = createSettings();
        when(settingsService.getSettings()).thenReturn(settingsEntity);
        List<ProductSpecification> productSpecifications = createProductSpecifications();
        when(productSpecificationService.fetchProductSpecifications(any())).thenReturn(productSpecifications);
        Map<String, List<String>> expectedProductOrderItemSpecIdMap = createRelatedResourceIdList();
        when(productOrderService.getProductOrderItemLogicalResourcesIds(any(), any())).thenReturn(expectedProductOrderItemSpecIdMap);
        List<String> expectedPhysicalProductOrderItems = Collections.singletonList(DEFAULT_ORDER_ITEM_ID);
        when(productOrderService.getPhysicalProductOrderItems(any(), any())).thenReturn(expectedPhysicalProductOrderItems);
        StateContext<String, String> context = mockStateContext(ItemActionType.ADD);

        // When
        Mono<Void> result = catalogDrivenTasksAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        boolean areCatalogDrivenTasksSet = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_CATALOG_DRIVEN_TASKS_SET, FALSE);
        Map<String, List<String>> actualProductOrderItemLogicalResourcesMap = StateMachineUtil.getMapValue(context.getExtendedState().getVariables(), OrderCaptureConstants.PRODUCT_ORDER_ITEM_LOGICAL_RESOURCES);
        List<String> physicalProductOrderItems = StateMachineUtil.getListValue(context.getExtendedState().getVariables(), OrderCaptureConstants.PHYSICAL_PRODUCT_ITEM_ID_LIST, String.class);
        assertThat(areCatalogDrivenTasksSet).isTrue();
        assertThat(actualProductOrderItemLogicalResourcesMap).isEqualTo(expectedProductOrderItemSpecIdMap);
        assertThat(physicalProductOrderItems).isEqualTo(expectedPhysicalProductOrderItems);
    }

    private Map<String, List<String>> createRelatedResourceIdList() {
        Map<String, List<String>> productOrderItemRelatedResourceIds = new HashMap<>();
        productOrderItemRelatedResourceIds.put(PRODUCT_ORDER_ID_1, List.of(DEFAULT_RELATED_RESOURCE_ID_1));
        productOrderItemRelatedResourceIds.put(PRODUCT_ORDER_ID_2, List.of(DEFAULT_RELATED_RESOURCE_ID_2));
        return productOrderItemRelatedResourceIds;
    }

    private ProductOrder createProductOrder(ItemActionType orderItemActionType) {
        ProductOrderItem productOrderItem = ProductOrderItem.builder()
                .id(DEFAULT_ORDER_ITEM_ID)
                .action(orderItemActionType)
                .product(Product.builder()
                        .productSpecification(ProductSpecificationRef.builder()
                                .id(PRODUCT_SPEC_ID)
                                .build())
                        .build())
                .build();
        return ProductOrder.builder()
                .id(DEFAULT_PRODUCT_ORDER_ID)
                .href(DEFAULT_HREF)
                .creationDate(DEFAULT_CREATION_DATE)
                .productOrderItem(Collections.singletonList(productOrderItem))
                .state(DRAFT)
                .build();
    }

    private ProductOrder createProductOrderWithNoSpec() {
        ProductOrderItem productOrderItem = ProductOrderItem.builder()
                .id(DEFAULT_ORDER_ITEM_ID)
                .action(ItemActionType.ADD)
                .build();
        return ProductOrder.builder()
                .id(DEFAULT_PRODUCT_ORDER_ID)
                .href(DEFAULT_HREF)
                .creationDate(DEFAULT_CREATION_DATE)
                .productOrderItem(Collections.singletonList(productOrderItem))
                .state(DRAFT)
                .build();
    }

    private SettingsEntity createSettings() {
        return SettingsEntity.builder()
                .reservePhysicalResourceEnabled(true)
                .reserveLogicalResourceEnabled(true)
                .checkCommercialEligibilityEnabled(true)
                .checkPaymentRefEnabled(true)
                .build();
    }

    private List<ProductSpecification> createProductSpecifications() {
        return Collections.singletonList(ProductSpecification.builder()
                .id(PRODUCT_SPEC_ID)
                .build());
    }

    private DefaultStateContext<String, String> mockStateContext(ItemActionType itemActionType) {
        ExtendedState extendedState = new DefaultExtendedState();
        extendedState.getVariables().put(OrderCaptureConstants.CREATED_PRODUCT_ORDER, createProductOrder(itemActionType));

        ObjectStateMachine<String, String> stateMachine = new ObjectStateMachine<>(null, null, null, null, null,
                extendedState, null);

        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, null, null, extendedState, null, stateMachine, null,
                null, null);
    }
}