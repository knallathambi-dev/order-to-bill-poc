// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.repository;

import com.orange.discobole.productinventory.dto.v1.ProductOperationalStatusType;
import com.orange.discobole.productinventory.dto.v1.ProductRelationship;
import com.orange.discobole.productinventory.dto.v1.ProductRelationshipType;
import com.orange.discobole.productinventory.dto.v1.ProductStatusType;
import com.orange.discobole.productinventory.enumerate.ProductOfferingTypeEnum;
import com.orange.discobole.productinventory.model.OperationalStatusChangeEntity;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.model.StatusChangeEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static com.orange.discobole.productinventory.constant.QueryFields.*;

@Repository
@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
public class CustomProductRepository {
    private final MongoTemplate mongoTemplate;

    public List<ProductEntity> findProductsWithPastTerminationDate(Integer limit) {
        Instant currentDate = Instant.now();
        Query query = new Query();
        query.addCriteria(
                Criteria.where(ProductEntity.Fields.terminationDate).lt(currentDate)
                        .and(ProductEntity.Fields.operationalStatus).ne(ProductOperationalStatusType.TERMINATED)
                        .orOperator(Criteria.where(ProductEntity.Fields.executeTerminationProcess).isNull(), Criteria.where(ProductEntity.Fields.executeTerminationProcess).is(false))
        );
        query.fields().include(ProductEntity.Fields.id).include(ProductEntity.Fields.status)
                .include(ProductEntity.Fields.productSpecification)
                .include(ProductEntity.Fields.productOffering)
                .include(ProductEntity.Fields.productRelationship);
        query.limit(limit);

        return mongoTemplate.find(query, ProductEntity.class);
    }

    public void markProcessedProduct(Set<String> ids) {
        Query query = new Query(Criteria.where(ProductEntity.Fields.id).in(ids));
        Update update = new Update().set(ProductEntity.Fields.executeTerminationProcess, true);
        mongoTemplate.updateMulti(query, update, ProductEntity.class);
    }

    public List<ProductEntity> findAtomicProductOfferingSells(Set<String> productSpecificationsIds) {
        Query query = new Query();
        query.addCriteria(
                Criteria.where(PRODUCT_RELATIONSHIP_PRODUCT_ID).in(productSpecificationsIds)
                        .and(PRODUCT_OFFERING + AT_TYPE_SUFFIX).is(ProductOfferingTypeEnum.ATOMIC_PRODUCT_OFFERING.getValue())
                        .and(ProductEntity.Fields.productRelationship).elemMatch(Criteria.where(ProductRelationship.Fields.relationshipType).is(ProductRelationshipType.SELLS.getValue()))
        );
        query.fields().include(ProductEntity.Fields.id).include(ProductEntity.Fields.status);
        return mongoTemplate.find(query, ProductEntity.class);
    }

    public void updateStatusAndOperationalStatusOf(Set<String> ids, ProductStatusType productStatus, ProductOperationalStatusType operationalStatus) {
        Update update = new Update().set(ProductEntity.Fields.status, productStatus);
        update.set(ProductEntity.Fields.operationalStatus, operationalStatus);
        update.set(ProductEntity.Fields.lastUpdateDate, OffsetDateTime.now());
        StatusChangeEntity statusChange = StatusChangeEntity.builder().changeDate(OffsetDateTime.now()).status(productStatus).build();
        OperationalStatusChangeEntity operationalStatusChange = OperationalStatusChangeEntity.builder().changeDate(OffsetDateTime.now()).status(operationalStatus).build();
        update.addToSet(ProductEntity.Fields.statusChange, statusChange);
        update.addToSet(ProductEntity.Fields.operationalStatusChange, operationalStatusChange);
        Query query = new Query(Criteria.where(ProductEntity.Fields.id).in(ids));
        mongoTemplate.updateMulti(query, update, ProductEntity.class);
    }

    public void updateTerminationDateIfNotExist(Set<String> ids) {
        Query query = new Query(Criteria.where(ProductEntity.Fields.id).in(ids).and(ProductEntity.Fields.terminationDate).exists(false));
        Update update = new Update().set(ProductEntity.Fields.terminationDate, Instant.now());
        mongoTemplate.updateMulti(query, update, ProductEntity.class);
    }

    public List<ProductEntity> findNotTerminatedProductsByIds(Set<ObjectId> ids) {
        Query query = new Query(Criteria.where(ProductEntity.Fields.id)
                .in(ids).and(ProductEntity.Fields.operationalStatus).ne(ProductOperationalStatusType.TERMINATED));
        query.fields().include(ProductEntity.Fields.status)
                .include(ProductEntity.Fields.id)
                .include(ProductEntity.Fields.productOffering)
                .include(ProductEntity.Fields.productSpecification)
                .include(ProductEntity.Fields.productRelationship);
        return mongoTemplate.find(query, ProductEntity.class);
    }

    public List<ProductEntity> findProductForEventStatusChangeBy(Set<String> ids) {
        Query query = new Query(Criteria.where(ProductEntity.Fields.id).in(ids));
        query.fields().include(ProductEntity.Fields.status)
                .include(ProductEntity.Fields.statusChange)
                .include(ProductEntity.Fields.operationalStatus)
                .include(ProductEntity.Fields.operationalStatusChange)
                .include(ProductEntity.Fields.terminationDate)
                .include(ProductEntity.Fields.lastUpdateDate)
                .include(ProductEntity.Fields.id)
                .include(ProductEntity.Fields.atType);
        return mongoTemplate.find(query, ProductEntity.class);
    }
    public void cleanUpReferences(String productId) {
        Query query = new Query(Criteria.where("productRelationship.product.id").is(productId));
        Update update = new Update().pull("productRelationship", new Query(Criteria.where("product.id").is(productId))).set(ProductEntity.Fields.lastUpdateDate, OffsetDateTime.now());
        mongoTemplate.updateMulti(query, update, ProductEntity.class);
    }
}
