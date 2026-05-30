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
import com.orange.discobole.ordermanagement.commons.dto.product.specification.ProductSpecification;
import com.orange.discobole.ordermanagement.commons.dto.service.qualification.CheckServiceQualification;
import com.orange.discobole.ordermanagement.commons.dto.service.qualification.GeographicSite;
import com.orange.discobole.ordermanagement.commons.dto.service.qualification.Service;
import com.orange.discobole.ordermanagement.commons.dto.service.qualification.ServiceQualificationItem;
import com.orange.discobole.ordermanagement.ordercapture.constant.GuardNameConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductConfigurationService;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductSpecificationService;
import com.orange.discobole.ordermanagement.ordercapture.service.ServiceQualificationManagementService;
import com.orange.discobole.ordermanagement.ordercapture.service.SettingsService;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.processflow.ssm.guard.StateMachineGuard;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants.*;
import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.*;


@Component("isTechnicalEligibleGuard")
@Slf4j
public class TechnicalEligibilityGuard implements StateMachineGuard<String, String> {

    private final SettingsService settingsService;
    private final ProductConfigurationService productConfigurationService;
    private final ProductSpecificationService productSpecificationService;
    private final ServiceQualificationManagementService serviceQualificationManagementService;

    public TechnicalEligibilityGuard(SettingsService settingsService, ProductConfigurationService productConfigurationService, ProductSpecificationService productSpecificationService, ServiceQualificationManagementService serviceQualificationManagementService) {
        this.settingsService = settingsService;
        this.productConfigurationService = productConfigurationService;
        this.productSpecificationService = productSpecificationService;
        this.serviceQualificationManagementService = serviceQualificationManagementService;
    }

