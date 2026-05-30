// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.mapper.product.order;

import com.orange.discobole.ordermanagement.commons.dto.product.configuration.*;
import com.orange.discobole.ordermanagement.commons.dto.product.configuration.AddressCharacteristic;
import com.orange.discobole.ordermanagement.commons.dto.product.configuration.DateCharacteristic;
import com.orange.discobole.ordermanagement.commons.dto.product.configuration.ObjectCharacteristic;
import com.orange.discobole.ordermanagement.commons.dto.product.configuration.Price;
import com.orange.discobole.ordermanagement.commons.dto.product.configuration.PriceAlteration;
import com.orange.discobole.ordermanagement.commons.dto.product.configuration.ProductOfferingPriceRef;
import com.orange.discobole.ordermanagement.commons.dto.product.configuration.ProductOfferingRef;
import com.orange.discobole.ordermanagement.commons.dto.product.configuration.ProductRef;
import com.orange.discobole.ordermanagement.commons.dto.product.configuration.StringCharacteristic;
import com.orange.discobole.ordermanagement.commons.dto.product.configuration.TaxItem;
import com.orange.discobole.ordermanagement.commons.dto.product.configuration.TimePeriod;
import com.orange.discobole.ordermanagement.commons.dto.product.configuration.ValidityCharacteristic;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants;
import com.orange.discobole.ordermanagement.ordercapture.util.CurrencyUtils;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.Characteristic;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.PartyRef;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOfferingPriceRelationship;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductSpecificationRef;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.RelatedPartyRefOrPartyRoleRef;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Condition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.*;
import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.ATOMIC_PRODUCT_OFFERING_TYPE;
import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.ORDER_ITEM_RELATIONSHIP_TYPE;

@Component
@Mapper(componentModel = "spring")
public interface ProductOrderMapper {
    String ORDER_PRICE = "OrderPrice";
    String PRICE = "Price";
    String PRICE_ALTERATION = "PriceAlteration";
    String PRODUCT_OFFERING_PRICE_REF = "ProductOfferingPriceRef";
    String INSTALLMENT_CHARGE = "InstallmentCharge";
    String PRODUCT_OFFERING_PRICE_CHARGE = "ProductOfferingPriceCharge";
    String PRODUCT_OFFERING_PRICE_RELATIONSHIP = "ProductOfferingPriceRelationship";
    String TAX_ITEM = "TaxItem";
    String ORDER_TERM = "OrderTerm";

    default List<ProductOrderItem> mapQueryConfigToOrderItems(QueryProductConfiguration productConfig, String configAction) {
        if (productConfig == null || productConfig.getComputedProductConfigurationItems() == null) {
            return Collections.emptyList();
        }
        List<QueryProductConfigurationItem> selectedProductConfigItems = getSelectedProductConfigItems(productConfig.getComputedProductConfigurationItems());
        List<ProductOrderItem> productOrderItems = createProductOrderItems(configAction, selectedProductConfigItems);
        processOrderItemRelationships(productOrderItems);
        processRequiredRelationships(selectedProductConfigItems, productOrderItems);
        return productOrderItems;
    }

    private List<ProductOrderItem> createProductOrderItems(String configAction, List<QueryProductConfigurationItem> selectedProductConfigItems) {
        return !OrderCaptureConstants.MODIFICATION.equals(configAction) ?
                selectedProductConfigItems.stream()
                        .map(productConfigItem -> createProductOrderItem(productConfigItem, selectedProductConfigItems))
                        .toList() :
                selectedProductConfigItems.stream()
                        .filter(this::isNonContractProductOffering)
                        .map(productConfigItem -> createProductOrderItem(productConfigItem, selectedProductConfigItems))
                        .toList();
    }


    private List<QueryProductConfigurationItem> getSelectedProductConfigItems(List<QueryProductConfigurationItem> productConfigItems) {
        List<QueryProductConfigurationItem> bundledConfigurationItems = getBundledConfigurationItems(productConfigItems);
        return productConfigItems.stream()
                .filter(productConfigItem -> isHierarchyFullySelected(bundledConfigurationItems, productConfigItem))
                .filter(this::hasSelectedConfigurationAction)
                .toList();
    }

    private boolean isNonContractProductOffering(QueryProductConfigurationItem item) {
        ProductOfferingRef productOffering = item.getProductConfiguration().getProductOffering();
        return productOffering != null &&
                productOffering.getReferredType() != null &&
                !ServiceConstants.CONTRACT_PRODUCT_OFFERING_TYPE.equals(productOffering.getReferredType());
    }

