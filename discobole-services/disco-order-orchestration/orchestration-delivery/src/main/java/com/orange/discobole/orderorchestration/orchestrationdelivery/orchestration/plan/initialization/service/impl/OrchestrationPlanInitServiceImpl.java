// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import com.orange.discobole.orderorchestration.exception.model.*;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductRelationType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.specification.ProductSpecificationCharacteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.DuplicateOrchestrationPlanException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.mappings.ProductOrderItemCharacteristicsMappingException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.mappings.ProductOrderItemMappingException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.notfounds.ProductSpecificationNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.validations.ProductOrderItemValidationException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.validations.ProductOrderValidationException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.mapper.CharacteristicMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.mapper.OrchestrationPlanMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.mapper.ServiceSpecificationMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.service.OrchestrationPlanInitService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.ProductCatalogCustomMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.util.ValidationUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode.*;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductType.SHIPMENT_PRODUCT;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.enums.ProductSpecificationBaseType.SHIPPING_PRODUCT_SPECIFICATION;

@Slf4j
@Service
@SuppressFBWarnings("EI_EXPOSE_REP2")
@RequiredArgsConstructor
public class OrchestrationPlanInitServiceImpl implements OrchestrationPlanInitService {
    private final OrchestrationPlanMapper productOrderMapper;
    private final ServiceSpecificationMapper serviceSpecificationMapper = Mappers.getMapper(ServiceSpecificationMapper.class);
    private final OrchestrationPlanRepository orchestrationPlanRepository;
    private final ProductCatalogCustomMapper productCatalogCustomMapper;
    private final CharacteristicMapper characteristicMapper;


    @Override
    public OrchestrationPlan createOrchestrationPlan(ProductOrder productOrder) throws CoodMappingException, CoodDBException {
        log.info("OrchestrationPlanServiceImpl | createOrchestrationPlan | Create Orchestration plan from product order {}", productOrder);
        Optional<OrchestrationPlan> orchestrationPlanOptional = orchestrationPlanRepository.findOrchestrationPlanByRelatedProductOrder_Id(productOrder.getId());
        if (orchestrationPlanOptional.isEmpty()) {
            try {
                return productOrderMapper.toOrchestrationPlan(productOrder);
            } catch (Exception e) {
                log.error("OrchestrationPlanServiceImpl | createOrchestrationPlan | Failed to create Orchestration plan from product order {} with exception {}", productOrder, e.toString());
                throw new CoodRecoverableAndNonRetryableException(new CoodMappingException(PRODUCT_ORDER_MAPPING_EXCEPTION, ProductOrder.class, OrchestrationPlan.class, e));
            }
        } else if (State.INITIALIZED.equals(orchestrationPlanOptional.get().getState())) {
            return orchestrationPlanOptional.get();
        } else {
            throw new CoodNonRecoverableAndNonRetryableException(new DuplicateOrchestrationPlanException(ORCHESTRATION_PLAN_DUPLICATION, orchestrationPlanOptional.get().getId()));
        }
    }

    @Override
    public List<ProductOrderItem> filterOrderItems(ProductOrder productOrder) {
        log.info("OrchestrationPlanServiceImpl | filterOrderItems | Filter product order {}", productOrder);
        List<ProductOrderItem> filteredItemList = new ArrayList<>();
        productOrder.getProductOrderItem().forEach(productOrderItem -> {
            ProductRefOrValue product = productOrderItem.getProduct();
            boolean isValidProduct = product instanceof Product && ValidationUtil.isValid(product);
            boolean productHasSpecifications = product instanceof Product p && Objects.nonNull((p).getProductSpecification());
            boolean productActionIsNotNoAction = !ItemActionType.NOCHANGE.equals(productOrderItem.getAction());

            if (isValidProduct && productHasSpecifications && productActionIsNotNoAction) {
                filteredItemList.add(productOrderItem);
            }
        });
        log.info("OrchestrationPlanServiceImpl | filterOrderItems | Filter product order {} result {}", productOrder, filteredItemList);
        productOrder.setProductOrderItem(filteredItemList);
        return filteredItemList;
    }

