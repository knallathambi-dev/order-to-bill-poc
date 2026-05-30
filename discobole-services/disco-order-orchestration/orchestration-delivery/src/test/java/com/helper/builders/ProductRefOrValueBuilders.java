// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.helper.builders;

import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;

import java.util.List;

public class ProductRefOrValueBuilders {

    public static ProductRefOrValue.ProductRefOrValueBuilder productRefOrValueDTOBuilder() {
        return Product.builder()
                .isBundle(false)
                .productSpecification(ProductSpecificationRef.builder()
                        .id("df32402e-ceb9-4467-aafd-fec0bbff3124")
                        .name("Mobile Line")
                        .atType("ProductSpecificationRef")
                        .build())
                .productCharacteristic(List.of(
                                StringCharacteristic.builder().name("MSISDN").value("4152797439").valueType("string").build()
                        )
                )
                .realizingResource(List.of(ResourceRef.builder().id("122").atType("Resource").build()));
    }
}
