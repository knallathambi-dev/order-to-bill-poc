// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.util;

import com.orange.discobole.productinventory.validation.pageable.FieldsFetcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Slf4j
public class QueryUtils {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    private QueryUtils() {
    }

    public static MultiValueMap<String, Object> parseQueryToMultiValueMap(String query, FieldsFetcher fieldsFetcher) {
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        if (query == null || query.isEmpty()) {
            return multiValueMap;
        }
        String[] filters = query.split("&");

        for (String filter : filters) {
            String[] parts = filter.split("=", 2);
            if (parts.length == 2) {
                String key = parts[0].trim();
                String value = parts[1].trim();
                // Match and parse the value
                Object parsedValue = parseValue(value, fieldsFetcher.fetch(key.replaceAll("(.gte|.lte)$", "")));

                // Add parsed value to MultiValueMap
                multiValueMap.add(key, parsedValue);
            } else if (parts.length == 1) {
                String key = parts[0].trim();
                multiValueMap.add(key, null);
            }
        }
        Set<String> keys = Set.copyOf(multiValueMap.keySet());
        for (String key : keys) {
            if (key.contains("relatedParty.partyOrPartyRole")) {
                // Get the values associated with the old key
                var values = multiValueMap.get(key);

                // Remove the old key
                multiValueMap.remove(key);

                // Add the new key with the same values
                multiValueMap.put(key.replace("relatedParty.partyOrPartyRole", "relatedParty"), values);
            }
        }
        return multiValueMap;
    }

    private static Object parseValue(String value, Field field) {
        Class<?> fieldType = field.getType();
        try {
            // Check for OffsetDateTime
            if (OffsetDateTime.class.equals(fieldType)) {
                return OffsetDateTime.parse(value);
            }

            // Check for LocalDate
            if (LocalDate.class.equals(fieldType)) {
                return LocalDate.parse(value);
            }

            // Handle Integer
            if (Integer.class.equals(fieldType) || int.class.equals(fieldType)) {
                return Integer.parseInt(value);
            }

            // Handle Long
            if (Long.class.equals(fieldType) || long.class.equals(fieldType)) {
                return Long.parseLong(value);
            }

            // Handle Double
            if (Double.class.equals(fieldType) || double.class.equals(fieldType)) {
                return Double.parseDouble(value);
            }

            // Handle Boolean
            if (Boolean.class.equals(fieldType) || boolean.class.equals(fieldType)) {
                return Boolean.parseBoolean(value);
            }
            // Handle Enum
            if (fieldType.isEnum()) {
                return fromValue(fieldType.asSubclass(Enum.class), value);
            }
        } catch (Exception e) {
            log.error("Query values parsing Error", e);
        }

        // Fallback: return the raw string if no matching type is found or parsing fails
        return value;
    }

    public static <T extends Enum<T>> T fromValue(Class<T> enumType, String value) {
        try {
            // Get the "fromValue" method from the enum class
            Method fromValueMethod = enumType.getMethod("fromValue", String.class);
            // Invoke the "fromValue" method with the input value
            @SuppressWarnings("unchecked")
            T result = (T) fromValueMethod.invoke(null, value);
            return result;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert value: " + value + " to enum: " + enumType.getName(), e);
        }
    }

    public static boolean isValidDate(String date) {
        try {
            DATE_FORMATTER.parse(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
