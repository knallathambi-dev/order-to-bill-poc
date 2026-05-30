// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.converter.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.productinventory.converter.product.impl.CsvToProductConverter;
import com.orange.discobole.productinventory.converter.product.impl.JsonToProductConverter;
import com.orange.discobole.productinventory.converter.product.impl.ProductToCsvConverter;
import com.orange.discobole.productinventory.converter.product.impl.ProductToJsonConverter;
import com.orange.discobole.productinventory.mapper.ProductMapper;
import com.orange.discobole.productinventory.model.job.enumerate.FileType;
import com.orange.discobole.productinventory.util.filter.FieldsFiltrationUsingMixIn;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
public class FormatConverterFactory {
    private final ProductMapper productMapper;
    private final ObjectMapper mapper;

    private final FieldsFiltrationUsingMixIn fieldsFiltrationUsingMixIn;

    public FormatConverter createConverter(FileType fileType, Set<String> fieldsToInclude) {
        if (fileType == null) {
            throw new IllegalArgumentException("unspecified format");
        }
        switch (fileType) {
            case JSON -> {
                return new ProductToJsonConverter(productMapper, fieldsFiltrationUsingMixIn, fieldsToInclude);
            }
            case CSV -> {
                return new ProductToCsvConverter(productMapper, mapper, fieldsToInclude);
            }
            default -> throw new IllegalArgumentException("invalid format");
        }
    }

    public FormatConverter createConverter(FileType fileType) {
        if (fileType == null) {
            throw new IllegalArgumentException("unspecified format");
        }
        switch (fileType) {
            case JSON -> {
                return new ProductToJsonConverter(productMapper, fieldsFiltrationUsingMixIn);
            }
            case CSV -> {
                return new ProductToCsvConverter(productMapper, mapper);
            }
            default -> throw new IllegalArgumentException("invalid format");
        }
    }

    public StringToProductConverter createStringToProductConverter(FileType fileType) {
        return switch (fileType) {
            case JSON -> new JsonToProductConverter(mapper);
            case CSV -> new CsvToProductConverter(mapper, productMapper);
        };
    }
}