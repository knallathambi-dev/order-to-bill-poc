// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.validation.impl;

import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.dto.v1.ProductOperationalStatusType;
import com.orange.discobole.productinventory.dto.v1.ProductRelationshipType;
import com.orange.discobole.productinventory.dto.v1.ProductStatusType;
import com.orange.discobole.productinventory.exception.ProductInventoryException;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.model.ProductRefEntity;
import com.orange.discobole.productinventory.model.ProductRelationshipEntity;
import com.orange.discobole.productinventory.validation.ProductEntityValidator;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.function.Predicate;

import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INVALID_INPUT;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.RESOURCE_NOT_FOUND;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.*;

@Component
@Slf4j
@Order(5)
@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
public class ProductRelationshipValidator implements ProductEntityValidator {
    public static final Predicate<ProductRelationshipEntity> hasParentRelationshipPredicate = productRelationship -> productRelationship.getRelationshipType().equals(ProductRelationshipType.HASPARENT.getValue());
    private final MongoTemplate mongoTemplate;

    private void checkProductRelationShipHasParent(ProductRefEntity p) {
                String parentId = p.getId().toString();
                Query query = new Query().addCriteria(Criteria.where(Product.Fields.id)
                        .is(parentId));
                query.fields().include(Product.Fields.id, Product.Fields.status, Product.Fields.operationalStatus);
                ProductEntity parent = mongoTemplate.findOne(query, ProductEntity.class);
                if (parent == null) {
                    log.error("The product with id {} does not exist", parentId);
                    throw new ProductInventoryException(HttpStatus.NOT_FOUND, RESOURCE_NOT_FOUND.getCode(), RESOURCE_NOT_FOUND.getStatus(), String.format(THE_PRODUCT_WITH_ID_S_DOES_NOT_EXIST, parentId));
                }
                checkParentProductStatus(parent);

    }

    private void checkParentProductStatus(ProductEntity product) {
        log.info("product with id: {} exist", product.getId());
        List<ProductStatusType> validStatus = List.of(ProductStatusType.ACTIVE, ProductStatusType.CREATED);
        if (!validStatus.contains(product.getStatus())) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), STATUS_OF_THE_RELATED_PRODUCT_IS_INVALID);
        }
        List<ProductOperationalStatusType> validOpStatus = List.of(ProductOperationalStatusType.PENDINGTERMINATE, ProductOperationalStatusType.PENDINGCANCEL);
        if (validOpStatus.contains(product.getOperationalStatus())) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), THE_OPERATIONAL_STATUS_OF_THE_RELATED_PRODUCT_IS_INVALID);
        }
    }


    @Override
    public void validate(ProductEntity product) {

        if (!CollectionUtils.isEmpty(product.getProductRelationship())) {
            // validate has parent relationships
            product
                    .getProductRelationship()
                    .stream()
                    .filter(hasParentRelationshipPredicate)
                    .map(ProductRelationshipEntity::getProduct)
                    .forEach(this::checkProductRelationShipHasParent);
        }
    }

}
