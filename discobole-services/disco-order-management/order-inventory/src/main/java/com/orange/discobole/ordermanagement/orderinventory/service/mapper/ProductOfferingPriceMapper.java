// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.service.mapper;

import com.orange.discobole.ordermanagement.orderinventory.domain.*;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import org.mapstruct.Mapper;



/**
 * Mapper for the entity {@link ProductOfferingPriceRef} and its DTO {@link ProductOfferingPriceRef}.
 * Provides mapping methods between entities and DTOs for product and product reference objects.
 */
@Mapper(componentModel = "spring")
public interface ProductOfferingPriceMapper {

    /**
     * Maps a {@link ProductOfferingPriceRefOrValueEntity} to its DTO equivalent.
     * Handles polymorphic mapping based on actual entity type.
     *
     * @param entity the entity to map
     * @return the corresponding DTO or null if entity is null
     */
    default ProductOfferingPriceRefOrValue mapToDto(ProductOfferingPriceRefOrValueEntity entity) {
        if (entity == null) {
            return null;
        }

        if (entity instanceof InstallmentChargeEntity installmentChargeEntity) {
            return mapToInstallmentCharge(installmentChargeEntity);
        } else if (entity instanceof ProductOfferingPriceChargeEntity productOfferingPriceChargeEntity) {
            return mapToProductOfferingPriceCharge(productOfferingPriceChargeEntity);
        } else if (entity instanceof ProductOfferingPriceRefEntity productOfferingPriceRefEntity) {
            return mapToProductOfferingPriceRef(productOfferingPriceRefEntity);
        }

        return null;
    }

    /**
     * Maps a {@link ProductOfferingPriceRefOrValue} DTO to its entity equivalent.
     * Handles polymorphic mapping based on actual DTO type.
     *
     * @param dto the DTO to map
     * @return the corresponding entity or null if dto is null
     */
    default ProductOfferingPriceRefOrValueEntity mapToEntity(ProductOfferingPriceRefOrValue dto) {
        if (dto == null) {
            return null;
        }

        if (dto instanceof InstallmentCharge installmentCharge) {
            return mapToInstallmentChargeEntity(installmentCharge);
        } else if (dto instanceof ProductOfferingPriceCharge productOfferingPriceCharge) {
            return mapToProductOfferingPriceChargeEntity(productOfferingPriceCharge);
        } else if (dto instanceof ProductOfferingPriceRef productOfferingPriceRef) {
            return mapToProductOfferingPriceRefEntity(productOfferingPriceRef);
        }

        return null;
    }
    InstallmentCharge mapToInstallmentCharge(InstallmentChargeEntity entity);

    InstallmentChargeEntity mapToInstallmentChargeEntity(InstallmentCharge dto);

    // ========== ProductOfferingPriceCharge Mappings ==========
    ProductOfferingPriceCharge mapToProductOfferingPriceCharge(ProductOfferingPriceChargeEntity entity);

    ProductOfferingPriceChargeEntity mapToProductOfferingPriceChargeEntity(ProductOfferingPriceCharge dto);

    // ========== ProductOfferingPriceRef Mappings ==========

    ProductOfferingPriceRef mapToProductOfferingPriceRef(ProductOfferingPriceRefEntity entity);

    ProductOfferingPriceRefEntity mapToProductOfferingPriceRefEntity(ProductOfferingPriceRef dto);




}
