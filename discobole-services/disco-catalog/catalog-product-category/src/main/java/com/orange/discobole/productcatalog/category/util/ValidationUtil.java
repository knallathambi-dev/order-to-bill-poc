// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.util;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import com.orange.discobole.productcatalog.category.deserialier.BeanValidationDeserializer;
import com.orange.discobole.productcatalog.category.exception.DiscoClientException;
import com.orange.discobole.productcatalog.category.pojo.category.*;

import jakarta.validation.ConstraintViolationException;
import java.util.List;

public class ValidationUtil {

    private static final String CANNOT_DESERIALIZE_THE_TYPE_OF_PROPERTY = "Cannot deserialize, the type of property ";
    private static final String NOTMATCH = " does not match with the schema";
    private static final String DISCO_INVALID_JSON_BODY = "DISCO_INVALID_JSON_BODY";

    private ValidationUtil() {}

    public static SimpleModule getSimpleModule() {
        SimpleModule validationModule = new SimpleModule();
        validationModule.setDeserializerModifier(new BeanDeserializerModifier() {
            @Override
            public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc,
                                                          JsonDeserializer<?> deserializer) {
                if (deserializer instanceof BeanDeserializer) {
                    return new BeanValidationDeserializer((BeanDeserializer) deserializer);
                }
                return deserializer;
            }
        });
        return validationModule;
    }

    public static Class<?> getClassToConvertCategory(String className) {
        return switch (className) {
            case "selectEntityType" -> SelectEntityType.class;
            case "defineCategoryIdentityData" -> DefineCategoryIdentityData.class;
            case "defineEntity" -> DefineEntity.class;
            case "cancelEntityOperation" -> CancelEntityOperation.class;
            case "validateEntityOperation" -> ValidateEntityOperation.class;
            case "defineSubcategory" -> DefineSubcategory.class;
            case "selectCategory" -> SelectCategory.class;
            case "selectEntities" -> SelectEntities.class;
            default -> throw new IllegalArgumentException("Converting into " + className + " is not possible");
        };
    }

    public static Object validatePojo(Object value, String entity, String className) {
        return validate(value, className, false);
    }

    public static List<Object> validateArrayOfPojo(Object value, String entity, String className) {
        return (List<Object>) validate(value, className, true);
    }

    private static Object validate(Object value, String className, boolean isArray) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(getSimpleModule());
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Class<?> entityClass = getClassToConvertCategory(className);

        try {
            if (isArray) {
                return objectMapper.readValue(
                        objectMapper.writeValueAsString(value),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, entityClass)
                );
            } else {
                return objectMapper.readValue(objectMapper.writeValueAsString(value), entityClass);
            }
        } catch (InvalidFormatException e) {
            throw new InvalidParameterException(formatErrorMessage(e, "format", "yyyy-MM-dd'T'HH:mm:ss.SSSX"));
        } catch (UnrecognizedPropertyException e) {
            throw new InvalidParameterException(formatErrorMessage(e, "unrecognized property", null));
        } catch (MismatchedInputException e) {
            throw new InvalidParameterException(formatErrorMessage(e, "type mismatch", null));
        } catch (ConstraintViolationException | JsonMappingException e) {
            int index = e.getMessage().indexOf("(");
            String result = (index >0) ? e.getMessage().substring(0, index-1) : e.getMessage();
            throw new DiscoManagedClientException(DISCO_INVALID_JSON_BODY,e.getMessage(),result);
        } catch (Exception e) {
            throw new DiscoClientException(e.getMessage());
        }
    }

    private static String formatErrorMessage(JsonMappingException e, String errorType, String format) {
        List<JsonMappingException.Reference> references = e.getPath();
        String fieldName = getFieldName(references);
        int size = references.size();

        if ("format".equals(errorType)) {
            return "Cannot deserialize, format of " + fieldName + " is wrong. Should be in " + format + " pattern";
        } else if ("unrecognized property".equals(errorType)) {
            String extraField = size > 2 ? references.get(size - 1).getFieldName() : fieldName;
            return "Cannot deserialize, unrecognized property " + extraField + " in " + fieldName;
        } else if ("type mismatch".equals(errorType)) {
            String extraField = size > 2 ? references.get(size - 1).getFieldName() : fieldName;
            return CANNOT_DESERIALIZE_THE_TYPE_OF_PROPERTY + extraField + " in " + fieldName + NOTMATCH;
        }
        return "Cannot deserialize: error in " + fieldName;
    }

    private static String getFieldName(List<JsonMappingException.Reference> references) {
        return references.get(0).getFieldName() != null ? references.get(0).getFieldName()
                : (references.size() > 1 ? references.get(1).getFieldName() : "unknown field");
    }
}
