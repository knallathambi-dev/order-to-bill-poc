// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.mapper.product.order;

import com.orange.discobole.ordermanagement.orderinventory.dto.v1.PartyRef;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.PartyRefOrPartyRoleRef;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.PartyRoleRef;
import com.orange.discobole.productinventory.dto.v1.PartyOrPartyRole;
import org.mapstruct.Mapper;

/**
 * Mapper interface for converting various types of `PartyOrPartyRole` objects
 * from order management to the corresponding DTOs in product inventory.
 */
@Mapper(componentModel = "spring")
public interface PartyOrPartyRoleMapper {

    // Generic mapping
    default PartyOrPartyRole mapPartyToDto(PartyRefOrPartyRoleRef partyRefOrPartyRoleRef) {
        if (partyRefOrPartyRoleRef instanceof PartyRef partyRef) {
            return mapPartyRefToDto(partyRef);
        } else if (partyRefOrPartyRoleRef instanceof PartyRoleRef partyRoleRef) {
            return mapPartyRoleRefToDto(partyRoleRef);
        }
        return null;
    }

    // Single-value mappings
    com.orange.discobole.productinventory.dto.v1.PartyRef mapPartyRefToDto(PartyRef partyRef);

    com.orange.discobole.productinventory.dto.v1.PartyRoleRef mapPartyRoleRefToDto(PartyRoleRef partyRoleRef);
}