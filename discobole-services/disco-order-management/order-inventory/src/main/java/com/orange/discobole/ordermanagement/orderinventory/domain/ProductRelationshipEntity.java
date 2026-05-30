// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.domain;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.*;

import java.net.URI;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class ProductRelationshipEntity {
    private String id;
    private ProductRefOrValueEntity product;
    private String relationshipType;
    private String atBaseType;
    private URI atSchemaLocation;
    private String atType;
    private String href;
    private String name;
    private String atReferredType;
}