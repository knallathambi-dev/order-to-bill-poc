// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.action;

import com.orange.discobole.ordermanagement.commons.dto.product.specification.ProductConfSpecCharacteristicValue;
import com.orange.discobole.ordermanagement.commons.dto.product.specification.ProductConfigurationSpec;
import com.orange.discobole.ordermanagement.commons.dto.product.specification.ProductSpecification;
import com.orange.discobole.ordermanagement.commons.dto.product.stock.*;
import com.orange.discobole.ordermanagement.commons.dto.resource.inventory.Resource;
import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.domain.SettingsEntity;
import com.orange.discobole.ordermanagement.ordercapture.service.*;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.processflow.ssm.action.StateMachineStateAction;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.*;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;


@Component("reserveResourcesAction")
@Slf4j
public class ResourcesReservationAction implements StateMachineStateAction<String, String> {
    private final ResourceInventoryService resourceInventoryService;
    private final ProductOrderService productOrderService;
    private final ProductSpecificationService productSpecificationService;
    private final ProductStockManagementService productStockManagementService;
    private final SettingsService settingsService;
    private final List<String> logicalCharacteristic = List.of(ICCID, MSISDN, VOIP, IMSI);

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ResourcesReservationAction(ResourceInventoryService resourceInventoryService, ProductOrderService productOrderService, ProductSpecificationService productSpecificationService, ProductStockManagementService productStockManagementService, SettingsService settingsService) {
        this.resourceInventoryService = resourceInventoryService;
        this.productOrderService = productOrderService;
        this.productSpecificationService = productSpecificationService;
        this.productStockManagementService = productStockManagementService;
        this.settingsService = settingsService;
    }

