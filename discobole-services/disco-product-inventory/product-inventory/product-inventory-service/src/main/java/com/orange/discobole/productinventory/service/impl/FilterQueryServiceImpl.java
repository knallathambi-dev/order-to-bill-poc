// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service.impl;

import com.orange.discobole.productinventory.constant.QueryFields;
import com.orange.discobole.productinventory.dto.PageableTMF;
import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.dto.v1.ProductRelationshipType;
import com.orange.discobole.productinventory.dto.v1.RelatedPartyOrPartyRole;
import com.orange.discobole.productinventory.enumerate.FilterOperatorsEnum;
import com.orange.discobole.productinventory.exception.ProductInventoryException;
import com.orange.discobole.productinventory.service.FilterQueryService;
import com.orange.discobole.productinventory.validation.pageable.FieldsFetcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.orange.discobole.productinventory.constant.Constant.*;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INVALID_QUERY_STRING_PARAMETER;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.*;
import static java.lang.String.format;

@Service
@Slf4j
public class FilterQueryServiceImpl implements FilterQueryService {


    private static void validateSpaces(String fieldsQueryParam) {
        Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(fieldsQueryParam);
        boolean containsSpaces = matcher.find();
        if (containsSpaces) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(),
                    SUPPLEMENT_SPACES_CANNOT_BE_INCLUDED_ON_FIELDS);
        }
    }

    private static void addFieldIfNotExists(String fieldsQueryParam, String requiredField, StringBuilder stringBuilder) {
        if (!fieldsQueryParam.contains(requiredField)) {
            stringBuilder.append(",");
            stringBuilder.append(requiredField);
        }
    }

    private static StringBuilder modifyStringWithAt(String fieldsQueryParam) {
        List<String> splitAroundAt = Arrays.asList(fieldsQueryParam.split("@"));
        for (int i = 1; i < splitAroundAt.size(); i++) {
            splitAroundAt.set(i, StringUtils.capitalize(splitAroundAt.get(i)));
        }
        return new StringBuilder(String.join("at", splitAroundAt));
    }

    private static String removeFilterOperators(String operator) {
        return operator.replaceAll("(.gte|.lte)$", "");
    }

    private static FilterOperatorsEnum getFilterOperator(String key) {
        if (key.endsWith(".gte")) {
            return FilterOperatorsEnum.GTE;
        }
        if (key.endsWith(".lte")) {
            return FilterOperatorsEnum.LTE;
        }
        return null;
    }

    public static Map<String, Object> getProductCharacteristicsAttributes(MultiValueMap<String, Object> attributes) {
        Map<String, Object> resultMap = new HashMap<>();
        List<String> keys = new ArrayList<>();
        for (String key : attributes.keySet()) {
            if (key.startsWith(PRODUCT_CHARACTERISTIC)) {
                resultMap.put(key, attributes.getFirst(key));
                keys.add(key);
            }
        }
        keys.forEach(attributes::remove);
        return resultMap;
    }

    private static void setValueByTypeToQueryParameterMap(MultiValueMap<String, Object> attributes, List<Criteria> andCriteriaList) {
        for (Map.Entry<String, List<Object>> entry : attributes.entrySet()) {
            String filterKey = entry.getKey();
            List<Object> value = entry.getValue();

            if (!value.isEmpty()) {
                handleFilterKey(filterKey, value, andCriteriaList);
            }
        }
    }

    private static void handleFilterKey(String filterKey, List<Object> value, List<Criteria> andCriteriaList) {
        FilterOperatorsEnum filterOperator = getFilterOperator(filterKey);

        if (filterOperator == null) {
            processExactMatch(filterKey, value, andCriteriaList);
        } else {
            processFilterOperator(filterKey, value, andCriteriaList, filterOperator);
        }
    }

    private static void processExactMatch(String filterKey, List<Object> value, List<Criteria> andCriteriaList) {
        List<Object> exactMatchValues = new ArrayList<>();

        for (Object o : value) {
            if (o instanceof String v && v.contains("*")) {
                String regexPattern = getRegexPattern(v);
                andCriteriaList.add(Criteria.where(filterKey).regex(regexPattern, "i"));
            } else {
                exactMatchValues.add(o);
            }
        }

        if (!CollectionUtils.isEmpty(exactMatchValues)) {
            andCriteriaList.add(Criteria.where(filterKey).in(exactMatchValues));
        }
    }

    private static void processFilterOperator(String filterKey, List<Object> value, List<Criteria> andCriteriaList, FilterOperatorsEnum filterOperator) {
        String fieldParameter = removeFilterOperators(filterKey);
        Object fieldValue = value.get(0);

        try {
            if (filterOperator == FilterOperatorsEnum.GTE) {
                andCriteriaList.add(Criteria.where(fieldParameter).gte(fieldValue));
            } else if (filterOperator == FilterOperatorsEnum.LTE) {
                andCriteriaList.add(Criteria.where(fieldParameter).lte(fieldValue));
            }
        } catch (IllegalArgumentException | DateTimeParseException e) {
            throw new ProductInventoryException(
                    HttpStatus.BAD_REQUEST,
                    INVALID_QUERY_STRING_PARAMETER.getCode(),
                    INVALID_QUERY_STRING_PARAMETER.getStatus(),
                    INCORRECT_VALUE_TYPE + filterKey
            );
        }
    }

    private static String escapeSpecialCharacters(String input) {
        String specialCharacters = "[]\\^$.|?+(){}";
        StringBuilder escapedString = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (specialCharacters.indexOf(c) != -1) {
                escapedString.append('\\');
            }
            escapedString.append(c);
        }
        return escapedString.toString();
    }

    private static String getRegexPattern(String value) {
        value = escapeSpecialCharacters(value);
        return "^" + value.replace("*", ".*") + "$";
    }

    private static Map<String, Object> addProductCharacteristicsCriteriaIfExists(MultiValueMap<String, Object> attributes, List<Criteria> andCriteriaList) {
        Map<String, Object> productCharacteristicsAttributes = getProductCharacteristicsAttributes(attributes);
        if (!productCharacteristicsAttributes.isEmpty()) {
            List<Criteria> productCharacteristicCriteria = new ArrayList<>();
            for (Map.Entry<String, Object> entry : productCharacteristicsAttributes.entrySet()) {
                String filterKey = entry.getKey();
                Object value = entry.getValue();
                String fieldName = filterKey.substring(filterKey.lastIndexOf(".") + 1);
                if (value instanceof String v && v.contains("*")) {
                    v = getRegexPattern(v);
                    productCharacteristicCriteria.add(Criteria.where(fieldName).regex(v, "i"));
                } else {
                    productCharacteristicCriteria.add(Criteria.where(fieldName).is(value));
                }
            }
            if (productCharacteristicsAttributes.get("productCharacteristic.value") != null) {
                productCharacteristicCriteria.add(Criteria.where("valueType").is("string"));
            }
            andCriteriaList.add(Criteria.where(PRODUCT_CHARACTERISTIC).elemMatch(new Criteria().andOperator(productCharacteristicCriteria)));
        }
        return productCharacteristicsAttributes;
    }

    private static Boolean addIsRootCriteriaIfExists(MultiValueMap<String, Object> attributes, List<Criteria> andCriteriaList) {
        Boolean isRoot = (Boolean) attributes.getFirst(QueryFields.IS_ROOT);
        if (Objects.nonNull(isRoot)) {
            attributes.remove(QueryFields.IS_ROOT);
            if (Boolean.TRUE.equals(isRoot)) {
                andCriteriaList.add(Criteria.where(QueryFields.IS_ROOT_PRODUCT).is(true));
            } else {
                andCriteriaList.add(Criteria.where(QueryFields.PRODUCT_RELATIONSHIP_RELATIONSHIP_TYPE).in(ProductRelationshipType.ROOTPRODUCT.getValue()));
            }
        }
        return isRoot;
    }

    private static void setPaginationAndSortParamsToQuery(Query query, Integer offset, Integer limit, List<String> sort) {
        if (Objects.nonNull(offset)) {
            query.skip(offset);
        }
        if (Objects.nonNull(limit)) {
            query.limit(limit);
        }
        if (sort != null && !sort.isEmpty()) {
            Sort.Order[] orders = sort.stream()
                    .map(field -> field.startsWith("-") ? Sort.Order.desc(field.substring(1)) : Sort.Order.asc(field))
                    .toArray(Sort.Order[]::new);
            query.with(Sort.by(orders));
        }
    }

    @Override
    public String[] extractFields(String fieldsQueryParam, String... requiredFields) {
        log.debug("Extracting fields: {}", fieldsQueryParam);
        if (Objects.isNull(fieldsQueryParam) || NULL.equalsIgnoreCase(fieldsQueryParam)) {
            log.debug("Extracted fields: [empty]");
            return new String[0];
        }
        if (fieldsQueryParam.contains(PRODUCT_CHARACTERISTIC) && !fieldsQueryParam.contains("productCharacteristic.atType")) {
            fieldsQueryParam = (fieldsQueryParam.endsWith(",")) ? fieldsQueryParam + "productCharacteristic.atType" : fieldsQueryParam + ",productCharacteristic.atType";
        }

        fieldsQueryParam = addRelatedPartyDotPartyOrPartyRoleTypeDiscriminatorIfMissing(fieldsQueryParam);

        validateSpaces(fieldsQueryParam);
        if (!NONE.equalsIgnoreCase(fieldsQueryParam)) {
            StringBuilder fieldsStringBuilder = modifyStringWithAt(fieldsQueryParam);
            for (String requiredField : requiredFields) {
                addFieldIfNotExists(fieldsQueryParam, requiredField, fieldsStringBuilder);
            }
            log.debug("Extracted fields: [{}]", fieldsStringBuilder);
            return Arrays.stream(fieldsStringBuilder.toString().split(",")).distinct().toArray(String[]::new);
        } else {
            log.debug("Extracted fields: " + Arrays.toString(requiredFields));
            return requiredFields;
        }
    }

    private String addRelatedPartyDotPartyOrPartyRoleTypeDiscriminatorIfMissing(String fieldsQueryParam) {
        final String relatedPartyDotPartyOrPartyRoleJsonPathWithTrailingDot = Product.Fields.relatedParty + "." + RelatedPartyOrPartyRole.Fields.partyOrPartyRole + ".";
        final String relatedPartyDotPartyOrPartyRoleAtTypeJsonPath = relatedPartyDotPartyOrPartyRoleJsonPathWithTrailingDot + "@type";
        if (fieldsQueryParam.contains(relatedPartyDotPartyOrPartyRoleJsonPathWithTrailingDot) && !fieldsQueryParam.contains(relatedPartyDotPartyOrPartyRoleAtTypeJsonPath)) {
            return fieldsQueryParam + (fieldsQueryParam.endsWith(",") ? relatedPartyDotPartyOrPartyRoleAtTypeJsonPath : "," + relatedPartyDotPartyOrPartyRoleAtTypeJsonPath);
        } else {
            return fieldsQueryParam;
        }
    }

    @Override
    public void validateFieldsToFetch(FieldsFetcher fieldsFetcher, String... fieldArray) {
        for (String field : fieldArray) {
            fieldsFetcher.fetch(field);
        }
    }

    @Override
    public Query createQuery(MultiValueMap<String, Object> attributes) {
        List<Criteria> andCriteriaList = new ArrayList<>();
        handleFilterWithListOfValues(attributes);
        var removedProductCharacteristic = addProductCharacteristicsCriteriaIfExists(attributes, andCriteriaList);
        var isRoot = addIsRootCriteriaIfExists(attributes, andCriteriaList);
        setValueByTypeToQueryParameterMap(attributes, andCriteriaList);
        for (Map.Entry<String, Object> entry : removedProductCharacteristic.entrySet()) {
            String k = entry.getKey();
            Object v = entry.getValue();
            attributes.add(k, v);
        }
        if (Objects.nonNull(isRoot)) {
            attributes.add(QueryFields.IS_ROOT, isRoot);
        }
        Query query = new Query();
        query.addCriteria(!andCriteriaList.isEmpty() ? new Criteria().andOperator(andCriteriaList) : new Criteria());
        return query;
    }

    private void handleFilterWithListOfValues(MultiValueMap<String, Object> attributes) {
        attributes.forEach((key, values) -> {
            if (Objects.isNull(values)) {
                throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(),
                        format(THE_VALUE_OF_THE_KEY_SHOULD_NOT_BE_NULL, key));
            }
            values.forEach(v -> {
                String fieldValue = v.toString();
                validateSpaces(fieldValue);
                log.debug("Adding criteria: key={}, value={}", key, values);
            });
        });
    }

    @Override
    public Query createAndValidateQuery(PageableTMF pageable, FieldsFetcher fieldsFetcher, String[] requiredFields) {
        String[] fieldArray = extractFields(pageable.getFields(), requiredFields);
        validateFieldsToFetch(fieldsFetcher, fieldArray);

        fieldArray = mapRelatedPartyDTOFieldsToEntityFields(fieldArray);

        Query query = createQuery(pageable.getFilter());
        query.fields().include(fieldArray);
        pageable.validatePaginationParameters();
        setPaginationAndSortParamsToQuery(query, pageable.getOffset(), pageable.getLimit(), pageable.getSort());
        return query;
    }

    private String[] mapRelatedPartyDTOFieldsToEntityFields(String[] fieldArray) {
        final String relatedPartyDotPartyOrPartyRoleJsonPath = Product.Fields.relatedParty + "." + RelatedPartyOrPartyRole.Fields.partyOrPartyRole;
        return Arrays.stream(fieldArray)
                .map(field -> field.startsWith(relatedPartyDotPartyOrPartyRoleJsonPath) ? field.replace(relatedPartyDotPartyOrPartyRoleJsonPath, Product.Fields.relatedParty) : field)
                .toArray(String[]::new);
    }
}
