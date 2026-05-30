// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.model;

import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.util.Objects;

@Builder
@Setter
@Getter
@NoArgsConstructor
@FieldNameConstants
@AllArgsConstructor
public class ProductRelationshipEntity {

    private String relationshipType;

    private ProductRefEntity product;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductRelationshipEntity that)) {
            return false;
        }
        return Objects.equals(relationshipType, that.relationshipType) && Objects.equals(product, that.product);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
