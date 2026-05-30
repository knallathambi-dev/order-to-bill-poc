// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service.impl;

import com.orange.discobole.productinventory.dto.CatalogEntityRef;
import com.orange.discobole.productinventory.dto.v1.ProductRelationshipType;
import com.orange.discobole.productinventory.enumerate.ProductTypeEnum;
import com.orange.discobole.productinventory.exception.ProductInventoryException;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.model.ProductOfferingRefEntity;
import com.orange.discobole.productinventory.model.ProductRelationshipEntity;
import com.orange.discobole.productinventory.model.ProductSpecificationRefEntity;
import com.orange.discobole.productinventory.service.CatalogRequestService;
import com.orange.discobole.productinventory.service.CatalogService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.orange.discobole.productinventory.constant.Constant.*;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INTERNAL_ERROR;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INVALID_INPUT;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.*;

@Component
@Slf4j
@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class CatalogServiceImpl implements CatalogService {

    private static final List<String> nonCollectableRelationships = List.of(ProductRelationshipType.ROOTPRODUCT.getValue(), ProductRelationshipType.HASPARENT.getValue());

    private final CatalogRequestService catalogRequestService;

    private static Set<String> findMissingIds(Set<String> productsParamIds, List<CatalogEntityRef> existsCatalogRefs) {
        return productsParamIds.stream().filter(paramId -> existsCatalogRefs.stream().noneMatch(catalogEntityRef -> catalogEntityRef.getId().equals(paramId))).collect(Collectors.toSet());
    }

    private static Mono<Boolean> returnDoesNotExistInCatalogError(String entity, Set<String> missingIds) {
        return returnError(entity + " with id(s) : " + String.join(",", missingIds) + DOESN_T_EXIST_IN_CATALOG);
    }

    private static Mono<Boolean> returnError(String missingId, String businessError) {
        return returnError(PRODUCT_OFFERING + " with id : " + missingId + businessError);
    }

    private static Mono<Boolean> returnError(String message) {
        log.error("Invalid body field error: {}", message);
        return Mono.error(new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), message));
    }

    private static void collectCatalogValidationData(Map<String, ProductEntity> productMap, ProductEntity rootProduct, CatalogValidationData catalogValidationData) {
        collectOfferingValidationData(rootProduct, catalogValidationData);
        collectSpecificationValidationData(rootProduct, catalogValidationData);

        if (!CollectionUtils.isEmpty(rootProduct.getProductPrice())) {
            catalogValidationData.productOfferingPriceIds().addAll(extractProductOfferingPriceId(rootProduct));
        }

        collectRelationshipValidationData(productMap, rootProduct, catalogValidationData);
    }

    private static void collectOfferingValidationData(ProductEntity productEntity, CatalogValidationData catalogValidationData) {
        if (Objects.nonNull(productEntity.getProductOffering())) {
            if (productEntity.getProductOffering().getId().isEmpty()) {
                throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), PRODUCT_OFFERING_ID_CANNOT_BE_EMPTY);
            }
            catalogValidationData.productOfferings().add(productEntity.getProductOffering().getId());
        }
    }

    private static void collectSpecificationValidationData(ProductEntity productEntity, CatalogValidationData catalogValidationData) {
        if (Objects.nonNull(productEntity.getProductSpecification())) {
            if (productEntity.getProductSpecification().getId().isEmpty()) {
                throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), EMPTY_PRODUCT_SPECIFICATION_ID_DETECTED);
            }
            if (productEntity.getAtType().equals(ProductTypeEnum.PHYSICAL_PRODUCT.getValue())) {
                catalogValidationData.physicalProductsSpecIds().add(productEntity.getProductSpecification().getId());
            }
            if (productEntity.getAtType().equals(ProductTypeEnum.SHIPMENT_PRODUCT.getValue())) {
                catalogValidationData.shipmentProductsSpecsIds().add(productEntity.getProductSpecification().getId());
            }
            catalogValidationData.productSpecifications().add(productEntity.getProductSpecification().getId());
        }
    }

    private static boolean isBasicOffering(ProductEntity product) {
        return product.getProductOffering() != null && product.getProductSpecification() == null;
    }

    private static boolean isBasicSpecification(ProductEntity product) {
        return product.getProductOffering() == null && product.getProductSpecification() != null;
    }

    private static boolean isFusedSpecAndOffering(ProductEntity productEntity) {
        return productEntity.getProductSpecification() != null && productEntity.getProductOffering() != null;
    }

    private static List<String> extractProductOfferingPriceId(ProductEntity productEntity) {
        List<String> ids = new ArrayList<>();
        productEntity.getProductPrice().stream().filter(productPrice -> Objects.nonNull(productPrice.getProductOfferingPrice())).forEach(productPrice -> {
            if (productPrice.getProductOfferingPrice().getId().isEmpty()) {
                log.error("Empty product offering price ID detected");
                throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), EMPTY_PRODUCT_OFFERING_PRICE_ID_DETECTED);
            }
            ids.add(productPrice.getProductOfferingPrice().getId());
        });
        return ids;
    }

    private static Mono<Boolean> validateCatalogRelationshipData(CatalogValidationData catalogValidationData, List<CatalogEntityRef> existsCatalogRefs) {
        for (CatalogValidationRelationshipData relationshipDatum : catalogValidationData.relationshipData()) {
            Optional<CatalogEntityRef> any = existsCatalogRefs.stream().filter(catalogEntityRef -> catalogEntityRef.getId().equals(relationshipDatum.parent().getProductOffering().getId())).findAny();
            if (any.isEmpty()) {
                return returnError(relationshipDatum.parent().getProductOffering().getId(), DOESN_T_EXIST_IN_CATALOG);
            }
            CatalogEntityRef catalogEntityRef = any.get();
            if (Boolean.TRUE.equals(relationshipDatum.isSpec())) {
                String childSpecId = Optional.of(relationshipDatum).map(CatalogValidationRelationshipData::child).map(ProductEntity::getProductSpecification).map(ProductSpecificationRefEntity::getId).orElse(null);
                if (catalogEntityRef.getProductSpecification() == null || !catalogEntityRef.getProductSpecification().getId().equals(childSpecId)) {
                    return returnError(relationshipDatum.parent().getProductOffering().getId(), String.format(INVALID_RELATIONSHIP, childSpecId));
                }
            } else {
                String childOfferingId = Optional.of(relationshipDatum).map(CatalogValidationRelationshipData::child).map(ProductEntity::getProductOffering).map(ProductOfferingRefEntity::getId).orElse(null);
                if (catalogEntityRef.getBundledProductOffering() == null || catalogEntityRef.getBundledProductOffering().isEmpty() || catalogEntityRef.getBundledProductOffering().stream().noneMatch(cbpo -> cbpo.getId() != null && cbpo.getId().equals(childOfferingId))) {
                    return returnError(relationshipDatum.parent().getProductOffering().getId(), String.format(INVALID_RELATIONSHIP, childOfferingId));
                }
            }
        }
        log.debug("All Product param exists");
        return Mono.just(true);
    }

    private static void collectRelationshipValidationData(Map<String, ProductEntity> productMap, ProductEntity productEntity, CatalogValidationData catalogValidationData) {
        if (isFusedSpecAndOffering(productEntity)) {
            addFusedSpecAndOfferingData(productEntity, catalogValidationData);
        }

        if (productEntity.getProductRelationship() != null) {
            processProductRelationships(productMap, productEntity, catalogValidationData);
        }
    }

    private static void addFusedSpecAndOfferingData(ProductEntity productEntity, CatalogValidationData catalogValidationData) {
        catalogValidationData.relationshipData().add(new CatalogValidationRelationshipData(true, productEntity, productEntity));
    }

    private static void processProductRelationships(Map<String, ProductEntity> productMap, ProductEntity productEntity, CatalogValidationData catalogValidationData) {
        for (ProductRelationshipEntity relationshipEntity : productEntity.getProductRelationship()) {
            if (isRelationshipCollectable(relationshipEntity.getRelationshipType())) {
                handleCollectableRelationship(productMap, relationshipEntity, productEntity, catalogValidationData);
            }
        }
    }

    private static void handleCollectableRelationship(Map<String, ProductEntity> productMap, ProductRelationshipEntity relationshipEntity, ProductEntity productEntity, CatalogValidationData catalogValidationData) {
        ProductEntity relatedProduct = productMap.get(relationshipEntity.getProduct().getId().toString());

        if (relatedProduct == null) {
            return; // Skip if the product is not found in the map
        }

        addCatalogValidationData(productEntity, relatedProduct, catalogValidationData);
        collectCatalogValidationData(productMap, relatedProduct, catalogValidationData);
    }

    private static void addCatalogValidationData(ProductEntity productEntity, ProductEntity relatedProduct, CatalogValidationData catalogValidationData) {
        if (isBasicOffering(relatedProduct)) {
            catalogValidationData.relationshipData().add(new CatalogValidationRelationshipData(false, productEntity, relatedProduct));
        } else if (isBasicSpecification(relatedProduct)) {
            catalogValidationData.relationshipData().add(new CatalogValidationRelationshipData(true, productEntity, relatedProduct));
        }
    }

    private static boolean isRelationshipCollectable(String productRelationshipType) {
        return !nonCollectableRelationships.contains(productRelationshipType);
    }


    CompletableFuture<Boolean> validateTangibleProductsSpecs(CatalogValidationData catalogValidationData) {
        Set<String> specIds = Stream.concat(catalogValidationData.physicalProductsSpecIds().stream(), catalogValidationData.shipmentProductsSpecsIds().stream()).collect(Collectors.toSet());
        if (!specIds.isEmpty()) {
            return catalogRequestService.listProductSpecifications(specIds).flatMap(responseEntity -> {
                log.debug("Response Status code: {}", responseEntity.getStatusCode());
                log.debug("Response Body: {}", (Object) responseEntity.getBody());
                HttpStatusCode statusCode = responseEntity.getStatusCode();
                if (statusCode.isSameCodeAs(HttpStatus.OK)) {
                    List<CatalogEntityRef> existsCatalogRefs = Arrays.stream(Objects.requireNonNull(responseEntity.getBody())).toList();
                    try {
                        checkSupportEntityToPhysicalProductSpec(existsCatalogRefs, catalogValidationData.physicalProductsSpecIds());
                        checkSupportEntityToShipmentProductSpec(existsCatalogRefs, catalogValidationData.shipmentProductsSpecsIds());
                    } catch (ProductInventoryException e) {
                        return Mono.error(e);
                    }

                    log.debug("All Product param exists");
                    return Mono.just(true);
                } else if (statusCode.isSameCodeAs(HttpStatus.NO_CONTENT)) {
                    return returnDoesNotExistInCatalogError(PRODUCT_OFFERING, catalogValidationData.productOfferings());
                } else {
                    log.error("Internal server error occurred");
                    return Mono.error(new ProductInventoryException(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_ERROR.getCode(), INTERNAL_ERROR.getStatus(), INTERNAL_SERVER_ERROR));
                }
            }).toFuture();
        }
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public void checkCatalogService(ProductEntity rootProduct, List<ProductEntity> productEntities) {
        CatalogValidationData catalogValidationData = CatalogValidationData.init();
        Map<String, ProductEntity> productMap = productEntities.stream().collect(Collectors.toMap(ProductEntity::getId, productEntity -> productEntity));
        collectCatalogValidationData(productMap, rootProduct, catalogValidationData);
        CompletableFuture<?> validateOfferingsAndSpecsInCatalogResult = validateOfferingsAndSpecsInCatalog(catalogValidationData);
        CompletableFuture<?> validateTangibleProductsSpecsResult = validateTangibleProductsSpecs(catalogValidationData);
        CompletableFuture<?> validatePricesInCatalogResult = CompletableFuture.completedFuture(null);
        if (!catalogValidationData.productOfferingPriceIds().isEmpty()) {
            validatePricesInCatalogResult = validatePricesInCatalog(catalogValidationData.productOfferingPriceIds());
        }
        try {
            CompletableFuture.allOf(validateOfferingsAndSpecsInCatalogResult, validatePricesInCatalogResult, validateTangibleProductsSpecsResult).join();
        } catch (CompletionException ex) {
            // Unwrap the cause of the CompletionException
            Throwable cause = ex.getCause();
            if (cause instanceof ProductInventoryException productInventoryException) {
                throw productInventoryException;
            } else {
                throw new ProductInventoryException(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_ERROR.getCode(), "Error getting result");
            }
        }

    }

    private CompletableFuture<?> validatePricesInCatalog(Set<String> productOfferingPriceIds) {
        if (!productOfferingPriceIds.isEmpty()) {
            return catalogRequestService.listProductOfferingPrices(productOfferingPriceIds).flatMap(responseEntity -> {
                log.debug("Response Status code: {}", responseEntity.getStatusCode());
                log.debug("Response Body: {}", (Object) responseEntity.getBody());
                HttpStatusCode statusCode = responseEntity.getStatusCode();
                if (statusCode.isSameCodeAs(HttpStatus.OK)) {
                    List<CatalogEntityRef> existsCatalogRefs = Arrays.stream(Objects.requireNonNull(responseEntity.getBody())).toList();
                    Set<String> missingIds = findMissingIds(productOfferingPriceIds, existsCatalogRefs);
                    if (!missingIds.isEmpty()) {
                        return returnDoesNotExistInCatalogError(PRODUCT_OFFERING_PRICE, missingIds);
                    }
                    log.debug("All Product param exists");
                    return Mono.just(true);
                } else if (statusCode.isSameCodeAs(HttpStatus.NO_CONTENT)) {
                    return returnDoesNotExistInCatalogError(PRODUCT_OFFERING_PRICE, productOfferingPriceIds);
                } else {
                    log.error("Internal server error occurred");
                    return Mono.error(new ProductInventoryException(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_ERROR.getCode(), INTERNAL_ERROR.getStatus(), INTERNAL_SERVER_ERROR));
                }
            }).toFuture();
        }
        return CompletableFuture.completedFuture(false);
    }

    private CompletableFuture<?> validateOfferingsAndSpecsInCatalog(CatalogValidationData catalogValidationData) {
        if (!catalogValidationData.productOfferings().isEmpty()) {
            return catalogRequestService.listProductOfferings(catalogValidationData.productOfferings()).flatMap(responseEntity -> {
                log.debug("Response Status code: {}", responseEntity.getStatusCode());
                log.debug("Response Body: {}", (Object) responseEntity.getBody());
                HttpStatusCode statusCode = responseEntity.getStatusCode();
                if (statusCode.isSameCodeAs(HttpStatus.OK)) {
                    List<CatalogEntityRef> existsCatalogRefs = Arrays.stream(Objects.requireNonNull(responseEntity.getBody())).toList();
                    Set<String> missingIds = findMissingIds(catalogValidationData.productOfferings(), existsCatalogRefs);
                    if (!missingIds.isEmpty()) {
                        return returnDoesNotExistInCatalogError(PRODUCT_OFFERING, missingIds);
                    }
                    return validateCatalogRelationshipData(catalogValidationData, existsCatalogRefs);
                } else if (statusCode.isSameCodeAs(HttpStatus.NO_CONTENT)) {
                    return returnDoesNotExistInCatalogError(PRODUCT_OFFERING, catalogValidationData.productOfferings());
                } else {
                    log.error("Internal server error occurred");
                    return Mono.error(new ProductInventoryException(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_ERROR.getCode(), INTERNAL_ERROR.getStatus(), INTERNAL_SERVER_ERROR));
                }
            }).toFuture();
        }
        return CompletableFuture.completedFuture(false);

    }


    private void checkSupportEntityToShipmentProductSpec(List<CatalogEntityRef> existsCatalogRefs, Set<String> shipmentProductSpecIds) {
        Optional<String> matchedId = existsCatalogRefs.stream().filter(catalogEntityRef -> shipmentProductSpecIds.contains(catalogEntityRef.getId())).filter(catalogEntityRef -> !(CFS_SPEC.equals(catalogEntityRef.getSupportEntity()) && SHIPPING_PRODUCT_SPECIFICATION.equals(catalogEntityRef.getAtBaseType()))).map(CatalogEntityRef::getId).findFirst();

        if (matchedId.isPresent()) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), String.format(PRODUCT_SPECIFICATION_HAS_NOT_CFS_SPEC_OR_SHIPMENT_PRODUCT_SPECIFICATION, matchedId.get()));
        }
    }

    private void checkSupportEntityToPhysicalProductSpec(List<CatalogEntityRef> catalogEntityRefs, Set<String> idsPhysicalProductSpec) {
        Optional<String> matchedId = catalogEntityRefs.stream().filter(catalogEntityRef -> idsPhysicalProductSpec.contains(catalogEntityRef.getId())).filter(catalogEntityRef -> !STOCK_ITEM_TYPE.equals(catalogEntityRef.getSupportEntity())).map(CatalogEntityRef::getId).findFirst();

        if (matchedId.isPresent()) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), String.format(PRODUCT_SPECIFICATION_HAS_NOT_STOCK_ITEM_TYPE_IN_SUPPORT_ENTITY, matchedId.get()));
        }
    }
    @SuppressFBWarnings("EI_EXPOSE_REP")
    public record CatalogValidationData(List<CatalogValidationRelationshipData> relationshipData,
                                        Set<String> productSpecifications, Set<String> productOfferings,
                                        Set<String> productOfferingPriceIds, Set<String> physicalProductsSpecIds,
                                        Set<String> shipmentProductsSpecsIds) {
        public static CatalogValidationData init() {
            return new CatalogValidationData(new ArrayList<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
        }

        @SuppressFBWarnings("EI_EXPOSE_REP")
        public List<CatalogValidationRelationshipData> relationshipData() {
            return relationshipData;
        }


        public Set<String> productSpecifications() {
            return productSpecifications;
        }

        @SuppressFBWarnings("EI_EXPOSE_REP")
        public Set<String> productOfferings() {
            return productOfferings;
        }

        @SuppressFBWarnings("EI_EXPOSE_REP")
        public Set<String> productOfferingPriceIds() {
            return productOfferingPriceIds;
        }

        @SuppressFBWarnings("EI_EXPOSE_REP")
        public Set<String> physicalProductsSpecIds() {
            return physicalProductsSpecIds;
        }

        @SuppressFBWarnings("EI_EXPOSE_REP")
        public Set<String> shipmentProductsSpecsIds() {
            return shipmentProductsSpecsIds;
        }
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public record CatalogValidationRelationshipData(Boolean isSpec, ProductEntity parent, ProductEntity child) {

    }
}
