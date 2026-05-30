// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.policyrule.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductSpecification {
    @JsonProperty("id")
    private String id = null;

    @JsonProperty("lifecycleStatus")
    private String lifecycleStatus = null;

    public ProductSpecification() {
    }

    public ProductSpecification(String productSpecificationId) {
        this.id = productSpecificationId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLifecycleStatus() {
        return lifecycleStatus;
    }

    public void setLifecycleStatus(String lifecycleStatus) {
        this.lifecycleStatus = lifecycleStatus;
    }
}
