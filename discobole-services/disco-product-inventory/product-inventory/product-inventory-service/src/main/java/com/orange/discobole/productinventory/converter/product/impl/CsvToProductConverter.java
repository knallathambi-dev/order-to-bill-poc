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
import com.orange.discobole.productinventory.converter.product.StringToProductConverter;
import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.exception.ProductInventoryException;
import com.orange.discobole.productinventory.mapper.ProductMapper;
import com.orange.discobole.productinventory.model.ProductEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
@AllArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
public class CsvToProductConverter implements StringToProductConverter {
    private final ObjectMapper mapper;
    private final ProductMapper productMapper;

    public static void setNestedValue(Map<String, Object> map, String key, Object value, int arrayIndex) {
        if (value == null) {
            return;
        }

        String[] keys = key.split("\\.");
        Map<String, Object> currentMap = map;
        Class<?> currentClass = ProductEntity.class;

        try {
            for (int i = 0; i < keys.length - 1; i++) {
                String currentKey = keys[i];
                Field field = currentClass.getDeclaredField(currentKey);

                if (List.class.isAssignableFrom(field.getType())) {
                    Type genericType = field.getGenericType();
                    if (genericType instanceof ParameterizedType listType) {
                        currentClass = (Class<?>) listType.getActualTypeArguments()[0];
                    } else {
                        throw new ProductInventoryException("Unable to determine list element type for field: " + currentKey);
                    }

                    List<Object> list = (List<Object>) currentMap.computeIfAbsent(currentKey, k -> new ArrayList<>());
                    ensureListSize(list, arrayIndex);
                    if (list.get(arrayIndex) == null) {
                        list.set(arrayIndex, new HashMap<String, Object>());
                    }
                    currentMap = (Map<String, Object>) list.get(arrayIndex);
                } else {
                    currentClass = field.getType();
                    currentMap = (Map<String, Object>) currentMap.computeIfAbsent(currentKey, k -> new HashMap<>());
                }
            }

            String lastKey = keys[keys.length - 1];
            Field lastField = currentClass.getDeclaredField(lastKey);

            if (List.class.isAssignableFrom(lastField.getType())) {
                List<Object> list = (List<Object>) currentMap.computeIfAbsent(lastKey, k -> new ArrayList<>());
                list.add(arrayIndex, value);
            } else {
                currentMap.put(lastKey, value);
            }

        } catch (NoSuchFieldException e) {
            throw new ProductInventoryException("Invalid field: " + key, e);
        }
    }

    private static void ensureListSize(List<Object> list, int index) {
        while (list.size() <= index) {
            list.add(null);
        }
    }

    private static Map<String, Object> buildProductJson(String[] headers, List<String[]> batch) {
        Map<String, Object> jsonMap = new HashMap<>();
        int arraysIndex = 0;
        for (String[] values : batch) {
            for (int j = 0; j < headers.length; j++) {
                if (j < values.length && !values[j].isEmpty()) {
                    setNestedValue(jsonMap, headers[j], values[j], arraysIndex);
                }
            }
            arraysIndex++;
        }

        return jsonMap;
    }
    private static boolean isEffectiveNull(String csvCell) {
        return csvCell == null || csvCell.trim().isEmpty();
    }

    private static String[] parseCsvLine(String line) {
        return Arrays.stream(line.split(";", -1))
                .map(s -> s.replaceAll("^\"(.*)\"$", "$1")) // Remove quotes only if both are present
                .toArray(String[]::new);
    }


    @Override
    public List<Product> convert(Path filePath) throws IOException {
        Path tempJsonFile = Files.createTempFile(UUID.randomUUID() + "-products", ".json");
        try {
            // Convert CSV to JSON and write to a temp file
            parseCsvToJsonFile(filePath, tempJsonFile);

            // Read JSON from the temp file and convert to List<Product>
            return readProductsFromJsonFile(tempJsonFile);
        } catch (Exception e) {
            Files.deleteIfExists(tempJsonFile);
            throw e;
        }

    }

    private void parseCsvToJsonFile(Path csvFilePath, Path jsonFilePath) throws IOException {


        final String[] headers = extractHeaders(csvFilePath);
        final int expectedColumnCount = headers.length; // Expected number of columns is based on the header

        try (BufferedWriter writer = Files.newBufferedWriter(jsonFilePath);
             Stream<String> lines = Files.lines(csvFilePath).skip(1)) {

            List<String[]> batch = new ArrayList<>();
            lines.forEach(line -> {
                String[] values = parseCsvLine(line);

                if (values.length != expectedColumnCount) {
                    log.error("Invalid number of columns at line: {}. Expected: {} but got: {} (skipping line)", line, expectedColumnCount, values.length);
                    return; // Skip this line or handle it accordingly
                }
                if (!isEffectiveNull(values[0]) && !batch.isEmpty()) { // New product detected, id field is not null
                    try {
                        writer.write(mapper.writeValueAsString(buildProductJson(headers, batch)));
                        writer.newLine();
                    } catch (IOException e) {
                        log.error("Error writing JSON to file", e);
                    }
                    batch.clear();
                }
                batch.add(values);
            });
            if (!batch.isEmpty()) {
                writer.write(mapper.writeValueAsString(buildProductJson(headers, batch)));
            }
        }
    }

    private String[] extractHeaders(Path csvFilePath) {
        String[] tempHeaders = new String[0];
        try (BufferedReader reader = Files.newBufferedReader(csvFilePath)) {
            return parseCsvLine(reader.readLine()); // Read the first line
        } catch (IOException e) {
            log.error("Error reading CSV file", e);
        }
        return tempHeaders;
    }


    private List<Product> readProductsFromJsonFile(Path jsonFilePath) throws IOException {
        List<Product> productList = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(jsonFilePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                ProductEntity productEntity = mapper.readValue(line, ProductEntity.class);
                Product dtoWithFullMapping = productMapper.toDtoWithFullMapping(productEntity);
                productList.add(dtoWithFullMapping);
            }
        }
        return productList;
    }

}