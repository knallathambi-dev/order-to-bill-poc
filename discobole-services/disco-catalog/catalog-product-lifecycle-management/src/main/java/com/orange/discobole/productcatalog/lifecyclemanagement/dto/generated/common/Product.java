// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@ApiModel(description = "A product offering procured by a customer or other interested party playing a party role. A product is realized as one or more service(s) and / or resource(s).")
@JsonInclude(NON_EMPTY)
public class Product {

    private String id = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
