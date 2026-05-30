// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.service.mapper;

import com.orange.discobole.ordermanagement.orderinventory.domain.ProductOrderEntity;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link ProductOrderEntity} and its DTO {@link ProductOrder}.
 */
@Mapper(componentModel = "spring", uses = {ProductOrderItemMapper.class, PartyRefOrPartyRoleRefMapper.class, ProductOfferingPriceMapper.class})
public interface ProductOrderMapper extends EntityMapper<ProductOrder, ProductOrderEntity> {
}