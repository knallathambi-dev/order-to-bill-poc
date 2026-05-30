// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.service.mapper;

import com.orange.discobole.ordermanagement.orderinventory.domain.PartyRefEntity;
import com.orange.discobole.ordermanagement.orderinventory.domain.PartyRefOrPartyRoleRefEntity;
import com.orange.discobole.ordermanagement.orderinventory.domain.PartyRoleRefEntity;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.PartyRef;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.PartyRefOrPartyRoleRef;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.PartyRoleRef;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link PartyRefOrPartyRoleRefEntity} and its DTO {@link PartyRefOrPartyRoleRef}.
 * Provides mapping methods between entities and DTOs for party reference and party role reference objects.
 */
@Mapper(componentModel = "spring")
public interface PartyRefOrPartyRoleRefMapper {

    /**
     * Maps a {@link PartyRefOrPartyRoleRefEntity} to its DTO equivalent.
     *
     * @param entity the source entity to be mapped
     * @return the mapped {@link PartyRefOrPartyRoleRef}, or {@code null} if the input is {@code null}
     */
    default PartyRefOrPartyRoleRef mapToDto(PartyRefOrPartyRoleRefEntity entity) {
        if (entity instanceof PartyRefEntity partyRefEntity) {
            return mapToPartyRef(partyRefEntity);
        } else if (entity instanceof PartyRoleRefEntity partyRoleRefEntity) {
            return mapToPartyRoleRef(partyRoleRefEntity);
        }
        return null;
    }

    /**
     * Maps a {@link PartyRefOrPartyRoleRef} DTO to its entity equivalent.
     *
     * @param dto the source DTO to be mapped
     * @return the mapped {@link PartyRefOrPartyRoleRefEntity}, or {@code null} if the input is {@code null}
     */
    default PartyRefOrPartyRoleRefEntity mapToEntity(PartyRefOrPartyRoleRef dto) {
        if (dto instanceof PartyRef partyRef) {
            return mapToPartyRefEntity(partyRef);
        } else if (dto instanceof PartyRoleRef partyRoleRef) {
            return mapToPartyRoleRefEntity(partyRoleRef);
        }
        return null;
    }

    /**
     * Maps a {@link PartyRefEntity} to its DTO {@link PartyRef}.
     *
     * @param entity the source entity to be mapped
     * @return the mapped {@link PartyRef}
     */
    PartyRef mapToPartyRef(PartyRefEntity entity);

    /**
     * Maps a {@link PartyRoleRefEntity} to its DTO {@link PartyRoleRef}.
     *
     * @param entity the source entity to be mapped
     * @return the mapped {@link PartyRoleRef}
     */
    PartyRoleRef mapToPartyRoleRef(PartyRoleRefEntity entity);

    /**
     * Maps a {@link PartyRef} DTO to its entity {@link PartyRefEntity}.
     *
     * @param dto the source DTO to be mapped
     * @return the mapped {@link PartyRefEntity}
     */
    PartyRefEntity mapToPartyRefEntity(PartyRef dto);

    /**
     * Maps a {@link PartyRoleRef} DTO to its entity {@link PartyRoleRefEntity}.
     *
     * @param dto the source DTO to be mapped
     * @return the mapped {@link PartyRoleRefEntity}
     */
    PartyRoleRefEntity mapToPartyRoleRefEntity(PartyRoleRef dto);
}