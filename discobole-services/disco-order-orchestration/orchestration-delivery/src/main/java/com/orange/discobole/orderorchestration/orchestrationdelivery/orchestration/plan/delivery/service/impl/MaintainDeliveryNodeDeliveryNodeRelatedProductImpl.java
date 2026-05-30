// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.service.impl;

import com.orange.discobole.orderorchestration.exception.model.CoodRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.client.ProductManagementService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductRelationType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.notfounds.ProductNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.notfounds.RelatedProductNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.ProductSpecification;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RealisingService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedProduct;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.service.MaintainDeliveryNodeRelatedProduct;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationPlanService;
import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.dto.v1.ProductRef;
import com.orange.discobole.productinventory.dto.v1.ProductRelationship;
import com.orange.discobole.productinventory.dto.v1.ServiceRef;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.enums.ProductOrderItemRelationshipType.RELIES_ON;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.enums.ProductSpecificationRelationshipType.RELIES_FROM;

@Component
@Slf4j
@Setter
@SuppressFBWarnings("EI_EXPOSE_REP2")
@RequiredArgsConstructor
public class MaintainDeliveryNodeDeliveryNodeRelatedProductImpl implements MaintainDeliveryNodeRelatedProduct {

    public static final String PRODUCT_SPECIFICATION_ID = "productSpecification.id";

    public static final String FOR_ORCHESTRATION_PLAN_NODE_LOG_MESSAGE = "for orchestration plan node {} ";

    private final OrchestrationPlanService orchestrationPlanService;

    private final ProductManagementService productManagementService;

    private static void maintainRelatedProductWithRelationshipDelivers(OrchestrationPlanNode orchestrationPlanNode, Product productDTOFromCPIB) {
        log.info("MaintainDeliveryNodeDeliveryNodeRelatedProductImpl | maintainRelatedProductWithRelationshipDelivers | starting orchestration plan node id {} with product from CPIB {}", orchestrationPlanNode.getId(), productDTOFromCPIB);
        Optional<RelatedProduct> relatedProduct = Optional.ofNullable(orchestrationPlanNode.getRelatedProduct())
                .orElseThrow(() -> new CoodRecoverableAndNonRetryableException(new RelatedProductNotFoundException(ExceptionCode.NODE_RELATED_PRODUCT_NOT_FOUND, orchestrationPlanNode.getId())))
                .stream()
                .filter(p -> p.getRelationshipType().equals(RelatedProductRelationType.DELIVERS) && !p.getType().equals(RelatedProductType.SHIPMENT_PRODUCT))
                .findFirst();

        List<RealisingService> realisingServices = Optional.ofNullable(productDTOFromCPIB.getRealizingService())
                .orElse(Collections.emptyList())
                .stream()
                .map(getRealisingService())
                .toList();

        relatedProduct.ifPresent(product -> {
            if (!CollectionUtils.isEmpty(realisingServices)) {
                product.setRealisingService(realisingServices);
            }
            product.setId(productDTOFromCPIB.getId());
            log.info("MaintainDeliveryNodeDeliveryNodeRelatedProductImpl | maintainRelatedProductWithRelationshipDelivers | Delivers -- set related product id and realisingServices of relatedproduct: {} ", relatedProduct);
        });
        log.info("MaintainDeliveryNodeDeliveryNodeRelatedProductImpl | maintainRelatedProductWithRelationshipDelivers | finished orchestration plan node id {} with product from CPIB {}", orchestrationPlanNode.getId(), productDTOFromCPIB);
        log.debug("MaintainDeliveryNodeDeliveryNodeRelatedProductImpl | maintainRelatedProductWithRelationshipDelivers | orchestration plan node {}", orchestrationPlanNode);
    }

    private static Function<ServiceRef, RealisingService> getRealisingService() {
        return serviceRefDTO ->
                RealisingService
                        .builder()
                        .id(serviceRefDTO.getId())
                        .href(serviceRefDTO.getHref())
                        .build();
    }

    private static Set<String> getRelatedProductSpecificationIdsEqualsRelationshipTypeReliesOn(List<RelatedProduct> relatedProducts) {
        return relatedProducts.stream()
                .filter(relatedProduct -> relatedProduct.getRelationshipType().equals(RelatedProductRelationType.RELIES_ON))
                .map(RelatedProduct::getProductSpecification)
                .map(ProductSpecification::getId)
                .collect(Collectors.toSet());
    }

