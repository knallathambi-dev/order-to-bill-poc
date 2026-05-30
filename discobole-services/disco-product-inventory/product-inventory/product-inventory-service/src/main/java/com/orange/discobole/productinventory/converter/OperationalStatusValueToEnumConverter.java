// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.converter;

import com.orange.discobole.productinventory.dto.v1.ProductOperationalStatusType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class OperationalStatusValueToEnumConverter implements Converter<String, ProductOperationalStatusType> {


    @Override
    public ProductOperationalStatusType convert(String source) {
        return ProductOperationalStatusType.fromValue(source);
    }
}
