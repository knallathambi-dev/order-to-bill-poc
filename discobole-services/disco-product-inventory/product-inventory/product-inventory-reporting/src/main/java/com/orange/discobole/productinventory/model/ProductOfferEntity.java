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
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents a unique product offering stored in the "product-offer" collection in MongoDB.
 * This entity is used to query related product offering data in the ProductOfferReportEntity.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@SuperBuilder
@Document(collection = "product-offer")
public class ProductOfferEntity {

    @Id
    private String id;
    private String productOfferName;
    private String productOfferType;

}
