// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.converter.product.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.productinventory.converter.product.FormatConverter;
import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.mapper.ProductMapper;
import com.orange.discobole.productinventory.model.ProductEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
@AllArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
public class ProductToCsvConverter implements FormatConverter {
    private final ProductMapper productMapper;
    private final ObjectMapper mapper;
    private Set<String> fieldsToInclude;


    @SneakyThrows
    public static void writeProductCsv(String csvProduct, OutputStreamWriter writer) {
        writer.write(csvProduct);
    }

    private static boolean isAllowedTypes(Class<?> fieldType) {
        return EXPORT_TYPES_TO_INCLUDE
                .stream()
                .anyMatch(c -> c == fieldType);
    }

    private static boolean arrayItemExists(String field, Map<String, List<Object>> attributeValuesMap, int i) {
        return field != null && attributeValuesMap.get(field).size() > i;
    }

    private List<String> getProductFieldsList() {
        List<String> result = new ArrayList<>();
        //TODO: change ProductEntity with product as we shouldn't expose internal ProductEntity to external export file,
        // also we need to account for this change in CsvToProductConverter:readProductsFromJsonFile
        extractFields(ProductEntity.class, "", result);
        return result;
    }

    private void extractFields(Class<?> clazz, String prefix, List<String> result) {
        List<Field> fields = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> CollectionUtils.isEmpty(fieldsToInclude) || fieldsToInclude.contains(prefix + field.getName()))
                .filter(field -> !EXPORT_FIELDS_TO_EXCLUDE.contains(prefix + field.getName()))
                .toList();

        for (Field field : fields) {
            Class<?> fieldType = field.getType();
            String fieldName = prefix + field.getName();

            // Handle primitives, String, basic types, and enums
            if (fieldType.isPrimitive() || isAllowedTypes(fieldType) || fieldType.isEnum()) {
                result.add(fieldName);
            } else if (List.class.isAssignableFrom(fieldType)) {
                // Handle ArrayList with generic type
                ParameterizedType listType = (ParameterizedType) field.getGenericType();
                Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];

                if (!listClass.isPrimitive()) {
                    extractFields(listClass, fieldName + ".", result); // Using "." to denote list elements
                }
            } else {
                // Recursively extract fields of complex fields (objects)
                extractFields(fieldType, fieldName + ".", result);
            }
            // If none of the above conditions match, it does nothing and skips to the next iteration
        }
    }

    @Override
    public void convert(Stream<ProductEntity> productStream, OutputStreamWriter writer) throws IOException {
        writer.write(getCsvHeader());
        writer.write(LINE_SEPARATOR);
        List<String> productFieldsList = getProductFieldsList();

        productStream
                .map(productMapper::toDtoWithProductIdOnly)
                .map(s -> convertProductToCsvLine(s, productFieldsList))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEachOrdered(jsonProduct -> writeProductCsv(jsonProduct, writer));
        writer.flush();
    }

    private Optional<String> convertProductToCsvLine(Product product, List<String> productFieldsList) {
        try {
            Map<String, List<Object>> attributeValuesMap = new HashMap<>();
            StringBuilder csvProduct = new StringBuilder();

            for (String field : productFieldsList) {
                attributeValuesMap.put(field, new ArrayList<>());
                extractFieldsValues(field, field, product, attributeValuesMap);
            }

            int maxSize = productFieldsList.stream()
                    .mapToInt(s -> attributeValuesMap.get(s).size())
                    .max()
                    .orElse(0);

            for (int i = 0; i < maxSize; i++) {
                for (String field : productFieldsList) {
                    csvProduct.append(arrayItemExists(field, attributeValuesMap, i)
                            ? mapper.writeValueAsString(attributeValuesMap.get(field).get(i))
                            : "").append(";");
                }
                csvProduct.deleteCharAt(csvProduct.length() - 1);  // Delete the last ";"
                csvProduct.append(LINE_SEPARATOR);
            }

            return Optional.of(csvProduct.toString());
        } catch (Exception e) {
            log.error("Error converting product to CSV line:", e);
            return Optional.empty();
        }
    }

    private String sanitize(String input) { // fix RCE vulnerability
        if (input == null) {
            return "";
        }

        String trimmed = input.trim();
        if (trimmed.startsWith("=") || trimmed.startsWith("+") ||
                trimmed.startsWith("-") || trimmed.startsWith("@") ||
                trimmed.contains("\t") || trimmed.contains("\r")) {
            return "'" + input;
        }
        return input;
    }

    @SneakyThrows
    private void extractFieldsValues(String fullFieldPath, String nestedField, Object o, Map<String, List<Object>> attributeValuesMap) {
        //relationshipType
        String[] fullFieldPathArray = nestedField.split("\\.");
        if (fullFieldPathArray.length == 0) {
            return;
        }
        if (fullFieldPathArray.length == 1) {
            Object value = getValue(nestedField, o);
            if (value == null) {
                return;
            }

            if (value instanceof String stringValue) {
                value = sanitize(stringValue);
            }

            attributeValuesMap.get(fullFieldPath).add(value);
            return;
        }
        Object value = getValue(fullFieldPathArray[0], o);
        if (value == null) {
            return;
        }
        String nestedFieldWithoutParent = String.join(".", Arrays.copyOfRange(fullFieldPathArray, 1, fullFieldPathArray.length));
        if (List.class.isAssignableFrom(value.getClass())) {
            List<Object> arrayValues = (List<Object>) value;
            for (Object v : arrayValues) {
                extractFieldsValues(fullFieldPath, nestedFieldWithoutParent, v, attributeValuesMap);
            }
            return;
        }
        extractFieldsValues(fullFieldPath, nestedFieldWithoutParent, value, attributeValuesMap);
    }

    @SneakyThrows
    private Object getValue(String attributeName, Object item) {
        Method[] fieldMethods = item.getClass().getMethods();
        Optional<Method> any = Arrays.stream(fieldMethods)
                .filter(method -> isGetter(method) && getPropertyName(method).equals(attributeName))
                .findAny();
        if (any.isPresent()) {
            return any.get().invoke(item);
        }
        return null;
    }

    private boolean isGetter(Method method) {
        // Assuming you already have an implementation for this
        return method.getName().startsWith("get") && method.getParameterCount() == 0;
    }

    private String getPropertyName(Method method) {
        // Assuming you already have an implementation for this
        String name = method.getName().substring(3);
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }


    private String getCsvHeader() {
        List<String> fields = getProductFieldsList();
        StringBuilder csvHeader = new StringBuilder();
        for (String field : fields) {
            csvHeader.append("\"").append(field).append("\";");
        }
        // Remove the trailing comma
        if (!csvHeader.isEmpty()) {
            csvHeader.deleteCharAt(csvHeader.length() - 1);
        }
        return csvHeader.toString();
    }
}
