// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.action;

import com.orange.discobole.ordermanagement.commons.dto.product.configuration.ProductConfiguration;
import com.orange.discobole.ordermanagement.commons.dto.product.configuration.QueryProductConfiguration;
import com.orange.discobole.ordermanagement.commons.dto.product.configuration.QueryProductConfigurationItem;
import com.orange.discobole.ordermanagement.commons.dto.product.offering.ProductOffering;
import com.orange.discobole.ordermanagement.commons.dto.product.offering.price.ProductOfferingPrice;
import com.orange.discobole.ordermanagement.commons.dto.product.specification.ProductSpecification;
import com.orange.discobole.ordermanagement.commons.dto.product.specification.ProductSpecificationRelationship;
import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants;
import com.orange.discobole.ordermanagement.ordercapture.enums.ConfigurationState;
import com.orange.discobole.ordermanagement.ordercapture.mapper.product.order.ProductOrderMapper;
import com.orange.discobole.ordermanagement.ordercapture.service.*;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import com.orange.discobole.processflow.dto.generated.ChannelRef;
import com.orange.discobole.processflow.dto.generated.RelatedEntity;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.processflow.ssm.action.StateMachineStateAction;
import com.orange.discobole.productinventory.dto.v1.Product;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants.ERROR_RETRIEVING_PRODUCT_CONFIGURATION_ITEMS;
import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.*;
import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.*;
import static com.orange.discobole.ordermanagement.ordercapture.util.CurrencyUtils.applyRounding;
import static com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderStateType.DRAFT;
import static com.orange.discobole.ordermanagement.orderinventory.dto.v1.RelationshipType.ISCHILD;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Component("instantiateOrderAction")
public class OrderInstantiationAction implements StateMachineStateAction<String, String> {
    private static final Set<ItemActionType> MODIFY_MIGRATE_ACTIONS =
            Collections.unmodifiableSet(EnumSet.of(ItemActionType.MODIFY, ItemActionType.MIGRATE));
    private static final Set<ItemActionType> DELETE_MIGRATE_ACTIONS =
            Collections.unmodifiableSet(EnumSet.of(ItemActionType.DELETE, ItemActionType.MIGRATE));
    private static final Set<ItemActionType> ADD_MIGRATE_DELETE_ACTIONS = Set.of(
            ItemActionType.ADD,
            ItemActionType.MIGRATE,
            ItemActionType.DELETE
    );
    private final ProductOrderMapper productOrderMapper = Mappers.getMapper(ProductOrderMapper.class);
    private final ProductConfigurationService configurationService;
    private final ProductOrderService productOrderService;
    private final ProductSpecificationService productSpecificationService;
    private final ProductOfferingService productOfferingService;
    private final ProductInventoryService productInventoryService;
    private final SettingsService settingsService;
    private final ProductOfferingPriceService productOfferingPriceService;
    private final BillingCycleService billingCycleService;
    private final Map<String, BiFunction<StateContext<String, String>, List<ProductOrderItem>, Instant>> completionDateHandlers;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public OrderInstantiationAction(ProductConfigurationService configurationService,
                                    ProductOrderService productOrderService,
                                    ProductSpecificationService productSpecificationService,
                                    ProductOfferingService productOfferingService,
                                    ProductInventoryService productInventoryService,
                                    SettingsService settingsService, ProductOfferingPriceService productOfferingPriceService, BillingCycleService billingCycleService) {
        this.configurationService = configurationService;
        this.productOrderService = productOrderService;
        this.productSpecificationService = productSpecificationService;
        this.productOfferingService = productOfferingService;
        this.productInventoryService = productInventoryService;
        this.settingsService = settingsService;
        this.productOfferingPriceService = productOfferingPriceService;
        this.billingCycleService = billingCycleService;

        this.completionDateHandlers = Map.of(
                MODIFICATION, this::adjustModificationCompletionDate,
                TERMINATION, (ctx, items) -> adjustTerminationCompletionDate(ctx),
                MIGRATE, this::adjustMigrationCompletionDate
        );
    }

    @Override
    public Mono<Void> apply(StateContext<String, String> context) {
        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            return Mono.empty();
        }