    private static Boolean doesRelatedProductIdsContainProductSpecificationId(Set<String> relatedProductIds, String productSpecificationId) {
        return !relatedProductIds.stream()
                .filter(r -> r.equalsIgnoreCase(productSpecificationId))
                .findFirst()
                .isEmpty();
    }

    private static List<String> getRelatedProductIdsWithRelationship(Product installedProduct, String relationshipType) {
        if (Objects.isNull(installedProduct.getProductRelationship())) {
            return List.of();
        }
        return installedProduct.getProductRelationship()
                .stream()
                .filter(productRelationship -> productRelationship.getRelationshipType().equals(relationshipType))
                .map(ProductRelationship::getProduct)
                .map(ProductRef.class::cast)
                .map(ProductRef::getId)
                .toList();
    }

    private static Consumer<String> addRelatedProductWithReliesFromRelation(OrchestrationPlanNode opn) {
        return dependentProductId ->
                opn.getRelatedProduct().add(RelatedProduct.builder()
                        .id(dependentProductId)
                        .relationshipType(RelatedProductRelationType.RELIES_FROM)
                        .build());
    }

    public void maintainOrchestrationPlanNodeRelatedProduct(OrchestrationPlanNode eventOrchestrationPlanNode, Product productDTO) {
        log.info("MaintainDeliveryNodeDeliveryNodeRelatedProductImpl | maintainOrchestrationPlanNodeRelatedProduct | fetching product from cpib in related product delivery id: {} " +
                "for orchestration plan node id {} ", productDTO.getId(), eventOrchestrationPlanNode.getId());
        log.debug("MaintainDeliveryNodeDeliveryNodeRelatedProductImpl | maintainOrchestrationPlanNodeRelatedProduct | fetching product from cpib in related product delivery body: {} " +
                FOR_ORCHESTRATION_PLAN_NODE_LOG_MESSAGE, productDTO, eventOrchestrationPlanNode);
        maintainRelatedProductWithRelationshipDelivers(eventOrchestrationPlanNode, productDTO);
        maintainRelatedProductsWithRelationshipReliesOn(eventOrchestrationPlanNode, productDTO);
        maintainRelatedProductsWithRelationshipReliesFrom(eventOrchestrationPlanNode, productDTO);
        maintainRelatedProductsWithRelationshipMigratedFrom(eventOrchestrationPlanNode);
        orchestrationPlanService.updateNodeRelatedProducts(eventOrchestrationPlanNode);

        log.info("MaintainDeliveryNodeDeliveryNodeRelatedProductImpl | maintainOrchestrationPlanNodeRelatedProduct | finished product from cpib in related product delivery id: {} " +
                "for orchestration plan node id {} ", productDTO.getId(), eventOrchestrationPlanNode.getId());
        log.debug("MaintainDeliveryNodeDeliveryNodeRelatedProductImpl | maintainOrchestrationPlanNodeRelatedProduct | finished maintaining orchestration plan node {} ", eventOrchestrationPlanNode);

    }

    private void maintainRelatedProductsWithRelationshipMigratedFrom(OrchestrationPlanNode eventOrchestrationPlanNode) {
        eventOrchestrationPlanNode.getRelatedProduct().stream()
                .filter(
                        relatedProduct ->
                                relatedProduct.getRelationshipType().equals(RelatedProductRelationType.MIGRATED_FROM)
                )
                .forEach(relatedProduct -> {
                    Product installedProduct = productManagementService.getProductsByOrderIdAndItemIds(
                                    List.of(relatedProduct.getProductOrderItemId()),
                                    eventOrchestrationPlanNode.getRelatedProductOrder().getId()
                            )
                            .stream().findFirst().orElseThrow(
                                    () -> new CoodRecoverableAndNonRetryableException(
                                            new ProductNotFoundException(
                                                    ExceptionCode.INSTALLED_PRODUCT_NOT_FOUND_EXCEPTION,
                                                    eventOrchestrationPlanNode.getRelatedProductOrder().getId(),
                                                    relatedProduct.getProductOrderItemId(),
                                                    null
                                            )
                                    )
                            );
                    relatedProduct.setId(installedProduct.getId());
                });
    }

