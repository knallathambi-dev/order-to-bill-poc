// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.guard;

import com.orange.discobole.ordermanagement.commons.dto.product.configuration.*;
import com.orange.discobole.ordermanagement.commons.dto.product.specification.OperationSpecification;
import com.orange.discobole.ordermanagement.commons.dto.product.specification.ProductSpecification;
import com.orange.discobole.ordermanagement.commons.dto.product.specification.ProductSpecificationRelationship;
import com.orange.discobole.ordermanagement.commons.dto.service.qualification.CheckServiceQualification;
import com.orange.discobole.ordermanagement.commons.dto.service.qualification.GeographicSite;
import com.orange.discobole.ordermanagement.commons.dto.service.qualification.Service;
import com.orange.discobole.ordermanagement.commons.dto.service.qualification.ServiceQualificationItem;
import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.GuardNameConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants;
import com.orange.discobole.ordermanagement.ordercapture.domain.SettingsEntity;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductConfigurationService;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductSpecificationService;
import com.orange.discobole.ordermanagement.ordercapture.service.ServiceQualificationManagementService;
import com.orange.discobole.ordermanagement.ordercapture.service.SettingsService;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
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

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TechnicalEligibilityGuardTest {

    private static final String PRODUCT_OFFERING_ID = RandomStringUtils.randomAlphabetic(5);
    private static final String CONFIGURATION_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String CHARACTERISTIC_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String ADDRESS_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String GEOGRAPHIC_SITE_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_SPECIFICATION_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_SPECIFICATION_RELATIONSHIP_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String ADDRESS_CHARACTERISTIC_TYPE = "AddressCharacteristic";
    private static final String QUALIFIED_STATUS = "qualified";

    @Mock
    private SettingsService settingsService;

    @Mock
    private ServiceQualificationManagementService serviceQualificationManagementService;

    @Mock
    private ProductSpecificationService productSpecificationService;

    @Mock
    private ProductConfigurationService productConfigurationService;

    @InjectMocks
    private TechnicalEligibilityGuard technicalEligibilityGuard;

    @Test
    @DisplayName("Given guard was already executed, " +
            "when confirm order, " +
            "then returns true")
    void shouldReturnTrueWhenGuardReExecuted() {
        // Given
        StateContext<String, String> context = createMockStateContext();
        StateMachineUtil.setGuardContext(context, Boolean.TRUE, GuardNameConstants.TECHNICAL_ELIGIBILITY_GUARD);
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, Boolean.TRUE);

        // When
        Mono<Boolean> result = technicalEligibilityGuard.apply(context);

        // Then
        StepVerifier.create(result).expectNext(true).verifyComplete();
    }

    @Test
    @DisplayName("Given technical eligibility check is disabled, " +
            "when confirm order, " +
            "then returns true")
    void shouldReturnTrueWhenTechnicalEligibilityCheckIsDisabled() {
        // Given
        setTechnicalEligibilityFlag(false);
        StateContext<String, String> context = createMockStateContext();

        // When
        Mono<Boolean> result = technicalEligibilityGuard.apply(context);

        // Then
        StepVerifier.create(result).expectNext(true).verifyComplete();
        Mono<Boolean> guardResult = StateMachineUtil.getGuardResult(context, GuardNameConstants.TECHNICAL_ELIGIBILITY_GUARD);
        StepVerifier.create(guardResult).expectNext(true).verifyComplete();
    }

    @Test
    @DisplayName("Given invalid configuration, " +
            "when confirm order, " +
            "then returns true")
    void shouldReturnTrueWhenConfigurationIsInvalid() {
        // Given
        setTechnicalEligibilityFlag(true);
        StateContext<String, String> context = createMockStateContext();

        when(productConfigurationService.getProductConfigurationById(any()))
                .thenReturn(createProductConfiguration(OrderCaptureConstants.MODIFICATION, OrderCaptureConstants.STOCK_ITEM_TYPE));

        // When
        Mono<Boolean> result = technicalEligibilityGuard.apply(context);

        // Then
        StepVerifier.create(result).expectNext(true).verifyComplete();
        Mono<Boolean> guardResult = StateMachineUtil.getGuardResult(context, GuardNameConstants.TECHNICAL_ELIGIBILITY_GUARD);
        StepVerifier.create(guardResult).expectNext(true).verifyComplete();
    }

    @Test
    @DisplayName("Given valid configuration with no qualification needed, " +
            "when confirm order, " +
            "then returns true")
    void shouldReturnTrueWhenNoQualificationNeeded() {
        // Given
        setTechnicalEligibilityFlag(true);
        StateContext<String, String> context = createMockStateContext();

        ProductSpecification productSpec = ProductSpecification
                .builder()
                .id(PRODUCT_SPECIFICATION_ID)
                .productSpecificationRelationship(Collections.singletonList(
                        ProductSpecificationRelationship.builder()
                                .id(PRODUCT_SPECIFICATION_RELATIONSHIP_ID)
                                .relationshipType("reliesOn")
                                .build()))
                .operationSpecification(Collections.singletonList(
                        OperationSpecification.builder()
                                .name("modify")
                                .isQualificationRequested(false)
                                .build()))
                .build();

        when(productConfigurationService.getProductConfigurationById(any()))
                .thenReturn(createProductConfiguration(OrderCaptureConstants.ADD, ADDRESS_CHARACTERISTIC_TYPE));
        when(productSpecificationService.fetchProductSpecifications(any()))
                .thenReturn(Collections.singletonList(productSpec));

        // When
        Mono<Boolean> result = technicalEligibilityGuard.apply(context);

        // Then
        StepVerifier.create(result).expectNext(true).verifyComplete();
        Mono<Boolean> guardResult = StateMachineUtil.getGuardResult(context, GuardNameConstants.TECHNICAL_ELIGIBILITY_GUARD);
        StepVerifier.create(guardResult).expectNext(true).verifyComplete();
    }

    @Test
    @DisplayName("Given valid configuration with qualification required and successful, " +
            "when confirm order, " +
            "then returns true")
    void shouldReturnTrueWhenQualificationRequiredAndSuccessful() {
        // Given
        setTechnicalEligibilityFlag(true);
        StateContext<String, String> context = createMockStateContext();

        ProductSpecification productSpec = ProductSpecification.builder()
                .id(PRODUCT_SPECIFICATION_ID)
                .productSpecificationRelationship(Collections.singletonList(
                        ProductSpecificationRelationship.builder()
                                .id(PRODUCT_SPECIFICATION_RELATIONSHIP_ID)
                                .relationshipType("reliesOn")
                                .build()))
                .operationSpecification(Collections.singletonList(
                        OperationSpecification.builder()
                                .name("add")
                                .isQualificationRequested(true)
                                .build()))
                .build();

        when(productConfigurationService.getProductConfigurationById(any()))
                .thenReturn(createProductConfiguration(OrderCaptureConstants.ADD, ADDRESS_CHARACTERISTIC_TYPE));
        when(productSpecificationService.fetchProductSpecifications(any()))
                .thenReturn(Collections.singletonList(productSpec));
        when(serviceQualificationManagementService.checkServiceQualification(any()))
                .thenReturn(createServiceQualification(QUALIFIED_STATUS));

        // When
        Mono<Boolean> result = technicalEligibilityGuard.apply(context);

        // Then
        StepVerifier.create(result).expectNext(true).verifyComplete();
        Mono<Boolean> guardResult = StateMachineUtil.getGuardResult(context, GuardNameConstants.TECHNICAL_ELIGIBILITY_GUARD);
        StepVerifier.create(guardResult).expectNext(true).verifyComplete();
    }

    @Test
    @DisplayName("Given valid configuration with qualification required and failed, " +
            "when confirm order, " +
            "then returns false")
    void shouldReturnFalseWhenQualificationRequiredAndFailed() {
        // Given
        setTechnicalEligibilityFlag(true);
        StateContext<String, String> context = createMockStateContext();

        ProductSpecification productSpec = ProductSpecification.builder()
                .id(PRODUCT_SPECIFICATION_ID)
                .productSpecificationRelationship(Collections.singletonList(
                        ProductSpecificationRelationship.builder()
                                .id(PRODUCT_SPECIFICATION_RELATIONSHIP_ID)
                                .relationshipType("reliesOn")
                                .build()))
                .operationSpecification(Collections.singletonList(
                        OperationSpecification.builder()
                                .name("add")
                                .isQualificationRequested(true)
                                .build()))
                .build();

        when(productConfigurationService.getProductConfigurationById(any()))
                .thenReturn(createProductConfiguration(OrderCaptureConstants.ADD, ADDRESS_CHARACTERISTIC_TYPE));
        when(productSpecificationService.fetchProductSpecifications(any()))
                .thenReturn(Collections.singletonList(productSpec));
        when(serviceQualificationManagementService.checkServiceQualification(any()))
                .thenReturn(createServiceQualification("unqualified"));

        // When
        Mono<Boolean> result = technicalEligibilityGuard.apply(context);

        // Then
        StepVerifier.create(result).expectNext(false).verifyComplete();
        Mono<Boolean> guardResult = StateMachineUtil.getGuardResult(context, GuardNameConstants.TECHNICAL_ELIGIBILITY_GUARD);
        StepVerifier.create(guardResult).expectNext(false).verifyComplete();
    }

    @Test
    @DisplayName("Given address ID is null, " +
            "when confirm order, " +
            "then returns false and sets description")
    void shouldReturnFalseWhenAddressIdIsNull() {
        // Given
        setTechnicalEligibilityFlag(true);
        StateContext<String, String> context = createMockStateContext();

        ProductSpecification productSpec = ProductSpecification.builder()
                .id(PRODUCT_SPECIFICATION_ID)
                .productSpecificationRelationship(Collections.singletonList(
                        ProductSpecificationRelationship.builder()
                                .id(PRODUCT_SPECIFICATION_RELATIONSHIP_ID)
                                .relationshipType("reliesOn")
                                .build()))
                .operationSpecification(Collections.singletonList(
                        OperationSpecification.builder()
                                .name("modify")
                                .isQualificationRequested(true)
                                .build()))
                .build();

        when(productConfigurationService.getProductConfigurationById(any()))
                .thenReturn(createProductConfigurationWithNullAddressId());
        when(productSpecificationService.fetchProductSpecifications(any()))
                .thenReturn(Collections.singletonList(productSpec));

        // When
        Mono<Boolean> result = technicalEligibilityGuard.apply(context);

        // Then
        StepVerifier.create(result).expectNext(false).verifyComplete();
        Mono<Boolean> guardResult = StateMachineUtil.getGuardResult(context, GuardNameConstants.TECHNICAL_ELIGIBILITY_GUARD);
        StepVerifier.create(guardResult).expectNext(false).verifyComplete();
        Assertions.assertEquals(DescriptionConstants.ADDRESS_ID_REQUIRED_DESCRIPTION, context.getExtendedState()
                .getVariables().get(StateMachineUtil.DESCRIPTION));
    }

    private StateContext<String, String> createMockStateContext() {
        ExtendedState extendedState = new DefaultExtendedState();
        extendedState.getVariables().put(OrderCaptureConstants.PRODUCT_OFFERING_ID, PRODUCT_OFFERING_ID);
        extendedState.getVariables().put(OrderCaptureConstants.CONFIGURATION_ID, CONFIGURATION_ID);
        ObjectStateMachine<String, String> stateMachine = new ObjectStateMachine<>(null, null, null, null, null, extendedState, null);
        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, null, null, extendedState, null, stateMachine, null, null, null);
    }

    private void setTechnicalEligibilityFlag(boolean enabled) {
        SettingsEntity settings = mock(SettingsEntity.class);
        when(settings.isCheckTechnicalEligibilityEnabled()).thenReturn(enabled);
        when(settingsService.getSettings()).thenReturn(settings);
    }

    private CheckServiceQualification createServiceQualification(String qualificationStatus) {
        GeographicSite geographicSite = GeographicSite.builder().id(GEOGRAPHIC_SITE_ID).build();
        Service service = Service.builder().place(Collections.singletonList(geographicSite)).build();
        ServiceQualificationItem qualificationItem = ServiceQualificationItem.builder().service(service).build();

        return CheckServiceQualification.builder()
                .isAppointmentRequired(Boolean.TRUE)
                .serviceQualificationItem(Collections.singletonList(qualificationItem))
                .qualificationResult(qualificationStatus)
                .build();
    }

    private QueryProductConfiguration createProductConfiguration(String action, String characteristicType) {
        ProductOfferingRef productOfferingRef = ProductOfferingRef.builder()
                .id(PRODUCT_OFFERING_ID)
                .referredType(ServiceConstants.ATOMIC_PRODUCT_OFFERING_TYPE)
                .build();

        ConfigurationAction configAction = ConfigurationAction.builder()
                .action(action)
                .isSelected(Boolean.TRUE)
                .build();

        ProductConfiguration productConfig = ProductConfiguration.builder()
                .configurationActions(Collections.singletonList(configAction))
                .productOffering(productOfferingRef)
                .isSelected(Boolean.TRUE)
                .configurationCharacteristics(Collections.singletonList(
                        ConfigurationCharacteristic.builder()
                                .configurationCharacteristicValues(Collections.singletonList(
                                        ConfigurationCharacteristicValue.builder()
                                                .isSelected(Boolean.TRUE)
                                                .characteristic(createCharacteristicValue(characteristicType))
                                                .type(characteristicType)
                                                .build()))
                                .type(characteristicType)
                                .build()))
                .productSpecification(ProductSpecificationRef.builder().id(ADDRESS_ID).build())
                .build();

        QueryProductConfigurationItem requestedItem = QueryProductConfigurationItem.builder()
                .productConfiguration(productConfig)
                .build();

        return QueryProductConfiguration.builder()
                .requestedProductConfigurationItems(Collections.singletonList(requestedItem))
                .computedProductConfigurationItems(Collections.singletonList(requestedItem))
                .build();
    }

    private QueryProductConfiguration createProductConfigurationWithNullAddressId() {
        ProductOfferingRef productOfferingRef = ProductOfferingRef.builder()
                .id(PRODUCT_OFFERING_ID)
                .referredType(ServiceConstants.ATOMIC_PRODUCT_OFFERING_TYPE)
                .build();

        ConfigurationAction configAction = ConfigurationAction.builder()
                .action(OrderCaptureConstants.MODIFICATION)
                .isSelected(Boolean.TRUE)
                .build();

        ProductConfiguration productConfig = ProductConfiguration.builder()
                .configurationActions(Collections.singletonList(configAction))
                .productOffering(productOfferingRef)
                .isSelected(Boolean.TRUE)
                .configurationCharacteristics(Collections.singletonList(
                        ConfigurationCharacteristic.builder()
                                .configurationCharacteristicValues(Collections.singletonList(
                                        ConfigurationCharacteristicValue.builder()
                                                .isSelected(Boolean.TRUE)
                                                .characteristic(AddressCharacteristic.builder()
                                                        .id(CHARACTERISTIC_ID)
                                                        .type(ADDRESS_CHARACTERISTIC_TYPE)
                                                        .build())
                                                .type(ADDRESS_CHARACTERISTIC_TYPE)
                                                .build()))
                                .type(ADDRESS_CHARACTERISTIC_TYPE)
                                .build()))
                .productSpecification(ProductSpecificationRef.builder().id(ADDRESS_ID).build())
                .build();

        QueryProductConfigurationItem requestedItem = QueryProductConfigurationItem.builder()
                .productConfiguration(productConfig)
                .build();

        return QueryProductConfiguration.builder()
                .requestedProductConfigurationItems(Collections.singletonList(requestedItem))
                .computedProductConfigurationItems(Collections.singletonList(requestedItem))
                .build();
    }

    private CharacteristicValue createCharacteristicValue(String characteristicType) {
        if (ADDRESS_CHARACTERISTIC_TYPE.equals(characteristicType)) {
            return AddressCharacteristic.builder()
                    .id(CHARACTERISTIC_ID)
                    .addressId(ADDRESS_ID)
                    .type(characteristicType)
                    .build();
        }
        return CharacteristicValue.builder()
                .id(CHARACTERISTIC_ID)
                .type(characteristicType)
                .build();
    }
}