    @Override
    public List<OrchestrationPlanNode> buildTangibleOrchestrationNodes(List<ProductOrderItem> productOrderItems, String productOrderId) throws CoodMappingException, CoodDBException {
        log.info("Initialize Orchestration plan nodes for product order id {}", productOrderId);
        List<OrchestrationPlanNode> tangibleNodes = new ArrayList<>();
        if (CollectionUtils.isEmpty(productOrderItems)) {
            log.error("Product order with ID {}" +
                    " does not have product order items with IDs", productOrderId);
            throw new CoodRecoverableAndNonRetryableException(new ProductOrderItemValidationException(PRODUCT_ORDER_INVALID_EVENT_NO_ITEMS, productOrderId));
        }
        productOrderItems.stream().filter(productOrderItem -> (SHIPPING_PRODUCT_SPECIFICATION.getValue().equals(((Product) productOrderItem.getProduct()).getProductSpecification().getAtBaseType())))
                .forEach(shipingProductOrderItem -> {
                    List<OrchestrationPlanNode> nodes = new ArrayList<>();
                    List<ProductOrderItem> tangProductOrderItems = getTangibleProductsOrderItems(shipingProductOrderItem, productOrderItems);
                    tangProductOrderItems.forEach(tangProductOrderItem -> nodes.add(createTangibleNode(productOrderId, shipingProductOrderItem, tangProductOrderItem)));
                    tangibleNodes.addAll(nodes);
                });

        return tangibleNodes;
    }

    @Override
    public List<ProductOrderItem> fetchShippingAndTangibleOrderItems(List<ProductOrderItem> productOrderItems) {

        List<ProductOrderItem> visitedTangibleOrderItems = new ArrayList<>();
        productOrderItems.stream().filter(productOrderItem -> (SHIPPING_PRODUCT_SPECIFICATION.getValue().equals(((Product) productOrderItem.getProduct()).getProductSpecification().getAtBaseType())))
                .forEach(shipingProductOrderItem -> {
                    List<ProductOrderItem> tangProductOrderItems = getTangibleProductsOrderItems(shipingProductOrderItem, productOrderItems);
                    visitedTangibleOrderItems.add(shipingProductOrderItem);
                    visitedTangibleOrderItems.addAll(tangProductOrderItems);
                });
        return visitedTangibleOrderItems;
    }

    @Override
    public List<OrchestrationPlanNode> buildDefaultCFSOrchestrationNodes(List<ProductOrderItem> cfsProductOrderItems, String productOrderId) throws CoodMappingException, CoodDBException {
        log.info("Initialize Orchestration plan nodes for product order {} and Cfs products {}", productOrderId, cfsProductOrderItems);
        List<OrchestrationPlanNode> nodeList = new ArrayList<>();
        for (ProductOrderItem productOrderItem : cfsProductOrderItems) {
            OrchestrationPlanNode node;
            try {
                node = productOrderMapper.toInitializedOrchestrationPlanNode(productOrderItem);
                addRelatedProductOrderItem(node, productOrderItem);
            } catch (Exception e) {
                throw new CoodNonRecoverableAndNonRetryableException(new CoodMappingException(PRODUCT_ORDER_ITEM_MAPPING_EXCEPTION, ProductOrderItem.class, OrchestrationPlanNode.class, e));
            }
            node.setRelatedProductOrder(RelatedProductOrder.builder().id(productOrderId).build());
            nodeList.add(node);

        }

        //IPCEISCOOD-60: separation - will need to maintain bi-directional relationship
        log.info("Finished Initializing Orchestration plan nodes");
        return nodeList;
    }

    /**
     * initialize related product with relationship delivers
     *
     * @param orchestrationPlanNodes
     * @param orderItemDTOS           initialized orchestration plan
     * @param productSpecificationMap Product Specifications [result from product catalog] mapped by id
     */