    private boolean hasSelectedConfigurationAction(QueryProductConfigurationItem item) {
        return item.getProductConfiguration().getConfigurationActions().stream()
                .anyMatch(ConfigurationAction::getIsSelected);
    }

    default void processRequiredRelationships(List<QueryProductConfigurationItem> selectedProductConfigItems, List<ProductOrderItem> productOrderItems) {
        selectedProductConfigItems.stream()
                .filter(item -> !CollectionUtils.isEmpty(item.getProductConfigurationItemRelationships()))
                .filter(item -> item.getProductConfigurationItemRelationships().stream()
                        .anyMatch(rel -> OrderCaptureConstants.REQUIRES.equals(rel.getRelationshipType())))
                .forEach(configurationItem -> addRequiredRelationships(productOrderItems, configurationItem));
    }

    private void addRequiredRelationships(List<ProductOrderItem> productOrderItems, QueryProductConfigurationItem configurationItem) {
        String requiredOrderItemId = configurationItem.getId();
        configurationItem.getProductConfigurationItemRelationships().stream()
                .filter(relationship -> OrderCaptureConstants.REQUIRES.equals(relationship.getRelationshipType()))
                .forEach(relationship -> {
                    Optional<ProductOrderItem> requiredOrderItem = findShippingOrderItemById(productOrderItems, relationship.getId());
                    requiredOrderItem.ifPresent(productOrderItem -> addRelationshipToRelatedItem(productOrderItem, requiredOrderItemId, RelationshipType.REQUIRES));
                });
    }

    private Optional<ProductOrderItem> findShippingOrderItemById(List<ProductOrderItem> productOrderItems, String id) {
        return productOrderItems.stream()
                .filter(item -> StringUtils.isNotBlank(id) && id.equals(item.getId()))
                .filter(this::isShippingOrderItem)
                .findFirst();
    }

    private boolean isShippingOrderItem(ProductOrderItem item) {
        return Optional.ofNullable(item.getProduct())
                .filter(Product.class::isInstance)
                .map(Product.class::cast)
                .map(Product::getProductSpecification)
                .map(ProductSpecificationRef::getAtBaseType)
                .filter(SHIPPING_PRODUCT_SPECIFICATION_BASE_TYPE::equals)
                .isPresent();
    }

    default void processOrderItemRelationships(List<ProductOrderItem> productOrderItems) {
        Map<String, ProductOrderItem> productOrderItemMap = productOrderItems.stream()
                .collect(Collectors.toMap(ProductOrderItem::getId, item -> item, (existing, replacement) -> existing));

        productOrderItems.forEach(item -> item.getProductOrderItemRelationship().forEach(relationship -> {
            if (RelationshipType.BUNDLES.equals(relationship.getRelationshipType())) {
                ProductOrderItem relatedProductOrderItem = productOrderItemMap.get(relationship.getId());
                if (relatedProductOrderItem != null) {
                    addRelationshipToRelatedItem(relatedProductOrderItem, item.getId(), RelationshipType.ISCHILD);
                }
            }
        }));
    }

    private void addRelationshipToRelatedItem(ProductOrderItem relatedProductOrderItem, String id, RelationshipType relType) {
        OrderItemRelationship newRelationship = createOrderItemRelationship(id, relType);
        List<OrderItemRelationship> orderItemRelationships = new ArrayList<>(relatedProductOrderItem.getProductOrderItemRelationship());
        orderItemRelationships.add(newRelationship);
        relatedProductOrderItem.setProductOrderItemRelationship(orderItemRelationships);
    }

    private boolean isHierarchyFullySelected(List<QueryProductConfigurationItem> bundledConfigurationItems, QueryProductConfigurationItem productConfigurationItem) {
        List<QueryProductConfigurationItem> hierarchyProductConfigurationItems = new ArrayList<>();
        buildProductHierarchy(bundledConfigurationItems, productConfigurationItem, hierarchyProductConfigurationItems);
        return hierarchyProductConfigurationItems.stream()
                .map(QueryProductConfigurationItem::getProductConfiguration)
                .allMatch(ProductConfiguration::getIsSelected);
    }

    private List<QueryProductConfigurationItem> getBundledConfigurationItems(List<QueryProductConfigurationItem> queryProductConfigurationItems) {
        return queryProductConfigurationItems.stream()
                .filter(item -> item.getProductConfiguration() != null &&
                        item.getProductConfiguration().getProductOffering() != null &&
                        (ServiceConstants.BUNDLE_PRODUCT_OFFERING_TYPE.equals(item.getProductConfiguration().getProductOffering().getReferredType()) ||
                                ServiceConstants.CONTRACT_PRODUCT_OFFERING_TYPE.equals(item.getProductConfiguration().getProductOffering().getReferredType())))
                .toList();
    }

