// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.validation.pageable.impl;

import com.orange.discobole.productinventory.exception.ProductInventoryException;
import com.orange.discobole.productinventory.validation.pageable.FieldsFetcher;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INVALID_QUERY_STRING_PARAMETER;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.NOT_INCLUDED_IN_PRODUCT_FIELDS;

public class FieldFetcher<T> implements FieldsFetcher {

    private final Class<T> entityClass;

    public FieldFetcher(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public Field fetch(String fieldParameter) {
        if (fieldParameter.endsWith(".")) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_QUERY_STRING_PARAMETER.getCode(),
                    INVALID_QUERY_STRING_PARAMETER.getStatus(), fieldParameter + NOT_INCLUDED_IN_PRODUCT_FIELDS);
        }

        String mappedFieldParameter = getEntityFieldEquivalent(fieldParameter);
        String[] fieldParam = mappedFieldParameter.split("\\.");
        if (fieldParam.length == 0) {
            return null;
        }
        try {
            Field field = entityClass.getDeclaredField(fieldParam[0]);
            for (int i = 1; i < fieldParam.length; i++) {
                if (List.class.isAssignableFrom(field.getType())) {
                    Type genericType = field.getGenericType();
                    if (genericType instanceof ParameterizedType) {
                        ParameterizedType listType = (ParameterizedType) genericType;
                        Class<?> elementType = (Class<?>) listType.getActualTypeArguments()[0];
                        field = elementType.getDeclaredField(fieldParam[i]);
                    }
                } else {
                    field = field.getType().getDeclaredField(fieldParam[i]);
                }
            }
            return field;
        } catch (NoSuchFieldException e) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_QUERY_STRING_PARAMETER.getCode(),
                    INVALID_QUERY_STRING_PARAMETER.getStatus(), fieldParameter + NOT_INCLUDED_IN_PRODUCT_FIELDS);
        }
    }

    private String getEntityFieldEquivalent(String fieldParameter) {
        // Add logic specific to entity field mappings here if needed.
        if (fieldParameter.equals("contentType")) {
            return "fileType";
        }
        if (fieldParameter.equals("jobSpecification.id")) {
            return "jobSpecification";
        }
        return fieldParameter;
    }
}
