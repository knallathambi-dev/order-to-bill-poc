// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.service.impl;

import com.orange.discobole.orderorchestration.exception.model.CoodNonRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.CoodRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.CoodTechnicalException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.exception.model.validations.OrchestrationPlanNodeValidationException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.client.ProductManagementService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductRelationType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.enums.ProductOrderItemActionType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.enums.ProductSpecificationRelationshipType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.ProductUnExpectedStateException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.notfounds.CPIBNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.notfounds.ProductNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.notfounds.ProductSpecificationNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.validations.ProductValidationException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedProduct;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.service.VerifyNodeService;
import com.orange.discobole.productinventory.dto.v1.*;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode.PRODUCT_ORDER_ITEM_ACTION_INVALID;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.enums.ProductOrderItemActionType.*;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.enums.ProductSpecificationRelationshipType.RELIES_FROM;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.service.impl.DeliverSelectedNodesServiceImpl.STATUS_FIELD;

@Slf4j
@Component
public class VerifyNodeServiceImpl implements VerifyNodeService {

    private final ProductManagementService productManagementService;

    @Value("${config.enablePrerequisiteValidation}")
    private boolean enablePrerequisiteValidation;

    @Value("${config.enableOperationalStatusValidationForDelivery}")
    private boolean enableOperationalStatusValidationForDelivery;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public VerifyNodeServiceImpl(ProductManagementService productManagementService) {
        this.productManagementService = productManagementService;
    }

    @Override
    public void verifyOrchestrationPlanNodeDelivery(OrchestrationPlanNode orchestrationPlanNode, Product installedProduct) {
        log.info("orchestration plan node id {}", orchestrationPlanNode.getId());
        log.debug("orchestration plan node {} installed product {}", orchestrationPlanNode, installedProduct);

        verifyNodeAction(orchestrationPlanNode);
        verifyProductState(orchestrationPlanNode, installedProduct);
        verifyProductSpecificationPrerequisites(orchestrationPlanNode);

        if (!enablePrerequisiteValidation) {
            return;
        }

        List<String> prerequisiteIds = getProductPrerequisiteIds(installedProduct);
        List<String> dependentProductsIds = getProductDependentsIds(installedProduct, orchestrationPlanNode);
        List<String> prerequisiteAndDependentIds = Stream.concat(prerequisiteIds.stream(), dependentProductsIds.stream()).toList();

        if (!prerequisiteAndDependentIds.isEmpty()) {
            Map<String, Product> productIdToProductMap = getProductDTOStatuses(prerequisiteAndDependentIds);
            verifyProductStatusesCPIBResponse(productIdToProductMap);
            verifyPrerequisitesAreActive(productIdToProductMap, prerequisiteIds);
            verifyDependentForDeleteAction(productIdToProductMap, dependentProductsIds);
        }


        log.info("finished orchestration plan node id {} is valid result: {}", orchestrationPlanNode.getId(), true);
        log.debug("finished orchestration plan node {} installed product {} result {}", orchestrationPlanNode, installedProduct, true);
    }

    private void verifyNodeAction(OrchestrationPlanNode orchestrationPlanNode) {
        if (orchestrationPlanNode.isTangibleOrchestrationPlanNode() && !Objects.equals(ADD.getValue(), orchestrationPlanNode.getActualRelatedOrderItem().getAction())) {
            throw CoodRecoverableAndNonRetryableException.of(new OrchestrationPlanNodeValidationException(PRODUCT_ORDER_ITEM_ACTION_INVALID, orchestrationPlanNode.getActualRelatedOrderItem().getAction()));
        }

        if (orchestrationPlanNode.isCFSOrchestrationPlanNode() && !Set.of(ADD.getValue(), MODIFY.getValue(), DELETE.getValue(), MIGRATE.getValue()).contains(orchestrationPlanNode.getActualRelatedOrderItem().getAction())) {
            throw CoodRecoverableAndNonRetryableException.of(new OrchestrationPlanNodeValidationException(PRODUCT_ORDER_ITEM_ACTION_INVALID, orchestrationPlanNode.getActualRelatedOrderItem().getAction()));
        }
    }

