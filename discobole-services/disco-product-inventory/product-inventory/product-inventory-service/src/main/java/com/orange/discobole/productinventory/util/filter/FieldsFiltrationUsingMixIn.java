// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.util.filter;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.orange.discobole.productinventory.dto.v1.Product;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
public class FieldsFiltrationUsingMixIn {
    private final ObjectMapper objectMapper;

    public String toJsonString(final Product product, final Set<String> fieldsToInclude) throws JsonProcessingException {
        final ObjectMapper customMapper = this.objectMapper.copy();
        if (fieldsToInclude != null && !fieldsToInclude.isEmpty()) {
            // Create a filter to include only the specified fields
            final SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter.filterOutAllExcept(fieldsToInclude);
            final FilterProvider filterProvider = new SimpleFilterProvider().addFilter("productFilter", simpleBeanPropertyFilter);
            customMapper.addMixIn(Product.class, ProductMixIn.class); // Add the MixIn to apply the filter
            customMapper.setFilterProvider(filterProvider);
        }

        return customMapper.writeValueAsString(product);
    }


    @JsonFilter("productFilter")
    private abstract static class ProductMixIn {
    }
}