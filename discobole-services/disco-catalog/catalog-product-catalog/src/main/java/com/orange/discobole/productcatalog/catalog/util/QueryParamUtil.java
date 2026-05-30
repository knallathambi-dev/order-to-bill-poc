// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.util;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.FacetOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.ObjectUtils;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Utility class for query parameters
 *
 * @author Vivek Singh
 * @since 1.0
 *
 */
public class QueryParamUtil {

    public static final String TYPE = "@type";
    private static final String COUNT = "count";
    public static final String OR_CONDITIONS = "orConditions";
    private static final String ROOT_ELEMENT = "ROOT";

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
                    if(ObjectUtils.isEmpty(value)){
                        continue;
                    }
                    String[] values = value.toString().split(",");
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
        }).toList();
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
        org.springframework.data.mongodb.core.query.Field fields = query.fields();
        // ALWAYS include mandatory fields
        fields.include("_id");
        fields.include(TYPE);
        fields.include("type");     // DB-level discriminator
        fields.include("href");
        for (String field : fieldList)
            fields.include(field);
        query.addCriteria(Criteria.where("_id").in(entityId));
        return query;
    }

    public static <T> Aggregation fetchEntityFilteredFacet(final Map<String, Object> requestParams, Long skip, Long limit,
                                                           String fields, Class<T> className) {

        List<AggregationOperation> baseFilter = new ArrayList<>();
        Criteria criteria = buildCriteria(requestParams);

        if (criteria != null) {
            baseFilter.add(Aggregation.match(criteria));
        }

        SortOperation sort = buildSortOperation(requestParams, className);
        if (sort != null) {
            baseFilter.add(sort);
        }

        List<AggregationOperation> dataPipeline = new ArrayList<>(baseFilter);

        if (skip != null && skip > 0) {
            dataPipeline.add(Aggregation.skip(skip));
        }

        if (limit != null && limit > 0) {
            dataPipeline.add(Aggregation.limit(limit));
        }

        if (fields != null && !fields.isEmpty()) {
            List<String> projectionFields = new ArrayList<>(Arrays.asList(fields.split(",")));

            //  Always include discriminator
            if (!projectionFields.contains(TYPE)) {
                projectionFields.add(TYPE);
            }

            // optional but recommended
            if (!projectionFields.contains("type")) {
                projectionFields.add("type");
            }

            dataPipeline.add(Aggregation.project(projectionFields.toArray(new String[0])));
        }


        if (dataPipeline.isEmpty()) {
            dataPipeline.add(Aggregation.match(new Criteria()));
        }

        List<AggregationOperation> countPipeline = new ArrayList<>(baseFilter);
        countPipeline.add(Aggregation.count().as(COUNT));

        if (countPipeline.isEmpty()) {
            countPipeline.add(Aggregation.match(new Criteria()));
            countPipeline.add(Aggregation.count().as(COUNT));
        }

        FacetOperation facet = Aggregation.facet(dataPipeline.toArray(new AggregationOperation[0])).as("data")
                .and(countPipeline.toArray(new AggregationOperation[0])).as(COUNT);

        return Aggregation.newAggregation(facet);

    }

    public static <T>Aggregation fetchEntityFiltered(final Map<String, Object> requestParams, Long skip, Long limit,
                                                     String fields, Class<T> className) {

        List<AggregationOperation> aggregations = new ArrayList<>();
        Criteria criteria = buildCriteria(requestParams);

        if (criteria != null) {
            aggregations.add(match(criteria));
        }

        SortOperation sortBy = buildSortOperation(requestParams, className);
        if (sortBy != null) {
            aggregations.add(sortBy);
        }

        if (skip != null) {
            aggregations.add(Aggregation.skip(skip));
        }

        if (fields != null) {
            aggregations.add(Aggregation.project(fields.split(",")));
        }

        if (limit != null) {
            aggregations.add(Aggregation.limit(limit));
        }

        return aggregations.isEmpty() ? null : Aggregation.newAggregation(aggregations);
    }


    public static Criteria buildCriteria(Map<String, Object> requestParams) {
        Map<String, Set<Object>> allParams = QueryParamUtil.mapper(requestParams);

        Set<String> orFields = new HashSet<>();
        if (allParams.containsKey(OR_CONDITIONS)) {
            orFields.addAll(allParams.get(OR_CONDITIONS).stream()
                    .map(Object::toString)
                    .collect(Collectors.toSet()));
            allParams.remove(OR_CONDITIONS);
        }

        // Delegate to the new builder class
        return MongoCriteriaBuilder.build(allParams, orFields);
    }

    public static <T> SortOperation buildSortOperation(
            Map<String, Object> requestParams,
            Class<T> className) {

        if (requestParams.containsKey("sort") && requestParams.get("sort") != null) {

            String[] values = requestParams.get("sort").toString().split(",");

            // Skip validation for Document / Object
            if (className == Document.class || className == Object.class) {
                List<Sort.Order> orders = Arrays.stream(values)
                        .map(v -> v.startsWith("-")
                                ? Sort.Order.desc(v.substring(1))
                                : Sort.Order.asc(v))
                        .toList();

                return sort(Sort.by(orders));
            }

            //  Old strict behavior
            Set<Object> sortParams = new LinkedHashSet<>(Arrays.asList(values));
            List<Sort.Order> orders = QueryParamUtil.sortParameters(className, sortParams);
            return sort(Sort.by(orders));
        }
        return null;
    }


    public static  <T> Map<String, Object> fetchEntityMap(Map<String, Object> requestParams, Long skip, Long limit,
                                                          String fields, String entiry, Class<T> className, MongoTemplate mongoTemplate) {

        Aggregation aggregation = QueryParamUtil.fetchEntityFilteredFacet(requestParams, skip, limit, fields, className);

        Document result = mongoTemplate.aggregate(aggregation, entiry, Document.class)
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

    public static Map<String, Object> fetchEntityMap(
            Map<String, Object> requestParams,
            Long skip,
            Long limit,
            String fields,
            String entity,
            MongoTemplate mongoTemplate) {

        Aggregation aggregation =
                QueryParamUtil.fetchEntityFilteredFacet(requestParams, skip, limit, fields, Document.class);

        Document result = mongoTemplate.aggregate(aggregation, entity, Document.class)
                .getUniqueMappedResult();

        Long count = 0L;
        List<Document> data = new ArrayList<>();

        if (result != null && result.containsKey("data")) {
            data = (List<Document>) result.get("data");

            List<Document> countDocs = (List<Document>) result.get("count");
            if (!countDocs.isEmpty()) {
                Object countValue = countDocs.get(0).get("count");
                if (countValue instanceof Number number) {
                    count = number.longValue();
                }
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        response.put("count", count);
        return response;
    }



    public static <T> long fetchCount(Map<String, Object> requestParams, String entity, MongoTemplate mongoTemplate, Class<T> className) {

        Criteria criteria = QueryParamUtil.buildCriteria(requestParams);
        List<AggregationOperation> operations = new ArrayList<>();

        if (criteria != null) {
            operations.add(Aggregation.match(criteria));
        }

        SortOperation sort = QueryParamUtil.buildSortOperation(requestParams, className);
        if (sort != null) {
            operations.add(sort);
        }

        operations.add(Aggregation.count().as(COUNT));

        Aggregation countAggregation = Aggregation.newAggregation(operations);

        Document result = mongoTemplate.aggregate(countAggregation, entity , Document.class)
                .getUniqueMappedResult();
        return result != null ? result.getInteger(COUNT, 0) : 0L;
    }

}
