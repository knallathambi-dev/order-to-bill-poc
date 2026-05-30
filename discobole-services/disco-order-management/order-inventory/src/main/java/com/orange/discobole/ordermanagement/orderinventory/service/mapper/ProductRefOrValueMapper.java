// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.service.mapper;

import com.orange.discobole.ordermanagement.orderinventory.domain.ProductEntity;
import com.orange.discobole.ordermanagement.orderinventory.domain.ProductRefEntity;
import com.orange.discobole.ordermanagement.orderinventory.domain.ProductRefOrValueEntity;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductRef;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductRefOrValue;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link ProductRefOrValueEntity} and its DTO {@link ProductRefOrValue}.
 * Provides mapping methods between entities and DTOs for product and product reference objects.
 */
@Mapper(componentModel = "spring", uses = CharacteristicMapper.class)
public interface ProductRefOrValueMapper {

    /**
     * Maps a {@link ProductRefOrValueEntity} to its DTO equivalent.
     *
     * @param entity the source entity to be mapped
     * @return the mapped {@link ProductRefOrValue}, or {@code null} if the input is {@code null}
     */
    default ProductRefOrValue mapToDto(ProductRefOrValueEntity entity) {
        if (entity instanceof ProductEntity productEntity) {
            return mapToProduct(productEntity);
        } else if (entity instanceof ProductRefEntity productRefEntity) {
            return mapToProductRef(productRefEntity);
        }
        return null;
    }

    /**
     * Maps a {@link ProductRefOrValue} DTO to its entity equivalent.
     *
     * @param productRefOrValue the source DTO to be mapped
     * @return the mapped {@link ProductRefOrValueEntity}, or {@code null} if the input is {@code null}
     */
    default ProductRefOrValueEntity mapToEntity(ProductRefOrValue productRefOrValue) {
        if (productRefOrValue instanceof Product product) {
            return mapToProductEntity(product);
        } else if (productRefOrValue instanceof ProductRef productRef) {
            return mapToProductRefEntity(productRef);
        }
        return null;
    }

    /**
     * Maps a {@link ProductEntity} to its DTO {@link Product}.
     *
     * @param entity the source entity to be mapped
     * @return the mapped {@link Product}
     */
    Product mapToProduct(ProductEntity entity);

    /**
     * Maps a {@link ProductRefEntity} to its DTO {@link ProductRef}.
     *
     * @param entity the source entity to be mapped
     * @return the mapped {@link ProductRef}
     */
    ProductRef mapToProductRef(ProductRefEntity entity);

    /**
     * Maps a {@link Product} DTO to its entity {@link ProductEntity}.
     *
     * @param product the source DTO to be mapped
     * @return the mapped {@link ProductEntity}
     */
    ProductEntity mapToProductEntity(Product product);

    /**
     * Maps a {@link ProductRef} DTO to its entity {@link ProductRefEntity}.
     *
     * @param productRef the source DTO to be mapped
     * @return the mapped {@link ProductRefEntity}
     */
    ProductRefEntity mapToProductRefEntity(ProductRef productRef);
}