    @Override
    public Mono<Void> apply(StateContext<String, String> context) {
        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            return Mono.empty();
        }
        log.info("Inside reserve resources action");
        try {
            Map<String, List<String>> productOrderItemLogicalResources = StateMachineUtil.getMapValue(context.getExtendedState().getVariables(), PRODUCT_ORDER_ITEM_LOGICAL_RESOURCES);
            List<String> physicalProductOrderItems = StateMachineUtil.getListValue(context.getExtendedState().getVariables(), PHYSICAL_PRODUCT_ITEM_ID_LIST, String.class);
            ProductOrder productOrder = StateMachineUtil.getObjectValue(context, CREATED_PRODUCT_ORDER, ProductOrder.class);

            Map<String, String> physicalProductOrderItemSerialNumberMap = new HashMap<>();
            Map<String, ResourceRef> reservedPhysicalResourceRefMap = new HashMap<>();
            Map<String, List<ResourceRef>> reservedLogicalResourceRefMap = new HashMap<>();

            // Reserve tangible products (physical resources)
            if (!physicalProductOrderItems.isEmpty() && Objects.nonNull(productOrder)) {
                SettingsEntity settings = settingsService.getSettings();
                if (!settings.isReservePhysicalResourceEnabled()) {
                    setPhysicalProductOrderItemId(physicalProductOrderItems, physicalProductOrderItemSerialNumberMap);
                } else {
                    Map<String, String> productOrderItemStockItemMap = createProductOrderItemToStockItemMap(productOrder, physicalProductOrderItems);
                    reservePhysicalResource(productOrderItemStockItemMap, productOrder, physicalProductOrderItemSerialNumberMap, reservedPhysicalResourceRefMap);

                    if (reservedPhysicalResourceRefMap.isEmpty()) {
                        setContextVariables(context, FALSE, null, null);
                        return Mono.empty();
                    }
                }
            }

            // Reserve logical products (logical resources)
            Map<String, List<String>> filteredLogicalResourceList = extractMatchingCharacteristic(productOrderItemLogicalResources);
            if (!CollectionUtils.isEmpty(filteredLogicalResourceList)) {
                Map<String, List<Resource>> reservedLogicalResourceList = resourceInventoryService.checkAndReserveLogicalResources(filteredLogicalResourceList);
                if (!reservedLogicalResourceList.isEmpty()) {
                    reservedLogicalResourceRefMap = convertResourceMapToLogicalResourceRefMap(reservedLogicalResourceList);
                    processUpdateItemCharacteristics(productOrder, reservedLogicalResourceList);
                } else {
                    setContextVariables(context, FALSE, null, null);
                    return Mono.empty();
                }
            }

            Map<String, List<ResourceRef>> reservedResourcesRefMap = combineMaps(reservedPhysicalResourceRefMap, reservedLogicalResourceRefMap);
            productOrderService.addResourceRef(productOrder, reservedResourcesRefMap);
            setContextVariables(context, TRUE, productOrder, physicalProductOrderItemSerialNumberMap);
        } catch (
                Exception e) {
            log.error("Unable to reserve resources [{}]:", e.getMessage(), e);
            setContextVariables(context, FALSE, null, null);
        }
        return Mono.empty();
    }

    private Map<String, List<String>> extractMatchingCharacteristic(Map<String, List<String>> productOrderItemLogicalResources) {
        return productOrderItemLogicalResources.entrySet().stream()
                .map(entry -> {
                    List<String> filteredResources = entry.getValue().stream()
                            .filter(logicalCharacteristic::contains)
                            .toList();
                    return new AbstractMap.SimpleEntry<>(entry.getKey(), filteredResources);
                })
                .filter(entry -> !entry.getValue().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    }

    private void processUpdateItemCharacteristics(ProductOrder productOrder, Map<String, List<Resource>> reservedLogicalResourceList) {
        for (Map.Entry<String, List<Resource>> entry : reservedLogicalResourceList.entrySet()) {
            ProductOrderItem currentItem = productOrder.getProductOrderItem()
                    .stream()
                    .filter(item -> entry.getKey().equals(item.getId()))
                    .findFirst()
                    .orElse(null);
            if (Objects.nonNull(currentItem)) {
                processUpdateItemCharacteristic(currentItem, entry);
            }
        }
        productOrderService.updateProduct(productOrder);
    }

    private void processUpdateItemCharacteristic(ProductOrderItem currentItem, Map.Entry<String, List<Resource>> entry) {
        if (Objects.nonNull(currentItem.getProduct())
                && currentItem.getProduct() instanceof Product p) {
            updateItemCharacteristic(p, entry.getValue());
        }
    }

    private void updateItemCharacteristic(Product product, List<Resource> resources) {
        if (!CollectionUtils.isEmpty(product.getProductCharacteristic())) {
            processProductCharacteristicsIfExist(product, resources);
        } else {
            processProductCharacteristicsIfNotExist(product, resources);
        }
    }

    private void processProductCharacteristicsIfNotExist(Product product, List<Resource> resources) {
        List<Characteristic> characteristics = new ArrayList<>();
        for (Resource resource : resources) {
            Characteristic newCharacteristic = processNewCharacteristics(resource);
            characteristics.add(newCharacteristic);
        }
        product.setProductCharacteristic(characteristics);
    }

    private void processProductCharacteristicsIfExist(Product product, List<Resource> resources) {
        for (Resource resource : resources) {
            boolean isPresentCharacteristic = product.getProductCharacteristic()
                    .stream()
                    .anyMatch(characteristic -> characteristic.getName().equals(resource.getName()));

            if (isPresentCharacteristic) {
                product.getProductCharacteristic()
                        .stream()
                        .filter(characteristic -> characteristic.getName().equals(resource.getName()))
                        .filter(StringCharacteristic.class::isInstance)
                        .forEach(characteristic -> ((StringCharacteristic) characteristic).setValue(resource.getValue()));
            } else {
                Characteristic newCharacteristic = processNewCharacteristics(resource);
                List<Characteristic> modifiedCharacteristics = new ArrayList<>(product.getProductCharacteristic());
                modifiedCharacteristics.add(newCharacteristic);
                product.setProductCharacteristic(modifiedCharacteristics);
            }
        }
    }

    private Characteristic processNewCharacteristics(Resource resource) {
        return StringCharacteristic.builder()
                .id(resource.getId())
                .name(resource.getName())
                .value(resource.getValue())
                .atType(resource.getType())
                .build();
    }

    private void setPhysicalProductOrderItemId(List<String> physicalProductOrderItems, Map<String, String> physicalProductOrderItemSerialNumberMap) {
        for (String productOrderItemId : physicalProductOrderItems) {
            physicalProductOrderItemSerialNumberMap.put(productOrderItemId, null);
        }
    }

    private ResourceRef buildResourceRef(String id, String href, String type) {
        return ResourceRef.builder()
                .id(id)
                .href(href)
                .atType(type)
                .build();
    }

    private Map<String, String> getProductOrderItemProductStockReservedMap(Map<String, ProductStock> reservedProductOrderItemProductStocks) {
        Map<String, String> productOrderItemProductStockReservedMap = new HashMap<>();
        for (Map.Entry<String, ProductStock> entry : reservedProductOrderItemProductStocks.entrySet()) {
            productOrderItemProductStockReservedMap.put(entry.getKey(), entry.getValue().getReserveProductStockItem().get(0).getProductStockReserved().getId());
        }
        return productOrderItemProductStockReservedMap;
    }

    private Map<String, String> createProductOrderItemToStockItemMap(ProductOrder productOrder, List<String> physicalProductOrderItems) {
        Map<String, Set<String>> productSpecificationToCharacteristicsMap = mapProductSpecificationsToCharacteristics(productOrder, physicalProductOrderItems);
        List<ProductSpecification> productSpecifications = fetchProductSpecifications(productSpecificationToCharacteristicsMap);
        return mapProductSpecificationsToStockItems(productOrder, productSpecifications, physicalProductOrderItems, productSpecificationToCharacteristicsMap);
    }

    private Map<String, Set<String>> mapProductSpecificationsToCharacteristics(ProductOrder productOrder, List<String> physicalProductItemIds) {
        return productOrder.getProductOrderItem().stream()
                .filter(orderItem -> physicalProductItemIds.contains(orderItem.getId()) &&
                        orderItem.getProduct() instanceof Product product &&
                        product.getProductSpecification() != null)
                .map(orderItem -> (Product) orderItem.getProduct())
                .collect(Collectors.toMap(
                        product -> product.getProductSpecification().getId(),
                        this::extractCharacteristicValues,
                        (existingSet, newSet) -> {
                            existingSet.addAll(newSet);
                            return existingSet;
                        }
                ));
    }

    private String extractCharacteristicValue(Characteristic characteristic) {
        if (characteristic instanceof StringCharacteristic stringCharacteristic) {
            return stringCharacteristic.getValue();
        } else if (characteristic instanceof BooleanCharacteristic booleanCharacteristic) {
            return String.valueOf(booleanCharacteristic.getValue());
        } else if (characteristic instanceof ObjectCharacteristic objectCharacteristic) {
            return objectCharacteristic.getValue().toString();
        } else if (characteristic instanceof IntegerCharacteristic integerCharacteristic) {
            return integerCharacteristic.getValue().toString();
        } else if (characteristic instanceof DateCharacteristic dateCharacteristic) {
            return dateCharacteristic.getValue().toString();
        }
        throw new IllegalArgumentException("Unsupported characteristic type: " + characteristic.getClass().getName());
    }

    private Set<String> extractCharacteristicValues(Product product) {
        if (product.getProductCharacteristic() == null) {
            return Collections.emptySet();
        }

        return product.getProductCharacteristic().stream()
                .map(this::extractCharacteristicValue)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private List<ProductSpecification> fetchProductSpecifications(Map<String, Set<String>> productSpecificationToCharacteristicsMap) {
        return productSpecificationService.fetchProductSpecifications(
                new ArrayList<>(productSpecificationToCharacteristicsMap.keySet())
        );
    }

    private Map<String, String> mapProductSpecificationsToStockItems(ProductOrder productOrder, List<ProductSpecification> productSpecifications, List<String> physicalProductOrderItems, Map<String, Set<String>> productSpecificationToCharacteristicsMap) {
        return productSpecifications.stream()
                .flatMap(productSpecification -> mapProductSpecificationToStockItemEntries(productOrder, productSpecification, physicalProductOrderItems, productSpecificationToCharacteristicsMap))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (existing, replacement) -> existing));
    }

    private Stream<Map.Entry<String, String>> mapProductSpecificationToStockItemEntries(
            ProductOrder productOrder,
            ProductSpecification productSpecification,
            List<String> physicalProductOrderItems,
            Map<String, Set<String>> productSpecificationToCharacteristicsMap) {
        List<ProductConfigurationSpec> configurations = productSpecification.getProductConfiguration();
        if (CollectionUtils.isEmpty(configurations)) {
            return Stream.empty();
        }

        List<String> matchingOrderItemIds = productOrder.getProductOrderItem().stream()
                .filter(item -> item.getProduct() instanceof Product product &&
                        product.getProductSpecification() != null &&
                        product.getProductSpecification().getId().equals(productSpecification.getId()) &&
                        physicalProductOrderItems.contains(item.getId()))
                .map(ProductOrderItem::getId)
                .toList();

        if (matchingOrderItemIds.isEmpty()) {
            return Stream.empty();
        }

        if (configurations.size() == 1) {
            String stockItemId = configurations.get(0).getStockItemRef().getId();
            return matchingOrderItemIds.stream()
                    .map(id -> new AbstractMap.SimpleEntry<>(id, stockItemId));
        } else {
            return configurations.stream()
                    .filter(productConfiguration -> isConfigurationMatch(productSpecification, productConfiguration, productSpecificationToCharacteristicsMap))
                    .flatMap(productConfiguration -> matchingOrderItemIds.stream()
                            .map(id -> new AbstractMap.SimpleEntry<>(id, productConfiguration.getStockItemRef().getId())));
        }
    }

    private boolean isConfigurationMatch(ProductSpecification productSpecification, ProductConfigurationSpec productConfiguration, Map<String, Set<String>> productSpecificationToCharacteristicsMap) {
        String productSpecId = productSpecification.getId();
        return productSpecificationToCharacteristicsMap.containsKey(productSpecId) &&
                productSpecificationToCharacteristicsMap.get(productSpecId).containsAll(
                        productConfiguration.getDefinedBy().stream()
                                .map(ProductConfSpecCharacteristicValue::getValue)
                                .collect(Collectors.toSet())
                );
    }

    private void reservePhysicalResource(Map<String, String> productOrderItemStockItemMap, ProductOrder productOrder, Map<String, String> physicalProductOrderItemSerialNumberMap, Map<String, ResourceRef> reservedPhysicalResourceRefMap) {
        Map<String, ProductStock> productOrderItemProductStockTobeReservedMap = buildProductOrderItemProductStockMap(productOrderItemStockItemMap, productOrder);
        Map<String, ProductStock> reservedProductOrderItemProductStocks = productStockManagementService.reserveProductStocks(productOrderItemProductStockTobeReservedMap);
        if (!reservedProductOrderItemProductStocks.isEmpty()) {
            Map<String, String> productOrderItemProductStockReservedMap = getProductOrderItemProductStockReservedMap(reservedProductOrderItemProductStocks);
            reservedProductOrderItemProductStocks = productStockManagementService.getReservedProductStocks(productOrderItemProductStockReservedMap);
            if (!reservedProductOrderItemProductStocks.isEmpty()) {
                for (Map.Entry<String, ProductStock> entry : reservedProductOrderItemProductStocks.entrySet()) {
                    physicalProductOrderItemSerialNumberMap.put(entry.getKey(), entry.getValue().getStockedProduct().getSerialNumber());
                    ResourceRef resourceRef = buildResourceRef(entry.getValue().getResource().getId(), entry.getValue().getResource().getHref(), PHYSICAL_RESOURCE);
                    reservedPhysicalResourceRefMap.put(entry.getKey(), resourceRef);

                }
            }
        }
    }


    private Map<String, ProductStock> buildProductOrderItemProductStockMap(Map<String, String> productOrderItemStockItemMap, ProductOrder productOrder) {
        Map<String, ProductStock> productOrderItemProductStockMap = new HashMap<>();
        for (Map.Entry<String, String> entry : productOrderItemStockItemMap.entrySet()) {
            Instant deliveryDate = getDeliveryDate(entry.getKey(), productOrder);
            ProductStock productStock = buildProductStock(productOrder, deliveryDate, entry.getValue());
            productOrderItemProductStockMap.put(entry.getKey(), productStock);
        }
        return productOrderItemProductStockMap;
    }

    private Instant getDeliveryDate(String productOrderItemId, ProductOrder productOrder) {
        Optional<ProductOrderItem> shippingProductOrderItem = productOrder.getProductOrderItem().stream()
                .filter(productOrderItem -> !CollectionUtils.isEmpty(productOrderItem.getProductOrderItemRelationship()))
                .filter(productOrderItem -> productOrderItem.getProductOrderItemRelationship().stream()
                        .anyMatch(orderItemRelationship -> orderItemRelationship.getId().equals(productOrderItemId) &&
                                orderItemRelationship.getRelationshipType().equals(RelationshipType.REQUIRES)))
                .filter(productOrderItem -> productOrderItem.getProduct() instanceof Product product &&
                        product.getProductSpecification() != null &&
                        SHIPPING_PRODUCT_SPECIFICATION_BASE_TYPE.equals(product.getProductSpecification().getAtBaseType()))
                .findFirst();

        if (shippingProductOrderItem.isPresent() &&
                shippingProductOrderItem.get().getProduct() instanceof Product product) {

            Optional<String> shippingRequestedDeliveryDate = product.getProductCharacteristic().stream()
                    .filter(characteristic -> characteristic.getName().equalsIgnoreCase(REQUESTED_DELIVERY_DATE))
                    .map(this::extractCharacteristicValue)
                    .findFirst();

            if (shippingRequestedDeliveryDate.isPresent()) {
                String shippingDeliveryDate = shippingRequestedDeliveryDate.get();
                if (shippingRequestedDeliveryDate.get().matches(DATE_TIME_PATTERN)) {
                    shippingDeliveryDate += "T00:00:00Z";
                }
                return Instant.parse(shippingDeliveryDate).plus(1, ChronoUnit.DAYS);
            }
        }
        throw new DiscoException("Invalid shipping delivery date");
    }

    private ReserveProductStockItem getReserveProductStockItem(String stockItemId) {
        QuantityRequested quantityRequested = new QuantityRequested();
        quantityRequested.setAmount(DEFAULT_QUANTITY_VALUE);

        StockItem stockItem = new StockItem();
        stockItem.setId(stockItemId);

        ReserveProductStockItem reserveProductStockItem = new ReserveProductStockItem();
        reserveProductStockItem.setId(DEFAULT_ID);
        reserveProductStockItem.setQuantityRequested(quantityRequested);
        reserveProductStockItem.setStockItem(stockItem);
        reserveProductStockItem.setType(RESERVE_PRODUCT_STOCK_ITEM);
        return reserveProductStockItem;
    }

    private ProductStock buildProductStock(ProductOrder productOrder, Instant deliveryDate, String stockItemId) {
        RelatedEntity relatedEntity = new RelatedEntity();
        relatedEntity.setId(productOrder.getId());
        relatedEntity.setRole(ORDER_TRIGGERING_THE_RESERVATION);
        relatedEntity.setReferredType(PRODUCT_ORDER_TYPE);
        relatedEntity.setType(ENTITY_REF);

        ValidFor validFor = new ValidFor();
        validFor.setStartDateTime(productOrder.getCreationDate().toString());
        validFor.setEndDateTime(deliveryDate.toString());
        validFor.setType(TIME_PERIOD);

        ProductStock productStock = new ProductStock();
        productStock.setRelatedEntity(Collections.singletonList(relatedEntity));

        ReserveProductStockItem reserveProductStockItem = getReserveProductStockItem(stockItemId);
        productStock.setReserveProductStockItem(Collections.singletonList(reserveProductStockItem));
        productStock.setValidFor(validFor);
        productStock.setType(RESERVE_PRODUCT_STOCK);
        return productStock;
    }

    private void setContextVariables(StateContext<String, String> context, boolean areResourcesReserved, ProductOrder productOrder, Map<String, String> physicalProductOrderItemSerialNumberMap) {
        context.getExtendedState().getVariables().put(ARE_RESOURCES_RESERVED, areResourcesReserved);
        if (areResourcesReserved) {
            context.getExtendedState().getVariables().put(CREATED_PRODUCT_ORDER, productOrder);
            context.getExtendedState().getVariables().put(PHYSICAL_PRODUCT_ORDER_ITEM_SERIAL_NUMBER_MAP, physicalProductOrderItemSerialNumberMap);
        } else {
            StateMachineUtil.setDescriptionContext(context, DescriptionConstants.REQUIRED_RESOURCES_NOT_AVAILABLE);
        }
    }

    private Map<String, List<ResourceRef>> convertResourceMapToLogicalResourceRefMap(Map<String, List<Resource>> resourceMap) {
        Map<String, List<ResourceRef>> convertedMap = new HashMap<>();
        for (Map.Entry<String, List<Resource>> entry : resourceMap.entrySet()) {
            List<ResourceRef> convertedList = new ArrayList<>();
            for (Resource resource : entry.getValue()) {
                ResourceRef resourceRef = buildResourceRef(resource.getId(), resource.getHref(), LOGICAL_RESOURCE);
                convertedList.add(resourceRef);
            }
            convertedMap.put(entry.getKey(), convertedList);
        }
        return convertedMap;
    }


    private Map<String, List<ResourceRef>> combineMaps(Map<String, ResourceRef> map1, Map<String, List<ResourceRef>> map2) {
        Map<String, List<ResourceRef>> combinedMap = new HashMap<>();

        // Copy values from map1
        for (Map.Entry<String, ResourceRef> entry : map1.entrySet()) {
            String key = entry.getKey();
            ResourceRef value = entry.getValue();
            List<ResourceRef> newList = new ArrayList<>();
            newList.add(value);
            combinedMap.put(key, newList);
        }

        // Merge values from map2
        for (Map.Entry<String, List<ResourceRef>> entry : map2.entrySet()) {
            String key = entry.getKey();
            List<ResourceRef> valueList = entry.getValue();

            if (combinedMap.containsKey(key)) {
                combinedMap.get(key).addAll(valueList);
            } else {
                combinedMap.put(key, valueList);
            }
        }

        return combinedMap;
    }
}