// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.commons.dto.product.stock;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductStock {
    private String id;
    private String href;
    private List<RelatedEntity> relatedEntity;
    private List<ReserveProductStockItem> reserveProductStockItem;
    private ValidFor validFor;
    private String reserveProductStockState;
    private String productStockStatusType;
    private StockedProduct stockedProduct;
    private Resource resource;
    @JsonProperty("@type")
    private String type;
}