    private void buildProductHierarchy(List<QueryProductConfigurationItem> bundledConfigurationItems,
                                       QueryProductConfigurationItem currentConfigurationItem,
                                       List<QueryProductConfigurationItem> hierarchy) {
        getParentProductConfigurationItem(bundledConfigurationItems, currentConfigurationItem)
                .ifPresent(parent -> buildProductHierarchy(bundledConfigurationItems, parent, hierarchy));
        hierarchy.add(currentConfigurationItem);
    }

    private Optional<QueryProductConfigurationItem> getParentProductConfigurationItem(List<QueryProductConfigurationItem> queryProductConfigurationItems,
                                                                                      QueryProductConfigurationItem productConfigurationItem) {
        return queryProductConfigurationItems.stream()
                .filter(item -> hasBundleRelationship(item, productConfigurationItem))
                .findFirst();
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

    private ProductOrderItem createProductOrderItem(QueryProductConfigurationItem productConfigItem, List<QueryProductConfigurationItem> productConfigItems) {
        ProductOrderItem productOrderItem = mapConfigItemToProductOrderItem(productConfigItem);
        productOrderItem.setProductOffering(mapProductOfferingToProductOfferingRef(productConfigItem.getProductConfiguration().getProductOffering()));
        productOrderItem.setItemPrice(mapConfigurationPricesToOrderPrices(productConfigItem.getProductConfiguration().getConfigurationPrices()));
        productOrderItem.setId(productConfigItem.getId());
        productOrderItem.setItemTerm(mapSelectedConfigurationTermsToItemTerm(productConfigItem.getProductConfiguration()));
        productOrderItem.setIsInstallable(productConfigItem.getProductConfiguration().getIsInstallable());
        productOrderItem.setProductOrderItemRelationship(mapItemRelationshipsToOrderItemRelationships(productConfigItem.getProductConfigurationItemRelationships(), productConfigItems));
        return productOrderItem;
    }

    private boolean hasAddMigrateSelectedActions(ProductConfiguration productConfiguration) {
        List<ConfigurationAction> actions = productConfiguration.getConfigurationActions();

        if (CollectionUtils.isEmpty(actions)) {
            return false;
        }

        return actions.stream().anyMatch(action ->
                (ADD.equals(action.getAction()) || MIGRATE.equals(action.getAction())) &&
                        Boolean.TRUE.equals(action.getIsSelected()));
    }

    private List<OrderTerm> mapSelectedConfigurationTermsToItemTerm(ProductConfiguration productConfiguration) {
        return Optional.ofNullable(productConfiguration)
                .filter(this::hasAddMigrateSelectedActions)
                .map(ProductConfiguration::getConfigurationTerms)
                .filter(terms -> !CollectionUtils.isEmpty(terms))
                .map(terms -> terms.stream()
                        .filter(term -> Boolean.TRUE.equals(term.getIsSelected()))
                        .map(this::mapConfigurationTermToItemTerm)
                        .toList())
                .orElse(Collections.emptyList());
    }

    @Mapping(target = "atBaseType", source = "baseType")
    @Mapping(source = "schemaLocation", target = "atSchemaLocation", qualifiedByName = "convertToUri")
    @Mapping(target = "atType", constant = ORDER_TERM)
    OrderTerm mapConfigurationTermToItemTerm(ConfigurationTerm configurationTerm);

    private OrderItemRelationship createOrderItemRelationship(String id, RelationshipType relType) {
        return OrderItemRelationship.builder()
                .id(id)
                .relationshipType(relType)
                .atType(ORDER_ITEM_RELATIONSHIP_TYPE)
                .build();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "atType", constant = "ProductOrderItem")
    @Mapping(target = "quantity", source = "productConfiguration.quantity")
    @Mapping(target = "action", source = "productConfiguration.configurationActions")
    @Mapping(target = "product", source = "productConfiguration")
    ProductOrderItem mapConfigItemToProductOrderItem(QueryProductConfigurationItem productConfigItem);

    @Mapping(target = "id", source = "productConfig", qualifiedByName = "getProductId")
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "productOffering", ignore = true)
    @Mapping(target = "productSpecification.version", source = "productSpecification.version")
    @Mapping(target = "productCharacteristic", source = "productConfig")
    @Mapping(target = "productSpecification.atType", constant = "ProductSpecificationRef")
    @Mapping(target = "productSpecification.atBaseType", source = "productSpecification.baseType")
    @Mapping(target = "isCustomerVisible", source = "isVisible")
    Product mapProductConfigurationToProduct(ProductConfiguration productConfig);

