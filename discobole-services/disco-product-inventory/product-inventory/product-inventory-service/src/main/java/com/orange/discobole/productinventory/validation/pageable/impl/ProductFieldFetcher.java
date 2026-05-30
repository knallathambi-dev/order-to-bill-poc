// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.validation.pageable.impl;

import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.exception.ProductInventoryException;
import com.orange.discobole.productinventory.validation.pageable.FieldsFetcher;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INVALID_QUERY_STRING_PARAMETER;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.NOT_INCLUDED_IN_PRODUCT_FIELDS;

public class ProductFieldFetcher implements FieldsFetcher {
    @Override
    public Field fetch(String fieldParameter) {
        if (fieldParameter.endsWith(".")) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(), fieldParameter + NOT_INCLUDED_IN_PRODUCT_FIELDS);
        }
        String[] fieldParam = fieldParameter.split("\\.");

        if (fieldParam.length == 0) {
            return null;
        }
        try {
            Field field = Product.class.getDeclaredField(fieldParam[0]);
            for (int i = 1; i < fieldParam.length; i++) {
                if (List.class.isAssignableFrom(field.getType())) {
                    ParameterizedType listType = (ParameterizedType) field.getGenericType();
                    Class<?> elementType = (Class<?>) listType.getActualTypeArguments()[0];
                    if (elementType.isAssignableFrom(StringCharacteristic.class)) {
                        field = getStringCharacteristicField(fieldParam[i]);
                    } else {
                        field = elementType.getDeclaredField(fieldParam[i]);
                    }
                } else if (field.getType().isAssignableFrom(ProductRef.class)) {
                    field = ProductRef.class.getDeclaredField(fieldParam[i]);
                } else if (field.getType().isAssignableFrom(PartyOrPartyRole.class)) {
                    field = getPartyOrPartyRoleField(fieldParam[i]);
                } else {
                    field = field.getType().getDeclaredField(fieldParam[i]);
                }
            }
            return field;
        } catch (NoSuchFieldException e) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(), fieldParameter + NOT_INCLUDED_IN_PRODUCT_FIELDS);
        }
    }

    private static Field getStringCharacteristicField(String fieldParam) throws NoSuchFieldException {
        Field field;
        try {
            field = StringCharacteristic.class.getDeclaredField(fieldParam);
        } catch (NoSuchFieldException ex) {
            field = Characteristic.class.getDeclaredField(fieldParam);
        }
        return field;
    }
    private static Field getPartyOrPartyRoleField(String fieldParam) throws NoSuchFieldException {
        Field field;
        try {
            field = PartyRef.class.getDeclaredField(fieldParam);
        } catch (NoSuchFieldException ex) {
            field = PartyRoleRef.class.getDeclaredField(fieldParam);
        }
        return field;
    }

}
