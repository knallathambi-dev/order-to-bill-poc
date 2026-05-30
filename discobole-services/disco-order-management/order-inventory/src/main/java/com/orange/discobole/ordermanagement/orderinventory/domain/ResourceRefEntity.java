// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.domain;

import lombok.*;

import java.net.URI;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResourceRefEntity {
    private String id;
    private String href;
    private String name;
    private String value;
    private String atBaseType;
    private URI atSchemaLocation;
    private String atType;
    private String atReferredType;
}