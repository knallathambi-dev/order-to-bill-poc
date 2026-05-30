package com.orange.discobole.productcatalog.administration.util;

// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.FacetOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * Utility class for query parameters
 *
 * @author Vivek Singh
 * @since 1.0
 *
 */
public class QueryParamUtil {
    private static final String COUNT = "count";
    private QueryParamUtil() {
        throw new IllegalStateException("Utility class");
    }
    public static <T> SortOperation buildSort(Map<String, Object> requestParams, Class<T> className) {
        if (requestParams.containsKey("sort") && requestParams.get("sort") != null) {
            String[] values = requestParams.get("sort").toString().split(",");
            Set<String> sortParams = new LinkedHashSet<>(Arrays.asList(values));
            List<Order> orders = QueryParamUtil.sortParameters(className, sortParams);

            if (!orders.isEmpty()) {
                return Aggregation.sort(Sort.by(orders));
            }
        }
        return null;
    }

    public static List<Order> sortParameters(Class<?> type, Set<String> sortParams) {
        List<Order> orders = new ArrayList<>();
        for (String param : sortParams) {
            String sortParamValue = param.trim();
            boolean desc = false;
            if (sortParamValue.startsWith("-")) {
                desc = true;
                sortParamValue = sortParamValue.substring(1);
            }
            if (isValid(type, sortParamValue)) {
                orders.add(desc ? new Order(Sort.Direction.DESC, sortParamValue)
                        : new Order(Sort.Direction.ASC, sortParamValue));
            } else {
                throw new InvalidParameterException("Enter a valid key name: " + sortParamValue);
            }
        }
        return orders;
    }

    public static boolean isValid(Class<?> type, String propertyName) {
        for (Field field : type.getDeclaredFields()) {
            if (field.getName().equals(propertyName)) {
                return true;
            }
        }
        return false;
    }
    public static  <T> Map<String, Object> fetchEntityMap(Map<String, Object> requestParams, String entity, Class<T> className, MongoTemplate mongoTemplate) {

        Aggregation aggregation = QueryParamUtil.fetchEntityFilteredFacet(requestParams, className);

        Document result = mongoTemplate.aggregate(aggregation, entity, Document.class)
                .getUniqueMappedResult();
        Long count = 0L;
        List<T> data = new ArrayList<>();
        if (result != null && result.containsKey("data")) {
            List<Document> rawData = (List<Document>) result.get("data");
            for (Document doc : rawData) {
                data.add(mongoTemplate.getConverter().read(className, doc));
            }
            List<Document> countDocs = (List<Document>) result.get(COUNT);
            if (!countDocs.isEmpty()) {
                Object countValue = countDocs.get(0).get(COUNT);
                if (countValue instanceof Number number) {
                    count = number.longValue();
                }
            }
        }
        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        response.put(COUNT, count);
        return response;
    }

    public static <T> Aggregation fetchEntityFilteredFacet(
            final Map<String, Object> requestParams,
            Class<T> className) {

        List<AggregationOperation> baseFilter = new ArrayList<>();

        Criteria criteria = buildCriteria(requestParams);
        if (criteria != null) {
            baseFilter.add(Aggregation.match(criteria));
        }

        SortOperation sortOperation = QueryParamUtil.buildSort(requestParams, className);
        if (sortOperation != null) {
            baseFilter.add(sortOperation);
        }

        List<AggregationOperation> dataPipeline = new ArrayList<>(baseFilter);
        if (dataPipeline.isEmpty()) {
            dataPipeline.add(Aggregation.match(new Criteria()));
        }

        List<AggregationOperation> countPipeline = new ArrayList<>(baseFilter);
        countPipeline.add(Aggregation.count().as(COUNT));

        FacetOperation facet = Aggregation
                .facet(dataPipeline.toArray(new AggregationOperation[0])).as("data")
                .and(countPipeline.toArray(new AggregationOperation[0])).as(COUNT);

        return Aggregation.newAggregation(facet);
    }

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

    public static Criteria buildCriteria(Map<String, Object> requestParams) {
        Map<String, Set<Object>> params = QueryParamUtil.mapper(requestParams);
        Criteria criteria = new Criteria();
        boolean criteriaInitialized = false;
        for (Map.Entry<String, Set<Object>> entry : params.entrySet()) {
            String key = entry.getKey();
            Set<Object> values = entry.getValue();
            if (values == null || values.isEmpty())
                continue;
            criteria = applyDefaultCriteria(criteria, key, values, criteriaInitialized);
                criteriaInitialized = true;
        }

        return criteriaInitialized ? criteria : null;
    }

    public static Criteria applyDefaultCriteria(Criteria criteria, String key, Set<Object> values,
                                                boolean criteriaInitialized) {
        List<Object> valueList = new ArrayList<>(values);
        if (valueList.size() == 1 && valueList.get(0) instanceof String str && ("true".equalsIgnoreCase(str) || "false".equalsIgnoreCase(str))) {
                Boolean boolValue = Boolean.parseBoolean(str);
                return criteriaInitialized
                        ? criteria.and(key).is(boolValue)
                        : Criteria.where(key).is(boolValue);
        }
        return criteriaInitialized ? criteria.and(key).in(valueList) : Criteria.where(key).in(valueList);
    }

}

