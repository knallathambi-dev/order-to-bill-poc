// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Field;

import java.net.URI;

@SuperBuilder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PartyRoleRefEntity extends PartyRefOrPartyRoleRefEntity {
    @Field("id")
    private String id;
    private String partyId;
    private String partyName;
    private String atBaseType;
    private URI atSchemaLocation;
    private String href;
    private String name;
    private String atReferredType;
}