    @Override
    public void initRelatedProductWithDeliversType(String orderId, Set<OrchestrationPlanNode> orchestrationPlanNodes, List<ProductOrderItem> orderItemDTOS, Map<String, com.orange.discobole.orderorchestration.orchestrationdelivery.dto.common.ProductSpecification> productSpecificationMap) {
        log.info("OrchestrationPlanNodes {}", orchestrationPlanNodes);

        orderItemDTOS.stream()
                .filter(orderItemDto -> {
                    ProductRefOrValue product = orderItemDto.getProduct();
                    return product instanceof Product &&
                            !SHIPPING_PRODUCT_SPECIFICATION.getValue().equals(((Product) product).getProductSpecification().getAtBaseType());
                })
                .forEach(orderItemDto -> {
                    String productSpecId = Optional.ofNullable(orderItemDto.getProduct())
                            .filter(Product.class::isInstance)
                            .map(product -> ((Product) product).getProductSpecification().getId())
                            .orElseThrow(() -> new CoodRecoverableAndNonRetryableException(
                                    new CoodTechnicalException(PRODUCT_ORDER_INVALID_EVENT_NO_PRODUCT_SPEC, orderItemDto.getId())));

                    com.orange.discobole.orderorchestration.orchestrationdelivery.dto.common.ProductSpecification productSpecification = Optional.of(productSpecificationMap.get(productSpecId))
                            .orElseThrow(() -> new CoodRecoverableAndNonRetryableException(
                                    new ProductSpecificationNotFoundException(COULD_NOT_GET_PRODUCT_SPECIFICATION, productSpecId, orderId)));

                    OrchestrationPlanNode orchestrationPlanNode = orchestrationPlanNodes.stream()
                            .filter(node -> node.getRelatedProductOrderItem().stream()
                                    .anyMatch(orderItem -> orderItem.getId().equals(orderItemDto.getId())))
                            .findFirst()
                            .orElseThrow(() -> new CoodTechnicalException(ORCHESTRATION_PLAN_WITH_PRODUCT_ORDER_NOT_FOUND, orderItemDto.getId()));

                    Set<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic> relatedProductCharacteristicSet = getProductOrderItemCharacteristics(orderItemDto, orderId);

                    List<ProductSpecificationCharacteristic> productSpecificationCharacteristic = productSpecification.getProductSpecificationCharacteristic();
                    List<ProductSpecificationCharacteristic> filteredCatalogCharacteristics = filterCatalogCharacteristicsNotPresentInOM(relatedProductCharacteristicSet, productSpecificationCharacteristic);
                    if (Objects.nonNull(filteredCatalogCharacteristics)) {
                        try {
                            relatedProductCharacteristicSet.addAll(Objects.requireNonNull(productCatalogCustomMapper.toProductCharacteristic(filteredCatalogCharacteristics)));
                        } catch (JsonProcessingException e) {
                            log.error("Error while mapping catalog characteristic value to product characteristic object", e);
                            throw new CoodTechnicalException(ExceptionCode.COOD_TECHNICAL_EXCEPTION, "Error while mapping catalog characteristic value to product characteristic object");
                        }
                    }

                    if (orderItemDto.getAction().equals(ItemActionType.MIGRATE) &&
                            !CollectionUtils.isEmpty(orderItemDto.getProductOrderItemRelationship()) &&
                            orderItemDto.getProductOrderItemRelationship().stream().anyMatch(orderItemRelationship -> Objects.nonNull(orderItemRelationship.getRelationshipType())
                                    && orderItemRelationship.getRelationshipType().equals(RelationshipType.MIGRATETO))) {

                        addCharacteristicsToMigratedFromRelatedProduct(orchestrationPlanNode, relatedProductCharacteristicSet);

                    } else {
                        RelatedProduct relatedProduct = getRelatedProduct(orderItemDto, relatedProductCharacteristicSet, orchestrationPlanNode, productSpecification);
                        orchestrationPlanNode.addRelatedProduct(relatedProduct);
                    }
                });
        log.info("Initiation of related product for nodes {} is Finished", orchestrationPlanNodes);
    }

