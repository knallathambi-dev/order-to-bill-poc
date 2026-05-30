// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.service.impl;

import com.orange.discobole.ordermanagement.orderinventory.constant.FilterOperatorsEnum;
import com.orange.discobole.ordermanagement.orderinventory.domain.ProductOrderStateType;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.PartyRef;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.PartyRefOrPartyRoleRef;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.ordermanagement.orderinventory.exception.ProductOrderInventoryException;
import com.orange.discobole.ordermanagement.orderinventory.exception.model.BusinessException;
import com.orange.discobole.ordermanagement.orderinventory.service.FilterQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import static com.orange.discobole.ordermanagement.orderinventory.constant.Constant.*;
import static com.orange.discobole.ordermanagement.orderinventory.constant.ErrorCodeEnum.INVALID_QUERY_STRING_PARAMETER;
import static java.lang.String.format;

@Service
@Slf4j
public class FilterQueryServiceImpl implements FilterQueryService {

    private static final Pattern SPACE_VALIDATION_PATTERN = Pattern.compile(SPACE_REGEX);
    private static final ConcurrentHashMap<String, Field> REFLECTION_FIELD_CACHE = new ConcurrentHashMap<>(128);
    private static final ConcurrentHashMap<String, String[]> PARSED_FIELDS_CACHE = new ConcurrentHashMap<>(64);
    private static final ConcurrentHashMap<String, String> FIELD_NAME_TRANSFORMATION_CACHE = new ConcurrentHashMap<>(128);

    @Override
    public String[] extractAndCacheFields(String fieldsQueryParameter) {
        if (fieldsQueryParameter == null || fieldsQueryParameter.equalsIgnoreCase(NULL)) {
            log.debug("Fields parameter is null, returning all fields");
            return new String[0];
        }

        if (fieldsQueryParameter.equalsIgnoreCase(NONE)) {
            log.debug("Fields parameter is 'none', returning only id and href");
            return new String[]{ID_FIELD, HREF_FIELD};
        }

        String[] cachedFieldArray = PARSED_FIELDS_CACHE.get(fieldsQueryParameter);
        if (cachedFieldArray != null) {
            log.debug("Retrieved fields from cache: [{}]", String.join(", ", cachedFieldArray));
            return cachedFieldArray;
        }

        String[] parsedFieldArray = parseAndValidateFields(fieldsQueryParameter);
        PARSED_FIELDS_CACHE.put(fieldsQueryParameter, parsedFieldArray);

        log.debug("Parsed and cached fields: [{}]", String.join(", ", parsedFieldArray));

        return parsedFieldArray;
    }

    @Override
    public void validateFieldsToFetch(String... fieldPaths) {
        for (String fieldPath : fieldPaths) {
            validateFieldPathWithCache(fieldPath);
        }
    }

    @Override
    public Query createOptimizedQuery(MultiValueMap<String, Object> queryParameters, String[] projectionFields) {
        Query mongoQuery = new Query();

        if (projectionFields.length > 0) {
            mongoQuery.fields().include(projectionFields);
        }

        validateAllParameterValues(queryParameters);
        List<Criteria> queryCriteriaList = buildMongoDBCriteriaList(queryParameters);

        if (!queryCriteriaList.isEmpty()) {
            mongoQuery.addCriteria(new Criteria().andOperator(queryCriteriaList.toArray(new Criteria[0])));
        }

        return mongoQuery;
    }

    private String[] parseAndValidateFields(String fieldsQueryParameter) {
        validateNoSpaces(fieldsQueryParameter);
        StringBuilder transformedFields = transformAtSymbolsInFieldNames(fieldsQueryParameter);
        ensureMandatoryFieldsPresent(fieldsQueryParameter, transformedFields);

        String[] fieldArray = Arrays.stream(transformedFields.toString().split(","))
                .distinct()
                .toArray(String[]::new);

        for (String fieldPath : fieldArray) {
            validateFieldPathWithCache(fieldPath);
        }

        return fieldArray;
    }

    private StringBuilder transformAtSymbolsInFieldNames(String fieldsQueryParameter) {
        String cachedTransformation = FIELD_NAME_TRANSFORMATION_CACHE.get(fieldsQueryParameter);
        if (cachedTransformation != null) {
            return new StringBuilder(cachedTransformation);
        }

        StringBuilder transformed = performAtSymbolTransformation(fieldsQueryParameter);
        String transformedString = transformed.toString();
        FIELD_NAME_TRANSFORMATION_CACHE.put(fieldsQueryParameter, transformedString);

        return new StringBuilder(transformedString);
    }

