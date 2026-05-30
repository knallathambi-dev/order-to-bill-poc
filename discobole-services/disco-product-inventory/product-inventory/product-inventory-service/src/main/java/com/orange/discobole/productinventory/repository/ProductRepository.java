// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.repository;


import com.orange.discobole.productinventory.dto.v1.ProductStatusType;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.model.ProductRelationshipEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@Repository
public interface ProductRepository extends MongoRepository<ProductEntity, String> {


    @Update("{ '$push' : { 'productRelationship' : ?1 } }")
    void findAndPushProductRelationshipByIdIn(Set<ObjectId> ids, ProductRelationshipEntity productRelationship);

    List<ProductEntity> findByProductOrderItemProductOrderIdAndStatus(String productOrderId, ProductStatusType status);
    Optional<ProductEntity> findFirstByIdInAndStatusNotIn(Set<String> productOrderId, List<ProductStatusType> status);
    Optional<ProductEntity>  findFirstByIdInAndStatusIn(Set<String> productOrderId, List<ProductStatusType> status);
    @Query(value = "{ '_id': ?0, 'relatedParty._id': ?1 }", exists = true)
    boolean existsByIdAndRelatedPartyId(String productId, String relatedPartyId);

}
