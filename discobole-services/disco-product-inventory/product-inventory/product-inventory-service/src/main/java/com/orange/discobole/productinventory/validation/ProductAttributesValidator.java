// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.validation;

import com.orange.discobole.productinventory.exception.ProductInventoryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.orange.discobole.productinventory.constant.Constant.NOT_PATCHABLE_ATTRIBUTES;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.METHOD_NOT_ALLOWED;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.PATCH_METHOD_NOT_SUPPORTED_BY_THAT_RESOURCE;

@Slf4j
public class ProductAttributesValidator {
    private ProductAttributesValidator() {
    }

    public static <T> void validateForUnpatchableAttributes(T existingProduct, T product) {
        Optional<Map.Entry<String, String>> invalidAttribute = NOT_PATCHABLE_ATTRIBUTES.entrySet().stream().filter(attribute -> {
            try {
                Object attributeValue = getAttributeValue(product, attribute.getValue());
                Object existingAttributeValue = getAttributeValue(existingProduct, attribute.getValue());
                return attributeValue != null && !Objects.equals(attributeValue, existingAttributeValue);
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }).findAny();
        invalidAttribute.ifPresent(attribute -> {
            throw new ProductInventoryException(HttpStatus.METHOD_NOT_ALLOWED, METHOD_NOT_ALLOWED.getCode(), METHOD_NOT_ALLOWED.getStatus(),
                    String.format(PATCH_METHOD_NOT_SUPPORTED_BY_THAT_RESOURCE, attribute.getKey()));
        });
    }

    private static Object getAttributeValue(Object product, String attributeName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        try {
            Method getterMethod = product.getClass().getMethod("get" + capitalizeFirstLetter(attributeName));
            return getterMethod.invoke(product);
        } catch (NoSuchMethodException e) {
            // Handle the case where no getter is found
            throw new NoSuchMethodException("Getter method for attribute " + attributeName + " not found");
        }
    }

    private static String capitalizeFirstLetter(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
