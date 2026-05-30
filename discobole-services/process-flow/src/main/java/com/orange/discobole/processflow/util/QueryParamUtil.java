// Software Name: process-flow
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt
// Software description: The TMF701 ProcessFlow API allows management of business process definition and execution. \\r\\n\\r\\nThis API is used as a Java library in the Order Capture Management and Orchestration Delivery Fallout Management APIs.\\r\\n\\r\\n\\r\\n

package com.orange.discobole.processflow.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class for query parameters
 *
 * @author Vivek Singh
 * @since 1.0
 */
public class QueryParamUtil {

    /**
     * Instantiates a new QueryParamUtil.
     */
    private QueryParamUtil() {
    }

    /**
     * Utility method to map the parameters with the passed value/range of values.
     *
     * @param requestParams map of string to object where Strings are parameter and
     *                      object can be suitable datatype
     * @return map of String to set of object
     */
    public static Map<String, Set<Object>> mapper(Map<String, Object> requestParams) {
        Map<String, Set<Object>> params = new HashMap<>();
        for (Map.Entry<String, Object> entry : requestParams.entrySet()) {
            Set<Object> set = new HashSet<>();
            Object value = entry.getValue();
            if (null != value && !entry.getKey().equals("sort")) {
                if (value instanceof String) {
                    String[] values = value.toString().split(",");
                    for (int i = 0; i < values.length; i++) {
                        values[i] = URLDecoder.decode(values[i], StandardCharsets.UTF_8);
                    }
                    set.addAll(Arrays.asList(values));
                } else {
                    set.add(value);
                }
                params.put(entry.getKey(), set);
            }
        }
        return params;

    }

    /**
     * Method to sort the product offering in an ordered fashion of
     * ascending/descending direction.
     *
     * @param sortParams set of parameters.
     * @return List of ordered product offering.
     */

    public static List<Order> sortParameters(Class<?> type, Set<Object> sortParams) {
        return sortParams.stream().map(paramKey -> {
            String sortParamValue = (String) paramKey;
            boolean desc = false;
            boolean asc = false;
            if (sortParamValue.startsWith("-")) {
                desc = true;
                sortParamValue = sortParamValue.substring(1);
            } else if (sortParamValue.startsWith("+")) {
                asc = true;
                sortParamValue = sortParamValue.substring(1);
            }
            if (isValid(type, sortParamValue)) {
                if (desc) {
                    return new Order(Sort.Direction.DESC, sortParamValue).ignoreCase();
                } else if (asc) {
                    return new Order(Sort.Direction.ASC, sortParamValue).ignoreCase();
                } else {
                    return new Order(Sort.Direction.ASC, sortParamValue).ignoreCase();
                }
            } else {
                throw new InvalidParameterException("Enter a valid key name ");
            }
        }).collect(Collectors.toList());
    }

    /**
     * Utility method to validate the key used for sorting the product offering.
     *
     * @param sortParamValue sort is done on the basis of this value (key)
     * @return boolean
     */

    public static boolean isValid(Class<?> type, String sortParamValue) {
        Field[] fields = type.getDeclaredFields();
        Set<String> fieldSet = new HashSet<>();

        for (Field field : fields) {
            if (field.isAnnotationPresent(JsonProperty.class)) {
                String annotationValue = field.getAnnotation(JsonProperty.class).value();
                fieldSet.add(annotationValue);
            }
        }

        return fieldSet.contains(sortParamValue);

    }

    public static Query prepareFieldsFilter(List<String> fieldList, String entityId) {
        Query query = new Query();
        org.springframework.data.mongodb.core.query.Field fields = query.fields().include("href");
        for (String field : fieldList)
            fields.include(field);
        query.addCriteria(Criteria.where("_id").in(entityId));
        return query;
    }

}
