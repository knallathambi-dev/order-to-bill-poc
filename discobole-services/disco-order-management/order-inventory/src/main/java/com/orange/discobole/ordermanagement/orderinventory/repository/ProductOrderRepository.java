// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.repository;

import com.orange.discobole.ordermanagement.orderinventory.domain.ProductOrderEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the ProductOrder entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductOrderRepository extends MongoRepository<ProductOrderEntity, String> {

    ProductOrderEntity getProductOrderById(String id);
    @Query(value = "{ '_id': ?0, 'relatedParty.partyOrPartyRole.id': ?1 }", exists = true)
    boolean existsByIdAndRelatedPartyId(String productOrderId, String relatedPartyId);
}