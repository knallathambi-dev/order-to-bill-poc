// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.util;

import com.orange.discobole.productinventory.model.job.JobSpecificationEntity;
import com.orange.discobole.productinventory.validation.pageable.impl.ProductFieldFetcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Objects;

import static com.orange.discobole.productinventory.util.QueryUtils.parseQueryToMultiValueMap;

@Slf4j
public class ApiUtil {

    private ApiUtil() {
    }

    public static void addListToMap(String fieldName, MultiValueMap<String, Object> multiValueMap, List<?> fields) {
        if (Objects.nonNull(fields)) {
            multiValueMap.addAll(fieldName, fields);
        }
    }

    public static <T> void addValueToMap(String fieldName, MultiValueMap<String, Object> multiValueMap, T fieldValue) {
        if (Objects.nonNull(fieldValue)) {
            multiValueMap.add(fieldName, fieldValue);
        }
    }
    public static boolean isValidObjectId(String fieldValue) {
        return fieldValue != null && fieldValue.length() == 24 && fieldValue.matches("[0-9a-fA-F]+");
    }

    public static MultiValueMap<String, Object> getQueryMap(JobSpecificationEntity jobSpecification) {
        return parseQueryToMultiValueMap(jobSpecification.getQuery(), new ProductFieldFetcher());
    }


}