    private StringBuilder performAtSymbolTransformation(String fieldsQueryParameter) {
        if (!fieldsQueryParameter.contains("@")) {
            return new StringBuilder(fieldsQueryParameter);
        }

        String[] segmentsSplitByAt = fieldsQueryParameter.split("@");
        if (segmentsSplitByAt.length == 1) {
            return new StringBuilder(fieldsQueryParameter);
        }

        StringBuilder result = new StringBuilder(segmentsSplitByAt[0]);

        for (int i = 1; i < segmentsSplitByAt.length; i++) {
            result.append("at");
            String segment = segmentsSplitByAt[i];
            if (!segment.isEmpty()) {
                result.append(Character.toUpperCase(segment.charAt(0)));
                if (segment.length() > 1) {
                    result.append(segment.substring(1));
                }
            }
        }

        return result;
    }

    private void ensureMandatoryFieldsPresent(String originalFields, StringBuilder transformedFields) {
        addFieldIfAbsent(originalFields, ID_FIELD, transformedFields);
        addFieldIfAbsent(originalFields, HREF_FIELD, transformedFields);
        addFieldIfAbsent(originalFields, TYPE_FIELD, transformedFields);
    }

    private void addFieldIfAbsent(String originalFields, String fieldToAdd, StringBuilder fieldsBuilder) {
        if (!originalFields.contains(fieldToAdd)) {
            fieldsBuilder.append(",").append(fieldToAdd);
        }
    }

    private Field validateFieldPathWithCache(String fieldPath) {
        return REFLECTION_FIELD_CACHE.computeIfAbsent(fieldPath, this::validateFieldPathUsingReflection);
    }

    private Field validateFieldPathUsingReflection(String fieldPath) {
        String[] pathSegments = fieldPath.split("\\.");
        if (pathSegments.length == 0) {
            return null;
        }

        Class<?> currentClass = ProductOrder.class;
        Field currentField = null;

        for (String segmentName : pathSegments) {
            try {
                currentField = currentClass.getDeclaredField(segmentName);

                if (List.class.isAssignableFrom(currentField.getType())) {
                    ParameterizedType genericListType = (ParameterizedType) currentField.getGenericType();
                    currentClass = (Class<?>) genericListType.getActualTypeArguments()[0];
                } else if (currentField.getType().isAssignableFrom(PartyRefOrPartyRoleRef.class)) {
                    currentClass = PartyRef.class;
                } else {
                    currentClass = currentField.getType();
                }
            } catch (NoSuchFieldException exception) {
                throw new ProductOrderInventoryException(
                        HttpStatus.BAD_REQUEST,
                        INVALID_QUERY_STRING_PARAMETER.getCode(),
                        INVALID_QUERY_STRING_PARAMETER.getStatus(),
                        format(BusinessException.NOT_INCLUDED_IN_PRODUCT_ORDER_FIELDS, fieldPath)
                );
            }
        }

        return currentField;
    }

    private void validateAllParameterValues(MultiValueMap<String, Object> queryParameters) {
        for (Map.Entry<String, List<Object>> parameterEntry : queryParameters.entrySet()) {
            List<Object> parameterValues = parameterEntry.getValue();

            if (parameterValues == null || parameterValues.isEmpty()) {
                throw new ProductOrderInventoryException(
                        HttpStatus.BAD_REQUEST,
                        INVALID_QUERY_STRING_PARAMETER.getCode(),
                        INVALID_QUERY_STRING_PARAMETER.getStatus(),
                        format(BusinessException.THE_VALUE_OF_THE_KEY_SHOULD_NOT_BE_NULL,
                                parameterEntry.getKey())
                );
            }

            for (Object value : parameterValues) {
                if (value != null) {
                    validateNoSpaces(value.toString());
                }
            }
        }
    }

    private void validateNoSpaces(String stringToValidate) {
        if (SPACE_VALIDATION_PATTERN.matcher(stringToValidate).find()) {
            throw new ProductOrderInventoryException(
                    HttpStatus.BAD_REQUEST,
                    INVALID_QUERY_STRING_PARAMETER.getCode(),
                    INVALID_QUERY_STRING_PARAMETER.getStatus(),
                    BusinessException.SUPPLEMENT_SPACES_CANNOT_BE_INCLUDED_ON_FIELDS
            );
        }
    }

