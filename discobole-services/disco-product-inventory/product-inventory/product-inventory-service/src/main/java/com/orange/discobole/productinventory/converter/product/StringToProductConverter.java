// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.converter.product;

import com.orange.discobole.productinventory.dto.v1.Product;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface StringToProductConverter {
    Map<String, String> PRODUCT_ENTITY_DTO_FIELDS_MAPPINGS = Map.of("atType", "@type");

    List<Product> convert(Path filePath) throws IOException;
}
