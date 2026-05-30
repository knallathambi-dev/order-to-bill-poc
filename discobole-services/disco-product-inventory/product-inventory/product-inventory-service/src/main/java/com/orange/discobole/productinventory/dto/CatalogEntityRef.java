// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.dto;


import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;


@Data
public class CatalogEntityRef {

    private String id;
    private String supportEntity;
    private String version;
    @JsonProperty("@baseType")
    private String atBaseType;
    @JsonProperty("@schemaLocation")
    private String atSchemaLocation;
    @JsonProperty("@type")
    @JsonAlias("atType")
    private String atType;

    private CatalogProductSpecification productSpecification;
    private List<CatalogBundledProductOffering> bundledProductOffering;
}