// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@SuperBuilder
@Document(collection = "product-offer-report")
@CompoundIndex(def = "{'date': 1, 'productOfferId': 1}")  // Compound index for date and dayOfYear
public class ProductOfferReportEntity extends StatusReportingFields {
    private ObjectId id;
    private String productOfferId;
    private String productOfferName;
    private String productOfferType;

    public ProductOfferReportEntity(StatusReportingFields p) {
        super(p);
    }
}
