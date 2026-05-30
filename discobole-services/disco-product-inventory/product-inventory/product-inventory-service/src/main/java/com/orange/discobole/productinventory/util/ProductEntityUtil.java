// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.util;

import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.dto.v1.ProductRelationship;
import com.orange.discobole.productinventory.dto.v1.ProductRelationshipType;
import com.orange.discobole.productinventory.dto.v1.ProductStatusType;
import com.orange.discobole.productinventory.enumerate.ProductOfferingTypeEnum;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.model.ProductRefEntity;
import com.orange.discobole.productinventory.model.ProductRelationshipEntity;
import org.bson.types.ObjectId;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.orange.discobole.productinventory.constant.Constant.PRODUCT_SPECIFICATION_REF;
import static com.orange.discobole.productinventory.enumerate.ProductOfferingTypeEnum.*;

public class ProductEntityUtil {

    private ProductEntityUtil() {
    }

    public static List<ProductRelationshipEntity> getProductRelationshipsByType(ProductEntity product, ProductRelationshipType relationshipType) {
        if (product == null || CollectionUtils.isEmpty(product.getProductRelationship())) {
            return List.of(); // Return an empty list if product or its relationships are null/empty
        }

        // Filter and return the list of relationships matching the specified type
        return product.getProductRelationship().stream().filter(relationship -> relationshipType.getValue().equals(relationship.getRelationshipType())).toList();
    }

    public static List<ProductRelationship> getProductRelationshipsByType(Product product, ProductRelationshipType relationShipType) {
        if (!CollectionUtils.isEmpty(product.getProductRelationship())) {
            return product.getProductRelationship().stream()
                    .filter(productRelationship -> productRelationship.getRelationshipType().equals(relationShipType.getValue()))
                    .toList();
        }
        return List.of();
    }



    public static boolean isBundlesRelationShip(ProductEntity child, ProductEntity parent) {
        if (Objects.isNull(child.getProductOffering()) || Objects.isNull(parent.getProductOffering())) {
            return false;
        }
        String productType = child.getProductOffering().getAtType();
        String relatedProductType = parent.getProductOffering().getAtType();
        return ((relatedProductType.equals(CONTRACT.getValue()) && productType.equals(BUNDLE_PRODUCT_OFFERING.getValue()))
                || (relatedProductType.equals(BUNDLE_PRODUCT_OFFERING.getValue()) && productType.equals(BUNDLE_PRODUCT_OFFERING.getValue()))
                || (relatedProductType.equals(BUNDLE_PRODUCT_OFFERING.getValue()) && productType.equals(ATOMIC_PRODUCT_OFFERING.getValue())));
    }

    public static boolean isProductOfferingType(ProductEntity productEntity, ProductOfferingTypeEnum type) {
        return !Objects.isNull(productEntity.getProductOffering()) && !Objects.isNull(productEntity.getProductOffering().getAtType()) && productEntity.getProductOffering().getAtType().equals(type.getValue());
    }


    public static ProductRelationshipEntity createRootProductRelationship(String contractId) {
        return ProductRelationshipEntity.builder().relationshipType(ProductRelationshipType.ROOTPRODUCT.getValue()).product(ProductRefEntity.builder().id(new ObjectId(contractId)).build()).build();
    }

    public static boolean isSellsRelationShip(ProductEntity child, ProductEntity parent) {
        if (Objects.isNull(child.getProductSpecification()) || Objects.isNull(parent.getProductOffering())) {
            return false;
        }
        String productType = child.getProductSpecification().getAtType();
        String relatedProductType = parent.getProductOffering().getAtType();
        return relatedProductType.equals(ATOMIC_PRODUCT_OFFERING.getValue()) && productType.equals(PRODUCT_SPECIFICATION_REF);
    }
    public static boolean isSellsRelationShip(Product child, Product parent) {
        if (Objects.isNull(child.getProductSpecification()) || Objects.isNull(parent.getProductOffering())) {
            return false;
        }
        String productType = child.getProductSpecification().getAtType();
        String relatedProductType = parent.getProductOffering().getAtType();
        return relatedProductType.equals(ATOMIC_PRODUCT_OFFERING.getValue()) && productType.equals(PRODUCT_SPECIFICATION_REF);
    }

    public static boolean isProductSpecification(ProductEntity product) {
        return !Objects.isNull(product.getProductSpecification()) && !Objects.isNull(product.getProductSpecification().getAtType()) && product.getProductSpecification().getAtType()
                .equals(PRODUCT_SPECIFICATION_REF);
    }

    public static boolean isRelationShipTypeOneOf(ProductRelationshipEntity productRelationship, ProductRelationshipType... types) {
        AtomicBoolean exist = new AtomicBoolean(false);
        Arrays.stream(types).forEach(productRelationshipType -> {
            if (productRelationshipType.getValue().equals(productRelationship.getRelationshipType())) {
                exist.set(true);
            }
        });
        return exist.get();
    }

    public static Optional<ProductEntity> findBy(List<ProductEntity> products, String productId) {
        return products.stream().filter(p -> p.getId().equals(productId)).findFirst();
    }

    public static Set<ObjectId> extractRelatedProduct(List<ProductEntity> productEntityList, List<ProductRelationshipType> productRelationshipTypes) {
        return productEntityList.stream()
                .filter(product -> Objects.nonNull(product.getProductRelationship()))
                .flatMap(product -> product.getProductRelationship().stream()
                        .filter(productRelationship -> productRelationshipTypes.stream().map(ProductRelationshipType::getValue).anyMatch(value -> value.equals(productRelationship.getRelationshipType())))
                        .map(productRelationship -> productRelationship.getProduct().getId()))
                .collect(Collectors.toSet());
    }

    public static List<ProductEntity> getProductsByStatusFrom(List<ProductEntity> productEntities, ProductStatusType statusType) {
        return productEntities.stream().filter(product -> product.getStatus().equals(statusType)).toList();
    }

    public static Set<String> getIds(Set<ProductEntity> activeProducts) {
        return activeProducts.stream().map(ProductEntity::getId).collect(Collectors.toSet());
    }

    public static void removeRelationShipFromProduct(Product product, ProductRelationshipType relationshipType) {
        if (!CollectionUtils.isEmpty(product.getProductRelationship())) {
            product.getProductRelationship().removeAll(ProductEntityUtil.getProductRelationshipsByType(product, relationshipType));
        }
    }

    public static void removeRelationShipFromProduct(ProductEntity product, ProductRelationshipType relationshipType) {
        if (!CollectionUtils.isEmpty(product.getProductRelationship())) {
            product.getProductRelationship().removeAll(ProductEntityUtil.getProductRelationshipsByType(product, relationshipType));
        }
    }
}