    private void maintainRelatedProductsWithRelationshipReliesOn(OrchestrationPlanNode orchestrationPlanNode, Product installedProduct) {
        log.info("MaintainDeliveryNodeDeliveryNodeRelatedProductImpl | maintainRelatedProductsWithRelationshipReliesOn | starting orchestration plan node id {} with product from CPIB {}", orchestrationPlanNode.getId(), installedProduct);
        log.debug("MaintainDeliveryNodeDeliveryNodeRelatedProductImpl | maintainRelatedProductsWithRelationshipReliesOn | fetching product from cpib in related product delivery body: {} " +
                FOR_ORCHESTRATION_PLAN_NODE_LOG_MESSAGE, installedProduct, orchestrationPlanNode);
        List<String> prerequisiteInstalledProductId = getRelatedProductIdsWithRelationship(installedProduct, RELIES_ON.getValue());
        if (CollectionUtils.isEmpty(prerequisiteInstalledProductId)) {
            return;
        }
        // get relationships from installed product when relation is RELIES_ON
        //  for each relation product -> search in node relatedproduct where (specid of the relation from cpib) =  spec id of the related product**
        //  the matched related product  (id = matched relation.product.id)
        Set<String> relatedProductSpecificationIds = getRelatedProductSpecificationIdsEqualsRelationshipTypeReliesOn(orchestrationPlanNode.getRelatedProduct());

        List<Product> prerequisiteInstalledProductsSpecification = productManagementService.getProductsByFields(prerequisiteInstalledProductId, List.of(PRODUCT_SPECIFICATION_ID));

        List<Product> prerequisiteInstalledProducts = prerequisiteInstalledProductsSpecification.stream().filter(productDTO ->
                doesRelatedProductIdsContainProductSpecificationId(relatedProductSpecificationIds, productDTO.getProductSpecification().getId())).toList();

        prerequisiteInstalledProducts.forEach(productDTO ->
                orchestrationPlanNode.getRelatedProduct().stream()
                        .filter(relatedProduct -> relatedProduct.getRelationshipType().equals(RelatedProductRelationType.RELIES_ON))
                        .filter(relatedProduct -> relatedProduct.getProductSpecification().getId().equalsIgnoreCase(productDTO.getProductSpecification().getId()))
                        // expect to return only 1 item to set productid.
                        .forEach(relatedProduct -> {
                            relatedProduct.setId(productDTO.getId());
                            log.info("MaintainDeliveryNodeDeliveryNodeRelatedProductImpl | maintainRelatedProductsWithRelationshipReliesOn | Relies on -- set related product id of relatedproduct: {} ", relatedProduct);
                        })
        );
        log.info("MaintainDeliveryNodeDeliveryNodeRelatedProductImpl | maintainRelatedProductsWithRelationshipReliesOn | finished orchestration plan node id {} with product from CPIB {}", orchestrationPlanNode.getId(), installedProduct);
        log.debug("MaintainDeliveryNodeDeliveryNodeRelatedProductImpl | maintainRelatedProductsWithRelationshipReliesOn | orchestration plan node {}", orchestrationPlanNode);
    }

    private void maintainRelatedProductsWithRelationshipReliesFrom(OrchestrationPlanNode orchestrationPlanNode, Product installedProduct) {
        log.info("MaintainDeliveryNodeDeliveryNodeRelatedProductImpl | maintainRelatedProductsWithRelationshipReliesFrom | starting orchestration plan node id {} with product from CPIB {}", orchestrationPlanNode.getId(), installedProduct);
        log.debug("MaintainDeliveryNodeDeliveryNodeRelatedProductImpl | maintainRelatedProductsWithRelationshipReliesFrom | fetching product from cpib in related product delivery body: {} " +
                FOR_ORCHESTRATION_PLAN_NODE_LOG_MESSAGE, installedProduct, orchestrationPlanNode);
        List<String> dependentProductIds = getRelatedProductIdsWithRelationship(installedProduct, RELIES_FROM.getValue());

        dependentProductIds.forEach(addRelatedProductWithReliesFromRelation(orchestrationPlanNode));
        log.info("MaintainDeliveryNodeDeliveryNodeRelatedProductImpl | maintainRelatedProductsWithRelationshipReliesFrom | finished orchestration plan node id {} with product from CPIB {}", orchestrationPlanNode.getId(), installedProduct);
        log.debug("MaintainDeliveryNodeDeliveryNodeRelatedProductImpl | maintainRelatedProductsWithRelationshipReliesFrom | orchestration plan node {}", orchestrationPlanNode);
    }
}