    private List<Criteria> buildMongoDBCriteriaList(MultiValueMap<String, Object> queryParameters) {
        List<Criteria> criteriaList = new ArrayList<>(queryParameters.size());

        for (Map.Entry<String, List<Object>> parameterEntry : queryParameters.entrySet()) {
            if (!parameterEntry.getValue().isEmpty()) {
                Criteria criteria = createCriteriaForParameter(
                        parameterEntry.getKey(),
                        parameterEntry.getValue()
                );
                criteriaList.add(criteria);
            }
        }

        return criteriaList;
    }

    private Criteria createCriteriaForParameter(String parameterKey, List<Object> parameterValues) {
        String transformedFieldName = transformAtSymbolsInFieldNames(parameterKey).toString();
        FilterOperatorsEnum filterOperator = extractFilterOperator(parameterKey);

        if (filterOperator == null) {
            return parameterValues.size() > 1
                    ? createInListCriteria(transformedFieldName, parameterValues)
                    : createEqualityCriteria(transformedFieldName, parameterValues.get(0));
        } else {
            return createComparisonCriteria(transformedFieldName, parameterValues.get(0).toString(), filterOperator);
        }
    }

    private Criteria createInListCriteria(String fieldName, List<Object> values) {
        List<Object> typedValues = new ArrayList<>(values.size());

        for (Object rawValue : values) {
            Object typedValue = parseValueToCorrectType(rawValue.toString(), fieldName);
            typedValues.add(typedValue);
        }

        return Criteria.where(fieldName).in(typedValues);
    }

    private Criteria createEqualityCriteria(String fieldName, Object rawValue) {
        Object typedValue = parseValueToCorrectType(rawValue.toString(), fieldName);
        return Criteria.where(fieldName).is(typedValue);
    }

    private Criteria createComparisonCriteria(String fieldNameWithOperator,
                                              String rawValue,
                                              FilterOperatorsEnum operator) {
        String fieldNameWithoutOperator = removeOperatorSuffix(fieldNameWithOperator);
        Object typedValue = parseValueToCorrectType(rawValue, fieldNameWithoutOperator);

        return switch (operator) {
            case GT -> Criteria.where(fieldNameWithoutOperator).gt(typedValue);
            case GTE -> Criteria.where(fieldNameWithoutOperator).gte(typedValue);
            case LT -> Criteria.where(fieldNameWithoutOperator).lt(typedValue);
            case LTE -> Criteria.where(fieldNameWithoutOperator).lte(typedValue);
        };
    }

    private Object parseValueToCorrectType(String stringValue, String fieldName) {
        Field field = validateFieldPathWithCache(fieldName);
        if (field == null) {
            return stringValue;
        }

        Class<?> fieldType = field.getType();
        return convertStringToFieldType(stringValue, fieldType);
    }

    private Object convertStringToFieldType(String stringValue, Class<?> targetType) {
        return switch (targetType.getSimpleName()) {
            case "Boolean" -> Boolean.parseBoolean(stringValue);
            case "Float" -> Float.parseFloat(stringValue);
            case "Integer" -> Integer.parseInt(stringValue);
            case "Instant" -> Instant.parse(stringValue);
            case "ProductOrderStateType" -> ProductOrderStateType.fromValue(stringValue);
            case "String" -> stringValue;
            default -> throw new ProductOrderInventoryException(
                    HttpStatus.BAD_REQUEST,
                    INVALID_QUERY_STRING_PARAMETER.getCode(),
                    INVALID_QUERY_STRING_PARAMETER.getStatus(),
                    format(BusinessException.CANNOT_PARSE_FIELD_VALUE_TO_TYPE, stringValue, targetType.getSimpleName())
            );
        };
    }

    private FilterOperatorsEnum extractFilterOperator(String parameterKey) {
        if (parameterKey.endsWith(".gte")) {
            return FilterOperatorsEnum.GTE;
        }
        if (parameterKey.endsWith(".lte")) {
            return FilterOperatorsEnum.LTE;
        }
        if (parameterKey.endsWith(".gt")) {
            return FilterOperatorsEnum.GT;
        }
        if (parameterKey.endsWith(".lt")) {
            return FilterOperatorsEnum.LT;
        }
        return null;
    }

    private String removeOperatorSuffix(String fieldNameWithOperator) {
        int lastDotIndex = fieldNameWithOperator.lastIndexOf('.');
        if (lastDotIndex > 0) {
            String suffix = fieldNameWithOperator.substring(lastDotIndex);
            if (suffix.equals(".gte") || suffix.equals(".lte") ||
                    suffix.equals(".gt") || suffix.equals(".lt")) {
                return fieldNameWithOperator.substring(0, lastDotIndex);
            }
        }
        return fieldNameWithOperator;
    }
}