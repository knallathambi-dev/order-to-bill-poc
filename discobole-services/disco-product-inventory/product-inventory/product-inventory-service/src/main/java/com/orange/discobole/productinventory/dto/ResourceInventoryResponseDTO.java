// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class ResourceInventoryResponseDTO {


    @JsonProperty("id")
    private String id;
    @JsonProperty("href")
    private String href;
    @JsonProperty("value")
    private String value;
    @JsonProperty("serialNumber")
    private String serialNumber;
    @JsonProperty("name")
    private String name;
    @JsonProperty("resourceStatus")
    private String resourceStatus;
    @JsonProperty("resourceSpecification")
    private ResourceSpecification resourceSpecification;
}