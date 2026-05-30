// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.orange.discobole.processflow.annotation.Array;
import com.orange.discobole.processflow.annotation.DefaultValue;
import com.orange.discobole.processflow.annotation.Format;
import com.orange.discobole.processflow.annotation.ReadOnly;
import com.orange.discobole.processflow.pojo.Property;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.time.OffsetDateTime;
import java.util.*;

/**
 * The Class CreateJsonFromPOJO reads all pojo from package and creates a schema
 * json in resources.
 *
 * @since 1.0
 */
@Slf4j
public class CreateJsonFromPOJO {
    private static final String OBJECT_TYPE = "object";
    private static final String ARRAY_TYPE = "array";

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws IOException signals that an I/O exception has occurred.
     */
    public static void main(String[] args) throws IOException {
        CreateJsonFromPOJO obj = new CreateJsonFromPOJO();
        Reflections reflections = new Reflections("com.orange.discobole.ordermanagement.ordercapture.pojo.spec.characteristic.operation",
                new SubTypesScanner(false));
        Set<Class<?>> allClasses = reflections.getSubTypesOf(Object.class);
        for (Class<?> type : allClasses) {
            Property nextTask = obj.createJson(type);
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter().withRootName(type.getSimpleName());
                String packageName = type.getPackageName();
                if ("com.orange.discobole.ordermanagement.ordercapture.pojo.spec.characteristic.operation".equals(packageName)) {
                    writer.writeValue(new File("./src/main/resources/schemas/" + type.getSimpleName() + ".json"),
                            nextTask);
                } else {
                    String packageList = packageName.substring(packageName.lastIndexOf('.') + 1);
                    writer.writeValue(new File(String.format("./src/main/resources/schemas/%s/%s.json", packageList, type.getSimpleName())),
                            nextTask);
                }
                log.info("Schema generated");
            } catch (JsonProcessingException e) {
                log.error(e.getMessage());
            }
        }
    }

    /**
     * Checks if is primitive or primitive wrapper.
     *
     * @param type the type
     * @return true, if is primitive or primitive wrapper
     */
    public static boolean isPrimitiveOrPrimitiveWrapper(Class<?> type) {
        return type.isPrimitive() && type != void.class || type == Double.class || type == Float.class
                || type == Long.class || type == Integer.class || type == Short.class || type == Byte.class;
    }

    /**
     * Gets the non-null fields from the pojo.
     *
     * @param type the class type
     * @return the non-null fields
     */
    public List<String> getNonNullFields(Class<?> type) {
        List<String> nonNullFields = new ArrayList<>();
        for (final Field field : type.getDeclaredFields()) {
            if (field.isAnnotationPresent(NotEmpty.class) || field.isAnnotationPresent(NotNull.class)
                    || field.isAnnotationPresent(NotBlank.class)) {
                if (field.isAnnotationPresent(JsonProperty.class)) {
                    nonNullFields.add(field.getAnnotation(JsonProperty.class).value());
                } else {
                    nonNullFields.add(field.getName());
                }
            }
        }
        return nonNullFields;
    }

    /**
     * Creates the json of either array or object type.
     *
     * @param type the class type
     * @return the property
     */
    public Property createJson(Class<?> type) {
        List<String> nonNullFields = getNonNullFields(type);
        Property nextTask = new Property();
        nextTask.setRequired(nonNullFields);
        if (type.isAnnotationPresent(Array.class)) {
            nextTask.setType(ARRAY_TYPE);
            Property arrayItem = new Property();
            arrayItem.setType(OBJECT_TYPE);
            Map<String, Property> arrayFinalMap = new LinkedHashMap<>(readPOJO(type));
            arrayItem.setProperties(arrayFinalMap);
            nextTask.setItems(arrayItem);
        } else {
            nextTask.setType(OBJECT_TYPE);
            Map<String, Property> finalMap = new LinkedHashMap<>(readPOJO(type));
            nextTask.setProperties(finalMap);
        }
        return nextTask;
    }

    /**
     * Read POJO method sets the properties based on its field type.
     *
     * @param type the type
     * @return the map
     */
    private Map<String, Property> readPOJO(Class<?> type) {
        Map<String, Property> propertyMap = new LinkedHashMap<>();
        for (final Field field : type.getDeclaredFields()) {
            Property fieldValue = new Property();
            setCustomFieldValue(field, fieldValue);
            if (isPrimitiveOrPrimitiveWrapper(field.getType())) {
                fieldValue.setType("number");
                setDefaultValue(field, fieldValue);
            } else if (field.getType() == String.class || field.getType() == Character.class
                    || field.getType() == Boolean.class) {
                fieldValue.setType(field.getType().getSimpleName().toLowerCase());
                setDefaultValue(field, fieldValue);
            } else if (OffsetDateTime.class.equals(field.getType())) {
                fieldValue.setType("string");
            } else if (field.getType().isEnum()) {
                fieldValue.setType("string");
                fieldValue.setEnumValues(Arrays.asList(field.getType().getEnumConstants()));
            } else if (List.class.equals(field.getType())) {
                fieldValue.setType(ARRAY_TYPE);
                ParameterizedType listType = (ParameterizedType) field.getGenericType();
                Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];
                fieldValue.setItems(createJson(listClass));
            } else {
                fieldValue.setType(OBJECT_TYPE);
                fieldValue.setRequired(getNonNullFields(field.getType()));
                fieldValue.setProperties(readPOJO(field.getType()));
            }
            if (field.isAnnotationPresent(JsonProperty.class)) {
                propertyMap.put(field.getAnnotation(JsonProperty.class).value(), fieldValue);
            } else {
                propertyMap.put(field.getName(), fieldValue);
            }
        }
        return propertyMap;
    }

    /**
     * Sets the default value based on the type of field.
     *
     * @param field the field
     * @param fieldValue the field value
     */
    private void setDefaultValue(Field field, Property fieldValue) {
        if (field.isAnnotationPresent(DefaultValue.class)) {
            String defaultValue = field.getAnnotation(DefaultValue.class).value();
            Class<?> type = field.getType();
            if (type == Integer.TYPE || type == Integer.class) {
                fieldValue.setDefaultValue(Integer.valueOf(defaultValue));
            } else if (type == Float.TYPE || type == Float.class) {
                fieldValue.setDefaultValue(Float.valueOf(defaultValue));
            } else if (type == Double.TYPE || type == Double.class) {
                fieldValue.setDefaultValue(Double.valueOf(defaultValue));
            } else if (type == Long.TYPE || type == Long.class) {
                fieldValue.setDefaultValue(Long.valueOf(defaultValue));
            } else if (type == Short.TYPE || type == Short.class) {
                fieldValue.setDefaultValue(Short.valueOf(defaultValue));
            } else if (type == Boolean.TYPE || type == Boolean.class) {
                fieldValue.setDefaultValue(Boolean.valueOf(defaultValue));
            } else {
                fieldValue.setDefaultValue(defaultValue);
            }
        }
    }

    /**
     * Sets the custom field value like format, pattern and read only in schema.
     *
     * @param field the field
     * @param fieldValue the field value
     */
    private void setCustomFieldValue(final Field field, Property fieldValue) {
        if (field.isAnnotationPresent(Format.class)) {
            fieldValue.setFormat(field.getAnnotation(Format.class).value());
        } else if (field.isAnnotationPresent(Pattern.class)) {
            fieldValue.setFormat(field.getAnnotation(Pattern.class).regexp());
        }
        if (field.isAnnotationPresent(ReadOnly.class)) {
            fieldValue.setReadOnly(field.getAnnotation(ReadOnly.class).value());
        }
    }
}