    @Override
    public boolean isCommercialMigrationNode(OrchestrationPlanNode node) {
        if (!ProductOrderItemActionType.MIGRATE.equals(ProductOrderItemActionType.fromValue(node.getActualRelatedOrderItem().getAction()))) {
            return false;
        }

        Optional<RelatedProduct> migrateToProduct = node.getActualRelatedProductOptional();
        Optional<RelatedProduct> migrateFromProduct = node.getRelatedProduct().stream().filter(relatedProduct -> RelatedProductRelationType.MIGRATED_FROM.equals(relatedProduct.getRelationshipType())).findFirst();

        if (migrateToProduct.isEmpty() || migrateFromProduct.isEmpty()) {
            throw CoodNonRecoverableAndNonRetryableException.of(new OrchestrationPlanNodeValidationException(ExceptionCode.MIGRATION_RELATED_PRODUCT_IS_MISSING, node.getId()));
        }

        Set<Characteristic> migrateToCharacteristics = migrateToProduct.get().getProductCharacteristic();
        Set<Characteristic> migrateFromCharacteristics = migrateFromProduct.get().getProductCharacteristic();


        return Objects.equals(migrateToCharacteristics, migrateFromCharacteristics);
    }

    private void verifyProductSpecificationPrerequisites(OrchestrationPlanNode orchestrationPlanNode) {
        //if the maintain step failed to set product id, the verify will failed to assert non null of product id
        log.info("VerifyNodeDeliveryServiceImpl | verifyProductSpecificationPrerequisites | orchestration plan node id {}", orchestrationPlanNode.getId());
        log.debug("VerifyNodeDeliveryServiceImpl | verifyProductSpecificationPrerequisites | orchestration plan node {}", orchestrationPlanNode);

        for (RelatedProduct relatedProduct : orchestrationPlanNode.getRelatedProduct()) {
            // to do : to be tested when is installable is false in product order dto
            boolean isRelatedProductInstallableAndIdNull = Objects.isNull(relatedProduct.getId()) && relatedProduct.getIsInstallable();
            if (isRelatedProductInstallableAndIdNull && Objects.isNull(relatedProduct.getProductSpecification())) {
                log.info("VerifyNodeDeliveryServiceImpl | verifyProductSpecificationPrerequisites | relatedProduct product specification {}", relatedProduct.getProductSpecification());
                throw new CoodRecoverableAndNonRetryableException(new ProductSpecificationNotFoundException(ExceptionCode.PRODUCT_SPECIFICATION_NOT_ADDED_FOR_RELATED_PRODUCT, relatedProduct.getIsInstallable(), relatedProduct.getRelationshipType()));
            } else if (isRelatedProductInstallableAndIdNull) {
                log.info("VerifyNodeDeliveryServiceImpl | verifyProductSpecificationPrerequisites | relatedProduct {}", relatedProduct);
                throw new CoodRecoverableAndNonRetryableException(new ProductValidationException(ExceptionCode.PRODUCT_SPECIFICATION_PREREQUISITES_EXCEPTION, relatedProduct.getProductSpecification().getId()));
            }
        }
    }

    private void verifyProductState(OrchestrationPlanNode orchestrationPlanNode, Product productDTO) {
        log.info("VerifyNodeDeliveryServiceImpl | verifyProductState | orchestration plan node id {}", orchestrationPlanNode.getId());
        log.debug("VerifyNodeDeliveryServiceImpl | verifyProductState | orchestration plan node {}", orchestrationPlanNode);
        if (Objects.nonNull(orchestrationPlanNode.getRelatedProductOrderItem()) && Objects.nonNull(orchestrationPlanNode.getActualRelatedOrderItem().getAction())
                && Objects.nonNull(productDTO.getStatus())) {
            switch (ProductOrderItemActionType.fromValue(orchestrationPlanNode.getActualRelatedOrderItem().getAction())) {
                case MIGRATE -> verifyProductsStatusForMigrationAction(orchestrationPlanNode, productDTO);
                case ADD ->
                        validateProductStatus(productDTO, ProductStatusType.CREATED, ProductOperationalStatusType.PENDINGACTIVE, ADD);
                case MODIFY ->
                        validateProductStatus(productDTO, ProductStatusType.ACTIVE, ProductOperationalStatusType.PENDINGMODIFICATION, MODIFY);
                case DELETE ->
                        validateProductStatus(productDTO, ProductStatusType.ACTIVE, ProductOperationalStatusType.PENDINGTERMINATE, DELETE);
                default ->
                        log.error("Unsupported related product order item action [{}] for product id [{}]", orchestrationPlanNode.getActualRelatedOrderItem().getAction(), productDTO.getId());

            }
        }
    }