        try {
            log.info("Inside of the instantiation of product order action");

            String configurationId = StateMachineUtil.getStringValue(context, CONFIGURATION_ID);
            String requestedAction = StateMachineUtil.getStringValue(context, REQUESTED_CONFIGURATION_ACTION);
            String contractProductId = StateMachineUtil.getStringValue(context, CONTRACT_PRODUCT_ID);

            List<ProductOrderItem> productOrderItems = fetchProductOrderItems(configurationId, requestedAction, contractProductId);

            List<OrderPrice> totalPrices = calculateTotalPricesFromOrderItems(productOrderItems);

            handleInvalidValidityCharacteristics(context, productOrderItems, requestedAction);

            Instant requestedCompletionDate = isBillCycleDateCheckEnabled(context)
                    ? adjustOrderRequestedCompletionDate(context, productOrderItems)
                    : null;

            ProductOrder productOrder = createOrUpdateProductOrder(context, productOrderItems, totalPrices, requestedCompletionDate);

            updateStateContextWithOrder(context, productOrder);
            return Mono.empty();

        } catch (DiscoException e) {
            handleDiscoException(context, e);
            return Mono.empty();
        } catch (Exception e) {
            log.error("Unable to instantiate product order [{}]:", e.getMessage(), e);
            StateMachineUtil.setDescriptionContext(context, DescriptionConstants.INTERNAL_SERVER_ERROR);
            updateStateContextWithError(context);
            return Mono.empty();
        }
    }

    private void handleInvalidValidityCharacteristics(StateContext<String, String> context, List<ProductOrderItem> productOrderItems, String requestedAction) {
        if (MODIFICATION.equals(requestedAction) || ADD.equals(requestedAction)) {
            Map<ProductOrderItem, List<ValidityCharacteristic>> validityMap = extractValidityCharacteristics(productOrderItems);

            if (!validityMap.isEmpty()) {
                String offeringId = StateMachineUtil.getStringValue(context, PRODUCT_OFFERING_ID);
                ProductOffering productOffering = productOfferingService.getProductOfferingById(offeringId);

                if (isPostpaidOrHybridBilling(productOffering)) {
                    updateValidityDatesBasedOnBillingCycle(context, validityMap);
                }
            }
        }
    }

    private Map<ProductOrderItem, List<ValidityCharacteristic>> extractValidityCharacteristics(List<ProductOrderItem> productOrderItems) {
        return productOrderItems.stream()
                .filter(item -> ADD.equals(item.getAction().getValue())
                        && item.getProduct() instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product
                        && !CollectionUtils.isEmpty(product.getProductCharacteristic())
                        && product.getProductCharacteristic().stream().anyMatch(ValidityCharacteristic.class::isInstance))
                .collect(Collectors.toMap(
                        item -> item,
                        item -> {
                            com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product =
                                    (com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product) item.getProduct();

                            return product.getProductCharacteristic().stream()
                                    .filter(ValidityCharacteristic.class::isInstance)
                                    .map(ValidityCharacteristic.class::cast)
                                    .filter(validity -> validity.getValue() != null && isValidityDateBeyondThreshold(validity.getValue().getValidTo()))
                                    .toList();
                        }
                ))
                .entrySet().stream()
                .filter(entry -> !CollectionUtils.isEmpty(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private void updateStateContextWithOrder(StateContext<String, String> context, ProductOrder productOrder) {
        setContextVariables(context, productOrder, TRUE);
        addRelatedEntityToContext(context, productOrder);
    }

    private void updateStateContextWithError(StateContext<String, String> context) {
        setContextVariables(context, null, FALSE);
    }

    private void updateValidityDatesBasedOnBillingCycle(StateContext<String, String> context, Map<ProductOrderItem, List<ValidityCharacteristic>> validityMap) {
        com.orange.discobole.processflow.dto.generated.RelatedParty relatedParty = getRelatedParty(context);
        Instant nextBillingDate = billingCycleService.getNextBillingDate(relatedParty.getId());
        validityMap.forEach((productOrderItem, validityCharacteristics) ->
                validityCharacteristics.forEach(validity ->
                        validity.getValue().setValidTo(nextBillingDate)));
    }

    private boolean isValidityDateBeyondThreshold(Instant validTo) {
        if (validTo != null) {
            Instant thresholdDate = LocalDate.of(2100, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant();
            return !validTo.isBefore(thresholdDate);
        }
        return false;
    }

    private void handleDiscoException(StateContext<String, String> context, DiscoException e) {
        if (e.getReason().equals(DescriptionConstants.INTERNAL_SERVER_ERROR)) {
            StateMachineUtil.setDescriptionContext(context, DescriptionConstants.INTERNAL_SERVER_ERROR);
        } else if (e.getReason().equals(DescriptionConstants.PRODUCT_CONFIGURATOR_SERVICE_UNREACHABLE)) {
            StateMachineUtil.setDescriptionContext(context, DescriptionConstants.PRODUCT_CONFIGURATOR_SERVICE_UNREACHABLE);
        } else if (e.getReason().equals(ERROR_RETRIEVING_PRODUCT_CONFIGURATION_ITEMS)) {
            StateMachineUtil.setDescriptionContext(context, DescriptionConstants.VALID_CONFIGURATION_IDENTIFIER_REQUIRED);
        }
        StateMachineUtil.setNextTaskToNull(context);
        setContextVariables(context, null, FALSE);
    }

    private List<ProductOrderItem> fetchProductOrderItems(String configurationId, String requestedConfigurationAction, String contractProductId) {
        QueryProductConfiguration productConfiguration = configurationService.getProductConfigurationById(configurationId);
        List<ProductOrderItem> productOrderItems = productOrderMapper.mapQueryConfigToOrderItems(productConfiguration, requestedConfigurationAction);
        enrichProductOrderItemsWithRelationships(productOrderItems, requestedConfigurationAction, productConfiguration, contractProductId);
        enrichProductOrderItemsResources(productOrderItems, requestedConfigurationAction);
        return productOrderItems;
    }

    private void enrichProductOrderItemsResources(List<ProductOrderItem> productOrderItems, String requestedConfigurationAction) {
        if (MIGRATE.equals(requestedConfigurationAction)) {
            List<ProductOrderItem> migrateItems = productOrderItems
                    .stream()
                    .filter(productOrderItem -> ATOMIC_PRODUCT_OFFERING_TYPE.equals(productOrderItem.getProductOffering().getAtType()) &&
                            ItemActionType.MIGRATE.equals(productOrderItem.getAction()))
                    .toList();
            if (!CollectionUtils.isEmpty(migrateItems)) {
                List<ProductOrderItem> migrateToItems = migrateItems.stream()
                        .filter(productOrderItem -> productOrderItem.getProductOrderItemRelationship()
                                .stream()
                                .anyMatch(orderItemRelationship -> RelationshipType.MIGRATETO.equals(orderItemRelationship.getRelationshipType()))
                        )
                        .toList();

                Map<String, Product> orderItemIdProductMap = getMatchingOrderItemIdToProduct(migrateToItems);
                if (!orderItemIdProductMap.isEmpty()) {
                    addResourcesToOrderItem(orderItemIdProductMap, migrateItems);
                }
            }
        }
    }

    private void addResourcesToOrderItem(Map<String, Product> orderItemIdProductMap, List<ProductOrderItem> migrateItems) {
        List<ProductOrderItem> migrateFromItems = filterMigrateFromItems(migrateItems);
        migrateFromItems.forEach(productOrderItem -> processMigrateFromItem(productOrderItem, orderItemIdProductMap));
    }

    private List<ProductOrderItem> filterMigrateFromItems(List<ProductOrderItem> migrateItems) {
        return migrateItems.stream()
                .filter(this::hasMigrateFromRelationship)
                .toList();
    }

    private boolean hasMigrateFromRelationship(ProductOrderItem productOrderItem) {
        return productOrderItem.getProductOrderItemRelationship() != null
                && productOrderItem.getProductOrderItemRelationship().stream()
                .anyMatch(orderItemRelationship -> RelationshipType.MIGRATEFROM.equals(orderItemRelationship.getRelationshipType()));
    }

    private void processMigrateFromItem(ProductOrderItem productOrderItem, Map<String, Product> orderItemIdProductMap) {
        findMigrateFromRelationship(productOrderItem)
                .ifPresent(orderItemRelationship -> addResourcesToMigrateFromItem(productOrderItem, orderItemRelationship, orderItemIdProductMap));
    }

    private Optional<OrderItemRelationship> findMigrateFromRelationship(ProductOrderItem productOrderItem) {
        if (productOrderItem.getProductOrderItemRelationship() == null) {
            return Optional.empty();
        }
        return productOrderItem.getProductOrderItemRelationship().stream()
                .filter(orderItemRelationship -> RelationshipType.MIGRATEFROM.equals(orderItemRelationship.getRelationshipType()))
                .findFirst();
    }

    private void addResourcesToMigrateFromItem(ProductOrderItem productOrderItem, OrderItemRelationship orderItemRelationship, Map<String, Product> orderItemIdProductMap) {
        if (!(productOrderItem.getProduct() instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product migrateFromItem)) {
            return;
        }
        Product product = orderItemIdProductMap.get(orderItemRelationship.getId());
        if (product == null) {
            return;
        }
        addResourcesIfPresent(product, migrateFromItem);
    }

    private void addResourcesIfPresent(Product product, com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product migrateFromItem) {
        if (!CollectionUtils.isEmpty(product.getRealizingResource())) {
            addRealizingResources(product.getRealizingResource(), migrateFromItem);
        }
        if (!CollectionUtils.isEmpty(product.getRealizingService())) {
            addRealizingServices(product.getRealizingService(), migrateFromItem);
        }
    }


    private Map<String, Product> getMatchingOrderItemIdToProduct(List<ProductOrderItem> migrateToItems) {
        Map<String, String> orderItemProductId = new HashMap<>();
        migrateToItems.forEach(productOrderItem -> {
            if (productOrderItem.getProduct() instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product) {
                orderItemProductId.put(product.getId(), productOrderItem.getId());
            }
        });
        if (!orderItemProductId.isEmpty()) {
            List<String> productIds = orderItemProductId.keySet()
                    .stream()
                    .toList();
            List<Product> products = productInventoryService.getProductByIds(productIds);
            Map<String, Product> orderItemIdProductMap = new HashMap<>();
            products.forEach(product -> {
                String orderItemId = orderItemProductId.get(product.getId());
                orderItemIdProductMap.put(orderItemId, product);
            });
            return orderItemIdProductMap;
        }
        return Collections.emptyMap();
    }

    private void addRealizingServices(List<com.orange.discobole.productinventory.dto.v1.ServiceRef> serviceRefs, com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product) {
        List<ServiceRef> newServices = new ArrayList<>();
        serviceRefs.forEach(serviceRef -> newServices.add(ServiceRef.builder()
                .id(serviceRef.getId())
                .href(serviceRef.getHref())
                .atType(SERVICE_TYPE)
                .build()));
        product.setRealizingService(newServices);
    }

    private void addRealizingResources(List<com.orange.discobole.productinventory.dto.v1.ResourceRef> realizingResource, com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product) {
        List<ResourceRef> newResources = new ArrayList<>();
        realizingResource.forEach(resourceRef -> newResources.add(ResourceRef.builder()
                .id(resourceRef.getId())
                .href(resourceRef.getHref())
                .atType(resourceRef.getAtType())
                .build()));
        product.setRealizingResource(newResources);
    }

    private ProductOrder createOrUpdateProductOrder(StateContext<String, String> context,
                                                    List<ProductOrderItem> productOrderItems,
                                                    List<OrderPrice> orderTotalPrices,
                                                    Instant requestedCompletionDate) {
        ProductOrder existingProductOrder = StateMachineUtil.getObjectValue(context, CREATED_PRODUCT_ORDER, ProductOrder.class);
        ConfigurationState configurationState = StateMachineUtil.getObjectValue(context, CONFIGURATION_STATE, ConfigurationState.class);
        boolean isProductOrderInstantiated = StateMachineUtil.getBooleanValue(context, IS_PRODUCT_ORDER_INSTANTIATED, FALSE);
        List<ChannelRef> channelRefList = StateMachineUtil.getListValue(context.getExtendedState().getVariables(), TASK_CHANNEL, ChannelRef.class);
        List<RelatedChannel> relatedChannels = buildChannels(channelRefList);
        if (isConfigurationModified(isProductOrderInstantiated, existingProductOrder, configurationState)) {
            productOrderService.updateOrderItemsAndOrderTotalPrice(existingProductOrder, productOrderItems, orderTotalPrices);
            return existingProductOrder;
        } else {
            ProductOrder newProductOrder = buildProductOrder(context, productOrderItems, orderTotalPrices, relatedChannels, requestedCompletionDate);
            return productOrderService.createProductOrder(newProductOrder);
        }
    }

    private List<RelatedChannel> buildChannels(List<ChannelRef> channelRefList) {
        String firstChannelId = CollectionUtils.isEmpty(channelRefList) ? null : channelRefList.get(0).getId();
        String firstChannelName = CollectionUtils.isEmpty(channelRefList) ? null : channelRefList.get(0).getName();
        if (!isBlank(firstChannelId) || !isBlank(firstChannelName)) {
            com.orange.discobole.ordermanagement.orderinventory.dto.v1.ChannelRef channelRef = com.orange.discobole.ordermanagement.orderinventory.dto.v1.ChannelRef.builder()
                    .id(firstChannelId)
                    .name(firstChannelName)
                    .atType(CHANNEL_REF_TYPE)
                    .build();

            return List.of(RelatedChannel.builder()
                    .channel(channelRef)
                    .atType(RELATED_CHANNEL_TYPE)
                    .build());
        }
        return List.of();
    }

    private ProductOrder buildProductOrder(StateContext<String, String> context,
                                           List<ProductOrderItem> productOrderItems,
                                           List<OrderPrice> orderTotalPrices,
                                           List<RelatedChannel> relatedChannels,
                                           Instant requestedCompletionDate) {
        List<RelatedPartyRefOrPartyRoleRef> relatedParties = extractRelatedPartyFromContext(context);
        Instant currentDateTime = Instant.now();
        setProductOrderItemId(productOrderItems);
        return ProductOrder.builder()
                .channel(relatedChannels)
                .productOrderItem(productOrderItems)
                .requestedCompletionDate(requestedCompletionDate)
                .orderTotalPrice(orderTotalPrices)
                .relatedParty(relatedParties)
                .creationDate(currentDateTime)
                .state(DRAFT)
                .atType(PRODUCT_ORDER_TYPE)
                .build();
    }

    private void setProductOrderItemId(List<ProductOrderItem> productOrderItems) {
        Map<String, String> productOrderItemIds = new HashMap<>();
        productOrderItems.forEach(productOrderItem -> {
            String itemId = String.valueOf(UUID.randomUUID());
            if (Objects.nonNull(productOrderItem.getId())) {
                productOrderItemIds.put(productOrderItem.getId(), itemId);
            }
            productOrderItem.setId(itemId);
            productOrderItem.setState(ProductOrderItemStateType.DRAFT);
        });
        setProductOrderItemRelationshipId(productOrderItems, productOrderItemIds);
    }

    private void setProductOrderItemRelationshipId(List<ProductOrderItem> productOrderItems, Map<String, String> productOrderItemIds) {
        productOrderItems.forEach(productOrderItem -> {
            if (productOrderItem.getProductOrderItemRelationship() != null) {
                productOrderItem.getProductOrderItemRelationship()
                        .forEach(orderItemRelationshipDTO -> {
                            if (orderItemRelationshipDTO.getId() != null
                                    && productOrderItemIds.containsKey(orderItemRelationshipDTO.getId())) {
                                orderItemRelationshipDTO.setId(productOrderItemIds.get(orderItemRelationshipDTO.getId()));
                            }
                        });
            }
        });
    }

    private void enrichProductOrderItemsWithRelationships(List<ProductOrderItem> productOrderItems, String requestedConfigurationAction, QueryProductConfiguration productConfiguration, String contractProductId) {
        processReliesOnRelationship(productOrderItems, productConfiguration, contractProductId, requestedConfigurationAction);
        processHasParentRelationship(productOrderItems, productConfiguration, requestedConfigurationAction);
        processMigrateFromRelationship(productOrderItems, requestedConfigurationAction);
    }

    private void processReliesOnRelationshipForModifyItemInMigration(List<ProductOrderItem> productOrderItems, String requestedConfigurationAction) {
        if (MIGRATE.equals(requestedConfigurationAction)) {
            List<ProductOrderItem> modifyItems = getProductOrderItemsByAction(productOrderItems, ItemActionType.MODIFY);
            if (!CollectionUtils.isEmpty(modifyItems)) {
                List<String> productSpecificationIds = extractProductSpecificationIds(modifyItems);
                if (!CollectionUtils.isEmpty(productSpecificationIds)) {
                    List<ProductOrderItem> migratedItems = getProductOrderItemsByAction(productOrderItems, ItemActionType.MIGRATE);
                    if (!CollectionUtils.isEmpty(migratedItems)) {
                        List<ProductSpecification> productSpecifications = productSpecificationService.fetchProductSpecifications(productSpecificationIds);
                        productSpecifications.forEach(productSpecification -> addReliesOnRelationship(productSpecification, modifyItems, migratedItems));
                    }
                }
            }

        }
    }

    private void addReliesOnRelationship(ProductSpecification productSpecification, List<ProductOrderItem> filtredItems, List<ProductOrderItem> reliesOnItems) {
        if (!CollectionUtils.isEmpty(productSpecification.getProductSpecificationRelationship())) {
            List<ProductSpecificationRelationship> reliesOnRelationships = getReliesOnProductSpecificationRelationships(productSpecification);
            reliesOnRelationships.forEach(reliesOnRelationship -> {
                List<ProductOrderItem> matchingProductOrderItems = findMatchingSpecItems(productSpecification, filtredItems);
                matchingProductOrderItems.forEach(orderItem -> addReliesOnRelationships(reliesOnRelationship.getId(), reliesOnItems, filtredItems, orderItem));
            });
        }
    }

    private List<ProductOrderItem> getProductOrderItemsByAction(List<ProductOrderItem> productOrderItems, ItemActionType itemActionType) {
        return productOrderItems
                .stream()
                .filter(productOrderItem -> ATOMIC_PRODUCT_OFFERING_TYPE.equals(productOrderItem.getProductOffering().getAtType())
                        && itemActionType.equals(productOrderItem.getAction()))
                .toList();
    }

    private List<ProductOrderItem> getAtomicItems(List<ProductOrderItem> productOrderItems) {
        return productOrderItems.stream()
                .filter(item -> item.getProduct() instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product
                        && product.getProductSpecification() != null
                        && !SHIPPING_PRODUCT_SPECIFICATION_BASE_TYPE.equals(product.getProductSpecification().getAtBaseType()))
                .toList();
    }

    private void processMigrateFromRelationship(List<ProductOrderItem> productOrderItems, String requestedConfigurationAction) {
        if (MIGRATE.equals(requestedConfigurationAction)) {
            List<ProductOrderItem> migrateFromItems = productOrderItems.stream()
                    .filter(productOrderItem -> Optional.ofNullable(productOrderItem.getProductOrderItemRelationship())
                            .map(list -> list.stream()
                                    .filter(Objects::nonNull)
                                    .anyMatch(orderItemRelationship -> RelationshipType.MIGRATEFROM.equals(orderItemRelationship.getRelationshipType())))
                            .orElse(false))
                    .toList();
            if (!CollectionUtils.isEmpty(migrateFromItems)) {
                for (ProductOrderItem productOrderItem : migrateFromItems) {
                    createMigrateFromRelationship(productOrderItem, productOrderItems);
                }
            }
        }
    }

    private void createMigrateFromRelationship(ProductOrderItem productOrderItem, List<ProductOrderItem> productOrderItems) {
        Optional<ProductOrderItem> migrateToItem = getMigrateToItem(productOrderItem, productOrderItems);
        if (migrateToItem.isPresent()
                && migrateToItem.get().getProduct() instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product productTo) {
            ProductRelationship productRelationship = ProductRelationship
                    .builder()
                    .relationshipType(MIGRATE_FROM)
                    .product(ProductRef
                            .builder()
                            .id(productTo.getId()).build())
                    .atType(PRODUCT_RELATIONSHIP_TYPE)
                    .build();

            if (productOrderItem.getProduct() instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product) {
                List<ProductRelationship> productRelationships = Objects.nonNull(product.getProductRelationship())
                        ? new ArrayList<>(product.getProductRelationship())
                        : new ArrayList<>();


                productRelationships.add(productRelationship);
                product.setProductRelationship(productRelationships);
            } else {
                List<ProductRelationship> productRelationships = new ArrayList<>();
                productRelationships.add(productRelationship);
                productOrderItem
                        .setProduct(com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product
                                .builder().productRelationship(productRelationships).build());

            }
        }
    }

    private Optional<ProductOrderItem> getMigrateToItem(ProductOrderItem productOrderItem, List<ProductOrderItem> productOrderItems) {
        Optional<String> migratedItemId = productOrderItem.getProductOrderItemRelationship()
                .stream()
                .filter(orderItemRelationship -> RelationshipType.MIGRATEFROM.equals(orderItemRelationship.getRelationshipType()))
                .map(OrderItemRelationship::getId)
                .findFirst();
        return migratedItemId.flatMap(migratedId -> productOrderItems
                .stream()
                .filter(productOrderItem1 -> migratedId.equals(productOrderItem1.getId()))
                .findFirst());
    }


    private void processReliesOnRelationship(List<ProductOrderItem> productOrderItems, QueryProductConfiguration productConfiguration, String contractProductId, String requestedConfigurationAction) {
        List<ProductOrderItem> filteredAtomicItems = filterAtomicProductOrderItems(productOrderItems);
        if (!CollectionUtils.isEmpty(filteredAtomicItems)) {
            List<String> productSpecificationIds = extractProductSpecificationIds(filteredAtomicItems);
            if (!CollectionUtils.isEmpty(productSpecificationIds)) {
                List<ProductSpecification> productSpecifications = productSpecificationService.fetchProductSpecifications(productSpecificationIds);
                if (ADD.equals(requestedConfigurationAction) || MIGRATE.equals(requestedConfigurationAction)) {
                    List<ProductOrderItem> atomicItems = getAtomicItems(productOrderItems);
                    productSpecifications.forEach(productSpecification -> applyReliesOnSpecRelationships(productSpecification, filteredAtomicItems, atomicItems));
                } else {
                    List<QueryProductConfigurationItem> bundledOrContractItems = getBundledOrContractItems(productConfiguration);
                    List<Product> products = productInventoryService.getProductsByRelationship(contractProductId);
                    productSpecifications.forEach(productSpecification -> applyReliesOnSpecRelationships(productSpecification, filteredAtomicItems, bundledOrContractItems, products));
                }
            }
        }
        processReliesOnRelationshipForModifyItemInMigration(productOrderItems, requestedConfigurationAction);
    }


    private void processHasParentRelationship(List<ProductOrderItem> productOrderItems, QueryProductConfiguration productConfiguration, String requestedConfigurationAction) {
        if (MODIFICATION.equals(requestedConfigurationAction)) {
            List<ProductOrderItem> filteredParentItems = productOrderItems.stream()
                    .filter(item -> ItemActionType.ADD.equals(item.getAction()))
                    .filter(OrderInstantiationAction::isParentProductOrderItem)
                    .filter(item -> !isShippingItem(item))
                    .toList();

            List<QueryProductConfigurationItem> bundledOrContractItems = getBundledOrContractItems(productConfiguration);
            filteredParentItems.forEach(orderItem -> {
                String parentProductId = getParentProductId(orderItem, bundledOrContractItems);
                if (!isBlank(parentProductId)) {
                    createHasParentRelationship(parentProductId, orderItem);
                }
            });
        }
    }

    private static boolean isShippingItem(ProductOrderItem item) {
        return item.getProduct() instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product
                && product.getProductSpecification() != null
                && SHIPPING_PRODUCT_SPECIFICATION_BASE_TYPE.equals(product.getProductSpecification().getAtBaseType());
    }


    private static Boolean isParentProductOrderItem(ProductOrderItem productOrderItem) {
        return Optional.ofNullable(productOrderItem.getProductOrderItemRelationship())
                .map(list -> list.stream()
                        .filter(Objects::nonNull)
                        .noneMatch(orderItemRelationship -> ISCHILD.equals(orderItemRelationship.getRelationshipType())))
                .orElse(true);
    }

    private List<QueryProductConfigurationItem> getBundledOrContractItems(QueryProductConfiguration productConfiguration) {
        return productConfiguration.getComputedProductConfigurationItems()
                .stream()
                .filter(queryProductConfigurationItem -> {
                    com.orange.discobole.ordermanagement.commons.dto.product.configuration.ProductOfferingRef offering = queryProductConfigurationItem.getProductConfiguration().getProductOffering();
                    return Objects.nonNull(offering) &&
                            (ServiceConstants.BUNDLE_PRODUCT_OFFERING_TYPE.equals(offering.getReferredType()) ||
                                    ServiceConstants.CONTRACT_PRODUCT_OFFERING_TYPE.equals(offering.getReferredType()));
                })
                .toList();
    }

    private void createHasParentRelationship(String bundledProductId, ProductOrderItem item) {
        if (item.getProduct() instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product) {
            if (CollectionUtils.isEmpty(product.getProductRelationship())) {
                product.setProductRelationship(List.of(ProductRelationship.builder()
                        .relationshipType(HAS_PARENT)
                        .product(ProductRef.builder()
                                .id(bundledProductId)
                                .build())
                        .atType(PRODUCT_RELATIONSHIP_TYPE)
                        .build()));
            } else {
                List<ProductRelationship> productRelationships = new ArrayList<>(product.getProductRelationship());
                productRelationships.add(ProductRelationship.builder()
                        .relationshipType(HAS_PARENT)
                        .product(ProductRef.builder()
                                .id(bundledProductId)
                                .build())
                        .atType(PRODUCT_RELATIONSHIP_TYPE)
                        .build());
                product.setProductRelationship(productRelationships);
            }
        }
    }

    private String getParentProductId(ProductOrderItem productOrderItem, List<QueryProductConfigurationItem> bundledQueryProductConfigurationItems) {
        return bundledQueryProductConfigurationItems.stream()
                .filter(queryProductConfigurationItem -> isBundleItem(queryProductConfigurationItem, productOrderItem))
                .map(QueryProductConfigurationItem::getProductConfiguration)
                .filter(Objects::nonNull)
                .map(this::getProductId)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private String getProductId(ProductConfiguration productConfiguration) {
        if (productConfiguration.getProduct() instanceof com.orange.discobole.ordermanagement.commons.dto.product.configuration.Product product) {
            return product.getId();
        }
        if (productConfiguration.getProduct() instanceof com.orange.discobole.ordermanagement.commons.dto.product.configuration.ProductRef productRef) {
            return productRef.getId();
        }
        return null;
    }

    private String getProductId(ProductOrderItem productOrderItem) {
        if (productOrderItem.getProduct() instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product matchingProduct) {
            return matchingProduct.getId();
        }
        return null;
    }

    private boolean isBundleItem(QueryProductConfigurationItem queryProductConfigurationItem, ProductOrderItem productOrderItem) {
        if (CollectionUtils.isEmpty(queryProductConfigurationItem.getProductConfigurationItemRelationships())) {
            return false;
        }

        return queryProductConfigurationItem.getProductConfigurationItemRelationships()
                .stream()
                .anyMatch(productConfigurationItemRelationship -> BUNDLES.equalsIgnoreCase(productConfigurationItemRelationship.getRelationshipType())
                        && productOrderItem.getId().equals(productConfigurationItemRelationship.getId())
                );
    }

    private List<ProductOrderItem> filterAtomicProductOrderItems(List<ProductOrderItem> productOrderItems) {
        return productOrderItems.stream()
                .filter(item -> ADD_MIGRATE_DELETE_ACTIONS.contains(item.getAction())
                        && item.getProduct() instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product
                        && product.getProductSpecification() != null
                        && !SHIPPING_PRODUCT_SPECIFICATION_BASE_TYPE.equals(product.getProductSpecification().getAtBaseType()))
                .toList();
    }

    private List<String> extractProductSpecificationIds(List<ProductOrderItem> filteredAtomicItems) {
        return filteredAtomicItems.stream()
                .filter(item -> item.getProduct() instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product &&
                        product.getProductSpecification() != null)
                .map(item -> ((com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product) item.getProduct())
                        .getProductSpecification()
                        .getId())
                .distinct()
                .toList();
    }

    private void applyReliesOnSpecRelationships(ProductSpecification productSpecification, List<ProductOrderItem> filteredAtomicItems, List<ProductOrderItem> atomicItems) {
        if (!CollectionUtils.isEmpty(productSpecification.getProductSpecificationRelationship())) {
            List<ProductSpecificationRelationship> reliesOnRelationships = getReliesOnProductSpecificationRelationships(productSpecification);
            reliesOnRelationships.forEach(reliesOnRelationship -> {
                List<ProductOrderItem> productOrderItems = findMatchingSpecItems(productSpecification, filteredAtomicItems);
                productOrderItems.forEach(orderItem -> addReliesOnRelationships(reliesOnRelationship.getId(), filteredAtomicItems, atomicItems, orderItem));
            });
        }
    }

    private void applyReliesOnSpecRelationships(ProductSpecification productSpecification, List<ProductOrderItem> filteredAtomicItems, List<QueryProductConfigurationItem> bundledOrContractItems, List<Product> products) {
        if (!CollectionUtils.isEmpty(productSpecification.getProductSpecificationRelationship())) {
            List<ProductSpecificationRelationship> reliesOnRelationships = getReliesOnProductSpecificationRelationships(productSpecification);
            reliesOnRelationships.forEach(reliesOnRelationship -> {
                List<ProductOrderItem> productOrderItems = findMatchingSpecItems(productSpecification, filteredAtomicItems);
                productOrderItems.forEach(orderItem -> addReliesOnRelationships(reliesOnRelationship.getId(), filteredAtomicItems, orderItem, bundledOrContractItems, products));
            });
        }
    }

    private List<ProductSpecificationRelationship> getReliesOnProductSpecificationRelationships(ProductSpecification productSpecification) {
        return productSpecification.getProductSpecificationRelationship().stream()
                .filter(rel -> StringUtils.isNotBlank(rel.getRelationshipType()) && RELIES_ON.equals(rel.getRelationshipType()))
                .toList();
    }

    private List<ProductOrderItem> findMatchingSpecItems(ProductSpecification productSpecification, List<ProductOrderItem> filteredItems) {
        return filteredItems.stream()
                .filter(item -> item.getProduct() instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product &&
                        productSpecification.getId().equals(product.getProductSpecification().getId()))
                .toList();
    }

    private void addReliesOnRelationships(String specId, List<ProductOrderItem> filteredItems, List<ProductOrderItem> atomicItems, ProductOrderItem productOrderItem) {
        // Attempt to find a sibling item with the same parent
        Optional<ProductOrderItem> matchingItem = findProductOrderItemBySpecAndFilter(
                specId,
                filteredItems,
                item -> hasSameParent(productOrderItem, item)
        );

        // Fallback to any matching item if sibling not found
        if (matchingItem.isEmpty()) {
            matchingItem = findProductOrderItemBySpecAndFilter(specId, filteredItems, item -> true);
        }

        if (matchingItem.isPresent()) {
            createOrderItemRelationship(productOrderItem, matchingItem.get());
        } else {
            matchingItem = findProductOrderItemBySpecAndFilter(specId, atomicItems, this::hasNoChangeModifyAction);
            matchingItem.ifPresent(matched -> createProductReliesOnRelationship(productOrderItem, matched));
        }
    }

    private void addReliesOnRelationships(String specId, List<ProductOrderItem> filteredItems, ProductOrderItem productOrderItem, List<QueryProductConfigurationItem> bundledOrContractItems, List<Product> products) {
        // Attempt to find a sibling item with the same parent
        Optional<ProductOrderItem> siblingItem = findProductOrderItemBySpecAndFilter(
                specId,
                filteredItems,
                item -> hasSameParentProduct(productOrderItem, item, bundledOrContractItems) || hasSameParent(productOrderItem, item)
        );

        //case of order item and its relationship reliesOn have the same parent in case of modification use case
        if (siblingItem.isPresent()) {
            createOrderItemRelationship(productOrderItem, siblingItem.get());
        } else {
            //case of order item and its relationship reliesOn already created in case of modification use case
            String parentProductId = getParentProductId(productOrderItem, bundledOrContractItems);
            Optional<Product> matchingProduct = (parentProductId != null)
                    ? findProductBySpec(specId, parentProductId, products)
                    : Optional.empty();

            if (matchingProduct.isPresent()) {
                createProductRelationship(productOrderItem, matchingProduct.get().getId());
            } else {
                Optional<ProductOrderItem> reliesOnItem = findProductOrderItemBySpecAndFilter(specId, filteredItems, item -> true);
                reliesOnItem.ifPresent(orderItem -> createOrderItemRelationship(productOrderItem, orderItem));
            }
        }
    }

    private boolean hasNoChangeModifyAction(ProductOrderItem productOrderItem) {
        return ItemActionType.NOCHANGE.equals(productOrderItem.getAction()) || ItemActionType.MODIFY.equals(productOrderItem.getAction());
    }

    private void createProductReliesOnRelationship(ProductOrderItem productOrderItem, ProductOrderItem matchingProductItem) {
        String productId = getProductId(matchingProductItem);
        if (productId != null) {
            createProductRelationship(productOrderItem, productId);
        }
    }


    private Optional<ProductOrderItem> findProductOrderItemBySpecAndFilter(String specId, List<ProductOrderItem> filteredItems, Predicate<ProductOrderItem> filter) {
        return filteredItems.stream()
                .filter(item -> item.getProduct() instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product &&
                        specId.equals(product.getProductSpecification().getId()))
                .filter(filter)
                .findFirst();
    }

    private void createProductRelationship(ProductOrderItem productOrderItem, String productId) {
        ProductRelationship productRelationship = ProductRelationship
                .builder()
                .relationshipType(RELIES_ON)
                .product(ProductRef
                        .builder()
                        .id(productId).build())
                .atType(PRODUCT_RELATIONSHIP_TYPE)
                .build();

        if (productOrderItem.getProduct() instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product) {
            List<ProductRelationship> productRelationships = Objects.nonNull(product.getProductRelationship())
                    ? new ArrayList<>(product.getProductRelationship())
                    : new ArrayList<>();

            productRelationships.add(productRelationship);
            product.setProductRelationship(productRelationships);
        }
    }

    private Optional<Product> findProductBySpec(String specId, String parentProductId, List<Product> products) {
        // Find child product IDs bundled with the parent product
        Set<String> childProductIds = products.stream()
                .filter(product -> product.getId().equals(parentProductId))
                .flatMap(product -> product.getProductRelationship()
                        .stream()
                        .filter(productRelationship -> BUNDLES.equals(productRelationship.getRelationshipType()))
                        .map(productRelationship -> ((com.orange.discobole.productinventory.dto.v1.ProductRef) productRelationship.getProduct()).getId())
                )
                .collect(Collectors.toSet());

        // Try to find a matching product among child products
        Optional<Product> matchingChildProduct = products.stream()
                .filter(product -> childProductIds.contains(product.getId()))
                .filter(product -> hasMatchingSpec(product, specId))
                .findFirst();

        // Fallback to searching all products if no child match is found
        return matchingChildProduct.isPresent()
                ? matchingChildProduct
                : products.stream()
                .filter(product -> hasMatchingSpec(product, specId))
                .findFirst();
    }

    private boolean hasMatchingSpec(Product product, String specId) {
        return product.getProductSpecification() != null
                && specId.equals(product.getProductSpecification().getId());
    }

    private void createOrderItemRelationship(ProductOrderItem productOrderItem, ProductOrderItem matchingItem) {
        OrderItemRelationship reliesOnOrderItemRelationship = buildOrderItemRelationship(matchingItem.getId());
        if (CollectionUtils.isEmpty(productOrderItem.getProductOrderItemRelationship())) {
            productOrderItem.setProductOrderItemRelationship(new ArrayList<>());
        }

        List<OrderItemRelationship> orderItemRelationships = new ArrayList<>(productOrderItem.getProductOrderItemRelationship());
        orderItemRelationships.add(reliesOnOrderItemRelationship);
        productOrderItem.setProductOrderItemRelationship(orderItemRelationships);

        createReliesOnRelationshipForModifyItems(productOrderItem, matchingItem);
    }

    private void createReliesOnRelationshipForModifyItems(ProductOrderItem productOrderItem, ProductOrderItem matchingItem) {
        if (ItemActionType.MODIFY.equals(productOrderItem.getAction())) {
            Optional<OrderItemRelationship> migrateRelationship = matchingItem.getProductOrderItemRelationship()
                    .stream()
                    .filter(orderItemRelationship -> RelationshipType.MIGRATETO.equals(orderItemRelationship.getRelationshipType()) || RelationshipType.MIGRATEFROM.equals(orderItemRelationship.getRelationshipType()))
                    .findFirst();

            migrateRelationship.ifPresent(itemRelationship -> productOrderItem.getProductOrderItemRelationship().add(buildOrderItemRelationship(itemRelationship.getId())));
        }
    }

    private OrderItemRelationship buildOrderItemRelationship(String reliesOnId) {
        OrderItemRelationship orderItemRelationship = new OrderItemRelationship();
        orderItemRelationship.setId(reliesOnId);
        orderItemRelationship.setRelationshipType(RelationshipType.RELIESON);
        orderItemRelationship.atType(ORDER_ITEM_RELATIONSHIP_TYPE);
        return orderItemRelationship;
    }

    private boolean hasSameParentProduct(ProductOrderItem productOrderItem1, ProductOrderItem productOrderItem2, List<QueryProductConfigurationItem> bundledOrContractItems) {
        String parentProductId1 = getParentProductId(productOrderItem1, bundledOrContractItems);
        String parentProductId2 = getParentProductId(productOrderItem2, bundledOrContractItems);
        if (parentProductId1 == null || parentProductId2 == null) {
            return false;
        }
        return parentProductId1.equals(parentProductId2);
    }

    private boolean hasSameParent(ProductOrderItem productOrderItem1, ProductOrderItem productOrderItem2) {
        OrderItemRelationship orderItemRelationship1 = getParentOrderItemRelationship(productOrderItem1);
        OrderItemRelationship orderItemRelationship2 = getParentOrderItemRelationship(productOrderItem2);
        if (orderItemRelationship1 == null || orderItemRelationship2 == null) {
            return false;
        }
        return orderItemRelationship1.getId().equals(orderItemRelationship2.getId());
    }

    private OrderItemRelationship getParentOrderItemRelationship(ProductOrderItem item) {
        if (CollectionUtils.isEmpty(item.getProductOrderItemRelationship())) {
            return null;
        }
        return item.getProductOrderItemRelationship()
                .stream()
                .filter(orderItemRelationship -> ISCHILD.equals(orderItemRelationship.getRelationshipType()))
                .findFirst()
                .orElse(null);
    }

    private void setContextVariables(StateContext<String, String> context, ProductOrder productOrder, Boolean isInstantiated) {
        context.getExtendedState().getVariables().put(IS_PRODUCT_ORDER_INSTANTIATED, isInstantiated);
        if (Boolean.TRUE.equals(isInstantiated)) {
            context.getExtendedState().getVariables().put(CREATED_PRODUCT_ORDER, productOrder);
        }
    }

    private void addRelatedEntityToContext(StateContext<String, String> context, ProductOrder createdProductOrder) {
        List<RelatedEntity> relatedEntities = StateMachineUtil.getListValue(context.getExtendedState().getVariables(), RELATED_ENTITY, RelatedEntity.class);
        RelatedEntity newEntity = new RelatedEntity().id(createdProductOrder.getId()).referredType(PRODUCT_ORDER_TYPE);
        relatedEntities.add(newEntity);
        context.getExtendedState().getVariables().put(RELATED_ENTITY, relatedEntities);
        context.getExtendedState().getVariables().put(PROCESS_RELATED_ENTITY, relatedEntities);
    }

    private boolean isConfigurationModified(boolean isProductOrderInstantiated, ProductOrder productOrder, ConfigurationState configurationState) {
        return isProductOrderInstantiated && productOrder != null && configurationState == ConfigurationState.MODIFIED;
    }

    private List<RelatedPartyRefOrPartyRoleRef> extractRelatedPartyFromContext(StateContext<String, String> context) {
        com.orange.discobole.processflow.dto.generated.RelatedParty relatedParty = getRelatedParty(context);
        if (Objects.isNull(relatedParty)) {
            return Collections.emptyList();
        }
        return productOrderMapper.mapRelatedPartyListToRefOrRoleRefList(Collections.singletonList(relatedParty));
    }

    private List<OrderPrice> calculateTotalPricesFromOrderItems(List<ProductOrderItem> productOrderItems) {
        List<OrderPrice> orderItemPrices = productOrderItems.stream()
                .flatMap(item -> CollectionUtils.isEmpty(item.getItemPrice()) ? Stream.empty() : item.getItemPrice().stream())
                .toList();

        List<OrderPrice> nonRecurringPrices = new ArrayList<>();
        Map<Quantity, List<OrderPrice>> recurringPricesByPeriod = new HashMap<>();
        List<InstallmentCharge> installmentCharges = new ArrayList<>();
        separatePrices(orderItemPrices, nonRecurringPrices, recurringPricesByPeriod, installmentCharges);

        List<OrderPrice> totalPrices = new ArrayList<>();
        computeAndAddTotalPrices(installmentCharges, nonRecurringPrices, recurringPricesByPeriod, totalPrices);

        return totalPrices;
    }

    private void separatePrices(List<OrderPrice> itemPrices, List<OrderPrice> nonRecurringPrices, Map<Quantity, List<OrderPrice>> recurringPricesByPeriod, List<InstallmentCharge> installmentCharges) {
        for (OrderPrice itemPrice : itemPrices) {
            String priceType = itemPrice.getPriceType();
            if (NRC_PRICE_TYPE.equals(priceType)) {
                nonRecurringPrices.add(itemPrice);
            } else if (RC_PRICE_TYPE.equals(priceType)) {
                Quantity recurringChargePeriod = normalizePeriod(itemPrice.getRecurringChargePeriod());
                recurringPricesByPeriod
                        .computeIfAbsent(recurringChargePeriod, k -> new ArrayList<>())
                        .add(itemPrice);
            }
        }
        extractInstallmentCharges(itemPrices, installmentCharges);
    }

    private void extractInstallmentCharges(List<OrderPrice> itemPrices, List<InstallmentCharge> installmentCharges) {
        List<InstallmentCharge> extracted = itemPrices.stream()
                .filter(Objects::nonNull)
                .map(OrderPrice::getProductOfferingPrice)
                .filter(Objects::nonNull)
                .filter(InstallmentCharge.class::isInstance)
                .map(InstallmentCharge.class::cast)
                .filter(installmentCharge -> Objects.nonNull(installmentCharge.getDownPayment()))
                .toList();
        installmentCharges.addAll(extracted);
    }


    private void computeAndAddTotalPrices(List<InstallmentCharge> installmentCharges, List<OrderPrice> nonRecurringPrices,
                                          Map<Quantity, List<OrderPrice>> recurringPricesByPeriod,
                                          List<OrderPrice> totalPrices) {
        if (!CollectionUtils.isEmpty(nonRecurringPrices) || !CollectionUtils.isEmpty(installmentCharges)) {
            addTotalPrice(nonRecurringPrices, installmentCharges, NRC_PRICE_TYPE, null, totalPrices);
        }
        if (!CollectionUtils.isEmpty(recurringPricesByPeriod)) {
            recurringPricesByPeriod.forEach((period, prices) ->
                    addTotalPrice(prices, null, RC_PRICE_TYPE, period, totalPrices)
            );
        }
    }

    private void addTotalPrice(List<OrderPrice> prices, List<InstallmentCharge> installmentCharges, String priceType, Quantity period, List<OrderPrice> totalPrices) {
        Price totalPrice = calculateTotalPrice(prices, installmentCharges);
        List<PriceAlteration> priceAlterations = calculatePriceAlterations(prices);

        applyRounding(totalPrice);
        priceAlterations.forEach(pa -> applyRounding(pa.getPrice()));

        totalPrices.add(OrderPrice.builder()
                .price(totalPrice)
                .priceType(priceType)
                .priceAlteration(priceAlterations)
                .atType(ORDER_PRICE_TYPE)
                .recurringChargePeriod(getRecurringPeriodIfRequired(priceType, period))
                .build());
    }

    private Quantity getRecurringPeriodIfRequired(String priceType, Quantity period) {
        return RC_PRICE_TYPE.equals(priceType) ? period : null;
    }

    private List<PriceAlteration> calculatePriceAlterations(List<OrderPrice> itemPrices) {
        Map<String, Map<Quantity, List<PriceAlteration>>> alterationsMap = new HashMap<>();
        Map<String, Map<Quantity, List<Money>>> basePricesMap = new HashMap<>();

        processOrderPrices(itemPrices, alterationsMap, basePricesMap);
        return aggregateResults(alterationsMap, basePricesMap);
    }

    private void processOrderPrices(
            List<OrderPrice> itemPrices,
            Map<String, Map<Quantity, List<PriceAlteration>>> alterationsMap,
            Map<String, Map<Quantity, List<Money>>> basePricesMap) {

        for (OrderPrice orderPrice : itemPrices) {
            if (isPriceAlterationInvalid(orderPrice)) {
                continue;
            }

            Money basePrice = extractPriceFromProductOfferingPrice(orderPrice.getProductOfferingPrice());

            for (PriceAlteration pa : orderPrice.getPriceAlteration()) {
                String type = pa.getAtType();
                Quantity period = normalizePeriod(pa.getRecurringChargePeriod());

                alterationsMap.computeIfAbsent(type, k -> new HashMap<>())
                        .computeIfAbsent(period, k -> new ArrayList<>())
                        .add(pa);

                basePricesMap.computeIfAbsent(type, k -> new HashMap<>())
                        .computeIfAbsent(period, k -> new ArrayList<>())
                        .add(basePrice);
            }
        }
    }

    private Money extractPriceFromProductOfferingPrice(ProductOfferingPriceRefOrValue productOfferingPrice) {
        if (productOfferingPrice instanceof ProductOfferingPriceCharge priceCharge) {
            return priceCharge.getPrice();
        }
        return null;
    }

    private List<PriceAlteration> aggregateResults(
            Map<String, Map<Quantity, List<PriceAlteration>>> alterationsMap,
            Map<String, Map<Quantity, List<Money>>> basePricesMap) {

        List<PriceAlteration> result = new ArrayList<>();

        for (Map.Entry<String, Map<Quantity, List<PriceAlteration>>> typeEntry : alterationsMap.entrySet()) {
            String priceType = typeEntry.getKey();
            Map<Quantity, List<PriceAlteration>> periodMap = typeEntry.getValue();

            for (Map.Entry<Quantity, List<PriceAlteration>> periodEntry : periodMap.entrySet()) {
                Quantity period = periodEntry.getKey();
                List<PriceAlteration> alterations = periodEntry.getValue();
                List<Money> basePrices = basePricesMap.get(priceType).get(period);

                if (basePrices == null || basePrices.isEmpty()) {
                    continue;
                }

                PeriodTotals totals = calculatePeriodTotals(alterations, basePrices);
                if (totals.hasNonZeroTotals()) {
                    result.add(createPriceAlteration(
                            totals.totalDutyFree,
                            totals.totalTaxIncluded,
                            totals.priceType,
                            period,
                            totals.atType,
                            totals.dutyFreeUnit,
                            totals.taxIncludedUnit
                    ));
                }
            }
        }

        return result;
    }

    private PeriodTotals calculatePeriodTotals(List<PriceAlteration> alterations, List<Money> basePrices) {
        BigDecimal totalDutyFree = BigDecimal.ZERO;
        BigDecimal totalTaxIncluded = BigDecimal.ZERO;
        String dutyFreeUnit = null;
        String taxIncludedUnit = null;
        String priceType = null;
        String atType = null;

        for (int i = 0; i < alterations.size(); i++) {
            PriceAlteration alteration = alterations.get(i);
            Money basePrice = basePrices.get(i);

            if (dutyFreeUnit == null) {
                dutyFreeUnit = extractUnitFromMoney(basePrice);
            }
            if (taxIncludedUnit == null) {
                taxIncludedUnit = extractUnitFromMoney(basePrice);
            }

            if (priceType == null) {
                priceType = alteration.getPriceType();
            }
            if (atType == null) {
                atType = alteration.getAtType();
            }

            totalDutyFree = totalDutyFree.add(calculateFinalDutyFreeAmount(alteration, basePrice));
            totalTaxIncluded = totalTaxIncluded.add(calculateFinalTaxIncludedAmount(alteration, basePrice));
        }

        return new PeriodTotals(totalDutyFree, totalTaxIncluded, dutyFreeUnit, taxIncludedUnit, priceType, atType);
    }

    private String extractUnitFromMoney(Money moneyAmount) {
        return (moneyAmount != null) ? moneyAmount.getUnit() : null;
    }

    private BigDecimal calculateFinalDutyFreeAmount(PriceAlteration alteration, Money basePrice) {
        BigDecimal directAmount = getAmount(alteration.getPrice().getDutyFreeAmount());
        if (directAmount != null) {
            return directAmount;
        }

        return calculateAmountFromPercentage(alteration.getPrice().getPercentage(), basePrice);
    }

    private BigDecimal calculateFinalTaxIncludedAmount(PriceAlteration alteration, Money basePrice) {
        BigDecimal directAmount = getAmount(alteration.getPrice().getTaxIncludedAmount());
        if (directAmount != null) {
            return directAmount;
        }

        return calculateAmountFromPercentage(alteration.getPrice().getPercentage(), basePrice);
    }

    private BigDecimal calculateAmountFromPercentage(Float percentage, Money basePrice) {
        if (percentage != null) {
            return calculateAmount(basePrice, percentage);
        }
        return BigDecimal.ZERO;
    }

    private record PeriodTotals(
            BigDecimal totalDutyFree,
            BigDecimal totalTaxIncluded,
            String dutyFreeUnit,
            String taxIncludedUnit,
            String priceType,
            String atType
    ) {
        boolean hasNonZeroTotals() {
            return !totalDutyFree.equals(BigDecimal.ZERO) || !totalTaxIncluded.equals(BigDecimal.ZERO);
        }
    }

    private Quantity normalizePeriod(Quantity period) {
        if (period == null) {
            return null;
        }
        return Quantity.builder()
                .amount(period.getAmount())
                .units(period.getUnits().toLowerCase())
                .build();
    }

    private PriceAlteration createPriceAlteration(
            BigDecimal totalDutyFree,
            BigDecimal totalTaxIncluded,
            String priceType,
            Quantity period,
            String atTypeAlteration,
            String dutyFreeUnit,
            String taxIncludedUnit) {
        return PriceAlteration.builder()
                .price(Price.builder()
                        .dutyFreeAmount(Money.builder()
                                .value(totalDutyFree.setScale(2, RoundingMode.HALF_UP).floatValue())
                                .unit(dutyFreeUnit)
                                .build())
                        .taxIncludedAmount(Money.builder()
                                .value(totalTaxIncluded.setScale(2, RoundingMode.HALF_UP).floatValue())
                                .unit(taxIncludedUnit)
                                .build())
                        .atType(PRICE_TYPE)
                        .build())
                .priceType(priceType)
                .atType(atTypeAlteration)
                .recurringChargePeriod(period)
                .build();
    }

    private boolean isPriceAlterationInvalid(OrderPrice itemPrice) {
        return Objects.isNull(itemPrice.getPriceAlteration()) || CollectionUtils.isEmpty(itemPrice.getPriceAlteration());
    }

    private BigDecimal getAmount(Money amount) {
        return amount != null && !Float.isNaN(amount.getValue()) ? BigDecimal.valueOf(amount.getValue()) : null;
    }

    private BigDecimal calculateAmount(Money amount, Float percentage) {
        BigDecimal value = getAmount(amount);
        if (value == null || isNullOrZero(value.floatValue())) {
            return BigDecimal.ZERO;
        }
        BigDecimal percentageValue = BigDecimal.valueOf(percentage);
        return value.multiply(percentageValue).divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
    }

    public boolean isNullOrZero(Float value) {
        return value == null || value == 0.0f;
    }

    private Price calculateTotalPrice(List<OrderPrice> itemPrices, List<InstallmentCharge> installmentCharges) {
        BigDecimal totalDutyFreeAmount = itemPrices.stream()
                .map(itemPrice -> getAmount(itemPrice.getPrice().getDutyFreeAmount()))
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        BigDecimal totalTaxIncludedAmount = itemPrices.stream()
                .map(itemPrice -> getAmount(itemPrice.getPrice().getTaxIncludedAmount()))
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDownPayment = calculateTotalDownPayment(installmentCharges);
        totalTaxIncludedAmount = totalTaxIncludedAmount.add(totalDownPayment);
        String dutyFreeAmountUnit = getUnit(itemPrices, true);
        String taxIncludedAmountUnit = getUnit(itemPrices, false);
        if (isBlank(taxIncludedAmountUnit)) {
            taxIncludedAmountUnit = getUnitIfInstallment(installmentCharges);
        }


        return Price.builder()
                .dutyFreeAmount(Money.builder()
                        .value(totalDutyFreeAmount.setScale(2, RoundingMode.HALF_UP).floatValue())
                        .unit(dutyFreeAmountUnit)
                        .build())
                .taxIncludedAmount(Money.builder()
                        .value(totalTaxIncludedAmount.setScale(2, RoundingMode.HALF_UP).floatValue())
                        .unit(taxIncludedAmountUnit)
                        .build())
                .atType(PRICE_TYPE)
                .build();
    }

    private BigDecimal calculateTotalDownPayment(List<InstallmentCharge> installmentCharges) {
        if (CollectionUtils.isEmpty(installmentCharges)) {
            return BigDecimal.ZERO;
        }

        return installmentCharges.stream()
                .filter(Objects::nonNull)
                .map(InstallmentCharge::getDownPayment)
                .filter(Objects::nonNull)
                .map(this::convertFloatToBigDecimal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal convertFloatToBigDecimal(Float value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(value);
    }

    private String getUnit(List<OrderPrice> itemPrices, boolean isDutyFree) {
        return itemPrices.stream()
                .map(itemPrice -> isDutyFree ? itemPrice.getPrice().getDutyFreeAmount().getUnit() : itemPrice.getPrice().getTaxIncludedAmount().getUnit())
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private String getUnitIfInstallment(List<InstallmentCharge> installmentCharges) {
        return installmentCharges.stream()
                .filter(Objects::nonNull)
                .map(InstallmentCharge::getPrice)
                .map(Money::getUnit)
                .findFirst()
                .orElse(null);
    }

    private boolean isBillCycleDateCheckEnabled(StateContext<String, String> context) {
        return settingsService.getSettings().isCheckAndSetBillCycleDateEnabled() &&
                isModificationOrTerminationOrMigrationUseCase(context);
    }

    private boolean isModificationOrTerminationOrMigrationUseCase(StateContext<String, String> context) {
        return StateMachineUtil.getBooleanValue(context, IS_MODIFICATION_OR_TERMINATION_OR_MIGRATION_USE_CASE, FALSE);
    }

    private Instant adjustOrderRequestedCompletionDate(StateContext<String, String> context, List<ProductOrderItem> productOrderItems) {
        ProductOffering productOffering = productOfferingService.getProductOfferingById(StateMachineUtil.getStringValue(context, PRODUCT_OFFERING_ID));

        if (!isPostpaidOrHybridBilling(productOffering)) {
            return null;
        }

        String configurationAction = StateMachineUtil.getStringValue(context, REQUESTED_CONFIGURATION_ACTION);
        BiFunction<StateContext<String, String>, List<ProductOrderItem>, Instant> handler = completionDateHandlers.get(configurationAction);
        if (handler == null) {
            return null;
        }
        return handler.apply(context, productOrderItems);
    }

    private Instant adjustMigrationCompletionDate(StateContext<String, String> context, List<ProductOrderItem> productOrderItems) {
        return adjustCompletionDateIfRequired(context, productOrderItems);
    }

    private boolean isPostpaidOrHybridBilling(ProductOffering productOffering) {
        return OrderCaptureConstants.POSTPAID.equalsIgnoreCase(productOffering.getBillingType()) ||
                OrderCaptureConstants.HYBRID.equalsIgnoreCase(productOffering.getBillingType());
    }

    private Instant adjustCompletionDateIfRequired(StateContext<String, String> context, List<ProductOrderItem> productOrderItems) {
        com.orange.discobole.processflow.dto.generated.RelatedParty relatedParty = getRelatedParty(context);
        if (requiresCompletionDateAdjustment(productOrderItems)) {
            return billingCycleService.getNextBillingDate(relatedParty.getId());
        }
        return null;
    }

    private Instant adjustModificationCompletionDate(StateContext<String, String> context, List<ProductOrderItem> productOrderItems) {
        return adjustCompletionDateIfRequired(context, productOrderItems);
    }

    private Instant adjustTerminationCompletionDate(StateContext<String, String> context) {
        com.orange.discobole.processflow.dto.generated.RelatedParty relatedParty = getRelatedParty(context);
        return billingCycleService.getNextBillingDate(relatedParty.getId());
    }

    private boolean requiresCompletionDateAdjustment(List<ProductOrderItem> orderItems) {
        return hasNonImmediatePaymentForActions(orderItems)
                || containsItemLinkedToBillingAccount(orderItems);
    }

    private boolean hasNonImmediatePaymentForActions(List<ProductOrderItem> orderItems) {
        if (CollectionUtils.isEmpty(orderItems)) {
            return false;
        }
        List<String> priceIds = orderItems.stream()
                .filter(Objects::nonNull)
                .filter(item -> MODIFY_MIGRATE_ACTIONS.contains(item.getAction()))
                .filter(item -> !CollectionUtils.isEmpty(item.getItemPrice()))
                .flatMap(item -> item.getItemPrice().stream()
                        .filter(Objects::nonNull)
                        .map(OrderPrice::getProductOfferingPrice)
                        .filter(Objects::nonNull)
                        .map(this::extractProductOfferingPriceId)
                        .filter(Objects::nonNull))
                .toList();

        if (priceIds.isEmpty()) {
            return false;
        }

        List<ProductOfferingPrice> offeringPrices = productOfferingPriceService.fetchProductOfferingPrices(priceIds);

        return offeringPrices.stream()
                .filter(Objects::nonNull)
                .filter(pop -> Objects.nonNull(pop.getImmediatePayment()))
                .anyMatch(pop -> !pop.getImmediatePayment());
    }

    private String extractProductOfferingPriceId(ProductOfferingPriceRefOrValue productOfferingPrice) {
        if (productOfferingPrice instanceof ProductOfferingPriceCharge priceCharge) {
            return priceCharge.getId();
        }
        return null;

    }

    private boolean containsItemLinkedToBillingAccount(List<ProductOrderItem> productOrderItems) {
        if (CollectionUtils.isEmpty(productOrderItems)) {
            return false;
        }
        Set<String> productIds = extractDeleteMigrateProductIds(productOrderItems);
        if (productIds.isEmpty()) {
            return false;
        }
        List<Product> products = productInventoryService.getProductByIds(new ArrayList<>(productIds));
        return products.stream()
                .anyMatch(product -> product.getBillingAccount() != null);
    }

    private Set<String> extractDeleteMigrateProductIds(List<ProductOrderItem> productOrderItems) {
        return productOrderItems.stream()
                .filter(this::isDeleteMigrateAction)
                .map(ProductOrderItem::getProduct)
                .filter(Objects::nonNull)
                .filter(com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product.class::isInstance)
                .map(com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product.class::cast)
                .map(com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private boolean isDeleteMigrateAction(ProductOrderItem item) {
        return item.getAction() != null && DELETE_MIGRATE_ACTIONS.contains(item.getAction());
    }

    private com.orange.discobole.processflow.dto.generated.RelatedParty getRelatedParty(StateContext<String, String> context) {
        return StateMachineUtil.getObjectValue(context, RELATED_PARTY, com.orange.discobole.processflow.dto.generated.RelatedParty.class);
    }
}