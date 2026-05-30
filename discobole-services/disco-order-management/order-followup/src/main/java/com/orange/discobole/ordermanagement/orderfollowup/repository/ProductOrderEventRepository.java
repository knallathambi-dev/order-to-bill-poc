// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.repository;

import com.orange.discobole.ordermanagement.orderfollowup.domain.ProductOrderItemStateChangedEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductOrderEventRepository extends MongoRepository<ProductOrderItemStateChangedEvent, String> {
    Optional<ProductOrderItemStateChangedEvent> findByProductOrderId(String productOrderId);
}