    private void verifyProductsStatusForMigrationAction(OrchestrationPlanNode node, Product migrateToProduct) {
        validateProductStatus(migrateToProduct, ProductStatusType.CREATED, ProductOperationalStatusType.CONFIRMED, MIGRATE);

        RelatedProduct migrateFromRelatedProduct = node.getRelatedProduct().stream().filter(relatedProduct -> RelatedProductRelationType.MIGRATED_FROM.equals(relatedProduct.getRelationshipType())).findFirst()
                .orElseThrow(() -> CoodNonRecoverableAndNonRetryableException.of(new OrchestrationPlanNodeValidationException(ExceptionCode.MIGRATION_RELATED_PRODUCT_IS_MISSING, node.getId())));

        List<Product> retrievedProducts = productManagementService.getProductsByOrderIdAndItemIds(List.of(migrateFromRelatedProduct.getProductOrderItemId()), node.getRelatedProductOrder().getId());
        if (CollectionUtils.isEmpty(retrievedProducts)) {
            throw CoodRecoverableAndNonRetryableException.of(new ProductNotFoundException(ExceptionCode.INSTALLED_PRODUCT_NOT_FOUND_EXCEPTION, node.getRelatedProductOrder().getId(), migrateFromRelatedProduct.getProductOrderItemId(), MIGRATE.getValue()));
        }

        Product migrateFromProduct = retrievedProducts.get(0);
        validateProductStatus(migrateFromProduct, ProductStatusType.ACTIVE, ProductOperationalStatusType.PENDINGMIGRATE, MIGRATE);
    }


    private static List<String> getProductPrerequisiteIds(Product resultCPIB) {
        if (Objects.isNull(resultCPIB.getProductRelationship()) || resultCPIB.getProductRelationship().isEmpty()) {
            return List.of();
        }
        return resultCPIB.getProductRelationship().stream()
                .filter(productRelationship -> productRelationship.getRelationshipType().equals(ProductSpecificationRelationshipType.RELIES_ON.getValue()))
                .map(ProductRelationship::getProduct)
                .map(productRefOrValue -> (ProductRef) productRefOrValue)
                .map(ProductRef::getId)
                .toList();
    }

    private static boolean isOrchestrationPlanNodeActionDelete(OrchestrationPlanNode opn) {
        String orchestrationPlanNodeAction = opn.getActualRelatedOrderItem().getAction();
        return orchestrationPlanNodeAction.equals(DELETE.getValue());
    }

    private List<String> getProductDependentsIds(Product productDtoFromCPIB, OrchestrationPlanNode orchestrationPlanNode) {
        if (!isOrchestrationPlanNodeActionDelete(orchestrationPlanNode) || Objects.isNull(productDtoFromCPIB.getProductRelationship())) {
            return List.of();
        }

        return productDtoFromCPIB.getProductRelationship()
                .stream()
                .filter(productRelationship -> productRelationship.getRelationshipType().equals(RELIES_FROM.getValue()))
                .map(ProductRelationship::getProduct)
                .map(productRefOrValue -> (ProductRef) productRefOrValue)
                .map(ProductRef::getId)
                .toList();
    }

