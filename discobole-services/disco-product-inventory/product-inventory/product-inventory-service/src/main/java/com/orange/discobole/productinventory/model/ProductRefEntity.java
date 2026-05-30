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
import org.bson.types.ObjectId;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder
public class ProductRefEntity {
    private ObjectId id;
    private String name;
    private String atType;

    public ProductRefEntity(String id) {
        if (!ObjectId.isValid(id)) {
            throw new IllegalArgumentException("Invalid ObjectId string: " + id);
        }
        this.id = new ObjectId(id);
    }

    public ProductRefEntity(ObjectId id) {
        this.id = id;
    }
}
