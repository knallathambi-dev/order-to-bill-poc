// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Objects;

/**
 * RelatedProductOrderItem (ProductOrder item) .The product order item which triggered product creation/change/termination.
 **/

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RelatedProductOrderItemEntity {
    private String orderItemAction;
    private String orderItemId;
    private String productOrderHref;
    @Indexed
    private String productOrderId;
    private String role;
    @JsonProperty("@referredType")
    @JsonAlias("atReferredType")
    private String atReferredType;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RelatedProductOrderItemEntity)) {
            return false;
        }
        RelatedProductOrderItemEntity that = (RelatedProductOrderItemEntity) o;
        return Objects.equals(orderItemId, that.orderItemId) && Objects.equals(productOrderId, that.productOrderId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}


