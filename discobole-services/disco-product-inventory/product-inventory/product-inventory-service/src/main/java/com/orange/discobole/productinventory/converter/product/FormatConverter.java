// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.converter.product;

import com.orange.discobole.productinventory.model.ProductEntity;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Stream;

public interface FormatConverter {
    String LINE_SEPARATOR = "\n";
    List<String> EXPORT_FIELDS_TO_EXCLUDE = List.of(
            "href",
            "billingAccount.href",
            "agreement.href",
            "place.href",
            "productOffering.href",
            "productOrderItem.productOrderHref",
            "productPrice.billingAccount.href",
            "productPrice.productOfferingPrice.href",
            "productPrice.productPriceAlteration.productOfferingPrice.href",
            "productCharacteristic.productCharacteristicRelationships",
            "productSpecification.href",
            "realizingResource.href",
            "realizingService.href",
            "relatedParty.href",
            "orderDate",
            "statusChange",
            "operationalStatusChange",
            "productPrice.price",
            "productPrice.productPriceAlteration.price"
    );
    List<Class<?>> EXPORT_TYPES_TO_INCLUDE = List.of(
            String.class,
            Long.class,
            Integer.class,
            Double.class,
            Float.class,
            Boolean.class,
            Character.class,
            ObjectId.class,
            OffsetDateTime.class,
            LocalDate.class
    );

    void convert(Stream<ProductEntity> productStream, OutputStreamWriter writer) throws IOException;

}