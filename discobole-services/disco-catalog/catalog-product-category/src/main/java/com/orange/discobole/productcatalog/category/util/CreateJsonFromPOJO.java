// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.orange.discobole.processflow.annotation.Array;
import com.orange.discobole.processflow.annotation.DefaultValue;
import com.orange.discobole.processflow.annotation.Format;
import com.orange.discobole.processflow.annotation.ReadOnly;
import com.orange.discobole.processflow.pojo.Property;

public class CreateJsonFromPOJO {

	private static final Logger LOGGER = LogManager.getLogger(CreateJsonFromPOJO.class);

	private static final String OBJECTTYPE = "object";
	private static final String ARRAYTYPE = "array";
	private static final String STRINGTYPE = "string";

	public static void main(String args[]) throws IOException {
		CreateJsonFromPOJO obj = new CreateJsonFromPOJO();

		Reflections reflections = new Reflections(
				"com.orange.discobole.productcatalog.category.pojo",
				new SubTypesScanner(false)
		);
		Set<Class<? extends Object>> allClasses = reflections.getSubTypesOf(Object.class);

		Path baseSchemasPath = Path.of("src", "main", "resources", "schemas");

		for (Class<?> type : allClasses) {
			Property schema = obj.createJson(type);

			ObjectMapper objectMapper = new ObjectMapper();
			try {
				ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter()
						.withRootName(type.getSimpleName());

				String packageName = type.getPackageName();
				Path outputPath;
				if ("com.orange.discobole.productcatalog.category.pojo".equals(packageName)) {
					outputPath = baseSchemasPath.resolve(type.getSimpleName() + ".json");
				} else {
					String packageList = packageName.substring(packageName.lastIndexOf('.') + 1);
					outputPath = baseSchemasPath.resolve(packageList)
							.resolve(type.getSimpleName() + ".json");
				}

				Files.createDirectories(outputPath.getParent());
				writer.writeValue(outputPath.toFile(), schema);
				LOGGER.info("Schema generated: {}", outputPath.toAbsolutePath());

			} catch (JsonProcessingException e) {
				LOGGER.error("Unable to generate schema for {}", type.getName(), e);
			}
		}
	}

	public List<String> getNonNullFields(Class<?> type) {
		List<String> nonNullFields = new ArrayList<>();
		for (Field field : type.getDeclaredFields()) {
			if (field.isAnnotationPresent(NotEmpty.class)
					|| field.isAnnotationPresent(NotNull.class)
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

	public static boolean isPrimitiveOrPrimitiveWrapper(Class<?> type) {
		return type.isPrimitive() && type != void.class
				|| type == Double.class
				|| type == Float.class
				|| type == Long.class
				|| type == Integer.class
				|| type == Short.class
				|| type == Byte.class;
	}

	public Property createJson(Class<?> type) {
		List<String> nonNullFields = getNonNullFields(type);
		Property schema = new Property();
		schema.setRequired(nonNullFields);

		if (type.isAnnotationPresent(Array.class)) {
			schema.setType(ARRAYTYPE);
			Property arrayItem = new Property();
			arrayItem.setType(OBJECTTYPE);
			arrayItem.setProperties(readPOJO(type));
			schema.setItems(arrayItem);
		} else {
			schema.setType(OBJECTTYPE);
			schema.setProperties(readPOJO(type));
		}

		return schema;
	}



	private Map<String, Property> readPOJO(Class<?> type) {
		Map<String, Property> propertyMap = new LinkedHashMap<>();

		for (Field field : type.getDeclaredFields()) {
			Property fieldProp = new Property();
			setCustomFieldValue(field, fieldProp);

			if (isPrimitiveOrPrimitiveWrapper(field.getType())) {
				handlePrimitiveField(field, fieldProp);
			} else if (isSimpleType(field.getType())) {
				handleSimpleTypeField(field, fieldProp);
			} else if (OffsetDateTime.class.equals(field.getType())) {
				handleOffsetDateTimeField(fieldProp);
			} else if (field.getType().isEnum()) {
				handleEnumField(field, fieldProp);
			} else if (List.class.equals(field.getType())) {
				handleListField(field, fieldProp);
			} else {
				handleNestedObjectField(field, fieldProp);
			}

			String propertyName = getJsonPropertyName(field);
			propertyMap.put(propertyName, fieldProp);
		}

		return propertyMap;
	}

	private boolean isSimpleType(Class<?> type) {
		return type == String.class || type == Character.class || type == Boolean.class;
	}

	private String getJsonPropertyName(Field field) {
		return field.isAnnotationPresent(JsonProperty.class)
				? field.getAnnotation(JsonProperty.class).value()
				: field.getName();
	}

	private void handlePrimitiveField(Field field, Property fieldProp) {
		fieldProp.setType("number");
		setDefaultValue(field, fieldProp);
	}

	private void handleSimpleTypeField(Field field, Property fieldProp) {
		if (field.getType() == String.class) {
			fieldProp.setType(STRINGTYPE);
		} else {
			fieldProp.setType(field.getType().getSimpleName().toLowerCase());
		}
		setDefaultValue(field, fieldProp);
	}

	private void handleOffsetDateTimeField(Property fieldProp) {
		fieldProp.setType(STRINGTYPE);
	}

	private void handleEnumField(Field field, Property fieldProp) {
		fieldProp.setType(STRINGTYPE);
		fieldProp.setEnumValues(Arrays.asList(field.getType().getEnumConstants()));
	}

	private void handleListField(Field field, Property fieldProp) {
		fieldProp.setType(ARRAYTYPE);

		ParameterizedType listType = (ParameterizedType) field.getGenericType();
		Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];

		if (isSimpleType(listClass) || isPrimitiveOrPrimitiveWrapper(listClass)) {
			Property itemProp = new Property();
			if (listClass == String.class) itemProp.setType(STRINGTYPE);
			else if (listClass == Boolean.class) itemProp.setType("boolean");
			else itemProp.setType("number");
			fieldProp.setItems(itemProp);
		} else {
			fieldProp.setItems(createJson(listClass));
		}
	}

	private void handleNestedObjectField(Field field, Property fieldProp) {
		fieldProp.setType(OBJECTTYPE);
		fieldProp.setRequired(getNonNullFields(field.getType()));
		fieldProp.setProperties(readPOJO(field.getType()));
	}



	private void setDefaultValue(Field field, Property fieldValue) {
		if (!field.isAnnotationPresent(DefaultValue.class)) return;

		String defaultValue = field.getAnnotation(DefaultValue.class).value();
		Class<?> type = field.getType();

		if (type == Integer.TYPE || type == Integer.class) fieldValue.setDefaultValue(Integer.valueOf(defaultValue));
		else if (type == Float.TYPE || type == Float.class) fieldValue.setDefaultValue(Float.valueOf(defaultValue));
		else if (type == Double.TYPE || type == Double.class) fieldValue.setDefaultValue(Double.valueOf(defaultValue));
		else if (type == Long.TYPE || type == Long.class) fieldValue.setDefaultValue(Long.valueOf(defaultValue));
		else if (type == Short.TYPE || type == Short.class) fieldValue.setDefaultValue(Short.valueOf(defaultValue));
		else if (type == Boolean.TYPE || type == Boolean.class) fieldValue.setDefaultValue(Boolean.valueOf(defaultValue));
		else fieldValue.setDefaultValue(defaultValue);
	}

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
