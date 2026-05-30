// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.service.mapper;

import java.util.List;

/**
 * Contract for a generic DTO-to-entity and entity-to-DTO mapper.
 *
 * @param <D> - Data Transfer Object type parameter.
 * @param <E> - Entity type parameter.
 */
public interface EntityMapper<D, E> {

    /**
     * Converts a DTO to an Entity.
     *
     * @param dto the Data Transfer Object to convert.
     * @return the converted Entity.
     */
    E mapToEntity(D dto);

    /**
     * Converts a list of DTOs to a list of Entities.
     *
     * @param dtoList the list of Data Transfer Objects to convert.
     * @return the list of converted Entities.
     */
    List<E> mapToEntityList(List<D> dtoList);

    /**
     * Converts an Entity to a DTO.
     *
     * @param entity the Entity to convert.
     * @return the converted Data Transfer Object.
     */
    D mapToDto(E entity);

    /**
     * Converts a list of Entities to a list of DTOs.
     *
     * @param entityList the list of Entities to convert.
     * @return the list of converted Data Transfer Objects.
     */
    List<D> mapToDtoList(List<E> entityList);
}