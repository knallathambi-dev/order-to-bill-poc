// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.codec;

import com.orange.discobole.ordermanagement.orderinventory.domain.ProductOrderStateType;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class ProductOrderStateTypeReaderConverter implements Converter<String, ProductOrderStateType> {

    @Override
    public ProductOrderStateType convert(@NotNull String source) {
        return ProductOrderStateType.fromValue(source);
    }
}