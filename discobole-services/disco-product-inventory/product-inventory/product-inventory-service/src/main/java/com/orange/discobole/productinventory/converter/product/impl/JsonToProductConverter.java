// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.converter.product.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.productinventory.converter.product.StringToProductConverter;
import com.orange.discobole.productinventory.dto.v1.Product;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@AllArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
public class JsonToProductConverter implements StringToProductConverter {

    private final ObjectMapper mapper;

    @Override
    public List<Product> convert(Path filePath) throws IOException {
        return new ArrayList<>(List.of(mapper.readValue(Files.newInputStream(filePath), Product[].class)));
    }
}