// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.role.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.jsonwebtoken.Claims;
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

import com.fasterxml.jackson.annotation.JsonProperty;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

/**
 * Utility class for query parameters
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
public class QueryParamUtil {
    private static final String COUNT = "count";

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


    public static List<String> convert(String token) {
        List<String> clientRole = new ArrayList<>();
        // Parse the token without verifying the signature
        Claims claims = getClaimsFromToken(token);
        Object rolesObject = claims.get("resource_access");
        Map<String, Object> resourceAccess = (Map<String, Object>) claims.get("resource_access");
        if (resourceAccess != null) {
            for (Map.Entry<String, Object> entry : resourceAccess.entrySet()) {
                Object clientRoles = entry.getValue();

                if (clientRoles instanceof Map) {
                    // Assuming roles is a List<String> within the clientRoles Map
                    List<String> roles = ((Map<?, ?>) clientRoles).containsKey("roles")
                            ? (List<String>) ((Map<?, ?>) clientRoles).get("roles")
                            : null;
                    clientRole.addAll(roles);
                }
            }
        }
        return clientRole;
    }

    private static Claims getClaimsFromToken(String jwtToken) {
        // JWT format: header.payload.signature
        String[] parts = jwtToken.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT token");
        }
        String payload = parts[1];
        byte[] decodedBytes = java.util.Base64.getUrlDecoder().decode(payload);
        String decodedPayload = new String(decodedBytes);
        return io.jsonwebtoken.Jwts.parserBuilder().build().parseClaimsJwt("." + payload + ".").getBody();
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
            if (!projectionFields.contains("@type")) {
                projectionFields.add("@type");
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

    public static <T> Aggregation fetchEntityFiltered(final Map<String, Object> requestParams, Long skip, Long limit,
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
        Map<String, Set<Object>> params = QueryParamUtil.mapper(requestParams);
        Criteria criteria = new Criteria();
        boolean criteriaInitialized = false;
        Set<Object> orConditions = params.get("orConditions");

        if (orConditions != null && !orConditions.isEmpty()) {
            List<Criteria> orCriteria = new ArrayList<>();
            for (Object x : orConditions) {
                Set<Object> values = params.get(x);
                if (values != null && !values.isEmpty()) {
                        List<Pattern> patterns = values.stream().map(
                                        value -> Pattern.compile(Pattern.quote(value.toString()), Pattern.CASE_INSENSITIVE))
                                .toList();
                        orCriteria.add(Criteria.where((String) x).in(patterns));
                        params.remove((String) x);

                }
            }
            if (!orCriteria.isEmpty()) {
                criteria.orOperator(orCriteria);
                criteriaInitialized = true;
            }
            params.remove("orConditions");
        }
        for (Map.Entry<String, Set<Object>> entry : params.entrySet()) {
            String key = entry.getKey();
            Set<Object> values = entry.getValue();

            if (values == null || values.isEmpty())
                continue;

            if ("involvementRole".equals(key) || "entitlement._id".equals(key) || "_id".equals(key)) {
                criteria = applyPatternCriteria(criteria, key, values, criteriaInitialized);
            }
            /* =========================================================
             * START: ONLY CHANGE — handle type / @type case-insensitive
             * ========================================================= */
            else if ("type".equals(key) || "@type".equals(key)) {

                List<Pattern> patterns = values.stream()
                        .map(v -> Pattern.compile(
                                "^" + Pattern.quote(v.toString()) + "$",
                                Pattern.CASE_INSENSITIVE))
                        .toList();

                if (criteriaInitialized) {
                    criteria.and(key).in(patterns);
                } else {
                    criteria = Criteria.where(key).in(patterns);
                }
            }
            /* =========================================================
             *  END: ONLY CHANGE
             * ========================================================= */
            else {
                criteria = applyDefaultCriteria(criteria, key, values, criteriaInitialized);
            }
            criteriaInitialized = true;
        }

        return criteriaInitialized ? criteria : null;
    }

    public static Criteria applyDateTimeCriteria(Criteria criteria, String key, Set<Object> values,
                                                 boolean isStartDate) {
        OffsetDateTime dateTime = OffsetDateTime.parse(values.iterator().next().toString(),
                DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        Date date = Date.from(dateTime.toInstant());

        return isStartDate ? criteria.and(key).gt(date) : criteria.and(key).lt(date);
    }

    public static Criteria applyPatternCriteria(Criteria criteria, String key, Set<Object> values,
                                                boolean criteriaInitialized) {
        List<Pattern> patterns = values.stream()
                .map(value -> Pattern.compile(Pattern.quote(value.toString()), Pattern.CASE_INSENSITIVE)).toList();
        return criteriaInitialized ? criteria.and(key).in(patterns) : Criteria.where(key).in(patterns);
    }

    public static Criteria applyDefaultCriteria(Criteria criteria, String key, Set<Object> values,
                                                boolean criteriaInitialized) {
        List<Object> valueList = new ArrayList<>(values);
        return criteriaInitialized ? criteria.and(key).in(valueList) : Criteria.where(key).in(valueList);
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


    public static <T> Map<String, Object> fetchEntityMap(Map<String, Object> requestParams, Long skip, Long limit,
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

        Document result = mongoTemplate.aggregate(countAggregation, entity, Document.class)
                .getUniqueMappedResult();
        return result != null ? result.getInteger(COUNT, 0) : 0L;
    }

}