    private RelatedProduct getRelatedProduct(ProductOrderItem orderItemDto, Set<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic> relatedProductCharacteristicSet, OrchestrationPlanNode orchestrationPlanNode, com.orange.discobole.orderorchestration.orchestrationdelivery.dto.common.ProductSpecification productSpecification) {
        RelatedProduct relatedProduct = RelatedProduct.builder()
                .relationshipType(RelatedProductRelationType.DELIVERS)
                .productCharacteristic(relatedProductCharacteristicSet)
                .isInstallable(orderItemDto.getIsInstallable())
                .productOrderItemId(orderItemDto.getId())
                .build();

        boolean isPhysicalProduct = Objects.nonNull(orchestrationPlanNode.getRelatedProduct()) && orchestrationPlanNode.getRelatedProduct().stream().anyMatch(rp -> SHIPMENT_PRODUCT.equals(rp.getType()));
        if (isPhysicalProduct) {
            relatedProduct.setProductSpecification(getProductSpecWithTangible(productSpecification));
            relatedProduct.setType(RelatedProductType.PHYSICAL_PRODUCT);
        } else {
            relatedProduct.setProductSpecification(getProductSpecWithServiceSpecification(productSpecification));
            relatedProduct.setType(RelatedProductType.CFS);
            setRelatedServiceOrderIfExist(productSpecification, orchestrationPlanNode);
        }
        return relatedProduct;
    }

    private List<ProductSpecificationCharacteristic> filterCatalogCharacteristicsNotPresentInOM(Set<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic> relatedProductCharacteristicSet, List<ProductSpecificationCharacteristic> productSpecificationCharacteristic) {
        if (Objects.isNull(productSpecificationCharacteristic)) {
            return List.of();
        }
        List<String> omCharacteristicsNames = relatedProductCharacteristicSet.stream().map(com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic::getName).toList();
        return productSpecificationCharacteristic.stream().filter(productSpecificationCharacteristic1 -> !omCharacteristicsNames.contains(productSpecificationCharacteristic1.getName())).toList();
    }

    private void addCharacteristicsToMigratedFromRelatedProduct(OrchestrationPlanNode orchestrationPlanNode, Set<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic> relatedProductCharacteristicSet) {
        RelatedProduct migratedFromRelatedProduct = orchestrationPlanNode.getRelatedProduct().stream()
                .filter(relatedProduct -> relatedProduct.getRelationshipType().equals(RelatedProductRelationType.MIGRATED_FROM))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No matching ProductOrderItem found"));
        migratedFromRelatedProduct.setProductCharacteristic(relatedProductCharacteristicSet);
    }

    @Override
    public List<OrchestrationPlanNode> buildMigrationOrchestrationNodes(List<ProductOrderItem> productOrderItems, String productOrderId) {

        List<OrchestrationPlanNode> nodesWithMigrateAction = new ArrayList<>();
        List<ProductOrderItem> orderItemsToBeMigrated = filterOrderItemsToBeMigrated(productOrderItems);
        for (ProductOrderItem productOrderItem : orderItemsToBeMigrated) {
            OrchestrationPlanNode node;
            ProductOrderItem migratedFromOrderItem = productOrderItems.stream().filter(orderItem -> productOrderItem.getProductOrderItemRelationship()
                            .stream()
                            .anyMatch(orderItemRelationship -> orderItem.getId().equals(orderItemRelationship.getId()) && orderItemRelationship.getRelationshipType().equals(RelationshipType.MIGRATETO)))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("No matching ProductOrderItem found"));
            try {
                node = productOrderMapper.toInitializedOrchestrationPlanNode(migratedFromOrderItem);
                addRelatedProductOrderItem(node, migratedFromOrderItem);
                addRelatedProductOrderItem(node, productOrderItem);
                RelatedProduct migratedFromRelatedProduct = buildMigratedFromRelatedProduct(productOrderItem);
                node.addRelatedProduct(migratedFromRelatedProduct);
            } catch (Exception e) {
                throw new CoodNonRecoverableAndNonRetryableException(new CoodMappingException(PRODUCT_ORDER_ITEM_MAPPING_EXCEPTION, ProductOrderItem.class, OrchestrationPlanNode.class, e));
            }
            node.setRelatedProductOrder(RelatedProductOrder.builder().id(productOrderId).build());
            nodesWithMigrateAction.add(node);
        }

