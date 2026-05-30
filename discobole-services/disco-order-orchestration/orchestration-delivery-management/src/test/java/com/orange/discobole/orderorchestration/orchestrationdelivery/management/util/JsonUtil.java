// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
        OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public static <T> T readObjectFromString(String json, TypeReference<T> typeReference) {
        T result;

        try {
            result = OBJECT_MAPPER.readValue(json, typeReference);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public static <T> T readObjectFromFile(String filePath, TypeReference<T> typeReference) {
        return readObjectFromString(readStringFromFile(filePath), typeReference);
    }

    public static String readStringFromFile(String filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }

        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("File does not exist at path: " + filePath);
        }

        try {
            return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readObjectFromResource(String resourcePath, TypeReference<T> typeReference) {
        return readObjectFromFile(JsonUtil.class.getResource(resourcePath).getPath(), typeReference);
    }

    public static String readStringFromResource(String resourcePath) {
        return readStringFromFile(JsonUtil.class.getResource(resourcePath).getPath());
    }

    public static <T> T readStringToObject(String value) {
        try {
            return OBJECT_MAPPER.readValue(toJsonStringFromObject(value), new TypeReference<T>(){} );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toJsonStringFromObject(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