    private void validateProductStatus(Product product, ProductStatusType mainStatus, ProductOperationalStatusType operationalStatus, ProductOrderItemActionType action) {
        if (!mainStatus.equals(product.getStatus())) {
            throw CoodRecoverableAndNonRetryableException.of(new ProductUnExpectedStateException(ExceptionCode.PRODUCT_UNEXPECTED_STATE_EXCEPTION, action.getValue(), product.getId(), product.getStatus().getValue(), mainStatus.getValue()));
        }

        if (enableOperationalStatusValidationForDelivery && !operationalStatus.equals(product.getOperationalStatus())) {
            throw CoodRecoverableAndNonRetryableException.of(new ProductUnExpectedStateException(ExceptionCode.PRODUCT_UNEXPECTED_OPERATIONAL_STATE_EXCEPTION, action.getValue(), product.getId(), product.getOperationalStatus().getValue(), operationalStatus.getValue()));
        }
    }

    private Map<String, Product> getProductDTOStatuses(List<String> productDTOIds) {
        List<Product> productDTOS = productManagementService.getProductsByFields(productDTOIds, List.of(STATUS_FIELD));
        if (Objects.isNull(productDTOS)) {
            return Map.of();
        }
        try {
            return productDTOS
                    .stream()
                    .collect(Collectors.toMap(Product::getId, Function.identity()));
        } catch (IllegalStateException exception) {
            throw new CoodNonRecoverableAndNonRetryableException(new CoodTechnicalException(ExceptionCode.COOD_TECHNICAL_EXCEPTION, "Duplication key error product id map, product ids : %s.".formatted(productDTOIds)));
        }
    }

    private void verifyProductStatusesCPIBResponse(Map<String, Product> productIdToProductMap) {
        if (productIdToProductMap.isEmpty()) {
            log.info("VerifyNodeDeliveryServiceImpl | verifyProductStatusesCPIBResponse | productIdToProductMap {}", productIdToProductMap);
            throw new CoodRecoverableAndNonRetryableException(new CPIBNotFoundException(ExceptionCode.CBIP_NOT_FOUND_EXCEPTION, "CPIB_NOT_REACHABLE"));
        }
    }

    private void verifyPrerequisitesAreActive(Map<String, Product> productIdToProductMap, List<String> productRelationshipIds) {
        if (Objects.isNull(productRelationshipIds) || productRelationshipIds.isEmpty()) {
            return;
        }

        for (String productRelationshipId : productRelationshipIds) {
            if (isProductStatusNotIn(List.of(ProductStatusType.ACTIVE, ProductStatusType.SOLD), productIdToProductMap.get(productRelationshipId))) {
                log.info("VerifyNodeDeliveryServiceImpl | verifyPrerequisitesAreActive | productIdToProductMap productRelationshipId {}", productIdToProductMap.get(productRelationshipId));
                throw new CoodRecoverableAndNonRetryableException(new ProductUnExpectedStateException(ExceptionCode.PRODUCT_PREREQUISITE_VALIDATION_EXCEPTION, productIdToProductMap.get(productRelationshipId).getId(), productIdToProductMap.get(productRelationshipId).getStatus().toString()));
            }
        }
    }

    private static boolean isProductStatusNotIn(List<ProductStatusType> productStatusTypes, Product productDTO) {
        return !productStatusTypes.contains(productDTO.getStatus());
    }

    private void verifyDependentForDeleteAction(Map<String, Product> productIdToProductMap, List<String> dependentProductsIds) {
        for (String dependentId : dependentProductsIds) {
            if (isProductStatusNotIn(List.of(ProductStatusType.TERMINATED, ProductStatusType.CANCELLED, ProductStatusType.ABORTED), productIdToProductMap.get(dependentId))) {
                log.info("VerifyNodeDeliveryServiceImpl | verifyDependentForDeleteAction | productIdToProductMap dependentId is {}", productIdToProductMap.get(dependentId));
                throw new CoodRecoverableAndNonRetryableException(new ProductUnExpectedStateException(ExceptionCode.DELETION_DEPENDENT_PRODUCT_STATE_INVALID, productIdToProductMap.get(dependentId).getId(), productIdToProductMap.get(dependentId).getStatus().toString()));
            }
        }
    }
}