        return nodesWithMigrateAction;
    }

    private List<ProductOrderItem> filterOrderItemsToBeMigrated(List<ProductOrderItem> orderItemSet) {
        return orderItemSet.stream()
                .filter(orderItem -> orderItem.getAction().equals(ItemActionType.MIGRATE)
                        && Objects.nonNull(orderItem.getProductOrderItemRelationship())
                        && !orderItem.getProductOrderItemRelationship().isEmpty()
                        && orderItem.getProductOrderItemRelationship().stream()
                        .anyMatch(orderItemRelationship -> Objects.nonNull(orderItemRelationship.getRelationshipType())
                                && orderItemRelationship.getRelationshipType().equals(RelationshipType.MIGRATETO)))
                .collect(Collectors.toList());
    }

    public Set<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic> getProductOrderItemCharacteristics(ProductOrderItem productOrderItem, String orderId) {
        ProductRefOrValue productRefOrValue = productOrderItem.getProduct();

        if (!(productRefOrValue instanceof Product product)) {
            return Set.of();
        }

        try {
            return characteristicMapper.from(product.getProductCharacteristic());
        } catch (Exception e) {
            throw new CoodRecoverableAndNonRetryableException(
                    new ProductOrderItemCharacteristicsMappingException(
                            PRODUCT_ORDER_ITEM_MAPPING_ERROR,
                            Characteristic.class,
                            com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic.class,
                            orderId,
                            productOrderItem.getId()
                    )
            );
        }
    }

    @Override
    public OrchestrationPlan getOrchestrationPlanByRelatedProductOrderId(String relatedProductOrderId) {
        return orchestrationPlanRepository.findOrchestrationPlanByRelatedProductOrder_Id(relatedProductOrderId).orElseThrow(() -> new NoSuchElementException("No orchestration plan found for orderId: " + relatedProductOrderId));
    }

    private ProductSpecification getProductSpecWithTangible(com.orange.discobole.orderorchestration.orchestrationdelivery.dto.common.ProductSpecification productSpecification) {
        return ProductSpecification.builder()
                .id(productSpecification.getId())
                .name(productSpecification.getName())
                .build();
    }

    private ProductSpecification getProductSpecWithServiceSpecification(
            com.orange.discobole.orderorchestration.orchestrationdelivery.dto.common.ProductSpecification productSpecification) {
        //TODO use async call or retrieve in one call
        List<ServiceSpecification> serviceSpecifications = retrieveServiceSpecificationIfExist(productSpecification);
        return ProductSpecification.builder()
                .id(productSpecification.getId())
                .name(productSpecification.getName())
                .serviceSpecification(serviceSpecifications)
                .build();
    }

    private List<ServiceSpecification> retrieveServiceSpecificationIfExist(
            com.orange.discobole.orderorchestration.orchestrationdelivery.dto.common.ProductSpecification productSpecification) {
        if ((!productSpecification.getServiceSpecification().isEmpty())) {
            return serviceSpecificationMapper.toServiceSpecification(productSpecification.getServiceSpecification());
        }
        return List.of();
    }

    private OrchestrationPlanNode createTangibleNode(String productOrderId, ProductOrderItem productOrderItem, ProductOrderItem tangProductOrderItem) {
        OrchestrationPlanNode node;
        try {
            node = productOrderMapper.toInitializedOrchestrationPlanNode(productOrderItem);
        } catch (Exception e) {
            throw new CoodRecoverableAndNonRetryableException(new ProductOrderItemMappingException(PRODUCT_ORDER_INVALID_EVENT, ProductOrderItem.class, OrchestrationPlanNode.class, productOrderItem.getId(), e));
        }
        node.setRelatedProductOrder(RelatedProductOrder.builder().id(productOrderId).build());
        addRelatedProductOrderItem(node, productOrderItem);
        addRelatedProductOrderItem(node, tangProductOrderItem);
        addRelatedProduct(node, productOrderItem, SHIPMENT_PRODUCT, RelatedProductRelationType.DELIVER_WITH);
        return node;
    }

    private void addRelatedProduct(OrchestrationPlanNode node, ProductOrderItem productOrderItem, RelatedProductType relatedProductType, RelatedProductRelationType relatedProductRelationType) {
        ProductRefOrValue productRefOrValue = productOrderItem.getProduct();

        if (productRefOrValue instanceof Product product) {
            RelatedProduct relatedProduct = RelatedProduct.builder()
                    .type(relatedProductType)
                    .relationshipType(relatedProductRelationType)
                    .productCharacteristic(characteristicMapper.from(product.getProductCharacteristic()))
                    .productOrderItemId(productOrderItem.getId())
                    .isInstallable(productOrderItem.getIsInstallable())
                    .build();

            node.addRelatedProduct(relatedProduct);
        }
    }

    private void addRelatedProductOrderItem(OrchestrationPlanNode node, ProductOrderItem productOrderItem) {
        node.addRelatedProductOrderItem(new RelatedProductOrderItem(productOrderItem.getId(), productOrderItem.getAction().toString(), productOrderItem.getQuantity()));
    }

    private RelatedProduct buildMigratedFromRelatedProduct(ProductOrderItem productOrderItem) {
        return RelatedProduct.builder()
                .relationshipType(RelatedProductRelationType.MIGRATED_FROM)
                .productOrderItemId(productOrderItem.getId())
                .build();
    }

    public List<ProductOrderItem> getTangibleProductsOrderItems(ProductOrderItem shippingOrderItem, List<ProductOrderItem> filteredList) {
        log.info("OrchestrationPlanServiceImpl | getTangibleProductsOrderItems | shippingOrderItem: {} filteredList: {}", shippingOrderItem, filteredList);
        List<String> tangProductOrderItemIds = shippingOrderItem.getProductOrderItemRelationship().stream()
                .filter(orderItemRelationship -> orderItemRelationship.getRelationshipType().equals(RelationshipType.REQUIRES))
                .map(OrderItemRelationship::getId)
                .toList();
        if (tangProductOrderItemIds.isEmpty()) {
            throw new ProductOrderValidationException(CAN_NOT_DELIVERY_EMPTY_SHIPMENT, shippingOrderItem.getId());
        }

        List<ProductOrderItem> matchingItems = filteredList.stream()
                .filter(item -> tangProductOrderItemIds.contains(item.getId()))
                .toList();
        if (matchingItems.isEmpty()) {
            throw new ProductOrderValidationException(COOD_TECHNICAL_EXCEPTION, "Error getting tangible product for shipment product");
        }
        log.info("OrchestrationPlanServiceImpl | getTangibleProductsOrderItems | shippingOrderItem: {} filteredList: {} result: {}", shippingOrderItem, filteredList, matchingItems);
        return matchingItems;
    }

    private void setRelatedServiceOrderIfExist(com.orange.discobole.orderorchestration.orchestrationdelivery.dto.common.ProductSpecification productSpecification, OrchestrationPlanNode orchestrationPlanNode) {
        if (Objects.nonNull(productSpecification.getRelatedResource()) && !productSpecification.getRelatedResource().isEmpty() && Objects.nonNull(productSpecification.getRelatedResource().get(0).getHref())) {
            RelatedServiceOrder relatedServiceOrder = RelatedServiceOrder.builder()
                    .somRef(productSpecification.getRelatedResource().get(0).getHref()).build();
            orchestrationPlanNode.setRelatedServiceOrder(relatedServiceOrder);
        }
    }

}
