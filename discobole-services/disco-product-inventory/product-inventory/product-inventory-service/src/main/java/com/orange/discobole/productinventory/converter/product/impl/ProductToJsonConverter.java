// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.converter.product.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orange.discobole.productinventory.converter.product.FormatConverter;
import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.mapper.ProductMapper;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.util.filter.FieldsFiltrationUsingMixIn;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
@AllArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
public class ProductToJsonConverter implements FormatConverter {
    private final ProductMapper productMapper;

    private final FieldsFiltrationUsingMixIn fieldsFiltrationUsingMixIn;

    private Set<String> fieldsToInclude;

    @SneakyThrows
    public static void writeProductJson(String jsonProduct, OutputStreamWriter writer, AtomicBoolean isFirstProduct) {
        if (!isFirstProduct.getAndSet(false)) {
            writer.write(",");
        }
        writer.write(jsonProduct);
    }

    @Override
    public void convert(Stream<ProductEntity> productStream, OutputStreamWriter writer) throws IOException {
        AtomicBoolean isFirstProduct = new AtomicBoolean(true);
        writer.write("[");
        productStream
                .map(productMapper::toDtoWithProductIdOnly)
                .map(this::convertProductToJsonObjectMapper)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEachOrdered(jsonProduct -> writeProductJson(jsonProduct, writer, isFirstProduct));
        writer.write("]");
        writer.flush();
    }

    private Optional<String> convertProductToJsonObjectMapper(Product product) {
        try {
            return Optional.of(fieldsFiltrationUsingMixIn.toJsonString(product, fieldsToInclude));
        } catch (JsonProcessingException e) {
            log.error("Error converting product to JSON: {}", e.getMessage());
            return Optional.empty();
        }
    }
}