    @Condition
    default boolean isValidForMapping(ProductConfiguration productConfig) {
        return productConfig != null;
    }

    default List<Characteristic> mapConfigurationCharacteristicsToProductCharacteristics(ProductConfiguration productConfig) {
        if (CollectionUtils.isEmpty(productConfig.getConfigurationCharacteristics())) {
            return Collections.emptyList();
        }
        if (isNoChangeItem(productConfig)
                && isAtomicAndHavingProductCharacteristic(productConfig)
                && productConfig.getProduct() instanceof com.orange.discobole.ordermanagement.commons.dto.product.configuration.Product product) {
            return product.getProductCharacteristic().stream()
                    .map(this::mapProductCharacteristicValueToCharacteristic)
                    .filter(Objects::nonNull)
                    .toList();
        }
        if (isMigrateItem(productConfig) && isAtomicAndHavingProductCharacteristic(productConfig)) {
            return getProductCharacteristic(productConfig);
        }

        return productConfig.getConfigurationCharacteristics()
                .stream()
                .filter(cc -> !CollectionUtils.isEmpty(cc.getConfigurationCharacteristicValues()))
                .flatMap(cc -> cc.getConfigurationCharacteristicValues()
                        .stream()
                        .filter(ccv -> ccv != null
                                && ccv.getCharacteristic() != null
                                && Boolean.TRUE.equals(ccv.getIsSelected()))
                        .map(ccv -> mapCharacteristicValueToCharacteristic(ccv.getCharacteristic())))
                .filter(Objects::nonNull)
                .toList();
    }