    @Override
    public Mono<Boolean> apply(StateContext<String, String> context) {
        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            return StateMachineUtil.getGuardResult(context, GuardNameConstants.TECHNICAL_ELIGIBILITY_GUARD);
        } else {
            try {
                log.info("Inside guard to check the technical eligibility");
                if (settingsService.getSettings().isCheckTechnicalEligibilityEnabled()) {
                    QueryProductConfigurationItem itemWithAddressCharacteristic = getConfigurationItemWithAddressCharacteristic(context);
                    if (Objects.nonNull(itemWithAddressCharacteristic)) {
                        String addressId = getAddressId(itemWithAddressCharacteristic);
                        if (Objects.isNull(addressId)) {
                            StateMachineUtil.setGuardContext(context, false, GuardNameConstants.TECHNICAL_ELIGIBILITY_GUARD);
                            StateMachineUtil.setDescriptionContext(context, ADDRESS_ID_REQUIRED_DESCRIPTION);
                            return Mono.just(false);
                        } else {
                            return checkIfQualifiedItem(addressId, context);
                        }
                    }
                }
                StateMachineUtil.setGuardContext(context, true, GuardNameConstants.TECHNICAL_ELIGIBILITY_GUARD);
                return Mono.just(true);
            } catch (Exception e) {
                log.error("Unable to verify the check of the technical eligibility [{}]:", e.getMessage(), e);
                StateMachineUtil.setDescriptionContext(context, INTERNAL_SERVER_ERROR);
                StateMachineUtil.setNextTaskToNull(context);
                StateMachineUtil.setGuardContext(context, false, GuardNameConstants.TECHNICAL_ELIGIBILITY_GUARD);
                return Mono.just(false);
            }
        }
    }

    private Mono<Boolean> checkIfQualifiedItem(String addressId, StateContext<String, String> context) {
        CheckServiceQualification checkServiceQualificationDto = createServiceQualification(addressId);
        CheckServiceQualification checkServiceQualificationCreated = serviceQualificationManagementService.checkServiceQualification(checkServiceQualificationDto);
        String isQualified = checkServiceQualificationCreated.getQualificationResult();
        if (UNQUALIFIED.equals(isQualified)) {
            StateMachineUtil.setGuardContext(context, false, GuardNameConstants.TECHNICAL_ELIGIBILITY_GUARD);
            StateMachineUtil.setDescriptionContext(context, TECHNICAL_ELIGIBILITY_DESCRIPTION);
            return Mono.just(false);
        }
        context.getExtendedState().getVariables().put(IS_APPOINTMENT_REQUIRED, checkServiceQualificationCreated.getIsAppointmentRequired());
        StateMachineUtil.setGuardContext(context, true, GuardNameConstants.TECHNICAL_ELIGIBILITY_GUARD);
        return Mono.just(true);
    }

    private QueryProductConfigurationItem getConfigurationItemWithAddressCharacteristic(StateContext<String, String> context) {
        String configurationId = StateMachineUtil.getStringValue(context, CONFIGURATION_ID);
        QueryProductConfiguration extractedConfiguration = productConfigurationService.getProductConfigurationById(configurationId);
        return getItemWithAddressCharacteristic(extractedConfiguration);
    }

    private String getAddressId(QueryProductConfigurationItem itemWithAddressCharacteristic) {
        return itemWithAddressCharacteristic.getProductConfiguration().getConfigurationCharacteristics()
                .stream()
                .flatMap(configurationCharacteristic -> configurationCharacteristic.getConfigurationCharacteristicValues().stream())
                .filter(ConfigurationCharacteristicValue::getIsSelected)
                .map(ConfigurationCharacteristicValue::getCharacteristic)
                .filter(AddressCharacteristic.class::isInstance)
                .map(AddressCharacteristic.class::cast)
                .map(AddressCharacteristic::getAddressId)
                .filter(addressId -> !StringUtils.isBlank(addressId))
                .findFirst()
                .orElse(null);
    }

    private CheckServiceQualification createServiceQualification(String addressId) {
        GeographicSite geographicSite = GeographicSite.builder().id(addressId).build();
        Service service = Service.builder().place(List.of(geographicSite)).build();
        ServiceQualificationItem serviceQualificationItem = ServiceQualificationItem.builder().service(service).build();
        return CheckServiceQualification.builder()
                .serviceQualificationItem(List.of(serviceQualificationItem))
                .build();
    }

    private QueryProductConfigurationItem getItemWithAddressCharacteristic(QueryProductConfiguration extractedConfiguration) {
        return extractedConfiguration
                .getComputedProductConfigurationItems()
                .stream()
                .filter(item -> ServiceConstants.ATOMIC_PRODUCT_OFFERING_TYPE.equals(item.getProductConfiguration().getProductOffering().getReferredType()))
                .filter(queryProductConfigurationItem -> hasSelectedHierarchy(extractedConfiguration.getComputedProductConfigurationItems(), queryProductConfigurationItem)
                        && hasSelectedConfigurationAction(queryProductConfigurationItem))
                .filter(this::isAddressCharacteristic)
                .filter(this::isQualificationRequired)
                .findFirst()
                .orElse(null);
    }

    private boolean isAddressCharacteristic(QueryProductConfigurationItem queryProductConfigurationItem) {
        List<ConfigurationCharacteristic> characteristics = queryProductConfigurationItem.getProductConfiguration().getConfigurationCharacteristics();
        if (CollectionUtils.isEmpty(characteristics)) {
            return false;
        }

        return characteristics.stream()
                .anyMatch(characteristic -> {
                    List<ConfigurationCharacteristicValue> values = characteristic.getConfigurationCharacteristicValues();
                    return !CollectionUtils.isEmpty(values) && values.stream()
                            .anyMatch(value -> value.getIsSelected() && value.getCharacteristic() instanceof AddressCharacteristic);
                });
    }

    private boolean hasSelectedConfigurationAction(QueryProductConfigurationItem queryProductConfigurationItem) {
        return queryProductConfigurationItem.getProductConfiguration().getConfigurationActions().stream()
                .anyMatch(ConfigurationAction::getIsSelected);
    }

    private boolean hasSelectedHierarchy(List<QueryProductConfigurationItem> bundledConfigurationItems, QueryProductConfigurationItem productConfigurationItem) {
        List<QueryProductConfigurationItem> queryProductConfigurationItems = new ArrayList<>();
        buildProductHierarchy(bundledConfigurationItems, productConfigurationItem, queryProductConfigurationItems);

        return queryProductConfigurationItems.stream()
                .map(QueryProductConfigurationItem::getProductConfiguration)
                .allMatch(ProductConfiguration::getIsSelected);
    }

    private void buildProductHierarchy(List<QueryProductConfigurationItem> bundledConfigurationItems,
                                       QueryProductConfigurationItem currentConfigurationItem,
                                       List<QueryProductConfigurationItem> hierarchy) {
        Optional.ofNullable(getParentProductConfigurationItem(bundledConfigurationItems, currentConfigurationItem))
                .ifPresent(parent -> buildProductHierarchy(bundledConfigurationItems, parent, hierarchy));

        hierarchy.add(currentConfigurationItem);
    }

    private QueryProductConfigurationItem getParentProductConfigurationItem(List<QueryProductConfigurationItem> queryProductConfigurationItems,
                                                                            QueryProductConfigurationItem productConfigurationItem) {
        return queryProductConfigurationItems.stream()
                .filter(item -> hasBundleRelationship(item, productConfigurationItem))
                .findFirst()
                .orElse(null);
    }

    private boolean hasBundleRelationship(QueryProductConfigurationItem item,
                                          QueryProductConfigurationItem productConfigurationItem) {
        return Optional.ofNullable(item.getProductConfigurationItemRelationships())
                .map(relationships -> relationships.stream()
                        .anyMatch(relationship -> isBundleRelationship(relationship, productConfigurationItem)))
                .orElse(false);
    }

    private boolean isBundleRelationship(ProductConfigurationItemRelationship relationship,
                                         QueryProductConfigurationItem productConfigurationItem) {
        return BUNDLES.equals(relationship.getRelationshipType()) &&
                productConfigurationItem.getId().equals(relationship.getId());
    }

    private boolean isQualificationRequired(QueryProductConfigurationItem queryProductConfigurationItems) {
        ProductConfiguration productConfig = queryProductConfigurationItems.getProductConfiguration();
        String specificationId = (productConfig.getProductSpecification() != null)
                ? productConfig.getProductSpecification().getId()
                : null;
        if (specificationId != null) {
            List<ProductSpecification> specifications = productSpecificationService.fetchProductSpecifications(List.of(specificationId));
            if (!CollectionUtils.isEmpty(specifications)) {
                String configurationAction = productConfig.getConfigurationActions().stream()
                        .filter(ConfigurationAction::getIsSelected)
                        .map(ConfigurationAction::getAction)
                        .findFirst()
                        .orElse(null);
                if (!StringUtils.isBlank(configurationAction)) {
                    return !CollectionUtils.isEmpty(specifications.get(0).getOperationSpecification()) && specifications
                            .get(0)
                            .getOperationSpecification()
                            .stream()
                            .anyMatch(opSpec -> configurationAction.equals(opSpec.getName()) && Boolean.TRUE.equals(opSpec.getIsQualificationRequested()));
                }
            }
        }
        return false;
    }
}