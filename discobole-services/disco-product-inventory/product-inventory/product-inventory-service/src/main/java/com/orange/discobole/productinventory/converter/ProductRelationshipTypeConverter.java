// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.converter;

import com.orange.discobole.productinventory.dto.v1.ProductRelationshipType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ProductRelationshipTypeConverter implements Converter<String, ProductRelationshipType> {


    @Override
    public ProductRelationshipType convert(String source) {
        return ProductRelationshipType.fromValue(source);
    }
}