    private ProductCharacteristic getProductCharacteristic(ProductConfiguration productConfig, ConfigurationCharacteristicValue configurationCharacteristicValue) {
        if (productConfig.getProduct() instanceof com.orange.discobole.ordermanagement.commons.dto.product.configuration.Product product
                && !CollectionUtils.isEmpty(product.getProductCharacteristic())) {
            return product.getProductCharacteristic()
                    .stream()
                    .filter(characteristicValueProduct -> configurationCharacteristicValue.getCharacteristic().getName().equals(characteristicValueProduct.getName()))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    private List<Characteristic> getProductCharacteristic(ProductConfiguration productConfig) {
        List<Characteristic> characteristics = new ArrayList<>();

        for (ConfigurationCharacteristic configurationCharacteristic : productConfig.getConfigurationCharacteristics()) {

            configurationCharacteristic.getConfigurationCharacteristicValues()
                    .stream()
                    .filter(configurationCharacteristicValue -> Boolean.TRUE.equals(configurationCharacteristicValue.getIsSelected()))
                    .map(configurationCharacteristicValue -> mapCharacteristicValueToCharacteristic(configurationCharacteristicValue.getCharacteristic()))
                    .forEach(characteristics::add);

            configurationCharacteristic.getConfigurationCharacteristicValues()
                    .stream()
                    .filter(configurationCharacteristicValue -> Boolean.FALSE.equals(configurationCharacteristicValue.getIsSelected())
                            && !isSameCharacteristicName(characteristics, configurationCharacteristicValue)
                            && isSameCharacteristicName(productConfig, configurationCharacteristicValue))
                    .map(configurationCharacteristicValue -> mapProductCharacteristicValueToCharacteristic(getProductCharacteristic(productConfig, configurationCharacteristicValue)))
                    .forEach(characteristics::add);
        }

        if (productConfig.getProduct() instanceof com.orange.discobole.ordermanagement.commons.dto.product.configuration.Product product
                && !CollectionUtils.isEmpty(product.getProductCharacteristic())) {
            product.getProductCharacteristic()
                    .stream()
                    .filter(characteristicValueProduct -> isNotHavingSameCharacteristicName(characteristicValueProduct, characteristics))
                    .map(this::mapProductCharacteristicValueToCharacteristic)
                    .forEach(characteristics::add);

        }

        return characteristics;
    }

    private boolean isNotHavingSameCharacteristicName(ProductCharacteristic characteristicValueProduct, List<Characteristic> characteristics) {
        if (!Objects.nonNull(characteristicValueProduct)) {
            return false;
        }
        return characteristics.stream().noneMatch(characteristic -> characteristicValueProduct.getName().equals(characteristic.getName()));
    }

    private boolean isAtomicAndHavingProductCharacteristic(ProductConfiguration productConfig) {
        return productConfig.getProduct() instanceof com.orange.discobole.ordermanagement.commons.dto.product.configuration.Product product
                && !CollectionUtils.isEmpty(product.getProductCharacteristic())
                && isAtomicItem(productConfig);
    }

    private boolean isShippingConfigItem(ProductConfiguration productConfig) {
        return productConfig.getProductSpecification() != null
                && SHIPPING_PRODUCT_SPECIFICATION_BASE_TYPE.equals(productConfig.getProductSpecification().getBaseType());
    }

    private boolean isSameCharacteristicName(ProductConfiguration productConfig, ConfigurationCharacteristicValue configurationCharacteristicValue) {
        return productConfig.getProduct() instanceof com.orange.discobole.ordermanagement.commons.dto.product.configuration.Product product
                && !CollectionUtils.isEmpty(product.getProductCharacteristic()) &&
                product.getProductCharacteristic()
                        .stream()
                        .anyMatch(characteristicValueProduct -> isHavingSameCharacteristicName(characteristicValueProduct, configurationCharacteristicValue));
    }

    private boolean isSameCharacteristicName(List<Characteristic> characteristics, ConfigurationCharacteristicValue configurationCharacteristicValue) {
        return characteristics
                .stream()
                .anyMatch(characteristic -> configurationCharacteristicValue.getCharacteristic().getName().equals(characteristic.getName()));
    }

    private boolean isHavingSameCharacteristicName(ProductCharacteristic characteristicValueProduct, ConfigurationCharacteristicValue configurationCharacteristicValue) {
        if (!Objects.nonNull(characteristicValueProduct)) {
            return false;
        }
        return configurationCharacteristicValue.getCharacteristic().getName().equals(characteristicValueProduct.getName());
    }

    private boolean isAtomicItem(ProductConfiguration productConfig) {
        return productConfig.getProductOffering().getReferredType().equals(ATOMIC_PRODUCT_OFFERING_TYPE)
                && !isShippingConfigItem(productConfig);
    }

    private boolean isNoChangeItem(ProductConfiguration productConfig) {
        return productConfig.getConfigurationActions().stream().anyMatch(configurationAction -> NO_CHANGE.equals(configurationAction.getAction())
                && Boolean.TRUE.equals(configurationAction.getIsSelected()));
    }

    private boolean isMigrateItem(ProductConfiguration productConfig) {
        return productConfig.getConfigurationActions().stream().anyMatch(configurationAction -> MIGRATE.equals(configurationAction.getAction())
                && Boolean.TRUE.equals(configurationAction.getIsSelected()));
    }

    default Characteristic mapProductCharacteristicValueToCharacteristic(ProductCharacteristic configCharacteristicValue) {
        if (configCharacteristicValue == null) {
            return null;
        }
        if (configCharacteristicValue instanceof ProductValidityCharacteristic validityCharacteristic) {
            return mapProductValidityCharacteristic(validityCharacteristic);
        } else if (configCharacteristicValue instanceof ProductObjectCharacteristic objectCharacteristic) {
            return mapProductObjectCharacteristic(objectCharacteristic);
        } else if (configCharacteristicValue instanceof ProductDateCharacteristic dateCharacteristic) {
            return mapProductDateCharacteristic(dateCharacteristic);
        } else if (configCharacteristicValue instanceof ProductStringCharacteristic stringCharacteristic) {
            return mapProductStringCharacteristic(stringCharacteristic);
        } else if (configCharacteristicValue instanceof ProductAddressCharacteristic addressCharacteristic) {
            return mapProductAddressCharacteristic(addressCharacteristic);
        }
        return null;
    }

    default Characteristic mapCharacteristicValueToCharacteristic(CharacteristicValue configCharacteristicValue) {
        if (configCharacteristicValue == null) {
            return null;
        }
        if (configCharacteristicValue instanceof ValidityCharacteristic validityCharacteristic) {
            return mapValidityCharacteristic(validityCharacteristic);
        } else if (configCharacteristicValue instanceof ObjectCharacteristic objectCharacteristic) {
            return mapObjectCharacteristic(objectCharacteristic);
        } else if (configCharacteristicValue instanceof DateCharacteristic dateCharacteristic) {
            return mapDateCharacteristic(dateCharacteristic);
        } else if (configCharacteristicValue instanceof StringCharacteristic stringCharacteristic) {
            return mapStringCharacteristic(stringCharacteristic);
        } else if (configCharacteristicValue instanceof AddressCharacteristic addressCharacteristic) {
            return mapAddressCharacteristic(addressCharacteristic);
        }
        return null;
    }

    @Mapping(source = "validTo", target = "value.validTo", qualifiedByName = "toInstant")
    @Mapping(source = "validFrom", target = "value.validFrom", qualifiedByName = "toInstant")
    @Mapping(source = "value", target = "value.value")
    @Mapping(source = "unitOfMeasure", target = "value.unitOfMeasure")
    @Mapping(source = "type", target = "atType")
    com.orange.discobole.ordermanagement.orderinventory.dto.v1.ValidityCharacteristic mapValidityCharacteristic(ValidityCharacteristic validityCharacteristic);

    @Mapping(target = "value", expression = "java(mapValidityCharacteristicValue(validityCharacteristic.getValue(), validityCharacteristic.getUnitOfMeasure()))")
    @Mapping(source = "type", target = "atType")
    com.orange.discobole.ordermanagement.orderinventory.dto.v1.ValidityCharacteristic mapProductValidityCharacteristic(ProductValidityCharacteristic validityCharacteristic);

    @Mapping(target = "value", expression = "java(mapValue(objectCharacteristic.getValue(), objectCharacteristic.getUnitOfMeasure()))")
    @Mapping(source = "type", target = "atType")
    com.orange.discobole.ordermanagement.orderinventory.dto.v1.ObjectCharacteristic mapObjectCharacteristic(ObjectCharacteristic objectCharacteristic);

    @Mapping(source = "type", target = "atType")
    com.orange.discobole.ordermanagement.orderinventory.dto.v1.ObjectCharacteristic mapProductObjectCharacteristic(ProductObjectCharacteristic objectCharacteristic);

    default ValidityValue mapValidityCharacteristicValue(Value value, String unitOfMeasure) {
        if (value == null) {
            return null;
        }

        ValidityValue.ValidityValueBuilder<?, ?> validityValue = ValidityValue.builder();
        validityValue.validTo(toInstant(value.getValidTo()));
        validityValue.validFrom(toInstant(value.getValidFrom()));
        validityValue.value(value.getValue());
        if (unitOfMeasure != null) {
            validityValue.unitOfMeasure(unitOfMeasure);
        }

        return validityValue.build();
    }

    default Map<String, Object> mapValue(Object value, String unitOfMeasure) {
        Map<String, Object> mappedValue = new HashMap<>();
        if (value != null) {
            mappedValue.put("value", value);
        }
        if (unitOfMeasure != null) {
            mappedValue.put("unitOfMeasure", unitOfMeasure);
        }
        return mappedValue;
    }

    @Mapping(source = "value", target = "value", qualifiedByName = "toInstant")
    @Mapping(target = "atType", constant = "DateCharacteristic")
    com.orange.discobole.ordermanagement.orderinventory.dto.v1.DateCharacteristic mapDateCharacteristic(DateCharacteristic dateCharacteristic);

    @Mapping(source = "value", target = "value", qualifiedByName = "toInstant")
    @Mapping(target = "atType", constant = "DateCharacteristic")
    com.orange.discobole.ordermanagement.orderinventory.dto.v1.DateCharacteristic mapProductDateCharacteristic(ProductDateCharacteristic dateCharacteristic);

    @Mapping(source = "type", target = "atType")
    com.orange.discobole.ordermanagement.orderinventory.dto.v1.StringCharacteristic mapStringCharacteristic(StringCharacteristic stringCharacteristic);

    @Mapping(source = "type", target = "atType")
    com.orange.discobole.ordermanagement.orderinventory.dto.v1.StringCharacteristic mapProductStringCharacteristic(ProductStringCharacteristic stringCharacteristic);

    @Mapping(source = "type", target = "atType")
    com.orange.discobole.ordermanagement.orderinventory.dto.v1.AddressCharacteristic mapAddressCharacteristic(AddressCharacteristic addressCharacteristic);

    @Mapping(source = "type", target = "atType")
    com.orange.discobole.ordermanagement.orderinventory.dto.v1.AddressCharacteristic mapProductAddressCharacteristic(ProductAddressCharacteristic addressCharacteristic);

    @Named("toInstant")
    default Instant toInstant(OffsetDateTime offsetDateTime) {
        return (offsetDateTime == null) ? null : offsetDateTime.toInstant();
    }

    @Named("resolveAtType")
    default String resolveAtType(String atType, String targetType) {
        return StringUtils.isBlank(atType) ? targetType : atType;
    }

    @Named("resolveConfigurationPriceAtType")
    default String resolveConfigurationPriceAtType(String atType) {
        if (StringUtils.isBlank(atType) || CONFIGURATION_PRICE_AT_TYPE.equals(atType)) {
            return ORDER_PRICE;
        }
        return atType;
    }

    default List<OrderPrice> mapConfigurationPricesToOrderPrices(List<ConfigurationPrice> configurationPrices) {
        if (configurationPrices == null) {
            return Collections.emptyList();
        }
        List<OrderPrice> orderPrices = configurationPrices.stream()
                .map(this::mapConfigurationPriceToOrderPrice)
                .toList();
        orderPrices.forEach(CurrencyUtils::applyRounding);
        return orderPrices;
    }

    default ItemActionType mapSelectedConfigurationActionToOrderActionType(List<ConfigurationAction> configurationActions) {
        if (configurationActions == null) {
            return null;
        }
        return configurationActions.stream()
                .filter(ConfigurationAction::getIsSelected)
                .findFirst()
                .map(configAction -> TERMINATION.equals(configAction.getAction()) ? ItemActionType.DELETE : mapConfigurationActionToOrderActionType(configAction))
                .orElse(null);
    }

    default ItemActionType mapConfigurationActionToOrderActionType(ConfigurationAction configurationAction) {
        if (configurationAction == null || configurationAction.getAction() == null) {
            return null;
        }
        return ItemActionType.fromValue(configurationAction.getAction());
    }

    @Mapping(target = "atSchemaLocation", source = "schemaLocation", qualifiedByName = "convertToUri")
    @Mapping(target = "unitOfMeasure", source = "unitOfMeasure.units")
    @Mapping(target = "priceAlteration", source = "priceAlterations")
    @Mapping(target = "productOfferingPrice", source = "configurationPrice")
    @Mapping(target = "atType", expression = "java(resolveConfigurationPriceAtType(configurationPrice.getType()))")
    OrderPrice mapConfigurationPriceToOrderPrice(ConfigurationPrice configurationPrice);

    default ProductOfferingPriceRefOrValue mapConfigurationProductOfferingPriceToProductOfferingPrice(ConfigurationPrice configurationPrice) {
        if (Objects.isNull(configurationPrice.getProductOfferingPrice())) {
            return null;
        } else if (INSTALLMENT_CHARGE.equals(configurationPrice.getProductOfferingPrice().getType())) {
            return mapInstallmentCharge(configurationPrice.getProductOfferingPrice());
        } else if (PRODUCT_OFFERING_PRICE_CHARGE.equals(configurationPrice.getProductOfferingPrice().getType())) {
            return mapConfigurationProductOfferingPriceChargeToProductOfferingPriceCharge(configurationPrice.getProductOfferingPrice());
        } else if (PRODUCT_OFFERING_PRICE_REF.equals(configurationPrice.getProductOfferingPrice().getType())) {
            return mapConfigurationProductOfferingPriceRefToProductOfferingPriceRef(configurationPrice.getProductOfferingPrice());

        }
        return null;
    }


    @Mapping(target = "atReferredType", source = "referredType")
    @Mapping(target = "atBaseType", source = "baseType")
    @Mapping(target = "atType", source = "type")
    ProductOfferingPriceCharge mapConfigurationProductOfferingPriceChargeToProductOfferingPriceCharge(ProductOfferingPriceRef productOfferingPriceRef);

    @Mapping(target = "atReferredType", source = "referredType")
    @Mapping(target = "atBaseType", source = "baseType")
    @Mapping(target = "atType", source = "type")
    InstallmentCharge mapInstallmentCharge(ProductOfferingPriceRef productOfferingPriceRef);

    @Mapping(target = "atReferredType", source = "referredType")
    @Mapping(target = "atBaseType", source = "baseType")
    @Mapping(target = "atType", source = "type")
    com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOfferingPriceRef mapConfigurationProductOfferingPriceRefToProductOfferingPriceRef(ProductOfferingPriceRef productOfferingPriceRef);


    @Mapping(target = "atType", expression = "java(resolveAtType(productOfferingPriceRelationship.getType(), PRODUCT_OFFERING_PRICE_RELATIONSHIP))")
    ProductOfferingPriceRelationship mapProductOfferingPriceRelationshipToProductOfferingPriceRelationship(com.orange.discobole.ordermanagement.commons.dto.product.configuration.ProductOfferingPriceRelationship productOfferingPriceRelationship);

    @Mapping(source = "endDateTime", target = "endDateTime", qualifiedByName = "toInstant")
    @Mapping(source = "startDateTime", target = "startDateTime", qualifiedByName = "toInstant")
    com.orange.discobole.ordermanagement.orderinventory.dto.v1.TimePeriod mapTimePeriodToTimePeriod(TimePeriod timePeriod);

    @Mapping(target = "atType", expression = "java(resolveAtType(taxItem.getType(), TAX_ITEM))")
    com.orange.discobole.ordermanagement.orderinventory.dto.v1.TaxItem mapTaxItemToTaxItem(TaxItem taxItem);

    @Mapping(target = "atType", expression = "java(resolveAtType(price.getType(), PRICE))")
    com.orange.discobole.ordermanagement.orderinventory.dto.v1.Price mapPriceToOrderPrice(Price price);

    @Mapping(target = "atType", expression = "java(resolveAtType(priceAlteration.getType(), PRICE_ALTERATION))")
    com.orange.discobole.ordermanagement.orderinventory.dto.v1.PriceAlteration mapPriceAlterationToOrderPriceAlteration(PriceAlteration priceAlteration);

    @Mapping(target = "atType", source = "referredType")
    @Mapping(target = "atReferredType", constant = "ProductOfferingRef")
    com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOfferingRef mapProductOfferingToProductOfferingRef(ProductOfferingRef productOfferingRef);

    default List<OrderItemRelationship> mapItemRelationshipsToOrderItemRelationships(List<ProductConfigurationItemRelationship> configItemRelationships, List<QueryProductConfigurationItem> productConfigItems) {
        if (configItemRelationships == null) {
            return Collections.emptyList();
        }
        return configItemRelationships.stream()
                .filter(rel -> isValidRelationship(rel, productConfigItems))
                .map(rel -> mapItemRelationshipToOrderItemRelationship(rel, parseRelationshipType(rel.getRelationshipType())))
                .toList();
    }

    private boolean isValidRelationship(ProductConfigurationItemRelationship relationship, List<QueryProductConfigurationItem> productConfigItems) {
        if (OrderCaptureConstants.REQUEST_ITEM.equalsIgnoreCase(relationship.getRelationshipType()) || OrderCaptureConstants.REQUIRES.equalsIgnoreCase(relationship.getRelationshipType())) {
            return false;
        }
        return productConfigItems.stream()
                .anyMatch(queryItem -> queryItem.getId().equals(relationship.getId()));
    }

    private RelationshipType parseRelationshipType(String relationshipType) {
        try {
            return RelationshipType.valueOf(relationshipType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    default OrderItemRelationship mapItemRelationshipToOrderItemRelationship(ProductConfigurationItemRelationship relationship, RelationshipType relType) {
        return createOrderItemRelationship(relationship.getId(), relType);
    }

    @Named("convertToUri")
    default URI convertToUri(String s) {
        return s == null ? null : URI.create(s);
    }

    @Named("getProductId")
    default String getProductId(ProductConfiguration productConfig) {
        if (productConfig.getProduct() instanceof com.orange.discobole.ordermanagement.commons.dto.product.configuration.Product product) {
            return product.getId();
        }
        if (productConfig.getProduct() instanceof ProductRef productRef) {
            return productRef.getId();
        }
        return null;
    }

    @Mapping(target = "atType", constant = "RelatedPartyRefOrPartyRoleRef")
    @Mapping(target = "role", source = "relatedParty.role")
    @Mapping(target = "partyOrPartyRole", source = "relatedParty")
    RelatedPartyRefOrPartyRoleRef mapRelatedPartyToRefOrRoleRef(com.orange.discobole.processflow.dto.generated.RelatedParty relatedParty);

    @Mapping(source = "referredType", target = "atReferredType")
    @Mapping(source = "baseType", target = "atBaseType")
    @Mapping(target = "atType", constant = "PartyRef")
    @Mapping(source = "schemaLocation", target = "atSchemaLocation", qualifiedByName = "convertToUri")
    PartyRef mapRelatedPartyToPartyRef(com.orange.discobole.processflow.dto.generated.RelatedParty relatedParty);

    List<RelatedPartyRefOrPartyRoleRef> mapRelatedPartyListToRefOrRoleRefList(List<com.orange.discobole.processflow.dto.generated.RelatedParty> relatedPartyDtoList);
}