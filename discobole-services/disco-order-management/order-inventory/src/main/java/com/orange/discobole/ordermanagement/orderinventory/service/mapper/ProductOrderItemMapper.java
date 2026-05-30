// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.service.mapper;

import com.orange.discobole.ordermanagement.orderinventory.domain.ProductOrderItemEntity;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderItem;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link ProductOrderItemEntity} and its DTO {@link ProductOrderItem}.
 * Provides mapping methods between entities and DTOs for product and product reference objects.
 */
@Mapper(componentModel = "spring", uses = {ProductRefOrValueMapper.class, ProductOfferingPriceMapper.class})
public interface ProductOrderItemMapper extends EntityMapper<ProductOrderItem, ProductOrderItemEntity> {
}